import GUIutils.GUIBoard;
import GUIutils.GUITile;
import GUIutils.GUITray;
import ScrabbleObjects.Tile;
import ScrabbleObjects.Tray;
import ScrabbleObjects.Word;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import utils.*;
import utils.Trie.Trie;

import java.util.ArrayList;
import java.util.Collections;

public class GUI extends Application implements EntryPoint {

    private static Trie dictionary;
    private static GUIBoard board;

    private static int BOARD_DIMENSIONS = 7;
    private static boolean GAMEOVER = false;

    private ArrayList<GUITile> bag = new ArrayList<>(100);

    private static GUITile selectedTile = null;
    private ArrayList<GUITile> placedTiles = new ArrayList<>();

    private GUITray playerTray = new GUITray();
    private Tray AITray = new Tray();

    private final TextArea gameInfoDisplay = new TextArea("");

    private final Label aiScore = new Label("0");
    private final Label playerScore = new Label("0");

    public static void main(String[] args) {
        dictionary = EntryPoint.parseClIForTrie(args);

        if (args.length == 2) {
            BOARD_DIMENSIONS = Integer.parseInt(args[1]);
        } else {
            BOARD_DIMENSIONS = 15;
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox rootDisplay = new VBox();


        // Score display banner
        Label aiScoreLabel = new Label("AI Score: ");
        Label playerScoreLabel = new Label("Player score: ");

        aiScoreLabel.setTextFill(Paint.valueOf("White"));
        aiScore.setTextFill(Paint.valueOf("White"));
        playerScoreLabel.setTextFill(Paint.valueOf("White"));
        playerScore.setTextFill(Paint.valueOf("White"));

        //Make score banner
        HBox scoreBanner = new HBox();
        Region spacer = new Region();
        spacer.setPadding(new Insets(0, 5, 0, 5));
        scoreBanner.setAlignment(Pos.CENTER);
        scoreBanner.getChildren().addAll(aiScoreLabel, aiScore, spacer, playerScoreLabel, playerScore);

        //Fill bag and shuffle
        fillBag();

        // Initialize trays with 7 tiles each
        AITray = new Tray(new ArrayList<>(bag.subList(0, 7)));
        for (GUITile newTile : bag.subList(0, 7)) {
            playerTray.addGUITile(newTile);
        }

        // Remove tiles that were given to player trays
        bag = new ArrayList<>(bag.subList(14, bag.size()));

        // Make event listeners for playerTray
        for (Tile tile : playerTray.getSpacesArray()) {
            setEventListenerOnPlayerTile((GUITile) tile);
        }

        // Make gui board
        board = new GUIBoard(BOARD_DIMENSIONS, BoardLayouts.getBoardLayout(BOARD_DIMENSIONS));
        board.getRoot().setAlignment(Pos.CENTER);

        // Make event listeners for board spaces
        for (int i = 0; i < board.BOARD_SIZE; i++) {
            for (int j = 0; j < board.BOARD_SIZE; j++) {
                GUITile tile = (GUITile) board.getTileAtCoordinates(i, j);

                tile.getRoot().setOnMouseClicked(event -> {
                    setEventListenerOnBoardSpace(tile);
                });
            }
        }


        // Make submission, reset, and swap buttons
        Button playerMoveSubmitButton = new Button("Submit Play");
        Button swapTilesButton = new Button("Swap Tiles");
        Button skipButton = new Button("Skip");
        Button playerReset = new Button("Reset Play");

        playerMoveSubmitButton.setOnMouseClicked(event -> {
            if (GAMEOVER) return;

            if (processPlayerMove(board)) {
                switchPlayerToMove(PlayerType.HUMAN);
            }
        });

        swapTilesButton.setOnMouseClicked(event -> {
            if (GAMEOVER) return;

            // Reset player move
            resetPlayerMove();

            // Collect all flipped tiles
            ArrayList<GUITile> flippedTiles = new ArrayList<>();
            for (Tile tile : playerTray.getSpacesArray()) {
                if (((GUITile) tile).isFlipped()) {
                    flippedTiles.add((GUITile) tile);
                }
            }

            // If no flipped tiles found return
            if (flippedTiles.isEmpty()) return;

            // Remove flipped tiles from player tray
            for (GUITile tile : flippedTiles) {
                tile.flipTile();
                tile.getRoot().setOnMouseClicked(null);
                playerTray.removeTileFromTray(tile);
                playerTray.getRoot().getChildren().remove(tile.getRoot());
                bag.add(tile);
            }

            // Swap new tiles into player tray
            for (int i = 0; i < flippedTiles.size() && !bag.isEmpty(); i++) {
                GUITile newTile = bag.removeFirst();
                playerTray.addGUITile(newTile);
                setEventListenerOnPlayerTile(newTile);
            }

            updateGameInfoDisplay("Player has swapped out tiles.\n");

            // End player turn
            switchPlayerToMove(PlayerType.HUMAN);
        });

        skipButton.setOnMouseClicked(event -> {
            if (GAMEOVER) return;

            resetPlayerMove();

            switchPlayerToMove(PlayerType.HUMAN);
        });

        playerReset.setOnMouseClicked(event -> {
            if (GAMEOVER) return;

            resetPlayerMove();
        });


        // Make button trailer HBox
        HBox trailer = new HBox();
        //Spacers to prettify display
        Region buttonSpacer1 = new Region();
        Region buttonSpacer2 = new Region();
        Region buttonSpacer3 = new Region();
        HBox.setHgrow(buttonSpacer1, Priority.ALWAYS);
        HBox.setHgrow(buttonSpacer2, Priority.ALWAYS);
        HBox.setHgrow(buttonSpacer3, Priority.ALWAYS);
        //Set properties and add children
        trailer.setSpacing(5);
        trailer.getChildren().addAll(skipButton, buttonSpacer3, playerReset, buttonSpacer1, swapTilesButton, buttonSpacer2, playerMoveSubmitButton);

        //Set properties of game info display
        gameInfoDisplay.setEditable(false);
        gameInfoDisplay.setStyle("-fx-control-inner-background: black; -fx-text-fill: white;");

        //Set properties of root display
        rootDisplay.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        rootDisplay.setPadding(new Insets(5, 5, 5, 5));
        rootDisplay.setSpacing(10);


        //Set all children on root display
        rootDisplay.getChildren().addAll(scoreBanner, board.getRoot(), playerTray.getRoot(), trailer, gameInfoDisplay);
        rootDisplay.setAlignment(Pos.CENTER);
        VBox.setVgrow(rootDisplay, Priority.ALWAYS);


        // Set scene on stage
        Scene root = new Scene(rootDisplay);
        primaryStage.setScene(root);
        primaryStage.setTitle("Scrabble");
        primaryStage.show();
    }

    /**
     * Sets an event listener on a players tile. If the player clicks on this tile in
     * the tray, we should toggle the selection indicator for this tile and the previously
     * selected tile (if it exists), and update the selectedTile variable with this tile
     */
    private void setEventListenerOnPlayerTile(GUITile tile) {
        tile.getRoot().setOnMouseClicked(event -> {
            // Check if left click
            if (event.getButton() == MouseButton.PRIMARY) {
                // Set this as the selected tile
                if (selectedTile != null) {
                    selectedTile.toggleUserSelectionIndicator();
                }

                //Un flip this tile
                if (tile.isFlipped()) tile.flipTile();

                selectedTile = tile;

                tile.toggleUserSelectionIndicator();
            } else if (event.getButton() == MouseButton.SECONDARY) {
                // Deselect this tile if it's currently selected
                if (selectedTile != null && selectedTile == tile) {
                    tile.toggleUserSelectionIndicator();
                    selectedTile = null;
                }

                // Flip this tile
                tile.flipTile();
            }

        });
    }

    /**
     * Sets an event listener on a board space. If the player clicks this
     * board space and a tile is selected, we want to place the selected tile on the space.
     */
    private void setEventListenerOnBoardSpace(GUITile space) {
        // Check if selected space is already filled
        if (space.getRoot().getChildren().size() > 2) return;

        if (selectedTile != null) {
            //Remove from player tray
            playerTray.removeTileFromTray(selectedTile);
            //Set on board
            playerTray.getRoot().getChildren().remove(selectedTile.getRoot());
            space.getRoot().getChildren().add(selectedTile.getRoot());
            //Set coordinates
            selectedTile.setCol(space.getCol());
            selectedTile.setRow(space.getRow());
            //Toggle selection indicator
            selectedTile.toggleUserSelectionIndicator();

            //Check if this is a blank tile
            if (selectedTile.getContents().contains("*")) {
                //We need the player to select what letter to use for this blank
                selectedTile.setGuiContents(showBlankTileDialogue());
            }

            //Add to placed tiles
            placedTiles.add(selectedTile);
            //Remove event handler
            selectedTile.getRoot().setOnMouseClicked(event -> {
            });
            selectedTile = null;
        }
    }

    /**
     * Checks for game over conditions and switches the game to the ai or human.
     * @param lastMoved Player that last moved
     */
    private void switchPlayerToMove(PlayerType lastMoved) {
        // Check for the move possibilities
        PlayData playerMove = Solver.solveBoardState(dictionary, new Pair<>(board, playerTray));
        PlayData aiMove = Solver.solveBoardState(dictionary, new Pair<>(board, AITray));

        // Check if neither player has a move
        if (playerMove == null && aiMove == null) {
            gameOver();
            return;
        }

        // Check for game over state
        if (bag.isEmpty()) {
            if (playerTray.isEmpty() || AITray.isEmpty()) {
                gameOver();
                return;
            }

            // Check if the player to play can move
            if (lastMoved == PlayerType.HUMAN) {
                if (aiMove == null) {
                    gameOver();
                    return;
                }
            } else {
                if (playerMove == null) {
                    gameOver();
                    return;
                }
            }
        }

        if (lastMoved == PlayerType.HUMAN) {
            if (aiMove != null) {
                makeAIMove(aiMove);
            } else {
                makeAITraySwap();
            }
        }
    }

    /**
     * Determines who won the game and update game info display.
     * Also removes all event handlers from tiles
     */
    public void gameOver() {
        GAMEOVER = true;

        int aiFinalScore = Integer.parseInt(aiScore.getText());
        int playerFinalScore = Integer.parseInt(playerScore.getText());

        //Adjust scores by unplayed tiles
        for (Tile tile : playerTray.getSpacesArray()) {
            playerFinalScore -= tile.getLetterPointValue();

            if (AITray.isEmpty()) {
                aiFinalScore += tile.getLetterPointValue();
            }
        }

        for (Tile tile : AITray.getSpacesArray()) {
            aiFinalScore -= tile.getLetterPointValue();

            if (playerTray.isEmpty()) {
                playerFinalScore += tile.getLetterPointValue();
            }
        }

        String winner = "";
        if (aiFinalScore == playerFinalScore) {
            if (Integer.parseInt(aiScore.getText()) > Integer.parseInt(playerScore.getText())) {
                winner = "AI";
            } else {
                winner = "Player";
            }
        } else {
            winner = (aiFinalScore > playerFinalScore) ? "AI" : "Player";
        }

        //Turn off event handlers
        for (Tile tile : playerTray.getSpacesArray()) {
            ((GUITile) tile).getRoot().setOnMouseClicked(null);
            selectedTile = null;
        }

        updateGameInfoDisplay("Game over!\n" + winner + " has won the game.");
    }

    /**
     * Updates the info display with new text in the bottom of the GUI.
     * @param output New text to append
     */
    private void updateGameInfoDisplay(String output) {
        gameInfoDisplay.appendText("\n" + output);
        gameInfoDisplay.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Takes a list of words and puts them into one nice string.
     * [s1, s2,...,sn] = "s1, s2, ..., sn"
     * @param newWords New words to put in string
     * @return String of new words joined by a comma
     */
    private String newWordsToString(ArrayList<Word> newWords) {
        StringBuilder out = new StringBuilder();

        for (Word word : newWords) {
            out.append(word).append(", ");
        }

        return out.substring(0, out.length() - 2);
    }

    /**
     * Resets the players turn. Any tiles on the board are returned back to the tray
     */
    private void resetPlayerMove() {
        for (GUITile placedTile : placedTiles) {
            ((GUITile) board.getTileAtCoordinates(placedTile.getRow(), placedTile.getCol())).getRoot().getChildren().remove(placedTile.getRoot());
            placedTile.setRow(-1);
            placedTile.setCol(-1);

            //Check if blank tile
            if (placedTile.getContents().toUpperCase().charAt(0) == placedTile.getContents().charAt(0)) {
                placedTile.setGuiContents("*");
            }

            playerTray.addGUITile(placedTile);
            setEventListenerOnPlayerTile(placedTile);
        }

        placedTiles = new ArrayList<>();
    }


    /**
     * Check if the players move is valid or not. If the paly is valid, it should be scored
     * and the turn should be switched to the AI.
     * @param board Board to check
     * @return If this move is valid
     */
    private boolean processPlayerMove(GUIBoard board) {
        //Make result board
        Board resultBoard = board.copyOf();

        //place tiles
        for (GUITile placedTile : placedTiles) {
            resultBoard.setTileOnBoard(placedTile);
        }

        //Check compatibility
        BoardCompatibilityCheckData data = EntryPoint.areBoardsCompatible(dictionary, board, resultBoard);

        //Make move if legal
        if (data.isLegal()) {
            //Set score
            int score = EntryPoint.scorePlay(board, data.newTiles().size(), data.newWords());
            playerScore.setText(String.valueOf(Integer.parseInt(playerScore.getText()) + score));



            //Update game info
            updateGameInfoDisplay("Player made move: \n" + data.output() + "New Word(s) are: " + newWordsToString(data.newWords()) + "\nScore: " + score + "\n");

            // Unflip player tiles
            for (Tile tile : playerTray.getSpacesArray()) {
                if (((GUITile) tile).isFlipped()) ((GUITile) tile).flipTile();
            }

            // Set tile on boards array
            for (GUITile placedTile : placedTiles) {
                //Set tiles on board
                board.setTileOnBoard(placedTile);
                board.getMultiplierAtCoordinates(placedTile.getRow(), placedTile.getCol()).setMultiplierAsUsed();
            }

            // Replenish player tray
            while (playerTray.size() < 7 && !bag.isEmpty()) {
                GUITile newTile = bag.removeFirst();
                setEventListenerOnPlayerTile(newTile);
                playerTray.addGUITile(newTile);
            }

            placedTiles = new ArrayList<>();
            return true;
        } else {
            //Put tiles back in tray
            resetPlayerMove();
        }

        return false;
    }

    /**
     * Fully swaps out the AI tray
     */
    private void makeAITraySwap() {
        ArrayList<GUITile> newTiles = new ArrayList<>();

        while (!AITray.isEmpty() && !bag.isEmpty()) {
            GUITile newTile = bag.removeFirst();
            newTiles.add(newTile);
            bag.add((GUITile) AITray.getSpacesArray().removeFirst());
        }

        while (!newTiles.isEmpty()) {
            AITray.addSpace(newTiles.removeFirst());
        }

        updateGameInfoDisplay("AI has swapped out tiles in tray.\n");

        switchPlayerToMove(PlayerType.AI);
    }

    /**
     * Makes an already computed move on the board. All this function has to do is
     * put the tiles on the GUI, and update relevant information displays.
     * @param move Move to make
     */
    private void makeAIMove(PlayData move) {
        // Turn tiles into GUITiles and remove from ai tray
        ArrayList<GUITile> guiTiles = new ArrayList<>();
        for (Tile ogTile : move.newPlay().getSpacesArray()) {
            guiTiles.add(new GUITile(ogTile));

            if (ogTile.getContents().toUpperCase().equals(ogTile.getContents())) {
                AITray.removeTileFromTray(new GUITile("*", -1, -1));
            } else {
                AITray.removeTileFromTray(ogTile);
            }
        }

        //Replenish ai tray
        while (AITray.size() < 7 && !bag.isEmpty()) {
            AITray.addSpace(bag.removeFirst());
        }

        //Update AI score
        aiScore.setText(String.valueOf(Integer.parseInt(aiScore.getText()) + move.score()));

        //Add gui tiles to board
        board.setGUITilesOnBoard(guiTiles);

        updateGameInfoDisplay("AI has made move: \n" + move.newPlayString() + "New Word(s) are: " + newWordsToString(move.newWords()) + "\nScore: " + move.score() + "\n");

        switchPlayerToMove(PlayerType.AI);
    }

    public String showBlankTileDialogue() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select Letter For Blank Tile");

        VBox dialogPane = new VBox();
        dialogPane.getChildren().add(new Label("Select Letter"));

        MenuBar menuBar = new MenuBar();
        Menu hamburgerMenu = new Menu("▼");

        for (int i = 0; i < 26; i++) {
            MenuItem newItem = new Menu(String.valueOf((char) (i + 'a')).toUpperCase());
            int finalI = i;
            newItem.setOnAction(e -> dialog.setResult(String.valueOf((char) (finalI + 'a')).toUpperCase()));

            hamburgerMenu.getItems().add(newItem);
        }

        menuBar.getMenus().add(hamburgerMenu);
        dialogPane.getChildren().add(menuBar);
        dialog.getDialogPane().setContent(dialogPane);

        dialog.showAndWait();

        return dialog.getResult();
    }

    private void fillBag() {
        String freq =
                """
                * 0 2
                e 1 12
                a 1 9
                i 1 9
                o 1 8
                n 1 6
                r 1 6
                t 1 6
                l 1 4
                s 1 4
                u 1 4
                d 2 4
                g 2 3
                b 3 2
                c 3 2
                m 3 2
                p 3 2
                f 4 2
                h 4 2
                v 4 2
                w 4 2
                y 4 2
                k 5 1
                j 8 1
                x 8 1
                q 10 1
                z 10 1
                """;

        for (String tileFreq : freq.split("\n")) {
            String[] contents = tileFreq.split(" ");

            for (int n = 0; n < Integer.parseInt(contents[2]); n++) {
                bag.add(new GUITile(contents[0], -1, -1));
            }
        }

        Collections.shuffle(bag);
    }
}

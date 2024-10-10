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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
        board = new GUIBoard(15, BoardLayouts.getBoardLayout(15));
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
        Button playerReset = new Button("Reset Play");

        playerMoveSubmitButton.setOnMouseClicked(event -> {
            if (processPlayerMove(board)) {
                switchPlayerToMove(PlayerType.HUMAN);
            }
        });

        swapTilesButton.setOnMouseClicked(event -> {
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


        playerReset.setOnMouseClicked(event -> {
            resetPlayerMove();
        });


        // Make button trailer HBox
        HBox trailer = new HBox();
        //Spacers to prettify display
        Region buttonSpacer1 = new Region();
        Region buttonSpacer2 = new Region();
        HBox.setHgrow(buttonSpacer1, Priority.ALWAYS);
        HBox.setHgrow(buttonSpacer2, Priority.ALWAYS);
        //Set properties and add children
        trailer.setSpacing(5);
        trailer.getChildren().addAll(playerReset, buttonSpacer1, swapTilesButton, buttonSpacer2, playerMoveSubmitButton);

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
            //Add to placed tiles
            placedTiles.add(selectedTile);
            //Remove event handler
            selectedTile.getRoot().setOnMouseClicked(event -> {
            });
            selectedTile = null;
        }
    }

    private void switchPlayerToMove(PlayerType lastMoved) {
        // Check for the move possibilities
        PlayData playerMove = Solver.solveBoardState(dictionary, new Pair<>(board, playerTray));
        PlayData aiMove = Solver.solveBoardState(dictionary, new Pair<>(board, AITray));

        // Check if neither player has a move
        if (playerMove == null && aiMove == null) {
            //Game over
        }

        // Check for game over state
        if (bag.isEmpty()) {
            if (playerTray.isEmpty() || AITray.isEmpty()) {
                //Game over
            }

            // Check if the player to play can move
            if (lastMoved == PlayerType.HUMAN) {
                if (aiMove == null) {
                    //game over
                }
            } else {
                if (playerMove == null) {
                    //game over
                }
            }
        }

        if (lastMoved == PlayerType.HUMAN) {
            if (aiMove != null) {
                makeAIMove(aiMove);
            } else {
                makeAITraySwap();
            }
        } else {
            //idk
        }
    }

    private void updateGameInfoDisplay(String output) {
        gameInfoDisplay.appendText("\n" + output);
        gameInfoDisplay.setScrollTop(Double.MAX_VALUE);
    }

    private String newWordsToString(ArrayList<Word> newWords) {
        StringBuilder out = new StringBuilder();

        for (Word word : newWords) {
            out.append(word).append(", ");
        }

        return out.substring(0, out.length() - 2);
    }

    private void resetPlayerMove() {
        for (GUITile placedTile : placedTiles) {
            ((GUITile) board.getTileAtCoordinates(placedTile.getRow(), placedTile.getCol())).getRoot().getChildren().remove(placedTile.getRoot());
            placedTile.setRow(-1);
            placedTile.setCol(-1);
            playerTray.addGUITile(placedTile);
            setEventListenerOnPlayerTile(placedTile);
        }

        placedTiles = new ArrayList<>();
    }


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

    private void makeAIMove(PlayData move) {
        // Turn tiles into GUITiles and remove from ai tray
        ArrayList<GUITile> guiTiles = new ArrayList<>();
        for (Tile ogTile : move.newPlay().getSpacesArray()) {
            guiTiles.add(new GUITile(ogTile));

            AITray.removeTileFromTray(ogTile);
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

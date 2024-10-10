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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import utils.Board;
import utils.BoardCompatibilityCheckData;
import utils.BoardLayouts;
import utils.Pair;
import utils.Trie.Trie;

import java.util.ArrayList;
import java.util.Collections;

public class GUI extends Application implements EntryPoint {

    ArrayList<GUITile> bag = new ArrayList<>(100);

    public static GUITile selectedTile = null;
    public ArrayList<GUITile> placedTiles = new ArrayList<>();

    private GUITray playerTray = new GUITray();
    private Tray AITray = new Tray();

    private Label aiScore = new Label("0");
    private Label playerScore = new Label("0");

    private static Trie dictionary;

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
            playerTray.addTile(newTile);
        }

        bag = new ArrayList<>(bag.subList(14, bag.size()));

        // Make event listeners for playerTray
        for (Tile tile : playerTray.getSpacesArray()) {
            setEventListenerOnPlayerTile((GUITile) tile);
        }

        // Make gui board
        GUIBoard guiBoard = new GUIBoard(15, BoardLayouts.getBoardLayout(15));

        // Make event listeners for board spaces
        for (int i = 0; i < guiBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < guiBoard.BOARD_SIZE; j++) {
                GUITile tile = (GUITile) guiBoard.getTileAtCoordinates(i, j);

                tile.getRoot().setOnMouseClicked(event -> {
                    setEventListenerOnBoardSpace(tile);
                });
            }
        }


        // Make submission and reset button
        Button playerMoveSubmitButton = new Button("Submit");
        Button playerReset = new Button("Reset");

        playerMoveSubmitButton.setOnMouseClicked(event -> {
            if (processPlayerMove(guiBoard)) {
                //Make ai move is player moved
                makeAIMove(dictionary, guiBoard);
            }
        });

        playerReset.setOnMouseClicked(event -> {
            resetPlayerMove(guiBoard);
        });

        HBox trailer = new HBox();
        Region buttonSpacer = new Region();
        HBox.setHgrow(buttonSpacer, Priority.ALWAYS);
        trailer.getChildren().addAll(playerReset, buttonSpacer, playerMoveSubmitButton);

        //Set all children on root
        rootDisplay.getChildren().addAll(scoreBanner, guiBoard.getRoot(), playerTray.getRoot(), trailer);

        //Set properties of root display
        rootDisplay.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        rootDisplay.setPadding(new Insets(5, 5, 5, 5));
        rootDisplay.setSpacing(10);

        // Set scene on stage
        Scene root = new Scene(rootDisplay);
        primaryStage.setScene(root);
        primaryStage.setTitle("Scrabble");
        primaryStage.show();
    }

    private void setEventListenerOnPlayerTile(GUITile tile) {
        tile.getRoot().setOnMouseClicked(event -> {
            if (selectedTile != null) {
                selectedTile.toggleUserSelectionIndicator();
            }

            selectedTile = tile;

            tile.toggleUserSelectionIndicator();
        });
    }

    private void setEventListenerOnBoardSpace(GUITile space) {
        // Check if selected space is already filled
        if (space.getRoot().getChildren().size() > 2) return;

        if (selectedTile != null) {
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
            selectedTile.getRoot().setOnMouseClicked(event -> {});
            selectedTile = null;
        }
    }

    private void resetPlayerMove(GUIBoard board) {
        for (GUITile placedTile : placedTiles) {
            ((GUITile) board.getTileAtCoordinates(placedTile.getRow(), placedTile.getCol())).getRoot().getChildren().remove(placedTile.getRoot());
            placedTile.setRow(-1);
            placedTile.setCol(-1);
            playerTray.addTile(placedTile);
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

            for (GUITile placedTile : placedTiles) {
                //Set tiles on board
                board.setTileOnBoard(placedTile);
                board.getMultiplierAtCoordinates(placedTile.getRow(), placedTile.getCol()).setMultiplierAsUsed();
            }

            placedTiles = new ArrayList<>();
            return true;
        } else {
            //Put tiles back in tray
            resetPlayerMove(board);
        }

        return false;
    }

    private void makeAIMove(Trie dictionary, GUIBoard board) {
        Pair<Word, BoardCompatibilityCheckData> move = Solver.solveBoardState(dictionary, new Pair<>(board, AITray));

        if (move != null) {
            // Turn tiles into GUITiles and remove from ai tray
            ArrayList<GUITile> guiTiles = new ArrayList<>();
            for (Tile ogTile : move.getSnd().newTiles()) {
                guiTiles.add(new GUITile(ogTile));

                AITray.removeTileFromTray(ogTile);
            }

            //Add gui tiles to board
            board.setGUITilesOnBoard(guiTiles);

            //Update AI score
            int score = EntryPoint.scorePlay(board, move.getSnd().newTiles().size(), move.getSnd().newWords());
            aiScore.setText(String.valueOf(Integer.parseInt(aiScore.getText()) + score));
        } else {
            //AI Couldn't find a move
        }

        System.out.println();
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

import GUIutils.GUIBoard;
import GUIutils.GUITile;
import GUIutils.GUITray;
import ScrabbleObjects.Tile;
import ScrabbleObjects.Tray;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utils.Board;
import utils.BoardLayouts;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class GUI extends Application implements EntryPoint {

    ArrayList<GUITile> bag = new ArrayList<>(100);

    public static GUITile selectedTile = null;

    private Label aiScore = new Label("0");
    private Label playerScore = new Label("0");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox rootDisplay = new VBox();

        //Fill bag and shuffle
        fillBag();

        // Initialize trays with 7 tiles each
        Tray AITray = new Tray(new ArrayList<>(bag.subList(0, 7)));
        GUITray playerTray = new GUITray(new ArrayList<>(bag.subList(7, 14)));
        bag = new ArrayList<>(bag.subList(14, bag.size()));

        // Make event listeners for playerTray
        for (Tile tile : playerTray.getSpacesArray()) {
            setEventListenerOnPlayerTile((GUITile) tile);
        }

        GUIBoard guiBoard = new GUIBoard(new Board(15, BoardLayouts.getBoardLayout(15)));

        Button playerMoveSubmitButton = new Button("Submit");

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


        //Set all children on root
        rootDisplay.getChildren().addAll(scoreBanner, guiBoard.getRoot(), playerTray.getRoot(), playerMoveSubmitButton);

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

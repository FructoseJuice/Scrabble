import GUIutils.GUIBoard;
import GUIutils.GUITile;
import GUIutils.GUITray;
import ScrabbleObjects.Tile;
import ScrabbleObjects.Tray;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import utils.Board;
import utils.BoardLayouts;

import java.util.ArrayList;
import java.util.Collections;

public class GUI extends Application implements EntryPoint {

    ArrayList<GUITile> bag = new ArrayList<>(100);

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


        GUIBoard guiBoard = new GUIBoard(new Board(15, BoardLayouts.getBoardLayout(15)));


        rootDisplay.getChildren().addAll(guiBoard.getRoot(), playerTray.getRoot());
        rootDisplay.setPadding(new Insets(5, 5, 5, 5));
        rootDisplay.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        rootDisplay.setSpacing(10);
        Scene root = new Scene(rootDisplay);
        primaryStage.setScene(root);
        primaryStage.setTitle("Scrabble");
        primaryStage.show();
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

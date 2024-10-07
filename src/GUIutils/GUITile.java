package GUIutils;

import ScrabbleObjects.Multiplier;
import ScrabbleObjects.Tile;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GUITile {
    private StackPane root = new StackPane();
    private Rectangle background = new Rectangle(40, 40);
    private Label guiContents = new Label();
    private Tile tile;
    private Multiplier multiplier;

    public GUITile(String contents, int row, int col) {
        tile = new Tile(contents, row, col);

        // Decide tile color based off multiplier contents
        multiplier = new Multiplier(contents);

        setBackgroundColorAndContents();

        root.getChildren().addAll(background, guiContents);
    }

    public StackPane getRoot() {
        return root;
    }

    private void setBackgroundColorAndContents() {
        if (multiplier.type == Multiplier.MultiplierType.LETTER) {
            switch (multiplier.value) {
                case ONE -> {
                    background.setFill(Paint.valueOf("Gray"));
                    guiContents.setText("");
                }
                case TWO -> {
                    background.setFill(Paint.valueOf("LightBlue"));
                    guiContents.setText("DL");
                }
                case THREE -> {
                    background.setFill(Paint.valueOf("Cyan"));
                    guiContents.setText("TL");
                }
            }
        } else {
            switch (multiplier.value) {
                case ONE -> {
                    background.setFill(Paint.valueOf("Gray"));
                    guiContents.setText("");
                }
                case TWO -> {
                    background.setFill(Paint.valueOf("Gold"));
                    guiContents.setText("DW");
                }
                case THREE -> {
                    background.setFill(Paint.valueOf("IndianRed"));
                    guiContents.setText("TW");
                }
            }
        }
    }
}

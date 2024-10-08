package GUIutils;

import ScrabbleObjects.Multiplier;
import ScrabbleObjects.Tile;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GUITile extends Tile {
    private StackPane root = new StackPane();
    private Rectangle background = new Rectangle(25, 25);
    private Label guiContents = new Label();
    private Multiplier multiplier;
    private boolean moveAble;

    public GUITile(String contents, int row, int col) {
        super(contents, row, col);

        // Decide tile color based off multiplier contents
        multiplier = new Multiplier(contents);

        setBackgroundColorAndContents(contents);
        background.setArcHeight(5);
        background.setArcWidth(5);


        root.getChildren().addAll(background, guiContents);
    }

    public GUITile(Tile tile) {
        super(tile);

        multiplier = new Multiplier(tile.getContents());

        setBackgroundColorAndContents(tile.getContents());

        root.getChildren().addAll(background, guiContents);
    }

    public void toggleUserSelectionIndicator() {
        if (moveAble) {
            if (background.getFill() == Paint.valueOf("Chocolate")) {
                background.setFill(Paint.valueOf("Goldenrod"));
            } else {
                background.setFill(Paint.valueOf("Chocolate"));
            }
        }
    }

    public StackPane getRoot() {
        return root;
    }

    private void setBackgroundColorAndContents(String contents) {
        // Check if letter
        if (containsLetter()) {
            moveAble = true;

            background.setFill(Paint.valueOf("Goldenrod"));
            guiContents.setText(contents);
        } else {
            moveAble = false;

            //This is a multiplier
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
                        background.setFill(Paint.valueOf("Wheat"));
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
}

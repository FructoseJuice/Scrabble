package GUIutils;

import ScrabbleObjects.Tile;
import ScrabbleObjects.Tray;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class GUITray extends Tray {
    private HBox guiTray;

    public GUITray() {
        super();
        guiTray = new HBox();
        guiTray.setAlignment(Pos.CENTER);
        guiTray.setSpacing(5);
    }

    public GUITray(ArrayList<Tile> initTray) {
        super(initTray);

        guiTray = new HBox();
        guiTray.setAlignment(Pos.CENTER);
        guiTray.setSpacing(5);

        for (Tile tile : initTray) {
            guiTray.getChildren().add(((GUITile) tile).getRoot());
        }
    }

    public void addTile(GUITile tile) {
        addSpace(tile);
        guiTray.getChildren().add(tile.getRoot());
    }

    //public Tray getTray() {
     //   return this.;
    //}

    public HBox getRoot() {
        return guiTray;
    }
}

package GUIutils;

import ScrabbleObjects.Multiplier;
import ScrabbleObjects.Tile;
import javafx.scene.layout.GridPane;
import utils.Board;

import java.util.ArrayList;
import java.util.List;

public class GUIBoard extends Board {
    private GridPane root;

    public GUIBoard(int size, String initContents) {
        super(size);

        GUITile[][] newBoard = new GUITile[size][size];
        Multiplier[][] newMultiplierBoard = new Multiplier[size][size];

        root = new GridPane(BOARD_SIZE, BOARD_SIZE);
        root.setHgap(3);
        root.setVgap(3);

        //Initialize empty board so that we can score later on
        String [] splitContents;

        //Initialize non-empty spaces
        splitContents = initContents.split("\n");

        String spaceContent = "";
        for (int i = 0; i < splitContents.length; i++) {
            //Split row by " "
            ArrayList<String> row = new ArrayList<>(List.of(splitContents[i].split(" ")));

            //Remove blank elements
            while (row.remove("")) {
                continue;
            }

            for (int j = 0; j < row.size(); j++) {
                spaceContent = row.get(j);

                //Add space to board
                newBoard[i][j] = new GUITile(spaceContent, i, j);
                root.add(newBoard[i][j].getRoot(), j, i);

                //Add multiplier to board
                newMultiplierBoard[i][j] = new Multiplier(spaceContent);
            }
        }

        setBoard(newBoard);
        setMultiplierBoard(newMultiplierBoard);
    }

    public void setGUITilesOnBoard(ArrayList<GUITile> tiles) {
        for (GUITile tile : tiles) {
            setTileOnBoard(tile);
            getMultiplierAtCoordinates(tile.getRow(), tile.getCol()).setMultiplierAsUsed();
            root.add(tile.getRoot(), tile.getCol(), tile.getRow());
        }
    }

    public GridPane getRoot() {
        return root;
    }
}

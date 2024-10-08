package GUIutils;

import javafx.scene.layout.GridPane;
import utils.Board;

public class GUIBoard {
    private Board arrayBoard;
    private GridPane guiBoard;


    public GUIBoard(Board board) {
        arrayBoard = board;
        guiBoard = new GridPane(board.BOARD_SIZE, board.BOARD_SIZE);
        guiBoard.setHgap(3);
        guiBoard.setVgap(3);

        for (int i = 0; i < board.BOARD_SIZE; i++) {
            for (int j = 0; j < board.BOARD_SIZE; j++) {
                GUITile newTile = new GUITile(board.getTileAtCoordinates(i, j).getContents(), i, j);
                guiBoard.add(newTile.getRoot(), i, j);
            }
        }
    }

    public GridPane getRoot() {
        return guiBoard;
    }
}

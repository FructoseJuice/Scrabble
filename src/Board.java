import java.util.Arrays;

public class Board {
    private final Space[][] board;

    public Board(int size, String initContents) {
        board = new Space[size][size];

        String[] splitContents = initContents.split("\n");

        for (int i = 0; i < size; i++) {
            String[] splitLine = splitContents[i].split(" ");

            for (int j = 0; j < size; j++) {
                //01_23_56_...
                board[i][j] = new Space(splitLine[j]);
            }
        }
    }
}

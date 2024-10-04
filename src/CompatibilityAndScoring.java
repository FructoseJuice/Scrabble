import Trie.Trie;
import utils.Pair;

import java.io.*;

public class CompatibilityAndScoring extends EntryPoint{
    public static void main(String[] args) throws IOException {
        //Load dictionary
        Trie dictionary = parseClIForTrie(args);

        Board originalBoard;
        Board resultBoard;

        while (true) {
            Pair<Board, Board> newBoards = readNumBoardsFromCli(2);

            if (newBoards == null) break;

            originalBoard = newBoards.getFst();
            resultBoard = newBoards.getSnd();

            System.out.println(originalBoard.toString());
            System.out.println(resultBoard.toString());

            BoardCompatibilityCheckData compatibilityCheckData = areBoardsCompatible(dictionary, originalBoard, resultBoard);

            if (compatibilityCheckData.isLegal()) {
                int score = scorePlay(originalBoard, compatibilityCheckData.numNewTiles(), compatibilityCheckData.newWords());
                System.out.println(compatibilityCheckData.output());
                System.out.println("Score: " + score);
            } else {
                System.out.println(compatibilityCheckData.output());
            }
        }
    }

    /**
     * Initializes N boards from cli.
     * @param num N number of boards to process
     * @return New N number of boards
     */
    public static Pair<Board, Board> readNumBoardsFromCli(int num) throws IOException {
        //Boards to return
        Pair<Board, Board> newBoards = new Pair<>();
        StringBuilder boardContents = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //Read in size of board, should be first line of input
        int boardSize;

        System.out.println("Enter original and result boards in this format for each: \n{board size}\n{board}");
        for (int n = 0; n < num; n++) {
            String line = reader.readLine();

            //Try till not null
            if (line == null) {
                int tries = 0;
                while (tries < 3 && (line = reader.readLine()) == null) {
                    tries++;
                }
            }

            if (line == null) return null;

            //If blank try to read next line
            if (line.isEmpty()) {
                line = reader.readLine();
            }

            boardSize = Integer.parseInt(line.trim());

            //Read each row
            for (int i = 0; i < boardSize; i++) {
                boardContents.append(reader.readLine()).append("\n");
            }

            //Initialize new board
            if (n == 0) {
                newBoards.setFst(new Board(boardSize, boardContents.toString()));
            } else {
                newBoards.setSnd(new Board(boardSize, boardContents.toString()));
            }

            //Clear board contents
            boardContents.setLength(0);
        }

        return newBoards;
    }
}
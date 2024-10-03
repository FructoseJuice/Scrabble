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

            originalBoard = newBoards.getFst();
            resultBoard = newBoards.getSnd();

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

}
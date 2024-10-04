import Trie.Trie;
import utils.Pair;
import utils.Side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class Solver extends EntryPoint {
    public static void main(String[] args) throws IOException {
        //Load dictionary
        Trie dictionary = parseClIForTrie(args);

        Board originalBoard;
        while (true) {
            Pair<Board, Tray> boardAndTray = readBoardAndTrayFromCLI();

            if (boardAndTray == null) break;

            originalBoard = boardAndTray.getFst();

            System.out.println(originalBoard.toString());

            generateHorizontalWords(originalBoard, boardAndTray.getSnd());
        }
    }

    /**
     * Initializes new board and Tray from Cli
     * @return New Pair of a board and tray
     */
    public static Pair<Board, Tray> readBoardAndTrayFromCLI() throws IOException {
        //Boards to return
        Pair<Board, Tray> newBoardAndTray = new Pair<>();
        StringBuilder boardContents = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //Read in size of board, should be first line of input
        int boardSize;

        System.out.println("Enter boards and tray in this format for each: \n{board size}\n{board}\n{tray}");

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
        newBoardAndTray.setFst(new Board(boardSize, boardContents.toString()));

        //Read and process tray
        Tray newTray = new Tray();

        line = reader.readLine();
        for (String letter : line.split("")) {
            newTray.addSpace(new Tile(letter, -1, -1));
        }

        newBoardAndTray.setSnd(newTray);


        return newBoardAndTray;
    }

    public static ArrayList<Word> generateHorizontalWords(Board originalBoard, Tray tray) {
        ArrayList<Pair<Pair<Tile, Word>, Side>> anchorSpaces = new ArrayList<>();

        // Traverse board for anchors
        int k;
        for (int i = 0; i < originalBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < originalBoard.BOARD_SIZE - 1; j++) {
                k = j;

                // If this space is empty and space ahead contains a letter, this is a left anchor
                if (originalBoard.getSpaceAtCoordinates(i, j).isBlank() && originalBoard.getSpaceAtCoordinates(i, j+1).containsLetter()) {
                    Pair<Tile, Word> newAnchor = new Pair<>();
                    newAnchor.setFst(originalBoard.getSpaceAtCoordinates(i, j));

                    Word anchorWord = new Word();

                    // Iterate through word to our right
                    while (originalBoard.getSpaceAtCoordinates(i, j+1).containsLetter()) {
                        anchorWord.addSpace(originalBoard.getSpaceAtCoordinates(i, ++j));
                    }
                    newAnchor.setSnd(anchorWord);

                    anchorSpaces.add(new Pair<>(newAnchor, Side.LEFT));
                }

                // If this space is empty and space before contains a letter, this is a right side anchor
                if (k > 0 && originalBoard.getSpaceAtCoordinates(i, k).isBlank() && originalBoard.getSpaceAtCoordinates(i, k-1).containsLetter()) {
                    // Collect this word
                    Pair<Tile, Word> newAnchor = new Pair<>();
                    newAnchor.setFst(originalBoard.getSpaceAtCoordinates(i, k));

                    Word anchorWord = new Word();

                    // Iterate through word to our left
                    while (k > 0 && originalBoard.getSpaceAtCoordinates(i, --k).containsLetter()) {
                        anchorWord.addSpace(originalBoard.getSpaceAtCoordinates(i, k));
                    }

                    // Reverse this word first
                    anchorWord.reverse();
                    newAnchor.setSnd(anchorWord);

                    anchorSpaces.add(new Pair<>(newAnchor, Side.RIGHT));
                }
            }
        }

        System.out.println();
        // Generate moves from anchors


        return null;
    }

    //Generate possible letters for each space
    /*
    Traverse Board
    When find letter ->
    Anchor space to left of letter
    Store anchored word int W
    Extend k to the left, k = min(board.length - current space, letters in tray)
        Try every combination of letters at that space
        Check if new letter L + W is within the Trie
        If so, add to running list of possible words
    For every new word, if it's a terminator add to possible words
    Try to extend these words to the right




    LeftPart(PartialWord, node N in dawg, l i m i t ) =
        ExtendRight (PartialWord, N , Anchorsquare)
        i f limit > 0 then
            f o r each edge E out of N
                i f the letter 1 labeling edge E is in our rack then

                    remove a tile labeled 1 from the rack

                    let N' be the node reached by following edge E

                    Leftpart (PartialWord . 1 , N ' , limit -1)

                    put the tile 1 back into the rack


    ExtendRight (PartialWord , node N in dawg ,
square) =
I f N i s a terminal node then
f o r each edge E out of N
i f square is vacant then
LegalMove (PartialWord)
i f the letter 1 labeling edge E is
1 is in the cross-check set of
in our rack and
square then
remove a tile 1 from the rack
let N' be the node reached by
let next-square be the square t o
ExtendRight (PartialWord - 1 , N',
put the tile 1 back into the
following edge E
the right of square
next-square )
rack
else
let 1 be the letter occupying square
if N has an edge labeled by 1 that
leads t o some node N' then
let next-square be the square t o
the right of square
ExtendRight (PartialWord . 1 , N',
next-square )
     */
}

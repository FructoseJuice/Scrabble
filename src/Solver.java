import Trie.Trie;
import utils.Pair;
import utils.Side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Solver extends EntryPoint {
    public static void main(String[] args) throws IOException {
        //Load dictionary
        Trie dictionary = parseClIForTrie(args);

        while (true) {
            Pair<Board, Tray> boardAndTray = readBoardAndTrayFromCLI();

            if (boardAndTray == null) break;

            System.out.println(boardAndTray.getFst().toString());
            System.out.println(boardAndTray.getSnd().toString());

            // Generate anchor spaces
            ArrayList<Pair<Pair<Tile, Word>, Side>> anchorSpaces = generateAnchors(boardAndTray.getFst());

            // Generate possible moves from anchor spaces
            ArrayList<Word> possibleWords = generatePossibleMoves(dictionary, boardAndTray.getFst(), boardAndTray.getSnd(), anchorSpaces);

            // Transpose board and repeat process
            Board transposedBoard = boardAndTray.getFst().transpose();

            anchorSpaces = generateAnchors(transposedBoard);

            ArrayList<Word> possibleVerticalWords = generatePossibleMoves(dictionary, transposedBoard, boardAndTray.getSnd(), anchorSpaces);

            //Transpose legal words
            possibleVerticalWords = pruneIllegalWords(dictionary, possibleVerticalWords);

            for (Word word : possibleVerticalWords) {
                for (Tile tile : word.getSpacesArray()) {
                    tile.transpose();
                }
            }

            // Merge all words
            possibleWords.addAll(possibleVerticalWords);
            System.out.println();
            //Prune illegal moves and find the highest scorer
            //Use Max heap for this

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

    /**
     * Finds all "anchors" on the board. An anchor is an empty space immediately to the left
     * or right of a letter.
     * @param board Board to check
     * @return All anchors
     */
    public static ArrayList<Pair<Pair<Tile, Word>, Side>> generateAnchors(Board board) {
        ArrayList<Pair<Pair<Tile, Word>, Side>> anchorSpaces = new ArrayList<>();

        // Traverse board for anchors
        int k;
        boolean leftEmpty;
        boolean rightEmpty;
        boolean aboveEmpty;
        boolean belowEmpty;
        for (int row = 0; row < board.BOARD_SIZE; row++) {
            for (int col = 0; col < board.BOARD_SIZE; col++) {
                k = col;

                // If this space is not empty this cannot be an anchor
                if (board.getSpaceAtCoordinates(row, col).containsLetter()) continue;

                // Check if left empty
                if (col > 0) {
                    leftEmpty = board.getSpaceAtCoordinates(row, col-1).isBlank();
                } else {
                    leftEmpty = true;
                }

                // Check if right empty
                if (col < board.BOARD_SIZE - 1) {
                    rightEmpty = board.getSpaceAtCoordinates(row, col+1).isBlank();
                } else {
                    rightEmpty = true;
                }

                // Spaces to left and right must be empty
                if (leftEmpty && rightEmpty) {
                    // Check For letter above this space
                    if (row - 1 >= 0) {
                        aboveEmpty = board.getSpaceAtCoordinates(row-1, col).isBlank();
                    } else {
                        aboveEmpty = true;
                    }

                    // Check space below for letter
                    if (row + 1 < board.BOARD_SIZE) {
                        belowEmpty = board.getSpaceAtCoordinates(row + 1, col).isBlank();
                    } else {
                        belowEmpty = true;
                    }

                    // If space above contains a letter, this is an achor
                    if (!aboveEmpty) {
                        Pair<Tile, Word> newAnchor = new Pair<>();
                        newAnchor.setFst(board.getSpaceAtCoordinates(row, col));
                        newAnchor.setSnd(new Word());

                        anchorSpaces.add(new Pair<>(newAnchor, Side.LEFT));

                        continue;
                    }

                    // If this space is empty and space below contains a letter, this is an anchor
                    if (!belowEmpty) {
                        Pair<Tile, Word> newAnchor = new Pair<>();
                        newAnchor.setFst(board.getSpaceAtCoordinates(row, col));
                        newAnchor.setSnd(new Word());

                        anchorSpaces.add(new Pair<>(newAnchor, Side.LEFT));

                        continue;
                    }
                }

                // If this space is empty and space ahead contains a letter, this is a left anchor
                if (!rightEmpty) {
                    Pair<Tile, Word> newAnchor = new Pair<>();
                    newAnchor.setFst(board.getSpaceAtCoordinates(row, col));

                    Word anchorWord = new Word();

                    // Iterate through word to our right
                    while (col+1 < board.BOARD_SIZE && board.getSpaceAtCoordinates(row, col+1).containsLetter()) {
                        anchorWord.addSpace(board.getSpaceAtCoordinates(row, ++col));
                    }
                    newAnchor.setSnd(anchorWord);

                    anchorSpaces.add(new Pair<>(newAnchor, Side.LEFT));
                }

                // If this space is empty and space before contains a letter, this is a right side anchor
                if (!leftEmpty) {
                    // Collect this word
                    Pair<Tile, Word> newAnchor = new Pair<>();
                    newAnchor.setFst(board.getSpaceAtCoordinates(row, k));

                    Word anchorWord = new Word();

                    // Iterate through word to our left
                    while (k > 0 && board.getSpaceAtCoordinates(row, --k).containsLetter()) {
                        anchorWord.addSpace(board.getSpaceAtCoordinates(row, k));
                    }

                    // Reverse this word first
                    anchorWord.reverse();
                    newAnchor.setSnd(anchorWord);

                    anchorSpaces.add(new Pair<>(newAnchor, Side.RIGHT));
                }
            }
        }

        return anchorSpaces;
    }

    /**
     * Generate all the possible moves that the AI can make
     * @param originalBoard Board to place on
     * @param anchors Anchor positions and linked words
     * @return Possible moves
     */
    public static ArrayList<Word> generatePossibleMoves(Trie dictionary, Board originalBoard, Tray tray, ArrayList<Pair<Pair<Tile, Word>, Side>> anchors) {
        /*
        For left words ->

        if k = length tray
        and j = anchor col
        iterate from j-1 to j-k

        proc:
        at j-i, take tile t out of tray,
        If Trie has sequence t + anchor.word
            i--
            proc()
            rightSearchProc()
         */
        ArrayList<Word> moves = new ArrayList<>();

        int anchorCol;
        int anchorRow;

        // Find possible moves to left and right of anchor words
        for (Pair<Pair<Tile, Word>, Side> anchor : anchors) {
            // Get col of anchor
            anchorCol = anchor.getFst().getFst().getCol();
            anchorRow = anchor.getFst().getFst().getRow();

            if (anchor.getSnd() == Side.LEFT) {
                if (anchor.equals(anchors.getLast())) {
                    System.out.println();
                }
                permuteLeft(dictionary, originalBoard, moves, anchor.getFst().getSnd(), tray, anchorRow, anchorCol);
            } else {
                permuteRight(dictionary, originalBoard, moves, anchor.getFst().getSnd(), tray, anchorRow, anchorCol);
            }
        }

        // Prune away illegal moves
        //moves = pruneIllegalWords(dictionary, moves);


        return moves;
    }


    private static void permuteLeft(
            Trie dictionary, Board board, ArrayList<Word> possible,
            Word permutation, Tray tray, int anchorRow, int currCol) {

        // Stop searching if at bounds of board or tray is empty
        if (currCol < 0 || tray.isEmpty()) return;

        // Check if there's a tile at this location
        if (board.getSpaceAtCoordinates(anchorRow, currCol).containsLetter()) {
            // Grab tile at these coordinates
            Tile newTile = new Tile(board.getSpaceAtCoordinates(anchorRow, currCol));
            // Add to the front of permutation
            permutation.addSpaceToFront(newTile);
            // Check if the sequence is in trie
            Word newWord = new Word(permutation);

            if (dictionary.containsWord(newWord.toString())) {
                // Add possible word
                possible.add(newWord);
            }
            // Continue decrementing left
            permuteLeft(dictionary, board, possible, new Word(permutation), tray, anchorRow, currCol - 1);
            // Also try extending word to the right
            permuteRight(dictionary, board, possible, new Word(permutation), tray, anchorRow, permutation.getSpacesArray().getLast().getCol()+1);
            // Remove new tile from permutation
            permutation.getSpacesArray().removeFirst();
            return;
        }

        // Iterate through tray
        for (int i = 0; i < tray.size(); i++) {
            // Take tile at index i out of tray
            Tile tile = tray.getSpacesArray().remove(i);
            // Add this tile to the front of permutation word
            permutation.addSpaceToFront(new Tile(tile.getContents(), anchorRow, currCol));

            // Check if this sequence is in trie
            Word newWord = new Word(permutation);
            if (dictionary.containsWord(newWord.toString())) {
                // If this word is already in the possible words we've checked those permutations
                if (!wordListAbsContains(possible, newWord)) {
                    // Add to possible words
                    possible.add(newWord);
                }
            }
            // Continue permutation
            permuteLeft(dictionary, board, possible, new Word(permutation), tray, anchorRow, currCol - 1);
            // Also search for right permutations to try and extend this word
            permuteRight(dictionary, board, possible, new Word(permutation), tray, anchorRow, permutation.getSpacesArray().getLast().getCol()+1);

            // Add tile back to tray
            tray.getSpacesArray().add(i, tile);
            // Remove new tile from permutation
            permutation.getSpacesArray().removeFirst();
        }
    }


    private static void permuteRight(
            Trie dictionary, Board board, ArrayList<Word> possible,
            Word permutation, Tray tray, int anchorRow, int currCol) {

        // Stop searching if at bounds of board or tray is empty
        if (currCol >= board.BOARD_SIZE || tray.isEmpty()) return;


        // Check if there's a tile at this location
        if (board.getSpaceAtCoordinates(anchorRow, currCol).containsLetter()) {
            // Grab tile at these coordinates
            Tile newTile = new Tile(board.getSpaceAtCoordinates(anchorRow, currCol));
            // Add to the back of permutation
            permutation.addSpaceToEnd(newTile);

            // Check if the sequence is in trie
            Word newWord = new Word(permutation);
            if (dictionary.containsWord(newWord.toString())) {
                // Add possible word
                possible.add(newWord);
            }
            // Continue incrementing right
            permuteRight(dictionary, board, possible, new Word(permutation), tray, anchorRow, currCol + 1);
            // Remove new tile from permutation
            permutation.getSpacesArray().removeLast();
            return;
        }

        // Iterate through tray
        for (int i = 0; i < tray.size(); i++) {
            // Take tile at index i out of tray
            Tile tile = tray.getSpacesArray().remove(i);
            // Add this tile to end of permutation word
            permutation.addSpaceToEnd(new Tile(tile.getContents(), anchorRow, currCol));

            // Check if this sequence is in trie
            Word newWord = new Word(permutation);
            if (dictionary.containsWord(newWord.toString())) {
                // If this word is already in the possible words we've checked those permutations
                if (!wordListAbsContains(possible, newWord)) {
                    // Add to possible words
                    possible.add(newWord);
                }
            }
            // Continue permutation
            permuteRight(dictionary, board, possible, new Word(permutation), tray, anchorRow, currCol + 1);
            // Add tile back to tray
            tray.getSpacesArray().add(i, tile);
            // Remove new tile from permutation
            permutation.getSpacesArray().removeLast();
        }
    }

    public static boolean wordListAbsContains(ArrayList<Word> wordList, Word word) {
        boolean differenceFound;
        for (Word seenWord : wordList) {
            if (seenWord.size() != word.size()) continue;

            differenceFound = false;
            for (int i = 0; i < seenWord.size(); i++) {
                if (!seenWord.getSpaceAtIndex(i).equals(word.getSpaceAtIndex(i))) {
                    differenceFound = true;
                    break;
                }

                if (!seenWord.getSpaceAtIndex(i).coordinateEquals(word.getSpaceAtIndex(i))) {
                    differenceFound = true;
                    break;
                }
            }

            if (!differenceFound) return true;
        }

        return false;
    }


    public static ArrayList<Word> pruneIllegalWords(Trie dictionary, ArrayList<Word> possibleWords) {
        ArrayList<Word> legalWords = new ArrayList<>();

        for (Word possible : possibleWords) {
            if (dictionary.containsWord(possible.toString())) {
                legalWords.add(possible);
            }
        }

        return legalWords;
    }


    /**
     * Prune away illegal moves and find the highest scorer among the legal moves
     * @param possibleWords Possible words to check
     * @return Highest scoring word
     */
    public static Word findHighestScorerFromPossibleMoves(ArrayList<Word> possibleWords) {
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

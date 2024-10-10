import ScrabbleObjects.Tile;
import ScrabbleObjects.Tray;
import ScrabbleObjects.Word;
import utils.Trie.Trie;
import utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class Solver implements EntryPoint {
    public static void main(String[] args) throws IOException {
        //Load dictionary
        Trie dictionary = EntryPoint.parseClIForTrie(args);

        while (true) {
            Pair<Board, Tray> boardAndTray = readBoardAndTrayFromCLI();

            if (boardAndTray == null) break;

            System.out.println(boardAndTray.getFst().toString());
            System.out.println(boardAndTray.getSnd().toString());

            // Solve board and tray for highest scoring move
            Pair<Word, BoardCompatibilityCheckData> highestScorer = solveBoardState(dictionary, boardAndTray);

            // Print info about the highest scoring move
            if (highestScorer == null) {
                System.out.println("Found no moves!");
            } else {
                System.out.println("Solution:");
                System.out.println(highestScorer.getSnd().output());
            }
        }
    }



    /**
     * Initializes new board and ScrabbleObjects.Tray from Cli
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


    public static Pair<Word, BoardCompatibilityCheckData> solveBoardState(Trie dictionary, Pair<Board, Tray> boardAndTray) {
        // Generate anchor spaces
        ArrayList<Pair<Pair<Tile, Word>, Side>> anchorSpaces = generateAnchors(boardAndTray.getFst());

        // Generate possible moves from anchor spaces
        ArrayList<Word> possibleWords = generatePossibleMoves(dictionary, boardAndTray.getFst(), boardAndTray.getSnd(), anchorSpaces);


        // Transpose board and repeat process
        Board transposedBoard = boardAndTray.getFst().transpose();

        //Generate anchor spaces for transposed board
        anchorSpaces = generateAnchors(transposedBoard);

        // Find vertical words through transposed board
        ArrayList<Word> possibleVerticalWords = generatePossibleMoves(dictionary, transposedBoard, boardAndTray.getSnd(), anchorSpaces);


        //Transpose legal words and merge into running total list
        //Convert words with blanks
        for (Word word : possibleVerticalWords) {
                /*
                 For some reason, tiles end up shared between words which
                 causes major issues for transposing, so we have to make copies
                 of all the found words. Would like to avoid this, but oh well.
                 */

            Word copy = word.copyOf();

            // Transpose every tile in this word
            for (Tile tile : copy.getSpacesArray()) {
                tile.transpose();
            }

            // Add new transposed copy
            possibleWords.add(copy);
        }


        // Convert word with blanks to actual words
        // Use all legal combinations
        deGenerifyWildCards(dictionary, possibleWords);

        // Find and return the highest scorer
        return findHighestScorerFromPossibleMoves(dictionary, boardAndTray.getFst(), possibleWords);
    }


    /**
     * Finds all "anchors" on the board. An anchor is an empty space immediately to the left
     * or right of a letter.
     * @param board utils.Board to check
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
                if (board.getTileAtCoordinates(row, col).containsLetter()) continue;

                // Check if left empty
                if (col > 0) {
                    leftEmpty = board.getTileAtCoordinates(row, col-1).isBlank();
                } else {
                    leftEmpty = true;
                }

                // Check if right empty
                if (col < board.BOARD_SIZE - 1) {
                    rightEmpty = board.getTileAtCoordinates(row, col+1).isBlank();
                } else {
                    rightEmpty = true;
                }

                // Spaces to left and right must be empty
                if (leftEmpty && rightEmpty) {
                    // Check For letter above this space
                    if (row - 1 >= 0) {
                        aboveEmpty = board.getTileAtCoordinates(row-1, col).isBlank();
                    } else {
                        aboveEmpty = true;
                    }

                    // Check space below for letter
                    if (row + 1 < board.BOARD_SIZE) {
                        belowEmpty = board.getTileAtCoordinates(row + 1, col).isBlank();
                    } else {
                        belowEmpty = true;
                    }

                    // If space above contains a letter, this is an achor
                    if (!aboveEmpty) {
                        Pair<Tile, Word> newAnchor = new Pair<>();
                        newAnchor.setFst(board.getTileAtCoordinates(row, col));
                        newAnchor.setSnd(new Word());

                        anchorSpaces.add(new Pair<>(newAnchor, Side.LEFT));

                        continue;
                    }

                    // If this space is empty and space below contains a letter, this is an anchor
                    if (!belowEmpty) {
                        Pair<Tile, Word> newAnchor = new Pair<>();
                        newAnchor.setFst(board.getTileAtCoordinates(row, col));
                        newAnchor.setSnd(new Word());

                        anchorSpaces.add(new Pair<>(newAnchor, Side.LEFT));

                        continue;
                    }
                }

                // If this space is empty and space ahead contains a letter, this is a left anchor
                if (!rightEmpty) {
                    Pair<Tile, Word> newAnchor = new Pair<>();
                    newAnchor.setFst(board.getTileAtCoordinates(row, col));

                    Word anchorWord = new Word();

                    // Iterate through word to our right
                    while (col+1 < board.BOARD_SIZE && board.getTileAtCoordinates(row, col+1).containsLetter()) {
                        anchorWord.addSpace(board.getTileAtCoordinates(row, ++col));
                    }
                    newAnchor.setSnd(anchorWord);

                    anchorSpaces.add(new Pair<>(newAnchor, Side.LEFT));
                }

                // If this space is empty and space before contains a letter, this is a right side anchor
                if (!leftEmpty) {
                    // Collect this word
                    Pair<Tile, Word> newAnchor = new Pair<>();
                    newAnchor.setFst(board.getTileAtCoordinates(row, k));

                    Word anchorWord = new Word();

                    // Iterate through word to our left
                    while (k > 0 && board.getTileAtCoordinates(row, --k).containsLetter()) {
                        anchorWord.addSpace(board.getTileAtCoordinates(row, k));
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
     * @param originalBoard utils.Board to place on
     * @param anchors Anchor positions and linked words
     * @return Possible moves
     */
    public static ArrayList<Word> generatePossibleMoves(Trie dictionary, Board originalBoard, Tray tray, ArrayList<Pair<Pair<Tile, Word>, Side>> anchors) {
        ArrayList<Word> moves = new ArrayList<>();

        int anchorCol;
        int anchorRow;

        // Find possible moves to left and right of anchor words
        for (Pair<Pair<Tile, Word>, Side> anchor : anchors) {
            // Get col of anchor
            anchorCol = anchor.getFst().getFst().getCol();
            anchorRow = anchor.getFst().getFst().getRow();

            if (anchor.getSnd() == Side.LEFT) {
                permuteLeft(dictionary, originalBoard, moves, anchor.getFst().getSnd(), tray, anchorRow, anchorCol);
            } else {
                permuteRight(dictionary, originalBoard, moves, anchor.getFst().getSnd(), tray, anchorRow, anchorCol);
            }
        }


        return moves;
    }


    /**
     * For every single anchor space that we've found ->
     * Find every permutation of the tray to the left of this anchor space.
     * For every permutation to the left, try to extend the word to the right
     * through the use of the permuteRight() function.
     * For every permutation that provides us with a word contained within the dictionary,
     * store that move in a "possible words list"
     * @param dictionary Dictionary of known words
     * @param board Input board
     * @param possible Running list of possible words
     * @param permutation Current permutation
     * @param tray Current ScrabbleObjects.Tray
     * @param anchorRow Row at which this anchor is on
     * @param currCol Current column in recursion
     */
    private static void permuteLeft(
            Trie dictionary, Board board, ArrayList<Word> possible,
            Word permutation, Tray tray, int anchorRow, int currCol) {

        // Stop searching if at bounds of board or tray is empty
        if (currCol < 0 || tray.isEmpty()) return;

        // Check if there's a tile at this location
        if (board.getTileAtCoordinates(anchorRow, currCol).containsLetter()) {
            // Grab tile at these coordinates
            Tile newTile = new Tile(board.getTileAtCoordinates(anchorRow, currCol));
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
            permutation.addSpaceToFront(new Tile(String.valueOf(tile.getContents()), anchorRow, currCol));

            // Make new copy of permutation
            Word newWord = new Word(permutation);

            // Check if this word is in the dictionary
            if (dictionary.containsWord(newWord.toString())) {
                // Don't add if already in the possible word list
                if (!wordListAbsContains(possible, newWord)) {
                    // Add to possible words
                    possible.add(newWord);
                }
            }

            // Continue permutation left
            permuteLeft(dictionary, board, possible, new Word(permutation), tray, anchorRow, currCol - 1);

            // Also search for right permutations to try and extend this word
            permuteRight(dictionary, board, possible, new Word(permutation), tray, anchorRow, permutation.getSpacesArray().getLast().getCol()+1);


            // Add tile back to tray
            tray.getSpacesArray().add(i, tile);
            // Remove this tile from permutation
            permutation.getSpacesArray().removeFirst();
        }
    }


    /**
     * A little bit simpler than permuteLeft().
     * Simply iterate to the right of the found anchor space
     * and find all permutations of the current tray.
     * @param dictionary Dictionary
     * @param board Input utils.Board
     * @param possible Accumulator of possible moves
     * @param permutation Current permutation of the tray and anchor word
     * @param tray Current state of the tray
     * @param anchorRow ScrabbleObjects.Tray at which this anchor is on
     * @param currCol Current column in recursion
     */
    private static void permuteRight(
            Trie dictionary, Board board, ArrayList<Word> possible,
            Word permutation, Tray tray, int anchorRow, int currCol) {

        // Stop searching if at bounds of board or tray is empty
        if (currCol >= board.BOARD_SIZE || tray.isEmpty()) return;


        // Check if there's a tile at this location
        if (board.getTileAtCoordinates(anchorRow, currCol).containsLetter()) {
            // Grab tile at these coordinates
            Tile newTile = new Tile(board.getTileAtCoordinates(anchorRow, currCol));
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


    /**
     * Check if this wordsList Absolutely contains a word.
     * This function will be checking each tile for both
     * Loose equality between the contents, and Coordinate equality.
     * @param wordList ScrabbleObjects.Word list to check
     * @param word ScrabbleObjects.Word to check for
     * @return If the words is Absolutely contained within the list
     */
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


    /**
     * Takes a word with a wildcard and creates all possible permutations of it. If that
     * permutation is contained within the dictionary, then add it to the possible words
     * list.
     * @param dictionary Dictionary to check new words against
     * @param possibleWords All possible words
     */
    public static void deGenerifyWildCards(Trie dictionary, ArrayList<Word> possibleWords) {
        ArrayList<Word> newWords = new ArrayList<>();
        HashSet<Word> wordsWithWildcards = new HashSet<>();

        char c;
        for (Word word : possibleWords) {
            for (int i = 0; i < word.size(); i++) {
                // Check if ScrabbleObjects.Tile i is a wildcard
                if (word.getSpaceAtIndex(i).getContents().contains("*")) {
                    wordsWithWildcards.add(word);

                    for (int k = 0; k < 26; k++) {
                        c = (char) (k + 'a');

                        Word newWord = word.copyOf();

                        newWord.getSpaceAtIndex(i).setContents(String.valueOf(Character.toUpperCase(c)));

                        if (dictionary.containsWord(newWord.toString())) {
                            newWords.add(newWord);
                        }
                    }
                }
            }
        }

        // Try one more loop to catch any words with 2 wildcards
        if (!newWords.isEmpty()) {
            deGenerifyWildCards(dictionary, newWords);
        }

        // Prune away wildcard words
        for (Word word : wordsWithWildcards) {
            possibleWords.remove(word);
        }

        // Add all new moves
        possibleWords.addAll(newWords);
    }


    /**
     * Prune away illegal moves and find the highest scorer among the legal moves
     * @param possibleWords Possible words to check
     * @return Highest scoring word
     */
    public static Pair<Word, BoardCompatibilityCheckData> findHighestScorerFromPossibleMoves(
            Trie dictionary, Board originalBoard, ArrayList<Word> possibleWords) {

        ArrayList<Pair<Word, BoardCompatibilityCheckData>> legalMoves = new ArrayList<>();
        Board resultBoard = originalBoard.copyOf();

        Word oldContents;
        BoardCompatibilityCheckData checkData;

        // Find legal moves
        for (Word possibleWord : possibleWords) {

            oldContents = resultBoard.temporarilyAddWord(possibleWord);

            // Check if legal
            checkData = EntryPoint.areBoardsCompatible(dictionary, originalBoard, resultBoard);

            // Replace old contents
            resultBoard.setWordOnBoard(oldContents);

            // Check if move was legal
            if (checkData.isLegal()) {
                legalMoves.add(new Pair<>(possibleWord, checkData));
            }
        }

        // Find the highest scoring move
        Pair<Word, BoardCompatibilityCheckData> highestScoringMove = null;
        int highestScore = 0;
        int score;
        for (Pair<Word, BoardCompatibilityCheckData> move : legalMoves) {
            score = EntryPoint.scorePlay(originalBoard, move.getSnd().newTiles().size(), move.getSnd().newWords());

            if (highestScore < score) {
                highestScoringMove = move;
                highestScore = score;
            }
        }

        // Refine the output a bit
        if (highestScoringMove != null) {
            String output = highestScoringMove.getSnd().output();
            output = output.split("\n")[0] + "\n";
            output += "ScrabbleObjects.Word is: " + highestScoringMove.getFst().toString() + "\n";
            output += "Score is: " + highestScore + "\n";
            BoardCompatibilityCheckData newData =
                    new BoardCompatibilityCheckData(true, output, highestScoringMove.getSnd().newWords(), highestScoringMove.getSnd().newTiles());

            return new Pair<>(highestScoringMove.getFst(), newData);
        }


        return highestScoringMove;
    }
}

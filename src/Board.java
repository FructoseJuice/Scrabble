import java.util.ArrayList;
import java.util.List;

/**
 * Brandon W. Hidalgo
 * This class describes the scrabble board. It has
 * a board for all the spaces, and a board for the multipliers.
 * They used to be just one, but because of requirements for this project,
 * I had to split them apart. This class defines methods to extract all the
 * words contained, spaces at specified coordinates, and multipliers.
 */
public class Board {
    private final Tile[][] board;
    private final Multiplier[][] multiplierBoard;
    public final int BOARD_SIZE;

    public Board(int size, String initContents) {
        this.BOARD_SIZE = size;
        board = new Tile[size][size];
        multiplierBoard = new Multiplier[size][size];

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
                board[i][j] = new Tile(spaceContent, i, j);

                //Add multiplier to board
                multiplierBoard[i][j] = new Multiplier(spaceContent);
            }
        }
    }

    /**
     * Find all the words on this board in the specified direction
     *
     * This method used to be two, but I had ChatGPT combine them for me
     * and in doing so left a lot of kinda nasty ternary operators.
     * Luckily, I haven't had to mess with it.
     *
     * @param horizontalDir If this function should find words in the horizontal direction
     * @return ArrayList<Word> all words found in that direction</Word>
     */
    private ArrayList<Word> findWordsInDirection(boolean horizontalDir) {
        ArrayList<Word> foundWords = new ArrayList<>();

        // Loop through rows or columns based on the direction
        for (int i = 0; i < BOARD_SIZE; i++) {
            //Initialize word
            Word word = new Word();
            int index = 0;

            while (index < BOARD_SIZE) {
                Tile tile = horizontalDir ? board[i][index] : board[index][i];

                //If empty, iterate and continue
                if (tile.isBlank()) {
                    index++;
                    continue;
                }

                //Check if this space is at the end of the word
                if (index == BOARD_SIZE - 1) {
                    if (!isLetterPartOfHorizontalWord(horizontalDir ? i : index, horizontalDir ? index : i)) {
                        // Add the last letter
                        word.addSpace(tile);
                        foundWords.add(new Word(word));
                    }

                    break;
                }

                // Check if this letter is part of a word in the same direction
                if (horizontalDir ? !isLetterPartOfHorizontalWord(i, index) : !isLetterPartOfVerticalWord(index, i)) {
                    // Check if it's part of the other direction's word
                    if (horizontalDir ? !isLetterPartOfVerticalWord(i, index) : !isLetterPartOfHorizontalWord(index, i)) {
                        // If not either, just add the single letter
                        word.addSpace(tile);
                        foundWords.add(new Word(word));
                        word.clear();
                    }

                    //Iterate and continue
                    index++;
                    continue;
                }

                //Add current letter to the word
                word.addSpace(tile);

                //Iterate and collect letters in the word
                while (horizontalDir ? index + 1 < BOARD_SIZE && board[i][index + 1].containsLetter()
                        : index + 1 < BOARD_SIZE && board[index + 1][i].containsLetter()) {
                    //Move to the next position
                    index++;
                    word.addSpace(horizontalDir ? board[i][index] : board[index][i]); // Add the letter
                }

                //Add the collected word to foundWords
                foundWords.add(new Word(word));

                //Clear the current word for the next iteration
                word.clear();

                //Move to the next position
                index++;
            }
        }

        return foundWords;
    }

    /**
     * Finds all words on the board.
     * Just packs up the result of calling findWordsInDirection in both
     * directions and returns the result.
     */
    public ArrayList<Word> findAllWords() {
        ArrayList<Word> allWords = new ArrayList<>();
        allWords.addAll(findWordsInDirection(true));
        allWords.addAll(findWordsInDirection(false));

        return allWords;
    }

    /**
     * Check if a letter has another letter to the left or right of it.
     * If the letter is at the edge of the board, that side of the letter
     * will be true
     * @return If this letter is part of a horizontal word
     */
    private boolean isLetterPartOfHorizontalWord(int row, int col) {
        boolean leftFound;
        boolean rightFound;

        if (col - 1 >= 0) {
            leftFound = board[row][col - 1].containsLetter();
        } else {
            leftFound = true;
        }

        if (col + 1 < BOARD_SIZE) {
            rightFound = board[row][col + 1].containsLetter();
        } else {
            rightFound = true;
        }

        return leftFound || rightFound;
    }

    /**
     * Checks if a letter is part of a larger vertical word
     */
    private boolean isLetterPartOfVerticalWord(int row, int col) {
        boolean belowFound;
        boolean aboveFound;

        if (row - 1 >= 0) {
            aboveFound = board[row - 1][col].containsLetter();
        } else {
            aboveFound = true;
        }

        if (row + 1 < BOARD_SIZE) {
            belowFound = board[row + 1][col].containsLetter();
        } else {
            belowFound = true;
        }

        return aboveFound || belowFound;
    }


    /**
     * Returns the space contained on the board at the specified coordinates
     * @param row row
     * @param col column
     * @return Space at coordinates (row, column)
     */
    public Tile getSpaceAtCoordinates(int row, int col) {
        return board[row][col];
    }

    /**
     * Returns multiplier at coordinates (row, col)
     */
    public Multiplier getMultiplierAtCoordinates(int row, int col) {
        return multiplierBoard[row][col];
    }


    /**
     * Pretty string of board
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Tile[] row : board) {
            for (Tile tile : row) {
                //Add extra space to prettify characters
                if (tile.toString().length() == 1) {
                    builder.append(" ");
                }

                builder.append(tile.toString()).append(" ");
            }

            //Add newline after each row
            builder.append("\n");
        }

        return builder.toString();
    }
}

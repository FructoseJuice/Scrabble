import Trie.Trie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Board {
    private final Space[][] board;
    private final Multiplier[][] multiplierBoard;
    public final int BOARD_SIZE;

    public Board(int size, String initContents) {
        this.BOARD_SIZE = size;
        board = new Space[size][size];
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
                board[i][j] = new Space(spaceContent, i, j);

                //Add multiplier to board
                multiplierBoard[i][j] = new Multiplier(spaceContent);
            }
        }
    }

    public ArrayList<Word> findHorizontalWords() {
        ArrayList<Word> foundWords = new ArrayList<>();

        // Check each row for words
        for (int row = 0; row < BOARD_SIZE; row++) {
            Word word = new Word(); // Initialize word for each row
            int col = 0;

            while (col < BOARD_SIZE) {
                Space space = board[row][col];

                //If empty iterate and continue
                if (space.isBlank()) {
                    col++;
                    continue;
                }

                // Check if this space is at the end of the horizontal
                if (col == BOARD_SIZE - 1) {
                    if (!isLetterPartOfVerticalWord(row, col)) {
                        word.addSpace(space); // Add the last letter
                        foundWords.add(new Word(word));
                    }
                    break; // Exit the loop if at the end of the col
                }

                //Check if this letter is part of a horizontal word
                if (!isLetterPartOfHorizontalWord(row, col)) {
                    //Check if this letter is part of a vertical word
                    if (!isLetterPartOfVerticalWord(row, col)) {
                        //If not either, just add the single letter
                        word.addSpace(space);
                        foundWords.add(new Word(word));
                        word.clear();
                    }

                    //Iterate and continue
                    col++;
                    continue;
                }

                // Check if this word extends to the right
                word.addSpace(space); // Add current letter

                // Iterate to the right and collect letters in word
                while (col + 1 < BOARD_SIZE && board[row][col + 1].containsLetter()) {
                    col++; // Move to the next column
                    word.addSpace(board[row][col]); // Add the letter
                }

                // Add the collected word to foundWords
                foundWords.add(new Word(word));
                word.clear(); // Clear the current word for the next iteration


                col++; // Move to the next column
            }
        }

        return foundWords;
    }

    public ArrayList<Word> findVerticalWords() {
        ArrayList<Word> foundWords = new ArrayList<>();

        // Check each column for words
        for (int col = 0; col < BOARD_SIZE; col++) {
            Word word = new Word(); // Initialize word for each column
            int row = 0;

            while (row < BOARD_SIZE) {
                Space space = board[row][col];

                // If empty, iterate and continue
                if (space.isBlank()) {
                    row++;
                    continue;
                }

                // Check if this space is at the end of the vertical
                if (row == BOARD_SIZE - 1) {
                    if (!isLetterPartOfHorizontalWord(row, col)) {
                        word.addSpace(space); // Add the last letter
                        foundWords.add(new Word(word));
                    }

                    break;
                }

                // Check if this letter is part of a vertical word
                if (!isLetterPartOfVerticalWord(row, col)) {
                    // Check if this letter is part of a horizontal word
                    if (!isLetterPartOfHorizontalWord(row, col)) {
                        // If not either, just add the single letter
                        word.addSpace(space);
                        foundWords.add(new Word(word));
                        word.clear();
                    }

                    // Iterate and continue
                    row++;
                    continue;
                }

                word.addSpace(space);

                // Iterate downward and collect letters in word
                while (row + 1 < BOARD_SIZE && board[row + 1][col].containsLetter()) {
                    row++;
                    word.addSpace(board[row][col]);
                }

                // Add the collected word to foundWords
                foundWords.add(new Word(word));
                word.clear();

                row++;
            }
        }

        return foundWords;
    }


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



    public Space getSpaceAtCoordinates(int row, int col) {
        return board[row][col];
    }

    public Multiplier getMultiplierAtCoordinates(int row, int col) {
        return multiplierBoard[row][col];
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Space[] row : board) {
            for (Space tile : row) {
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

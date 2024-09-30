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

    private ArrayList<Word> findWordsInDirection(boolean horizontalDir) {
        ArrayList<Word> foundWords = new ArrayList<>();

        // Loop through rows or columns based on the direction
        for (int i = 0; i < BOARD_SIZE; i++) {
            //Initialize word
            Word word = new Word();
            int index = 0;

            while (index < BOARD_SIZE) {
                Space space = horizontalDir ? board[i][index] : board[index][i];

                //If empty, iterate and continue
                if (space.isBlank()) {
                    index++;
                    continue;
                }

                //Check if this space is at the end of the word
                if (index == BOARD_SIZE - 1) {
                    if (!isLetterPartOfHorizontalWord(horizontalDir ? i : index, horizontalDir ? index : i)) {
                        // Add the last letter
                        word.addSpace(space);
                        foundWords.add(new Word(word));
                    }

                    break;
                }

                // Check if this letter is part of a word in the same direction
                if (horizontalDir ? !isLetterPartOfHorizontalWord(i, index) : !isLetterPartOfVerticalWord(index, i)) {
                    // Check if it's part of the other direction's word
                    if (horizontalDir ? !isLetterPartOfVerticalWord(i, index) : !isLetterPartOfHorizontalWord(index, i)) {
                        // If not either, just add the single letter
                        word.addSpace(space);
                        foundWords.add(new Word(word));
                        word.clear();
                    }

                    //Iterate and continue
                    index++;
                    continue;
                }

                //Add current letter to the word
                word.addSpace(space);

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

    public ArrayList<Word> findAllWords() {
        ArrayList<Word> allWords = new ArrayList<>();
        allWords.addAll(findWordsInDirection(true));
        allWords.addAll(findWordsInDirection(false));

        return allWords;
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

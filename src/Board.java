import Trie.Trie;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Space[][] board;
    private final int BOARD_SIZE;

    public Board(int size, String initContents) {
        this.BOARD_SIZE = size;
        board = new Space[size][size];

        String[] splitContents = initContents.split("\n");

        for (int i = 0; i < splitContents.length; i++) {
            //Split row by " "
            ArrayList<String> row = new ArrayList<>(List.of(splitContents[i].split(" ")));

            //Remove blank elements
            while (row.remove("")) {continue;}

            for (int j = 0; j < row.size(); j++) {
                board[i][j] = new Space(row.get(j), i, j);
            }
        }
    }

    public boolean isBoardInLegalState(Trie dictionary) {
        //Find all words on both boards
        ArrayList<Word> horizontalWords = findHorizontalWords();
        ArrayList<Word> verticalWords = findVerticalWords();

        //Check if this is the first move
        if (horizontalWords.size() + verticalWords.size() != 1) {
            //If this isn't the first move, check if all words are connected
            ArrayList<Word> allWords = new ArrayList<>();
            allWords.addAll(horizontalWords);
            allWords.addAll(verticalWords);

            if (!allWordsAreConnected(allWords)) {
                return false;
            }
        }

        //Check if all words are valid
        for (Word word : horizontalWords) {
            if (!dictionary.containsWord(word.toString())) {
                return false;
            }
        }

        return true;
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

                // Check if this word extends downward
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

    public boolean areBoardsCompatible(Trie dictionary, Board result) {
        //Find all words
        ArrayList<Word> originalWords = new ArrayList<>();
        originalWords.addAll(this.findHorizontalWords());
        originalWords.addAll(this.findVerticalWords());

        ArrayList<Word> resultWords = new ArrayList<>();
        resultWords.addAll(result.findHorizontalWords());
        resultWords.addAll(result.findVerticalWords());


        //Ensure that any new words are valid
        for (Word word : resultWords) {
            if (!dictionary.containsWord(word.toString())) {
                System.out.println(word.toString() + " Is invalid.");
                return false;
            }
        }


        //If this is the first move, we're good
        if (originalWords.isEmpty() && resultWords.size() == 1) {
            return true;
        }


        //If these have the same amount of words, or more than 1 new word, invalid
        if (originalWords.size() == resultWords.size() //No new words
                || originalWords.size() + 2 <= resultWords.size() //More than 1 new words
                || originalWords.size() > resultWords.size()) { //Less words somehow??
            System.out.println("Suspicious change in word count");
            System.out.println("Original: " +  originalWords.size() + " Result: " + resultWords.size());
            return false;
        }


        //Check to see if any words from the original board have moved
        for (Word originalWord : originalWords) {
            for (Space space : originalWord.getSpacesArray()) {
                //Check if this space is the same in the new board
                if (!space.equals(result.getSpaceAtCoordinates(space.getRow(), space.getCol()))) {
                    System.out.println(originalWord + " has moved");
                    //Word has moved
                    return false;
                }
            }
        }


        //Ensure all words are connected
        return allWordsAreConnected(resultWords);
    }

    public Space getSpaceAtCoordinates(int row, int col) {
        return board[row][col];
    }

    private boolean allWordsAreConnected(ArrayList<Word> allWords) {
        //Ensure all words are connected
        boolean hasConnection;
        for (int i = 0; i < allWords.size(); i++) {
            hasConnection = false;

            //Check all other words in the array
            for (int j = i + 1; j < allWords.size(); j++) {
                if (allWords.get(i).sharesASpaceWithOtherWord(allWords.get(j))) {
                    hasConnection = true;
                }
            }

            //If this word isn't connected to anything, it's invalid
            if (!hasConnection) {
                System.out.println(allWords.get(i).toString() + " has no connection");
                return false;
            }
        }

        return true;
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

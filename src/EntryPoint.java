/**
 * Brandon W. Hidalgo
 * This class serves to provide common functionality to the three
 * separate programs. All major entry points will need these methods
 * in order to properly function, and this provides to best way to share them.
 */

import ScrabbleObjects.Multiplier;
import utils.Board;
import utils.Trie.Trie;
import utils.BoardCompatibilityCheckData;
import ScrabbleObjects.Tile;
import ScrabbleObjects.Word;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public interface EntryPoint {
    /**
     * Parses the CLI to load the dictionary file and create a utils.Trie from it
     * @param args System args
     * @return New dictionary utils.Trie
     */
     static Trie parseClIForTrie(String[] args) {
        if (args.length == 0) {
            System.out.println("Expected dictionary file path.");
            System.exit(1);
        }

        String dictionaryFileName = args[0];
        return initTrie(dictionaryFileName);
    }


    /**
     * Makes a new trie with the specified dictionary
     * @param dictionaryFilePath Path of dictionary
     * @return New trie built from the dictionary
     */
    static Trie initTrie(String dictionaryFilePath) {
        Trie trie = new Trie();

        InputStream inputStream;

        //Try to read from disk local to this package
        try {
            inputStream = new FileInputStream(dictionaryFilePath);
        } catch (IOException e) {
            //Try to read from local dictionaries
            inputStream = CompatibilityAndScoring.class.getResourceAsStream("dictionaries_and_examples/" + dictionaryFilePath);
        }

        if (inputStream == null) {
            System.out.println("Failed to read dictionary.");
            System.exit(1);
        }


        // Using try-with-resources to ensure the BufferedReader is closed automatically
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            // Read lines until "exit" is entered
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    trie.addWord(line);
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to read dictionary file: " + dictionaryFilePath);
        }

        return trie;
    }


    /**
     * Checks if two boards are compatible, between the originalBoard and the resultBoard.
     * For two boards to be compatible, these conditions must be met:
     * Both boards must have matching multipliers
     * No words from the originalBoard should have moved in the result board
     * More specifically: No letters from the originalBoard should have been tampered with
     * The resultBoard must contain new letters
     * The resultBoard must have new words that are legal
     * The resultBoard must have only legal words
     * All words in the resultBoard must be connected
     * @param dictionary Dictionary of legal words
     * @param originalBoard Original utils.Board
     * @param resultBoard Original utils.Board after a move has been made
     * @return Data describing how the compatibility check went
     */
    static BoardCompatibilityCheckData areBoardsCompatible(Trie dictionary, Board originalBoard, Board resultBoard) {
        //Find play
        Word newPlay = new Word();

        //Ensure that these boards are different
        for (int i = 0; i < originalBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < originalBoard.BOARD_SIZE; j++) {
                //Check if these spaces are different
                if (!originalBoard.getTileAtCoordinates(i, j).equals(resultBoard.getTileAtCoordinates(i, j))) {
                    //If these are both blank this is a multiplier mismatch
                    if (originalBoard.getTileAtCoordinates(i, j).isBlank() && resultBoard.getTileAtCoordinates(i, j).isBlank()) {
                        return new BoardCompatibilityCheckData(false,"Incompatible boards: multiplier mismatch at (" + i + ", " + j + ")", null, null);
                    } else {
                        //Record that a difference has been found
                        //Do not break, because we still want to look
                        //For multiplier mismatches
                        newPlay.addSpace(resultBoard.getTileAtCoordinates(i, j));
                    }
                }
            }
        }


        //See if any spaces have been updated
        if (newPlay.isEmpty()) {
            return new BoardCompatibilityCheckData(false, "No new play found.", null, null);
        }


        //Find all words on both boards
        ArrayList<Word> originalWords = originalBoard.findAllWords();
        ArrayList<Word> resultWords = resultBoard.findAllWords();


        //If the result board has fewer words, invalid
        if (originalWords.size() > resultWords.size()) {
            String out = "Suspicious change in word count\n";
            out += "Original: " +  originalWords.size() + " Result: " + resultWords.size();
            return new BoardCompatibilityCheckData(false, out, null, null);
        }


        //Find new words
        ArrayList<Word> newWords = new ArrayList<>();
        for (Word newWord : resultWords) {
            boolean foundInOriginalBoard = false;
            for (Word originalWord : originalWords) {
                if (newWord.absEquals(originalWord)) {
                    foundInOriginalBoard = true;
                    break;
                }
            }

            //Check if this word is new
            if (!foundInOriginalBoard) {
                //This word is new
                newWords.add(newWord);
            }
        }

        //Check to see if any words from the original board have moved
        for (Word originalWord : originalWords) {
            for (Tile tile : originalWord.getSpacesArray()) {
                //Check if this space is the same in the new board
                if (!tile.equals(resultBoard.getTileAtCoordinates(tile.getRow(), tile.getCol()))) {
                    //ScrabbleObjects.Word has been altered in illegal way
                    String out = "Incompatible boards: \"" + originalWord + "\" been altered.\n";
                    out += String.format("Found at (%d, %d).\n%n", tile.getRow(), tile.getCol());
                    return new BoardCompatibilityCheckData(false, out, null, null);
                }
            }
        }

        //Trivial case, if this was the first move
        //if (originalWords.isEmpty()) {
        //    newPlay = resultWords.getFirst();
        //}

        //Save play info
        StringBuilder output = new StringBuilder();
        output.append("Play is");

        //Add new spaces to output
        for (Tile tile : newPlay.getSpacesArray()) {
            output.append(String.format(" %s -> (%d, %d),", tile.getContents(), tile.getRow(), tile.getCol()));
        }

        output.deleteCharAt(output.length()-1);
        output.append("\n");


        //Check if this is the first move
        if (originalWords.isEmpty() && resultWords.size() == 1) {
            //Check if the middle space is occupied
            int halfBoardSize = Math.floorDiv(originalBoard.BOARD_SIZE, 2);
            if (resultBoard.getTileAtCoordinates(halfBoardSize, halfBoardSize).isBlank()) {
                output.append("First move must be in center of board.\n");
                return new BoardCompatibilityCheckData(false, output.toString(), null, null);
            }
        }

        //Ensure that all words in new board are valid
        for (Word word : resultWords) {
            if (!dictionary.containsWord(word.toString())) {
                output.append("\"").append(word.toString()).append("\" Is an invalid word.");
                return new BoardCompatibilityCheckData(false, output.toString(), null, null);
            }
        }


        //Ensure all words are connected
        if (allWordsAreConnected(resultWords)) {
            return new BoardCompatibilityCheckData(true, output.toString(), newWords, newPlay.getSpacesArray());
        } else {
            output.append("Not all words are connected.\n");
            return new BoardCompatibilityCheckData(false, output.toString(), null, null);
        }
    }



    /**
     * Ensures that all words have a connection to another.
     * Fundamentally checks, if all words share a space with another word.
     * @param allWords All words on the board
     * @return If all the words are connected
     */
     static boolean allWordsAreConnected(ArrayList<Word> allWords) {
        HashSet<Word> connectedWords = new HashSet<>();

        //Ensure all words are connected
        boolean hasConnection;
        for (int i = 0; i < allWords.size() - 1; i++) {
            //If we have previously found a connection to this word,  iterate loop
            if (connectedWords.contains(allWords.get(i))) continue;

            hasConnection = false;

            //Check all other words in the array
            for (int j = 0; j < allWords.size(); j++) {
                //Don't check ourselves
                if (i == j) continue;

                //Check if we share a letter with the other word
                if (allWords.get(i).sharesASpaceWithAnotherWord(allWords.get(j))) {
                    connectedWords.add(allWords.get(i));
                    connectedWords.add(allWords.get(j));
                    hasConnection = true;
                    break;
                }
            }

            //If this word isn't connected to anything, it's invalid
            if (!hasConnection) {
                return false;
            }
        }

        return true;
    }


    /**
     * Score a play made
     * @param original Original board, used for multiplier values
     * @param numNewTiles Number of tiles placed
     * @param newWords New words formed
     * @return Score value of this play
     */
     static int scorePlay(Board original, int numNewTiles, ArrayList<Word> newWords) {
        //Score every new word
        int score = 0;
        for (Word word : newWords) {
            //Check if word or letter multiplier
            score += scoreWord(word, original);
        }

        //Bingo
        if (numNewTiles == 7) {
            score += 50;
        }

        return score;
    }


    /**
     * Scores an individual word. Takes into account the word multiplier, and letter multipliers.
     * @param word ScrabbleObjects.Word to score
     * @param board utils.Board that contains the multipliers
     * @return Score value of this word
     */
     static int scoreWord(Word word, Board board) {
        int score = 0;

        ArrayList<Multiplier> wordMultipliers = new ArrayList<>();
        Multiplier multiplier;
        for (Tile tile : word.getSpacesArray()) {
            multiplier = board.getMultiplierAtCoordinates(tile.getRow(), tile.getCol());

            // Apply multipliers
            if (!multiplier.hasMultiplierBeenUsed() && multiplier.type == Multiplier.MultiplierType.LETTER) {
                // Apply letter multiplier
                score += tile.getLetterPointValue() * multiplier.getMultiplierIntValue();
            } else {
                if (!multiplier.hasMultiplierBeenUsed() && multiplier.type == Multiplier.MultiplierType.WORD) {
                    // Add to word multiplier list
                    wordMultipliers.add(multiplier);
                }

                // Add just the tile letter point
                score += tile.getLetterPointValue();
            }
        }

        //Apply word multipliers
        for (Multiplier wordMultiplier : wordMultipliers) {
            score *= wordMultiplier.getMultiplierIntValue();
        }

        return score;
    }
}

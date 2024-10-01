import Trie.Trie;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        //Args for type of board
        //String filePath = "dictionaries_and_examples\\" + parseCLIForBoardFilePath(args);
        String dictionaryFileName = args[0];
        Trie dictionary = initTrie(dictionaryFileName);

        //Board board = initBoard(filePath);
        Board originalBoard;
        Board resultBoard;

        while (true) {
            originalBoard = initBoard("Enter original size and board:");
            resultBoard = initBoard("Enter result size and board:");

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
     * Parse the command line for the board file path. Then reads the
     * board from the specified path.
     * @param args CLI args
     * @return the board as a string
     */
    public static String parseCLIForBoardFilePath(String[] args) {
        if (args.length == 0) {
            return "scrabble_board";
        }

        return switch (args[0]) {
            case "-r" -> "scrabble_board";
            case "-su" -> "superscrabble_board";
            case "-w" -> "wordswithfriends_board";
            case "-sm" -> "small_board";
            default -> {
                System.out.println("Unknown option: " + args[0] + "\nDefaulting to regular board.");
                yield "scrabble_board";
            }
        };
    }

    /**
     * Makes a new trie with the specified dictionary
     * @param dictionaryFileName Path of dictionary
     * @return New trie built from the dictionary
     */
    public static Trie initTrie(String dictionaryFileName) throws IOException {
        Trie trie = new Trie();

        InputStream inputStream;

        //Try to read from disk, if in jar, try to read from class resource stream
        try {
            inputStream = new FileInputStream("resources/dictionaries_and_examples/" + dictionaryFileName);
        } catch (IOException e) {
            inputStream = Main.class.getResourceAsStream("/dictionaries_and_examples/" + dictionaryFileName);
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
            System.out.println("Failed to read dictionary file: " + dictionaryFileName);
        }

        return trie;
    }


    /**
     * Initializes a board from user input
     * @param inputPrompt Input prompt to give the user
     * @return New Board from user input
     */
    public static Board initBoard(String inputPrompt) throws IOException {
        StringBuilder boardContents = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //Read in size of board, should be first line of input

        int boardSize;
        System.out.println(inputPrompt);
        boardSize = Integer.parseInt(reader.readLine().trim());

        //Read each row
        for (int i = 0; i < boardSize; i++) {
            boardContents.append(reader.readLine()).append("\n");
        }


        /*
        System.out.println(inputPrompt);

        String line;
        while (!Objects.equals(line = reader.readLine(), "")) {
            boardContents.append(line).append("\n");
        }

        int boardSize = boardContents.toString().split("\n").length;

         */
        return new Board(boardSize, boardContents.toString());
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
     * @param originalBoard Original Board
     * @param resultBoard Original Board after a move has been made
     * @return Data describing how the compatibility check went
     */
    public static BoardCompatibilityCheckData areBoardsCompatible(Trie dictionary, Board originalBoard, Board resultBoard) {
        //Find play
        Word newPlay = new Word();

        //Ensure that these boards are different
        for (int i = 0; i < originalBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < originalBoard.BOARD_SIZE; j++) {
                //Check if these spaces are different
                if (!originalBoard.getSpaceAtCoordinates(i, j).equals(resultBoard.getSpaceAtCoordinates(i, j))) {
                    //If these are both blank this is a multiplier mismatch
                    if (originalBoard.getSpaceAtCoordinates(i, j).isBlank() && resultBoard.getSpaceAtCoordinates(i, j).isBlank()) {
                        return new BoardCompatibilityCheckData(false,"Incompatible boards: multiplier mismatch at (" + i + ", " + j + ")", null, -1);
                    } else {
                        //Record that a difference has been found
                        //Do not break, because we still want to look
                        //For multiplier mismatches
                        newPlay.addSpace(resultBoard.getSpaceAtCoordinates(i, j));
                    }
                }
            }
        }


        //See if any spaces have been updated
        if (newPlay.isEmpty()) {
            return new BoardCompatibilityCheckData(false, "No new play found.", null, -1);
        }


        //Find all words on both boards
        ArrayList<Word> originalWords = originalBoard.findAllWords();
        ArrayList<Word> resultWords = resultBoard.findAllWords();


        //If the result board has fewer words, invalid
        if (originalWords.size() > resultWords.size()) {
            String out = "Suspicious change in word count\n";
            out += "Original: " +  originalWords.size() + " Result: " + resultWords.size();
            return new BoardCompatibilityCheckData(false, out, null, -1);
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
            for (Space space : originalWord.getSpacesArray()) {
                //Check if this space is the same in the new board
                if (!space.equals(resultBoard.getSpaceAtCoordinates(space.getRow(), space.getCol()))) {
                    //Word has been altered in illegal way
                    String out = "Incompatible boards: \"" + originalWord + "\" been altered.\n";
                    out += String.format("Found at (%d, %d).\n%n", space.getRow(), space.getCol());
                    return new BoardCompatibilityCheckData(false, out, null, -1);
                }
            }
        }

        //Trivial case, if this was the first move
        if (originalWords.isEmpty()) {
            newPlay = resultWords.getFirst();
        }

        //Save play info
        StringBuilder output = new StringBuilder();
        output.append("Play is");

        //Add new spaces to output
        for (Space space : newPlay.getSpacesArray()) {
            output.append(String.format(" %s -> (%d, %d),", space.getContents(), space.getRow(), space.getCol()));
        }

        output.deleteCharAt(output.length()-1);
        output.append("\n");


        //Check if this is the first move
        if (originalWords.isEmpty() && resultWords.size() == 1) {
            //Check if the middle space is occupied
            int halfBoardSize = Math.floorDiv(originalBoard.BOARD_SIZE, 2);
            if (resultBoard.getSpaceAtCoordinates(halfBoardSize, halfBoardSize).isBlank()) {
                return new BoardCompatibilityCheckData(false, output.toString(), null, -1);
            }
        }

        //Ensure that all words in new board are valid
        for (Word word : resultWords) {
            if (!dictionary.containsWord(word.toString())) {
                output.append("\"").append(word.toString()).append("\" Is an invalid word.");
                output.append("\nPlay is not legal.");
                return new BoardCompatibilityCheckData(false, output.toString(), null, -1);
            }
        }


        //Ensure all words are connected
        if (allWordsAreConnected(resultWords)) {
            output.append("Play is legal.");
            return new BoardCompatibilityCheckData(true, output.toString(), newWords, newPlay.toString().length());
        } else {
            output.append("Play is not legal.");
            return new BoardCompatibilityCheckData(false, output.toString(), null, -1);
        }
    }

    /**
     * Score a play made
     * @param original Original board, used for multiplier values
     * @param numNewTiles Number of tiles placed
     * @param newWords New words formed
     * @return Score value of this play
     */
    public static int scorePlay(Board original, int numNewTiles, ArrayList<Word> newWords) {
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
     * @param word Word to score
     * @param board Board that contains the multipliers
     * @return Score value of this word
     */
    private static int scoreWord(Word word, Board board) {
        int score = 0;

        ArrayList<Multiplier> wordMultipliers = new ArrayList<>();
        Multiplier multiplier;
        for (Space space : word.getSpacesArray()) {
            multiplier = board.getMultiplierAtCoordinates(space.getRow(), space.getCol());

            if (!multiplier.hasMultiplierBeenUsed() && multiplier.type == Multiplier.MultiplierType.WORD) {
                wordMultipliers.add(multiplier);
            }

            score += space.getLetterPointValue();
        }

        //Apply word multipliers
        for (Multiplier wordMultiplier : wordMultipliers) {
            score *= wordMultiplier.getMultiplierIntValue();
        }

        return score;
    }

    /**
     * Ensures that all words have a connection to another.
     * Fundamentally checks, if all words share a space with another word.
     * @param allWords All words on the board
     * @return If all the words are connected
     */
    private static boolean allWordsAreConnected(ArrayList<Word> allWords) {
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
                System.out.println(allWords.get(i).toString() + " has no connection");
                return false;
            }
        }

        return true;
    }
}
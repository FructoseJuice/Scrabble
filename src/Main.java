import Trie.Trie;

import java.io.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        //Args for type of board
        //String filePath = "dictionaries_and_examples\\" + parseCLIForBoardFilePath(args);
        String filePath = "dictionaries_and_examples\\" + args[0];
        Trie dictionary = initTrie(filePath);
        //Board board = initBoard(filePath);
        Board originalBoard = initBoard("Enter original size and board:");

        Board resultBoard = initBoard("Enter result size and board:");

        System.out.println(originalBoard.isBoardInLegalState(dictionary));
        System.out.println(resultBoard.isBoardInLegalState(dictionary));
    }

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

    public static Trie initTrie(String filePath) {
        Trie trie = new Trie();

        // Using try-with-resources to ensure the BufferedReader is closed automatically
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            String line;

            // Read lines until "exit" is entered
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    trie.addWord(line);
                }
            }

        } catch (IOException e) {
            // Handle any IO exceptions
            System.err.println("An error occurred while reading input: " + e.getMessage());
        }

        return trie;
    }


    public static Board initBoard(String inputPrompt) throws IOException {
        StringBuilder boardContents = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //Read in size of board, should be first line of input
        int boardSize;
        System.out.println(inputPrompt);
        boardSize = Integer.parseInt(reader.readLine());

        //Read each row
        for (int i = 0; i < boardSize; i++) {
            boardContents.append(reader.readLine()).append("\n");
        }

        System.out.println(boardContents.toString());

        return new Board(boardSize, boardContents.toString());
    }
}
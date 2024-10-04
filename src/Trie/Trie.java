package Trie;

import java.util.HashMap;

/**
 * Brandon W. Hidalgo
 * This class is a Trie. It provides O(n) time, where
 * n = length(word), to access every word in a dictionary.
 * Has functions to add words, and check for the existence of words.
 */
public class Trie {
    private final HashMap<Character, ConnectionTree> rootConnectionTrees = new HashMap<>();

    public Trie() {}

    /**
     * Adds a new word to the dictionary
     * @param word word to add
     */
    public void addWord(String word) {
        ConnectionTree newConnection;

        //Check if first letter of word exists in trees
        if (rootConnectionTrees.containsKey(word.charAt(0))) {
            newConnection = rootConnectionTrees.get(word.charAt(0));
        } else {
            //Make new edge for this char
            newConnection = new ConnectionTree(word.charAt(0));

            //Add this to root of tree
            rootConnectionTrees.put(word.charAt(0), newConnection);
        }


        char connectionChar;

        for (int i = 1; i < word.length(); i++) {
            connectionChar = word.charAt(i);

            //If this connection already exists, just update newConnection with the found tree
            if (newConnection.hasConnection(connectionChar)) {
                newConnection = newConnection.getConnection(connectionChar);
            } else {
                //Make a new connection tree
                newConnection = newConnection.makeNewConnection(connectionChar);
            }
        }

        //This will be a leaf node, so it should be a terminator
        newConnection.setTerminator();
    }

    /**
     * Checks if this word is in the dictionary
     * @param word word to check for
     * @return if the dictionary contains this word
     */
    public boolean containsWord(String word) {
        //Ensure this word is in all lowercase
        word = word.toLowerCase();

        ConnectionTree connectionTree;

        //Check to see if first character is at the root of the tree
        if (rootConnectionTrees.containsKey(word.charAt(0))) {
            connectionTree = rootConnectionTrees.get(word.charAt(0));
        } else {
            return false;
        }

        char connectionChar;
        for (int i = 1; i < word.length(); i++) {
            connectionChar = word.charAt(i);

            //Check if the previous letter has a connection to this letter
            if (connectionTree.hasConnection(connectionChar)) {
                connectionTree = connectionTree.getConnection(connectionChar);
            } else {
                //If a connection wasn't found here, this word does not exist in the current dictionary
                return false;
            }
        }

        //This should be the leaf, so we return if this is a terminator or not
        return connectionTree.isATerminatorNode();
    }

    /**
     * Checks if this sequence is in the dictionary
     * @param word word to check for
     * @return if the dictionary contains this word
     */
    public boolean containsSequence(String word) {
        //Ensure this word is in all lowercase
        word = word.toLowerCase();

        ConnectionTree connectionTree;

        //Check to see if first character is at the root of the tree
        if (rootConnectionTrees.containsKey(word.charAt(0))) {
            connectionTree = rootConnectionTrees.get(word.charAt(0));
        } else {
            return false;
        }

        char connectionChar;
        for (int i = 1; i < word.length(); i++) {
            connectionChar = word.charAt(i);

            //Check if the previous letter has a connection to this letter
            if (connectionTree.hasConnection(connectionChar)) {
                connectionTree = connectionTree.getConnection(connectionChar);
            } else {
                //If a connection wasn't found here, this word does not exist in the current dictionary
                return false;
            }
        }

        //This should be the leaf, so we return if this is a terminator or not
        return true;
    }
}

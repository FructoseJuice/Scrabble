package Trie;

/**
 * Brandon W. Hidalgo
 * This class is a Trie. It provides O(n) time, where
 * n = length(word), to access every word in a dictionary.
 * Has functions to add words, and check for the existence of words.
 */
public class Trie {
    private final ConnectionTree rootConnectionTrees = new ConnectionTree('_');

    public Trie() {}

    /**
     * Adds a new word to the dictionary
     * @param word word to add
     */
    public void addWord(String word) {
        ConnectionTree newConnection;

        //Check if first letter of word exists in trees
        if (rootConnectionTrees.hasConnection(word.charAt(0))) {
            newConnection = rootConnectionTrees.getConnection(word.charAt(0));
        } else {
            //Add this to root of tree
            rootConnectionTrees.makeNewConnection(word.charAt(0));
            newConnection = rootConnectionTrees.getConnection(word.charAt(0));
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

        // Check for leading blank
        if (word.charAt(0) == '*') {
            return containsWordWithWildcard(rootConnectionTrees, word);
        }

        //Check to see if first character is at the root of the tree
        if (rootConnectionTrees.hasConnection(word.charAt(0))) {
            connectionTree = rootConnectionTrees.getConnection(word.charAt(0));
        } else {
            return false;
        }

        char connectionChar;
        for (int i = 1; i < word.length(); i++) {
            // Check for blank
            if (word.charAt(i) == '*') {
                return containsWordWithWildcard(connectionTree, word.substring(i));
            }

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
     * Checks to see if a word with a wildcard is in the tree. At the spot of the wildcard
     * try to follow every possible connection to see if there is a connection that leads to
     * a terminator.
     * @param parentTree Tree of letter before this wildcard
     * @param subString "*...c(n-1)c(n-1)cn"
     * @return If there is a path to a legal word from the wildcard
     */
    public boolean containsWordWithWildcard(ConnectionTree parentTree, String subString) {
        ConnectionTree tree;

        // Iterate through every possible character
        for (int letterIndex = 0; letterIndex < 26; letterIndex++) {
            char c = (char) (letterIndex + 'a');

            if (!parentTree.hasConnection(c)) continue;

            tree = parentTree.getConnection(c);

            //Start at 2 to ignore "ci" and "*"
            for (int subIndex = 1; subIndex < subString.length(); subIndex++) {
                if (tree.hasConnection(subString.charAt(subIndex))) {
                    //Update tree with child connection
                    tree = tree.getConnection(subString.charAt(subIndex));

                    //Check if at end of string
                    if (subIndex == subString.length()-1) {
                        //If this letter is a terminator, we've found a word
                        if (tree.isATerminatorNode()) return true;
                    }
                } else {
                    //If a link wasn't found to this node, then break, this is not a word
                    break;
                }
            }
        }

        return false;
    }
}

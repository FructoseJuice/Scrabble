package Trie_Graph;

import java.util.HashMap;

public class TrieGraph {
    private final HashMap<Character, ConnectionTree> rootConnectionTrees = new HashMap<>();

    public TrieGraph() {}

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

    public boolean containsWord(String word) {
        ConnectionTree connection;

        //Check to see if first character is at the root of the tree
        if (rootConnectionTrees.containsKey(word.charAt(0))) {
            connection = rootConnectionTrees.get(word.charAt(0));
        } else {
            return false;
        }

        char connectionChar;
        for (int i = 1; i < word.length(); i++) {
            connectionChar = word.charAt(i);

            if (connection.hasConnection(connectionChar)) {
                connection = connection.getConnection(connectionChar);
            } else {
                return false;
            }
        }

        //This should be the leaf, so we return if this is a terminator or not
        return connection.isTerminator();
    }
}

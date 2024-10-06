package utils.Trie;

import java.util.HashMap;

/**
 * Brandon W. Hidalgo
 * This class describes what I'm calling a "Connection Tree".
 * This is a tree where letter is the root note, and any letters
 * that it's connected to are the children connectionTrees of the root letter.
 * This lets us walk a path of connect nodes.
 * isATerminatorNode tells us when a word in the dictionary ends.
 */
public class ConnectionTree {
    private final char NODE;
    private boolean isATerminatorNode = false;

    //All connection trees connected to this node
    private final HashMap<Character, ConnectionTree> childConnectionTrees = new HashMap<>();

    public ConnectionTree(char node) {
        this.NODE = node;
    }

    public char getNode() {
        return NODE;
    }

    /**
     * Makes new child ConnectionTree
     * @param c Node of new ConnectionTree
     * @return new Connection Tree
     */
    public ConnectionTree makeNewConnection(char c) {
        ConnectionTree newTree = new ConnectionTree(c);
        childConnectionTrees.put(c, newTree);
        return newTree;
    }

    /**
     * Checks for a connection from between this node to the next
     * @param c Node to check for
     * @return if this node is connected to c
     */
    public boolean hasConnection(char c) {
        return childConnectionTrees.containsKey(c);
    }

    /**
     * Gets child ConnectionTree with node c
     * @param c Node of connectionTree to retrieve
     */
    public ConnectionTree getConnection(char c) {
        return childConnectionTrees.get(c);
    }

    /**
     * Check if this node indicates the stop of a word in the dictionary
     */
    public boolean isATerminatorNode() {
        return isATerminatorNode;
    }

    /**
     * Set this node as a terminator node.
     */
    public void setTerminator() {
        isATerminatorNode = true;
    }
}

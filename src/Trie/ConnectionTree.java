package Trie;

import java.util.HashMap;

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

    public ConnectionTree makeNewConnection(char c) {
        ConnectionTree newTree = new ConnectionTree(c);
        childConnectionTrees.put(c, newTree);
        return newTree;
    }

    public boolean hasConnection(char c) {
        return childConnectionTrees.containsKey(c);
    }

    public ConnectionTree getConnection(char c) {
        return childConnectionTrees.get(c);
    }

    public boolean isATerminatorNode() {
        return isATerminatorNode;
    }

    public void setTerminator() {
        isATerminatorNode = true;
    }
}

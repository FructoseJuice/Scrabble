package Trie_Graph;

import java.util.HashMap;

public class ConnectionTree {
    private final char node;
    private boolean isTerminator = false;

    //All connection trees connected to this node
    private final HashMap<Character, ConnectionTree> childConnectionTrees = new HashMap<>();

    public ConnectionTree(char node) {
        this.node = node;
    }

    public char getNode() {
        return node;
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

    public boolean isTerminator() {
        return isTerminator;
    }

    public void setTerminator() {
        isTerminator = true;
    }
}

package ScrabbleObjects;

import java.util.ArrayList;

/**
 * Brandon W. Hidalgo
 * This class describes a word. A word is just
 * an array of letters. This class provides a way to
 * easily encapsulate a full scrabble word. It provides
 * methods to compare words and access parts of this word.
 */
public class Word {
    //Holds all the letters in this word
    private final ArrayList<Tile> word;

    public Word() {
        word = new ArrayList<>();
    }

    public Word(ArrayList<Tile> word) {
        this.word = word;
    }

    public Word(Word word) {
        this.word = new ArrayList<>(word.getSpacesArray());
    }


    /**
     * Adds a new space to this word.
     * @param tile Space to add
     */
    public void addSpace(Tile tile) {
        word.add(tile);
    }

    /**
     * Adds a space to the front of this word
     * @param tile ScrabbleObjects.Tile to add to front
     */
    public void addSpaceToFront(Tile tile) {
        word.addFirst(tile);
    }

    /**
     * Adds a tile to the end of this word
     * @param tile ScrabbleObjects.Tile to add to end
     */
    public void addSpaceToEnd(Tile tile) {
        word.addLast(tile);
    }

    /**
     * Returns raw version of word, just an array of spaces
     */
    public ArrayList<Tile> getSpacesArray() {
        return word;
    }


    /**
     * Reverses this word
     */
    public void reverse() {
        int start = 0;
        int end = word.size() - 1;

        while (start < end) {
            // Swap the elements at start and end indices
            Tile temp = word.get(start);
            word.set(start, word.get(end));
            word.set(end, temp);

            // Move the pointers towards the center
            start++;
            end--;
        }
    }

    /**
     * Checks if this word contains the exact space given.
     * This check is done through the use of space.absEquals()
     * @param tile Space to check for
     * @return If this word contains the specified space
     */
    public boolean absContains(Tile tile) {
        for (Tile myTile : word) {
            if (myTile.absEquals(tile)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Empties this word out
     */
    public void clear() {
        this.word.clear();
    }

    /**
     * Checks if this word shares a space with another word. This
     * method uses space.absEquals() to check equality between each space.
     * @param other ScrabbleObjects.Word to compare with
     * @return If these two words share a space
     */
    public boolean sharesASpaceWithAnotherWord(Word other) {
        for (Tile myTile : word) {
            for (Tile otherTile : other.getSpacesArray()) {
                if (myTile.absEquals(otherTile)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Uses space.equals() to check for equality between words. This
     * is a loose equality check, and is only done between the letters
     * of each word.
     * @param other ScrabbleObjects.Word to compare with
     * @return If these two words are loosely equal
     */
    public boolean equals(Word other) {
        for (Tile myTile : word) {
            for (Tile otherTile : other.getSpacesArray()) {
                if (!myTile.equals(otherTile)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns letter at index i in the letter array
     * @param i index of letter
     * @return word.get(i)
     */
    public Tile getSpaceAtIndex(int i) {
        return word.get(i);
    }

    /**
     * Checks for absolute equality between two words. This method not
     * only checks for equality between the letters of the words, but
     * also the coordinates of the letters.
     * @param other ScrabbleObjects.Word to compare with
     * @return If these two words are absolutely equal
     */
    public boolean absEquals(Word other) {
        //Trivial check of size
        if (word.size() != other.getSpacesArray().size()) {
            return false;
        }

        //Check if each space is equal in letter and coordinates
        for (int i = 0; i < word.size(); i++) {
            if (!word.get(i).absEquals(other.getSpaceAtIndex(i))) {
                return false;
            }
        }

        //No discrepancies found
        return true;
    }

    /**
     * Check if this word is empty. Meaning there are
     * no letters int the letter array.
     * @return If this word is empty
     */
    public boolean isEmpty() {
        return word.isEmpty();
    }

    /**
     * @return The length of this word
     */
    public int size() {
        return word.size();
    }


    /**
     * Returns a copy of this words array and Tiles contained
     * @return A copy of this word
     */
    public Word copyOf() {
        ArrayList<Tile> newWord = new ArrayList<>();

        for (Tile tile : word) {
            newWord.add(tile.copyOf());
        }

        return new Word(newWord);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Tile tile : word) {
            builder.append(tile.toString().trim());
        }

        return builder.toString();
    }
}

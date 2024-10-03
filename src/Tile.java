/**
 * Brandon W. Hidalgo
 * This class describes a space on the scrabble board.
 * Each space has its contents, and coordinates saved.
 * This class can be used to get information about a
 * space on the board, or compare two spaces with one
 * another.
 */

public class Tile {
    private String contents;

    private final int row;
    private final int col;

    /**
     * Creates a new space with the specified contents and coordinates
     * @param contents Contents to initialize with
     */
    public Tile(String contents, int row, int col) {
        this.contents = contents;
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public String getContents() {
        return contents;
    }

    /**
     * Sets the contents of this Space to something new
     * @param contents contents to update with
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean containsLetter() {
        return !contents.contains(".");
    }

    /**
     * Returns if there's not a letter contained on this space.
     * @return If there's not a letter
     */
    public boolean isBlank() {
        return contents.contains(".");
    }

    /**
     * Loosely checks for equality. Simply checks if this letter
     * is equal to other.letter, where other is another space.
     * @param other Space to compare with
     * @return if these spaces are loosely equal
     */
    public boolean equals(Tile other) {
        return contents.trim().equalsIgnoreCase(other.getContents().trim());
    }

    /**
     * Checks for absolute equality with another space. This means that they
     * not only have to be loosely equal in terms of their letters, but
     * they also must have the same coordinates on the board.
     * @param other Space to compare with
     * @return If these two spaces are absolutely equal
     */
    public boolean absEquals(Tile other) {
        return equals(other) && coordinateEquals(other);
    }

    /**
     * Checks if the coordinates of this space are equal to another space
     * @param other Space to compare with
     * @return if the coordinates are equal
     */
    public boolean coordinateEquals(Tile other) {
        return row == other.getRow() && col == other.getCol();
    }

    /**
     * Converts our letter to a point value
     */
    public int getLetterPointValue() {
        char letter = contents.trim().charAt(0);

        return switch (letter) {
            case 'a', 'e', 'i', 'o', 'u', 'l', 'n', 's', 't', 'r' -> 1;
            case 'd', 'g' -> 2;
            case 'b', 'c', 'm', 'p' -> 3;
            case 'f', 'h', 'v', 'w', 'y' -> 4;
            case 'k' -> 5;
            case 'j', 'x' -> 8;
            case 'q', 'z' -> 10;
            default -> 0; //For wildcards
        };
    }

    @Override
    public String toString() {
        return contents;
    }
}

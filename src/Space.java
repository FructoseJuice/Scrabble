public class Space {
    private String contents;

    private final int row;
    private final int col;

    public Space(String contents, int row, int col) {
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

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public boolean containsLetter() {
        return !contents.contains(".");
    }

    public boolean isBlank() {
        return contents.contains(".");
    }

    public boolean equals(Space other) {
        //Check for wildcards
        if (contents.contains("*") || other.getContents().equals("*")) return true;

        return contents.trim().equalsIgnoreCase(other.getContents().trim());
    }

    public boolean coordinateEquals(Space other) {
        return row == other.getRow() && col == other.getCol();
    }

    public boolean absEquals(Space other) {
        return equals(other) && coordinateEquals(other);
    }

    public int getLetterPointValue() {
        char letter = contents.trim().charAt(0); // Convert to lowercase for uniformity

        return switch (letter) {
            case 'a', 'e', 'i', 'o', 'u', 'l', 'n', 's', 't', 'r' -> 1;
            case 'd', 'g' -> 2;
            case 'b', 'c', 'm', 'p' -> 3;
            case 'f', 'h', 'v', 'w', 'y' -> 4;
            case 'k' -> 5;
            case 'j', 'x' -> 8;
            case 'q', 'z' -> 10;
            default -> 0; // Non-letter characters or invalid letters
        };
    }

    @Override
    public String toString() {
        return contents;
    }
}

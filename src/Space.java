public class Space {
    private final int multiplier;
    private String contents;

    private final int row;
    private final int col;

    public Space(String contents, int row, int col) {
        this.contents = contents;
        this.row = row;
        this.col = col;

        if (contents.contains("3")) {
            multiplier = 3;
        } else if (contents.contains("2")) {
            multiplier = 2;
        } else {
            multiplier = 1;
        }
    }

    public int getMultiplier() {
        return multiplier;
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
        return contents.trim().equalsIgnoreCase(other.getContents().trim());
    }

    public boolean coordinateEquals(Space other) {
        return row == other.getRow() && col == other.getCol();
    }

    public boolean absEquals(Space other) {
        return equals(other) && coordinateEquals(other);
    }

    public int getScrabblePointValue() {
        char letter = contents.trim().charAt(0); // Convert to lowercase for uniformity

        int val;
        switch (letter) {
            case 'a', 'e', 'i', 'o', 'u', 'l', 'n', 's', 't', 'r' -> val = 1;
            case 'd', 'g' -> val = 2;
            case 'b', 'c', 'm', 'p' -> val = 3;
            case 'f', 'h', 'v', 'w', 'y' -> val = 4;
            case 'k' -> val = 5;
            case 'j', 'x' -> val = 8;
            case 'q', 'z' -> val = 10;
            default -> val = 0; // Non-letter characters or invalid letters
        }

        System.out.println(letter + " " + val + " " + multiplier);

        return val * multiplier;
    }

    @Override
    public String toString() {
        return contents;
    }
}

public class Space {
    public enum SpaceMultiplier {
        ONE, TWO, THREE;
    }

    private final SpaceMultiplier multiplier;
    private String contents;

    private final int row;
    private final int col;

    public Space(String contents, int row, int col) {
        this.contents = contents;
        this.row = row;
        this.col = col;

        if (contents.contains(".")) {
            if (contents.contains("3")) {
                multiplier = SpaceMultiplier.THREE;
            } else if (contents.contains("2")) {
                multiplier = SpaceMultiplier.TWO;
            } else {
                multiplier = SpaceMultiplier.ONE;
            }
        } else {
            multiplier = SpaceMultiplier.ONE;
        }
    }

    public int getMultiplier() {
        return switch (multiplier) {
            case ONE -> 1;
            case TWO -> 2;
            case THREE -> 3;
        };
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

    @Override
    public String toString() {
        return contents;
    }
}

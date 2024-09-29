public class Space {
    public enum SpaceMultiplier {
        ONE, TWO, THREE;
    }

    private final SpaceMultiplier multiplier;
    private String contents;

    public Space(String contents) {
        this.contents = contents;

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

    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean containsLetter() {
        return !contents.contains(".");
    }

    public boolean isBlank() {
        return contents.contains(".");
    }

    @Override
    public String toString() {
        return contents;
    }
}

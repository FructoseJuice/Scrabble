public class Space {
    public enum SpaceMultiplier {
        ONE, TWO, THREE;
    }

    private final SpaceMultiplier multiplier;
    private String contents;

    public Space(String contents) {
        this.contents = contents;

        if (contents.charAt(0) != '.') {
            multiplier = switch (contents.charAt(0)) {
                case '2' -> SpaceMultiplier.TWO;
                case '3' -> SpaceMultiplier.THREE;
                default -> SpaceMultiplier.ONE;
            };
        } else if (contents.charAt(1) != '.') {
            multiplier = switch (contents.charAt(1)) {
                case '2' -> SpaceMultiplier.TWO;
                case '3' -> SpaceMultiplier.THREE;
                default -> SpaceMultiplier.ONE;
            };
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

    @Override
    public String toString() {
        return contents;
    }
}

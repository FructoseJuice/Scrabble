public class Space {
    public enum SpaceMultiplier {
        ONE, TWO, THREE;
    }

    SpaceMultiplier multiplier;
    String contents;

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

    @Override
    public String toString() {
        return contents;
    }
}

/**
 * This class describes a Multiplier in scrabble
 * It defines the type of multiplier it is, utils.Word, or Letter,
 * and the value of it, One, Two, or Three.
 */
public class Multiplier {
    public enum MultiplierType {
        WORD, LETTER
    }

    public enum MultiplierValue {
        ONE, TWO, THREE
    }

    public final MultiplierValue value;
    public final MultiplierType type;
    boolean multiplierUsed = false;

    public Multiplier(MultiplierType type, MultiplierValue value) {
        this.value = value;
        this.type = type;
    }

    public Multiplier(Multiplier other) {
        this.value = other.value;
        this.type = other.type;
    }

    public Multiplier(String spaceContent) {
        if (spaceContent.contains("3")) {
            value = MultiplierValue.THREE;
            //Check if word or letter
            if (spaceContent.charAt(0) == '3') {
                //utils.Word
                type = MultiplierType.WORD;
            } else {
                //Letter
                type = MultiplierType.LETTER;
            }
        } else if (spaceContent.contains("2")) {
            value = MultiplierValue.TWO;
            //Check if word or letter
            if (spaceContent.charAt(0) == '2') {
                //utils.Word
                type = MultiplierType.WORD;
            } else {
                //Letter
                type = MultiplierType.LETTER;
            }
        } else {
            type = MultiplierType.LETTER;
            value = MultiplierValue.ONE;
        }
    }

    /**
     * Check if this multiplier has been used already
     */
    public boolean hasMultiplierBeenUsed() {
        return multiplierUsed;
    }

    /**
     * Converts the multiplier enum to an int
     */
    public int getMultiplierIntValue() {
        return switch (value) {
            case ONE -> 1;
            case TWO -> 2;
            case THREE -> 3;
        };
    }

    public void setMultiplierAsUsed() {
        multiplierUsed = true;
    }
}

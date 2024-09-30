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

    public Multiplier(String spaceContent) {
        if (spaceContent.contains("3")) {
            value = MultiplierValue.THREE;
            //Check if word or letter
            if (spaceContent.charAt(0) == '3') {
                //Word
                type = MultiplierType.WORD;
            } else {
                //Letter
                type = MultiplierType.LETTER;
            }
        } else if (spaceContent.contains("2")) {
            value = MultiplierValue.TWO;
            //Check if word or letter
            if (spaceContent.charAt(0) == '2') {
                //Word
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

    public boolean hasMultiplierBeenUsed() {
        return multiplierUsed;
    }

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

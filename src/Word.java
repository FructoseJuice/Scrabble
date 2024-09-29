import java.util.ArrayList;

public class Word {
    private ArrayList<Space> word;

    public Word() {
        word = new ArrayList<>();
    }

    public Word(ArrayList<Space> word) {
        this.word = word;
    }

    public Word(Word word) {
        this.word = new ArrayList<>(word.getSpacesArray());
    }

    public void addSpace(Space letter) {
        word.add(letter);
    }

    public ArrayList<Space> getSpacesArray() {
        return word;
    }

    public void clear() {
        this.word.clear();
    }

    public boolean sharesASpaceWithOtherWord(Word other) {
        for (Space mySpace : word) {
            for (Space otherSpace : other.getSpacesArray()) {
                if (mySpace.getCol() == otherSpace.getCol() && mySpace.getRow() == otherSpace.getRow()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean equals(Word other) {
        for (Space mySpace : word) {
            for (Space otherSpace : other.getSpacesArray()) {
                if (!mySpace.equals(otherSpace)) {
                    return false;
                }
            }
        }

        return true;
    }



    public boolean coordinatesEquals(Word other) {
        for (Space mySpace : word) {
            for (Space otherSpace : other.getSpacesArray()) {
                if (!mySpace.coordinateEquals(otherSpace)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Space letter : word) {
            builder.append(letter.toString().trim());
        }

        return builder.toString();
    }
}

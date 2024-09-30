import java.util.ArrayList;

public class Word {
    private final ArrayList<Space> word;

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

    public void insertIfAbsAbsent(Space space) {
        if (!absContains(space)) {
            addSpace(space);
        }
    }

    public boolean absContains(Space space) {
        for (Space mySpace : word) {
            if (mySpace.absEquals(space)) {
                return true;
            }
        }

        return false;
    }

    public void clear() {
        this.word.clear();
    }

    public boolean sharesALetterWithOtherWord(Word other) {
        for (Space mySpace : word) {
            for (Space otherSpace : other.getSpacesArray()) {
                if (mySpace.absEquals(otherSpace)) {
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

    public Space getSpaceAtIndex(int i) {
        return word.get(i);
    }

    public boolean absEquals(Word other) {
        //Trivial check of size
        if (word.size() != other.getSpacesArray().size()) {
            return false;
        }

        //Check if each space is equal in letter and coordinates
        for (int i = 0; i < word.size(); i++) {
            //Compare letter
            if (!word.get(i).equals(other.getSpaceAtIndex(i))) {
                return false;
            }
            //Compare coordinates
            if (!word.get(i).coordinateEquals(other.getSpaceAtIndex(i))) {
                return false;
            }
        }

        //No discrepancies found
        return true;
    }

    public boolean isEmpty() {
        return word.isEmpty();
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

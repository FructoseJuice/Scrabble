/**
 * Brandon W. Hidalgo
 * I realized that the Tray is basically just a word with some functionality differences.
 * So I just had it extend Word and remove said functionality, and add some more Tray specific methods.
 */

import java.util.ArrayList;

public class Tray extends Word {
    public Tray() {
        super();
    }

    public Tray(ArrayList<Tile> word) {
        super(word);
    }

    public Tray(Word word) {
        super(word);
    }

    public void removeLetter(Tile letter) {
        getSpacesArray().removeIf(myLetter -> myLetter.equals(letter));
    }


    /*
    Gut some functionality that wouldn't properly work
     */
    @Override
    public boolean absContains(Tile tile) {
        System.out.println("Cannot use method @absContains on object @Tray.");
        return false;
    }

    @Override
    public boolean sharesASpaceWithAnotherWord(Word other) {
        System.out.println("Cannot use method @sharesASpaceWithAnotherWord on object @Tray.");
        return false;
    }

    @Override
    public boolean absEquals(Word other) {
        System.out.println("Cannot use method @absEquals on object @Tray.");
        return false;
    }
}

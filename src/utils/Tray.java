package utils; /**
 * Brandon W. Hidalgo
 * I realized that the utils.Tray is basically just a word with some functionality differences.
 * So I just had it extend utils.Word and remove said functionality, and add some more utils.Tray specific methods.
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


    /*
    Gut some functionality that wouldn't properly work
     */
    @Override
    public boolean absContains(Tile tile) {
        System.out.println("Cannot use method @absContains on object @utils.Tray.");
        return false;
    }

    @Override
    public boolean sharesASpaceWithAnotherWord(Word other) {
        System.out.println("Cannot use method @sharesASpaceWithAnotherWord on object @utils.Tray.");
        return false;
    }

    @Override
    public boolean absEquals(Word other) {
        System.out.println("Cannot use method @absEquals on object @utils.Tray.");
        return false;
    }
}

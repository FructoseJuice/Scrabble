package ScrabbleObjects;
/**
 * Brandon W. Hidalgo
 * I realized that the ScrabbleObjects.Tray is basically just a word with some functionality differences.
 * So I just had it extend ScrabbleObjects.Word and remove said functionality, and add some more ScrabbleObjects.Tray specific methods.
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


    public void removeTileFromTray(Tile tileToRemove) {
        if (getSpacesArray().remove(tileToRemove)) return;

        // If tile is wildcard
        if (tileToRemove.getContents().contains("*")) {
            for (int i = 0; i < getSpacesArray().size(); i++) {
                if (getSpaceAtIndex(i).getContents().contains("*")) {
                    // Remove tile and exit
                    getSpacesArray().remove(i);
                    return;
                }
            }
        }

        // Remove first found instance of this letter
        for (int i = 0; i < getSpacesArray().size(); i++) {
            if (getSpaceAtIndex(i).equals(tileToRemove)) {
                // Remove tile and exit
                getSpacesArray().remove(i);
                return;
            }
        }
    }

    /*
    Gut some functionality that wouldn't properly work
     */
    @Override
    public boolean absContains(Tile tile) {
        System.out.println("Cannot use method @absContains on object @ScrabbleObjects.Tray.");
        return false;
    }

    @Override
    public boolean sharesASpaceWithAnotherWord(Word other) {
        System.out.println("Cannot use method @sharesASpaceWithAnotherWord on object @ScrabbleObjects.Tray.");
        return false;
    }

    @Override
    public boolean absEquals(Word other) {
        System.out.println("Cannot use method @absEquals on object @ScrabbleObjects.Tray.");
        return false;
    }
}

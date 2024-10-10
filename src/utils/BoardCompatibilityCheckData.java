package utils;

import ScrabbleObjects.Tile;
import ScrabbleObjects.Word;

import java.util.ArrayList;

/**
 * Brandon W. Hidalgo
 * This class encapsulates all the data needed to describe
 * if two boards are compatible or not.
 * @param isLegal If the result board is legal
 * @param output Output from the compatibility check
 * @param newWords all new words found during the check
 * @param newTiles all new tiles placed
 */
public record BoardCompatibilityCheckData(boolean isLegal, String output, ArrayList<Word> newWords, ArrayList<Tile> newTiles) {}

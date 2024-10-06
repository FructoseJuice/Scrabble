package utils;

import ScrabbleObjects.Word;

import java.util.ArrayList;

/**
 * Brandon W. Hidalgo
 * This class encapsulates all the data needed to describe
 * if two boards are compatible or not.
 * @param isLegal If the result board is legal
 * @param output Output from the compatibility check
 * @param newWords all new words found during the check
 * @param numNewTiles number of tiles placed
 */
public record BoardCompatibilityCheckData(boolean isLegal, String output, ArrayList<Word> newWords, int numNewTiles) {}

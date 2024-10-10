package utils;

import ScrabbleObjects.Word;

import java.util.ArrayList;

public record PlayData(String newPlayString, String output, Word newPlay, ArrayList<Word> newWords, int score) {
}

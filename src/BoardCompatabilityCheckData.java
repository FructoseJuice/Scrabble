import java.util.ArrayList;

public record BoardCompatabilityCheckData(boolean isLegal, String output, ArrayList<Word> newWords, int numNewTiles) {}

import java.util.ArrayList;

public class LookUpTableEntry {
    private final int EloWhite;
    private final int EloBLack;
    private final ArrayList<Float> probabilities;

    public LookUpTableEntry(String[] splitLine) {
        assert splitLine != null;
        EloWhite = Integer.getInteger(splitLine[0]);
        EloBLack = Integer.getInteger(splitLine[1]);
        this.probabilities = new ArrayList<>(3);
        for (String string : splitLine) {
            probabilities.add(Float.parseFloat(string));
        }
    }

    public int getEloWhite() {
        return EloWhite;
    }

    public int getEloBLack() {
        return EloBLack;
    }

    public ArrayList<Float> getProbabilities() {
        return probabilities;
    }
}

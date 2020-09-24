import java.util.Arrays;
import java.util.List;

public class LookUpTableEntry {
    private final int EloWhite;
    private final int EloBLack;
    private final Float[] probabilities;

    public LookUpTableEntry(String[] splitLine) {
        assert splitLine != null;
        EloWhite = Integer.parseInt(splitLine[0]);
        EloBLack = Integer.parseInt(splitLine[1]);
        probabilities = new Float[3];
        probabilities[0] = Float.parseFloat(splitLine[2]);
        probabilities[1] = Float.parseFloat(splitLine[3]);
        probabilities[2] = Float.parseFloat(splitLine[4]);
    }

    public int getEloWhite() {
        return EloWhite;
    }

    public int getEloBLack() {
        return EloBLack;
    }

    public List<Float> getProbabilities() {
        return Arrays.asList(probabilities);
    }
}

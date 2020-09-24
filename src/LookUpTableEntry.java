public class LookUpTableEntry {
    public final int ELO_WHITE;
    public final int ELO_BLACK;
    public final float[] PROBABILITIES;

    public LookUpTableEntry(String[] splitLine) {
        ELO_WHITE = Integer.parseInt(splitLine[0]);
        ELO_BLACK = Integer.parseInt(splitLine[1]);
        PROBABILITIES = new float[]{Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]), Float.parseFloat(splitLine[4])};
    }
}

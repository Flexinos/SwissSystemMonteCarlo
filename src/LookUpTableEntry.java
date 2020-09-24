public class LookUpTableEntry {
    public final float[] PROBABILITIES;

    public LookUpTableEntry(String[] splitLine) {
        PROBABILITIES = new float[]{Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]), Float.parseFloat(splitLine[4])};
    }
}

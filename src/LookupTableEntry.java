public class LookupTableEntry {
    public final float[] PROBABILITIES;

    public LookupTableEntry(String[] splitLine) {
        PROBABILITIES = new float[]{Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]), Float.parseFloat(splitLine[4])};
    }
}

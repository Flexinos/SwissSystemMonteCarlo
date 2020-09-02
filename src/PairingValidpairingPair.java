import java.util.ArrayList;

public class PairingValidpairingPair {
    private final ArrayList<Pairing> pairings;
    private final boolean isValid;

    public PairingValidpairingPair(ArrayList<Pairing> pairings, boolean isValid) {
        this.pairings = pairings;
        this.isValid = isValid;
    }

    public ArrayList<Pairing> getPairings() {
        return pairings;
    }

    public boolean getIsValid() {
        return isValid;
    }
}

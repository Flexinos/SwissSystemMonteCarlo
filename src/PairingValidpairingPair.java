import java.util.ArrayList;

public class PairingValidpairingPair {
    private ArrayList<Pairing> pairings;
    private boolean isValid;

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

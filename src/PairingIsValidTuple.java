import java.util.List;

public class PairingIsValidTuple {
    private final List<Pairing> pairings;
    private final boolean isValid;

    public PairingIsValidTuple(List<Pairing> pairings, boolean isValid) {
        this.pairings = pairings;
        this.isValid = isValid;
    }

    public List<Pairing> getPairings() {
        return pairings;
    }

    public boolean getIsValid() {
        return isValid;
    }
}

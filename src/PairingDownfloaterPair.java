import java.util.List;

public class PairingDownfloaterPair {
    private final List<Pairing> pairings;
    private final List<SimulatedPlayer> downfloater;

    public PairingDownfloaterPair(List<Pairing> pairings, List<SimulatedPlayer> downfloater) {
        this.pairings = pairings;
        this.downfloater = downfloater;
    }

    public List<Pairing> getPairings() {
        return pairings;
    }

    public List<SimulatedPlayer> getDownfloater() {
        return downfloater;
    }
}

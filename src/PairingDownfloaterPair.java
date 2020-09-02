import java.util.ArrayList;

public class PairingDownfloaterPair {
    private ArrayList<Pairing> pairings;
    private ArrayList<SimulatedPlayer> downfloater;

    public PairingDownfloaterPair(ArrayList<Pairing> pairings, ArrayList<SimulatedPlayer> downfloater) {
        this.pairings = pairings;
        this.downfloater = downfloater;
    }

    public ArrayList<Pairing> getPairings() {
        return pairings;
    }

    public ArrayList<SimulatedPlayer> getDownfloater() {
        return downfloater;
    }
}
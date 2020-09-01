import java.util.ArrayList;

public class Pairing {
    private final int board;
    private final ArrayList<SimulatedPlayer> pairing;
    private ResultOfGame result;

    public Pairing(int board, ArrayList<SimulatedPlayer> pairing) {
        this.board = board;
        this.pairing = pairing;
        simulateResult();
    }

    public int getBoard() {
        return board;
    }

    public ArrayList<SimulatedPlayer> getPairing() {
        return pairing;
    }

    private void simulateResult() {
        // get random number and map number to result
    }

}

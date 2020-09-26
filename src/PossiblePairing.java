public class PossiblePairing {
    private final SimulatedPlayer player1;
    private final SimulatedPlayer player2;

    public PossiblePairing(SimulatedPlayer player1, SimulatedPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public SimulatedPlayer getPlayer1() {
        return player1;
    }

    public SimulatedPlayer getPlayer2() {
        return player2;
    }
}

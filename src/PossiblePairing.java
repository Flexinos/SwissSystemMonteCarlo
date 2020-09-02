public class PossiblePairing {
    private final int board;
    private final SimulatedPlayer player1;
    private final SimulatedPlayer player2;

    public PossiblePairing(int board, SimulatedPlayer player1, SimulatedPlayer player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
    }

    public int getBoard() {
        return board;
    }

    public SimulatedPlayer getPlayer1() {
        return player1;
    }

    public SimulatedPlayer getPlayer2() {
        return player2;
    }
}

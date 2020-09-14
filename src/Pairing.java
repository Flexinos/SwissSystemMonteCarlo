public class Pairing {
    private final SimulatedPlayer player1;
    private final SimulatedPlayer player2;
    private GameResult.ResultOfGame result;

    public Pairing(PossiblePairing pairing) {
        this.player1 = pairing.getPlayer1();
        this.player2 = pairing.getPlayer2();
    }

    public Pairing(PossiblePairing pairing, boolean includesBye) {
        // only call if it includes bye
        this.player1 = pairing.getPlayer1();
        this.player2 = pairing.getPlayer2();
        if (includesBye) {
            result = GameResult.ResultOfGame.BYE;
            player1.addGame(player2, 1);
        }
    }

    public static boolean pairingAllowed(SimulatedPlayer player1, SimulatedPlayer player2) {
        //todo check if this really works
        if (player1.equals(player2)) {
            System.out.println("playing against oneself");
            return false;
        } else return !player1.getSimulatedTournament().haveMet(player1, player2);
    }

    public SimulatedPlayer getPlayer1() {
        return player1;
    }

    public SimulatedPlayer getPlayer2() {
        return player2;
    }

    public void simulateResult() {
        result = GameResult.randomResult(player1, player2);
        switch (result) {
            case WHITE_WIN -> {
                player1.addGame(player2, 1, true);
                player2.addGame(player1, 0, false);
            }
            case BLACK_WIN -> {
                player1.addGame(player2, 0, true);
                player2.addGame(player1, 1, false);
            }
            default -> {
                player1.addGame(player2, 0.5, true);
                player2.addGame(player1, 0.5, false);
            }
        }
        //System.out.println(player1.getParticipant().getName() + "played against " + player2.getParticipant().getName());
    }
}
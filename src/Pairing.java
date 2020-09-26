import java.util.Random;

public class Pairing {
    private final SimulatedPlayer player1;
    private final SimulatedPlayer player2;
    private static final Random random = new Random();
    private ResultOfGame result;

    public Pairing(SimulatedPlayer player1, SimulatedPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Pairing(SimulatedPlayer player1, SimulatedPlayer player2, boolean includesBye) {
        this(player1, player2);
        if (includesBye) {
            result = ResultOfGame.BYE;
            player1.addGame(player2, 1);
        }
    }

    public static boolean pairingAllowed(SimulatedPlayer player1, SimulatedPlayer player2) {
        if (player1.equals(player2)) {
            System.out.println("playing against oneself");
            return false;
        } else {
            return !player1.getSimulatedTournament().haveMet(player1, player2);
        }
    }

    public SimulatedPlayer getPlayer1() {
        return player1;
    }

    public SimulatedPlayer getPlayer2() {
        return player2;
    }

    private static ResultOfGame randomResultLookUp(SimulatedPlayer whitePlayer, SimulatedPlayer blackPlayer) {
        float randomValue = random.nextFloat();
        float[] probabilitiesArray = LookupTable.getProbabilities(whitePlayer.getElo(), blackPlayer.getElo());
        if (randomValue < probabilitiesArray[0]) {
            return ResultOfGame.BLACK_WIN;
        } else if (randomValue < probabilitiesArray[0] + probabilitiesArray[1]) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.WHITE_WIN;
        }
    }

    public void simulateResult() {
        if (result != null) {
            return;
        }
        result = randomResultLookUp(player1, player2);
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
    }

    @Override
    public String toString() {
        if (result.equals(ResultOfGame.WHITE_WIN)) {
            return player1 + "1-0 " + player2;
        } else if (result.equals(ResultOfGame.DRAW)) {
            return player1 + "0.5-0.5 " + player2;
        } else if (result.equals(ResultOfGame.BLACK_WIN)) {
            return player1 + "0-1 " + player2;
        } else {
            return player1 + " 1 " + "spielfrei";
        }
    }

    private enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN, BYE
    }
}
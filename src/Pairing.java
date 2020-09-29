import java.util.concurrent.ThreadLocalRandom;

public class Pairing {
    private final SimulatedPlayer player1;
    private final SimulatedPlayer player2;
    private ResultOfGame result;

    public Pairing(final SimulatedPlayer player1, final SimulatedPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public static void giveBye(final SimulatedPlayer player1) {
        player1.addGame(Tournament.BYE, 1);
        player1.setReceivedBye(true);
    }

    public static boolean pairingAllowed(final SimulatedPlayer player1, final SimulatedPlayer player2) {
        if (player1.equals(player2)) {
            System.out.println("playing against oneself");
            return false;
        } else {
            return !player1.getSimulatedTournament().haveMet(player1, player2);
        }
    }

    private static ResultOfGame randomResultLookUp(final SimulatedPlayer whitePlayer, final SimulatedPlayer blackPlayer) {
        final float randomValue = ThreadLocalRandom.current().nextFloat();
        final float[] probabilitiesArray = LookupTable.getProbabilities(whitePlayer.getElo(), blackPlayer.getElo());
        if (randomValue < probabilitiesArray[0]) {
            return ResultOfGame.BLACK_WIN;
        } else if (randomValue < probabilitiesArray[0] + probabilitiesArray[1]) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.WHITE_WIN;
        }
    }

    public SimulatedPlayer getPlayer1() {
        return player1;
    }

    public SimulatedPlayer getPlayer2() {
        return player2;
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
        if (result == ResultOfGame.WHITE_WIN) {
            return player1 + "1-0 " + player2;
        } else if (result == ResultOfGame.DRAW) {
            return player1 + "0.5-0.5 " + player2;
        } else if (result == ResultOfGame.BLACK_WIN) {
            return player1 + "0-1 " + player2;
        } else {
            return player1 + " 1 " + "spielfrei";
        }
    }

    private enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN, BYE
    }
}
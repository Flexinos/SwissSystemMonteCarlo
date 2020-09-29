import java.util.concurrent.ThreadLocalRandom;

public final class Pairing {
    private final SimulatedPlayer player1;
    private final SimulatedPlayer player2;
    private ResultOfGame result;

    public Pairing(final SimulatedPlayer player1, final SimulatedPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public static boolean canBePaired(final SimulatedPlayer player1, final SimulatedPlayer player2) {
        if (player1.equals(player2)) {
            System.out.println("playing against oneself");
            return false;
        } else {
            return !player1.playedAgainst(player2);
        }
    }

    private static ResultOfGame randomResultLookUp(final SimulatedPlayer whitePlayer, final SimulatedPlayer blackPlayer) {
        final float randomValue = ThreadLocalRandom.current().nextFloat();
        final float[] probabilitiesArray = LookupTable.getProbabilities(whitePlayer.getElo(), blackPlayer.getElo());
        if (randomValue < probabilitiesArray[0]) {
            return ResultOfGame.BLACK_WIN;
        } else if (randomValue < (probabilitiesArray[0] + probabilitiesArray[1])) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.WHITE_WIN;
        }
    }

    public SimulatedPlayer getPlayer1() {
        return this.player1;
    }

    public SimulatedPlayer getPlayer2() {
        return this.player2;
    }

    public void simulateResult() {
        this.result = randomResultLookUp(this.player1, this.player2);
        switch (this.result) {
            case WHITE_WIN -> {
                this.player1.addGame(this.player2, 1.0f, true);
                this.player2.addGame(this.player1, 0.0f, false);
            }
            case BLACK_WIN -> {
                this.player1.addGame(this.player2, 0.0f, true);
                this.player2.addGame(this.player1, 1.0f, false);
            }
            default -> {
                this.player1.addGame(this.player2, 0.5f, true);
                this.player2.addGame(this.player1, 0.5f, false);
            }
        }
    }

    @Override
    public String toString() {
        if (this.result == ResultOfGame.WHITE_WIN) {
            return this.player1 + "1-0 " + this.player2;
        } else if (this.result == ResultOfGame.DRAW) {
            return this.player1 + "0.5-0.5 " + this.player2;
        } else if (this.result == ResultOfGame.BLACK_WIN) {
            return this.player1 + "0-1 " + this.player2;
        } else {
            return this.player1 + " 1 " + "spielfrei";
        }
    }

    private enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN
    }
}
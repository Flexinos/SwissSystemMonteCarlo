import java.util.concurrent.ThreadLocalRandom;

public final class Pairing {
    private final SimulatedPlayer whitePlayer;
    private final SimulatedPlayer blackPlayer;
    private ResultOfGame result;

    public Pairing(final SimulatedPlayer whitePlayer, final SimulatedPlayer blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public static boolean canBePaired(final SimulatedPlayer player1, final SimulatedPlayer player2) {
        if (player1.equals(player2)) {
            System.out.println("playing against oneself");
            return false;
        } else {
            return !player1.hasPlayedAgainst(player2);
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

    public SimulatedPlayer getWhitePlayer() {
        return this.whitePlayer;
    }

    public SimulatedPlayer getBlackPlayer() {
        return this.blackPlayer;
    }

    public void simulateResult() {
        this.result = randomResultLookUp(this.whitePlayer, this.blackPlayer);
        switch (this.result) {
            case WHITE_WIN -> {
                this.whitePlayer.addGame(this.blackPlayer, 1.0f, true);
                this.blackPlayer.addGame(this.whitePlayer, 0.0f, false);
            }
            case BLACK_WIN -> {
                this.whitePlayer.addGame(this.blackPlayer, 0.0f, true);
                this.blackPlayer.addGame(this.whitePlayer, 1.0f, false);
            }
            default -> {
                this.whitePlayer.addGame(this.blackPlayer, 0.5f, true);
                this.blackPlayer.addGame(this.whitePlayer, 0.5f, false);
            }
        }
    }

    @Override
    public String toString() {
        if (this.result == ResultOfGame.WHITE_WIN) {
            return this.whitePlayer + "1-0 " + this.blackPlayer;
        } else if (this.result == ResultOfGame.DRAW) {
            return this.whitePlayer + "0.5-0.5 " + this.blackPlayer;
        } else if (this.result == ResultOfGame.BLACK_WIN) {
            return this.whitePlayer + "0-1 " + this.blackPlayer;
        } else {
            return this.whitePlayer + " 1 " + "spielfrei";
        }
    }

    private enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN
    }
}
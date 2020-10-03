import java.util.concurrent.ThreadLocalRandom;

public final class Pairing {
    private final Participant whitePlayer;
    private final Participant blackPlayer;
    private ResultOfGame result;

    public Pairing(final Participant whitePlayer, final Participant blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public static boolean canBePaired(final Participant player1, final Participant player2) {
        if (player1.equals(player2)) {
            System.out.println("playing against oneself");
            return false;
        } else {
            return !player1.hasPlayedAgainst(player2);
        }
    }

    private static ResultOfGame randomResultLookUp(final Participant whitePlayer, final Participant blackPlayer) {
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

    public Participant getWhitePlayer() {
        return this.whitePlayer;
    }

    public Participant getBlackPlayer() {
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

    private enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN
    }
}
import java.util.concurrent.ThreadLocalRandom;

public final class Pairing {
    private final Participant whitePlayer;
    private final Participant blackPlayer;

    public Pairing(final Participant whitePlayer, final Participant blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public static boolean canBePaired(final Participant player1, final Participant player2) {
        if (player1.equals(player2)) {
            throw new IllegalArgumentException("Can not play against oneself!");
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

    public static char randomResultFormula(final int whiteRating, final int blackRating) {
        double chanceWhite = 1.0, chanceBlack = 0.0;
        final double averageRating = (whiteRating + blackRating) / 2.0;
        final double ratingDifference = (whiteRating - blackRating);
        final double rmq = (averageRating > 1200.0) ? ((averageRating - 1200.0) / 1200.0) : 0.0;
        final double centerLimitWhite = 40.0;
        final double wcv = 0.45 - (0.1 * rmq * rmq);
        final double lowerLimitWhite = -1492.0 + (averageRating * 0.391);
        final double upperLimitWhite = 1691.0 - (averageRating * 0.428);
        final double centerLimitBlack = -80.0;
        final double bcv = 0.46 - (0.13 * rmq * rmq);
        final double lowerLimitBlack = -1753 + (averageRating * 0.416);
        final double upperLimitBlack = 1428 - (averageRating * 0.388);
        final double wf1 = (ratingDifference - lowerLimitWhite) / (centerLimitWhite - lowerLimitWhite);
        final double wf2 = (ratingDifference - upperLimitWhite) / (centerLimitWhite - upperLimitWhite);
        final double bf1 = (ratingDifference - lowerLimitBlack) / (centerLimitBlack - lowerLimitBlack);
        final double bf2 = (ratingDifference - upperLimitBlack) / (centerLimitBlack - upperLimitBlack);
        if (ratingDifference < lowerLimitWhite) {
            chanceWhite = 0.0;
        } else if ((lowerLimitWhite <= ratingDifference) && (ratingDifference <= centerLimitWhite)) {
            chanceWhite = wcv * wf1 * wf1;
        } else if ((centerLimitWhite <= ratingDifference) && (ratingDifference <= upperLimitWhite)) {
            chanceWhite = 1.0 - ((1.0 - wcv) * wf2 * wf2);
        } else if (ratingDifference > upperLimitWhite) {
            return 'w';
        }
        if (ratingDifference < lowerLimitBlack) {
            chanceBlack = 1.0;
        } else if ((lowerLimitBlack <= ratingDifference) && (ratingDifference <= centerLimitBlack)) {
            chanceBlack = 1.0 - ((1.0 - bcv) * bf1 * bf1);
        } else if ((lowerLimitBlack <= ratingDifference) && (ratingDifference <= upperLimitBlack)) {
            chanceBlack = bcv * bf2 * bf2;
        } else if (ratingDifference > upperLimitBlack) {
            chanceBlack = 0.0;
        }
        final double r = ThreadLocalRandom.current().nextDouble();
        if (r < chanceWhite) {
            return 'w';
        }
        if (r < (chanceWhite + chanceBlack)) {
            return 'b';
        }
        return '=';
    }

    public Participant getWhitePlayer() {
        return this.whitePlayer;
    }

    public Participant getBlackPlayer() {
        return this.blackPlayer;
    }

    public void simulateResult() {
        switch (randomResultLookUp(this.whitePlayer, this.blackPlayer)) {
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
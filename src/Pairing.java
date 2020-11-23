import java.util.concurrent.ThreadLocalRandom;

public final class Pairing {
    public static boolean canBePaired(final Participant player1, final Participant player2) {
        if (player1.equals(player2)) {
            throw new IllegalArgumentException("Can not play against oneself!");
        } else {
            return !player1.hasPlayedAgainst(player2);
        }
    }

    private static ResultOfGame randomResultLookUp(final int whiteElo, final int blackElo) {
        final float randomValue = ThreadLocalRandom.current().nextFloat();
        final float[] probabilitiesArray = LookupTable.getProbabilities(whiteElo, blackElo);
        if (randomValue < probabilitiesArray[0]) {
            return ResultOfGame.BLACK_WIN;
        } else if (randomValue < (probabilitiesArray[0] + probabilitiesArray[1])) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.WHITE_WIN;
        }
    }

    public static ResultOfGame randomResultFormula(final int whiteRating, final int blackRating) {
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
            return ResultOfGame.WHITE_WIN;
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
            return ResultOfGame.WHITE_WIN;
        }
        if (r < (chanceWhite + chanceBlack)) {
            return ResultOfGame.BLACK_WIN;
        }
        return ResultOfGame.DRAW;
    }

    public static void simulateResult(Participant whitePlayer, Participant blackPlayer) {
        switch (randomResultFormula(whitePlayer.getElo(), blackPlayer.getElo())) {
            case WHITE_WIN -> {
                whitePlayer.addGame(blackPlayer.getStartingRank(), '1', 'w');
                blackPlayer.addGame(whitePlayer.getStartingRank(), '0', 'b');
            }
            case BLACK_WIN -> {
                whitePlayer.addGame(blackPlayer.getStartingRank(), '0', 'w');
                blackPlayer.addGame(whitePlayer.getStartingRank(), '1', 'b');
            }
            default -> {
                whitePlayer.addGame(blackPlayer.getStartingRank(), '=', 'w');
                blackPlayer.addGame(whitePlayer.getStartingRank(), '=', 'b');
            }
        }
    }

    private enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN
    }
}
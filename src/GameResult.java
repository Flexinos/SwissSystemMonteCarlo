import java.util.Random;

public class GameResult {
    private static final Random random = new Random();

    public static ResultOfGame randomResult(SimulatedPlayer whitePlayer, SimulatedPlayer blackPlayer) {
        double eloDiff = whitePlayer.getElo() - blackPlayer.getElo();
        double whiteScore = 1 / (1 + Math.pow(10, -eloDiff / 400));
        double underdogScore;
        if (whiteScore > 0.5) {
            underdogScore = (1 - whiteScore) * 0.9;
        } else {
            underdogScore = whiteScore * 1.1;
        }

        double drawChance = underdogScore;
        double whiteWinChance = whiteScore - drawChance * 0.5;

        float randomValue = random.nextFloat();
        if (randomValue < whiteWinChance) {
            return ResultOfGame.WHITE_WIN;
        } else if (randomValue < whiteWinChance + drawChance) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.BLACK_WIN;
        }
    }

    public static ResultOfGame randomResultLookUp(SimulatedPlayer whitePlayer, SimulatedPlayer blackPlayer) {
        float randomValue = random.nextFloat();
        float[] probabilitiesArray = LookUpTable.getProbabilities(whitePlayer.getElo(), blackPlayer.getElo());
        if (randomValue < probabilitiesArray[0]) {
            return ResultOfGame.BLACK_WIN;
        } else if (randomValue < probabilitiesArray[0] + probabilitiesArray[1]) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.WHITE_WIN;
        }
    }

    public enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN, BYE
    }
}

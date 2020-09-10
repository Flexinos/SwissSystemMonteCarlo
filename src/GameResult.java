import java.util.Random;

public class GameResult {
    private static final Random random = new Random();

    public static ResultOfGame randomResult(SimulatedPlayer whitePlayer, SimulatedPlayer blackPlayer) {
        double eloDiff = whitePlayer.getParticipant().getElo() - blackPlayer.getParticipant().getElo();
        double whiteScore = 1 / (1 + Math.pow(10, -eloDiff / 400));
        double underdogScore;
        // give white small edge. completely arbitrary
        if (whiteScore > 0.5) {
            underdogScore = (1 - whiteScore) * 0.9;
        } else {
            underdogScore = whiteScore * 1.1;
        }

        double drawChance = underdogScore;
        double whiteWinChance = whiteScore - drawChance * 0.5;

        double randomValue = random.nextFloat();
        if (randomValue < whiteWinChance) {
            return ResultOfGame.WHITE_WIN;
        } else if (randomValue < whiteWinChance + drawChance) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.BLACK_WIN;
        }
    }

    public enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN, BYE
    }
}

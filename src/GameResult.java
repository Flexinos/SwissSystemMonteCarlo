import java.util.Random;

public class GameResult {
    private static final Random random = new Random();

    public static ResultOfGame randomResult(SimulatedPlayer WhitePlayer, SimulatedPlayer BlackPlayer) {
        double eloDiff = WhitePlayer.getParticipant().getElo() - BlackPlayer.getParticipant().getElo();
        double WhiteScore = 1 / (1 + Math.pow(10, -eloDiff / 400));
        double underdogScore;
        // give white small edge. completely arbitrary
        if (WhiteScore > 0.5) {
            underdogScore = (1 - WhiteScore) * 0.9;
        } else {
            underdogScore = WhiteScore * 1.1;
        }

        double drawChance = underdogScore;
        double WhiteWinChance = WhiteScore - drawChance * 0.5;

        double randomValue = random.nextDouble();
        if (randomValue < WhiteWinChance) {
            return ResultOfGame.WHITE_WIN;
        } else if (randomValue < WhiteWinChance + drawChance) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.BLACK_WIN;
        }
    }

    public enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN
    }
}

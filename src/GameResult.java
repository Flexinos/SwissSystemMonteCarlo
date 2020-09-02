import java.security.SecureRandom;

public class GameResult {
    private static final SecureRandom random = new SecureRandom();
    //static Map<Integer, ResultOfGame> indexToResult = Map.of(0, ResultOfGame.WHITE_WIN, 1, ResultOfGame.DRAW, 2, ResultOfGame.BLACK_WIN);

    public static ResultOfGame randomResult(SimulatedPlayer p1, SimulatedPlayer p2) {
        double eloDiff = p1.getParticipant().getElo() - p2.getParticipant().getElo();
        double p1Score = 1 / (1 + Math.pow(10, -eloDiff / 400));
        double underdogScore;
        if (p1Score > 0.5) {
            underdogScore = 1 - p1Score;
        } else {
            underdogScore = p1Score;
        }
        double drawChance = underdogScore;
        double p1WinChance = p1Score - drawChance * 0.5;
        //double p2WinChance = 1 - p1Score - drawChance * 0.5;
        double randomValue = random.nextDouble();
        if (randomValue < p1WinChance) {
            return ResultOfGame.WHITE_WIN;
        } else if (randomValue < p1WinChance + drawChance) {
            return ResultOfGame.DRAW;
        } else {
            return ResultOfGame.BLACK_WIN;
        }
    }

    public enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN
    }
}

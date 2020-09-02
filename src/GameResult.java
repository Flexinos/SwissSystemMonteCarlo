import java.security.SecureRandom;
import java.util.Map;

public class GameResult {
    private static final SecureRandom random = new SecureRandom();
    static Map<Integer, ResultOfGame> indexToResult = Map.of(0, ResultOfGame.WHITE_WIN, 1, ResultOfGame.DRAW, 2, ResultOfGame.BLACK_WIN);

    public static ResultOfGame randomResult(SimulatedPlayer p1, SimulatedPlayer p2) {
        int x = random.nextInt(3);
        return indexToResult.get(x);
    }


    public enum ResultOfGame {
        WHITE_WIN, DRAW, BLACK_WIN
    }
}

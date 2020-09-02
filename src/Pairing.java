public class Pairing {
    private final int board;
    private final SimulatedPlayer player1;
    private final SimulatedPlayer player2;
    private GameResult.ResultOfGame result;

    public Pairing(PossiblePairing pairing) {
        this.board = pairing.getBoard();
        this.player1 = pairing.getPlayer1();
        this.player2 = pairing.getPlayer2();
        simulateResult();
    }

    public static boolean pairingAllowed(SimulatedPlayer player1, SimulatedPlayer player2) {
        if (player1.equals(player2)) {
            /*
            System.out.println();
            System.out.println(player1.getParticipant().getName());
            System.out.println(player2.getParticipant().getName());
            System.out.println();
            System.out.println("fail bruno");
             */
            System.out.println("playing against oneself");
            return false;
        } else {
            if (player1.getPastOpponents().contains(player2)) {
                //System.out.println(player1.getParticipant().getName() + " playing against past opponent: " + player2.getParticipant().getName());
                return false;
            }
            return true;
        }
    }

    public int getBoard() {
        return board;
    }

    public SimulatedPlayer getPlayer1() {
        return player1;
    }

    public SimulatedPlayer getPlayer2() {
        return player2;
    }

    private void simulateResult() {
        //todo fix Byes
        System.out.print("Board " + getBoard() + ": ");
        if (player1.getParticipant().getName().equals("BYE")) {
            result = GameResult.ResultOfGame.BLACK_WIN;
        } else if (player2.getParticipant().getName().equals("BYE")) {
            result = GameResult.ResultOfGame.WHITE_WIN;
        } else {
            result = GameResult.randomResult(player1, player2);
            if (result.equals(GameResult.ResultOfGame.WHITE_WIN)) {
                player1.addGame(player2, 1);
                player2.addGame(player1, 0);
                System.out.println(player1.getParticipant().getName() + " won against " + player2.getParticipant().getName());
            } else if (result.equals(GameResult.ResultOfGame.BLACK_WIN)) {
                player1.addGame(player2, 0);
                player2.addGame(player1, 1);
                System.out.println(player1.getParticipant().getName() + " lost against " + player2.getParticipant().getName());
            } else {
                player1.addGame(player2, 0.5);
                player2.addGame(player1, 0.5);
                System.out.println(player1.getParticipant().getName() + " drew against " + player2.getParticipant().getName());
            }
        }
    }

}

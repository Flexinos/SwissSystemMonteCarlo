public class OpponentWrapper {
    final private int opponentStartingRank;
    private Character result;
    private Character color;

    public OpponentWrapper(int opponentStartingRank) {
        this.opponentStartingRank = opponentStartingRank;
    }

    public void setResult(Character result) {
        this.result = result;
    }

    public void setColor(Character color) {
        this.color = color;
    }

    public int getOpponentStartingRank() {
        return opponentStartingRank;
    }

    public Character getResult() {
        return result;
    }

    public Character getColor() {
        return color;
    }
}

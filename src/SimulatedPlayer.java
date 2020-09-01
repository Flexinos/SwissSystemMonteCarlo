public class SimulatedPlayer {
    private final Participant participant;
    private int score;

    public SimulatedPlayer(Participant participant) {
        this.participant = participant;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int compareTo(SimulatedPlayer p2) {
        if (this.getScore() > p2.getScore()) {
            return 1;
        } else if (this.getScore() < p2.getScore()) {
            return -1;
        } else {
            if (this.participant.getElo() > p2.participant.getElo()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}

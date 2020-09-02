import java.util.ArrayList;

public class SimulatedPlayer {
    private final Participant participant;
    private double score;
    private double buchholz;
    private ArrayList<SimulatedPlayer> pastOpponents = new ArrayList<>();

    public SimulatedPlayer(Participant participant) {
        this.participant = participant;
    }

    public double getScore() {
        return score;
    }

    public void addGame(SimulatedPlayer opponent, double result) {
        pastOpponents.add(opponent);
        score += result;
    }

    public double getBuchholz() {
        return buchholz;
    }

    public void setBuchholz(double buchholz) {
        this.buchholz = buchholz;
    }

    public ArrayList<SimulatedPlayer> getPastOpponents() {
        return pastOpponents;
    }

    public Participant getParticipant() {
        return participant;
    }

    public int compareToByScoreThenElo(SimulatedPlayer p2) {
        return -1 * Double.compare(this.getScore(), p2.getScore());
        /*else {
            if (this.participant.getElo() > p2.participant.getElo()) {
                return 1;
            } else {
                return -1;
            }
        }
        */
    }

    public int compareToByTieBreak(SimulatedPlayer p2) {
        /*
        if (this.getBuchholz() > p2.getBuchholz()) {
            return 1;
        } else {
            return -1;
        }
        */
        return Double.compare(this.getScore(), p2.getScore());
    }
}

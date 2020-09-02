import java.util.ArrayList;

public class SimulatedPlayer {
    private final Participant participant;
    private double score;
    private double buchholz;
    private final ArrayList<SimulatedPlayer> pastOpponents = new ArrayList<>();

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

    public static int compareToByScoreThenElo(SimulatedPlayer p1, SimulatedPlayer p2) {
        if (p1.getScore() > p2.getScore()) {
            return -1;
        } else if (p1.getScore() < p2.getScore()) {
            return 1;
        }
        return Double.compare(p2.getParticipant().getElo(), p1.getParticipant().getElo());

    }

    public double getBuchholz() {
        return buchholz;
    }

    public ArrayList<SimulatedPlayer> getPastOpponents() {
        return pastOpponents;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void updateBuchholz() {
        buchholz = 0;
        for (SimulatedPlayer opponent : pastOpponents) {
            buchholz += opponent.getScore();
        }
    }

    public static int compareToByTieBreak(SimulatedPlayer p1, SimulatedPlayer p2) {
        if (p1.getScore() < p2.getScore()) {
            return 1;
        } else if (p1.getScore() > p2.getScore()) {
            return -1;
        } else {
            return Double.compare(p2.getBuchholz(), p1.getBuchholz());
        }
    }
}

import java.util.ArrayList;
import java.util.List;

public class SimulatedPlayer {
    private final Participant participant;
    private SimulatedTournament simulatedTournament = null;
    private double score;
    private double buchholz;
    private final List<SimulatedPlayer> pastOpponents; //todo optimize
    private boolean receivedBye = false;
    private int colorDifference = 0;

    public SimulatedPlayer(Participant participant) {
        this.participant = participant;
        this.pastOpponents = new ArrayList<>(Main.numberOfRounds);
    }

    public SimulatedPlayer(Participant participant, SimulatedTournament simulatedTournament) {
        this.participant = participant;
        this.simulatedTournament = simulatedTournament;
        this.pastOpponents = new ArrayList<>(Main.numberOfRounds);
    }

    public double getScore() {
        return score;
    }

    public void addGame(SimulatedPlayer opponent, double result) {
        pastOpponents.add(opponent);
        simulatedTournament.addGame(this, opponent);
        score += result;
    }

    public void addGame(SimulatedPlayer opponent, double result, boolean isWhite) {
        pastOpponents.add(opponent);
        simulatedTournament.addGame(this, opponent);
        score += result;
        if (isWhite) {
            colorDifference++;
        } else {
            colorDifference--;
        }
    }

    public static int compareToByScoreThenElo(SimulatedPlayer p1, SimulatedPlayer p2) {
        if (p1.getScore() > p2.getScore()) {
            return -1;
        } else if (p1.getScore() < p2.getScore()) {
            return 1;
        }
        return Double.compare(p2.getParticipant().getElo(), p1.getParticipant().getElo());
    }

    public SimulatedTournament getSimulatedTournament() {
        return simulatedTournament;
    }

    public double getBuchholz() {
        return buchholz;
    }

    public List<SimulatedPlayer> getPastOpponents() {
        return pastOpponents;
    }

    public Participant getParticipant() {
        return participant;
    }

    public boolean hasReceivedBye() {
        return receivedBye;
    }

    public void setReceivedBye(boolean receivedBye) {
        this.receivedBye = receivedBye;
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

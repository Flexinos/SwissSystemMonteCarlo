import java.util.ArrayList;
import java.util.List;

public class SimulatedPlayer {
    private final Participant participant;
    private SimulatedTournament simulatedTournament = null;
    private double score;
    private final List<SimulatedPlayer> pastOpponents;
    private double tieBreak1;
    private double tieBreak2;
    private double tieBreak3;
    private boolean receivedBye = false;
    private int colorDifference = 0;

    public SimulatedPlayer(Participant participant) {
        this.participant = participant;
        this.pastOpponents = new ArrayList<>(Main.numberOfRounds);
    }

    public SimulatedPlayer(Participant participant, SimulatedTournament simulatedTournament) {
        this.participant = participant;
        this.score = participant.getScore();
        this.tieBreak1 = participant.getTieBreak1();
        this.tieBreak2 = participant.getTieBreak2();
        this.tieBreak3 = participant.getTieBreak3();
        this.simulatedTournament = simulatedTournament;
        this.pastOpponents = new ArrayList<>(Main.numberOfRounds);
    }

    public double getScore() {
        return score;
    }

    public static int compareToByTieBreak(SimulatedPlayer p1, SimulatedPlayer p2) {
        if (p1.getScore() < p2.getScore()) {
            return 1;
        } else if (p1.getScore() > p2.getScore()) {
            return -1;
        } else {
            return Double.compare(p2.getTieBreak1(), p1.getTieBreak1());
        }
    }

    public double getTieBreak1() {
        return tieBreak1;
    }

    public double getTieBreak2() {
        return tieBreak2;
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

    public double getTieBreak3() {
        return tieBreak3;
    }

    public void updateTieBreaks() {
        tieBreak1 = 0;
        for (SimulatedPlayer opponent : pastOpponents) {
            tieBreak1 += opponent.getScore();
        }
    }
}

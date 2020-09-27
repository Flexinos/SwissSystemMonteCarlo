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

    public static int compareToByScoreTieBreak(SimulatedPlayer p1, SimulatedPlayer p2) {
        int result = -Double.compare(p1.getScore(), p2.getScore());
        if (result != 0) {
            return result;
        }
        result = -Double.compare(p1.getTieBreak1(), p2.getTieBreak1());
        if (result != 0) {
            return result;
        }
        result = -Double.compare(p1.getTieBreak2(), p2.getTieBreak2());
        if (result != 0) {
            return result;
        }
        result = -Double.compare(p1.getTieBreak3(), p2.getTieBreak3());
        return result;
    }

    public static int compareToByScoreThenElo(SimulatedPlayer p1, SimulatedPlayer p2) {
        int result = -Double.compare(p1.getScore(), p2.getScore());
        return result != 0 ? result : -Double.compare(p1.getElo(), p2.getElo());
    }

    public double getScore() {
        return score;
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

    public SimulatedTournament getSimulatedTournament() {
        return simulatedTournament;
    }

    public int getElo() {
        return participant.getElo();
    }

    public int getStartingRank() {
        return participant.getStartingRank();
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

    public void updateTieBreaks() {
        tieBreak1 = 0;
        for (SimulatedPlayer opponent : pastOpponents) {
            tieBreak1 += opponent.getScore();
        }
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

    public double getTieBreak3() {
        return tieBreak3;
    }

    @Override
    public String toString() {
        return "Name: " + participant.getName() +
                "\tElo: " + participant.getElo() +
                "\tscore: " + score +
                "\ttieBreak1: " + tieBreak1 +
                "\ttieBreak2: " + tieBreak2 +
                "\ttieBreak3: " + tieBreak3;
    }
}

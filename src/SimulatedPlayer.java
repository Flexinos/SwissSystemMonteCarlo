import java.util.ArrayList;
import java.util.List;

public class SimulatedPlayer {
    private final Participant participant;
    private SimulatedTournament simulatedTournament = null;
    private final List<SimulatedPlayer> pastOpponents;
    private float score;
    private float tieBreak1;
    private float buchholz;
    private float buchholzCutOne;
    private float sonnenbornBerger;
    private float averageEloOpponents;
    private boolean receivedBye = false;
    private int colorDifference = 0;

    public SimulatedPlayer(Participant participant) {
        this.participant = participant;
        this.pastOpponents = new ArrayList<>(Main.numberOfRounds);
    }

    public SimulatedPlayer(Participant participant, SimulatedTournament simulatedTournament) {
        this.participant = participant;
        this.score = participant.getScore();
        this.buchholz = participant.getBuchholz();
        this.buchholzCutOne = participant.getBuchholzCutOne();
        this.sonnenbornBerger = participant.getSonnenbornBerger();
        this.averageEloOpponents = participant.getSonnenbornBerger();
        this.simulatedTournament = simulatedTournament;
        this.pastOpponents = new ArrayList<>(Main.numberOfRounds);
    }

    public static int compareToByScoreThenTieBreak(SimulatedPlayer p1, SimulatedPlayer p2) {
        int result = 0;
        for (int i = 0; i < Tournament.rankingOrder.size(); i++) {
            if (Tournament.rankingOrder.get(i) == Tournament.rankingBy.SCORE) {
                result = -Float.compare(p1.getScore(), p2.getScore());
            } else if (Tournament.rankingOrder.get(i) == Tournament.rankingBy.BUCHHOLZ) {
                result = -Float.compare(p1.getBuchholz(), p2.getBuchholz());
            } else if (Tournament.rankingOrder.get(i) == Tournament.rankingBy.BUCHHOLZCUTONE) {
                result = -Float.compare(p1.getBuchholz(), p2.getBuchholz());
            } else if (Tournament.rankingOrder.get(i) == Tournament.rankingBy.AVERAGEELOOPPONENTS) {
                result = -Float.compare(p1.getBuchholz(), p2.getBuchholz());
            } else if (Tournament.rankingOrder.get(i) == Tournament.rankingBy.SONNENBORNBERGER) {
                result = -Float.compare(p1.getBuchholz(), p2.getBuchholz());
            }
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public static int compareToByScoreThenElo(SimulatedPlayer p1, SimulatedPlayer p2) {
        int result = -Double.compare(p1.getScore(), p2.getScore());
        return result != 0 ? result : -Double.compare(p1.getElo(), p2.getElo());
    }

    public static float getBuchholz(List<SimulatedPlayer> opponents) {
        float buchholz = 0;
        for (SimulatedPlayer opponent : opponents) {
            buchholz += opponent.getScore();
        }
        return buchholz;
    }

    public static float getBuchholzCutOne(List<SimulatedPlayer> opponents) {
        if (opponents.size() == 0) {
            return 0;
        }
        float buchholz = 0;
        float lowestScore = Float.MAX_VALUE;
        for (SimulatedPlayer opponent : opponents) {
            if (opponent.getScore() <= lowestScore) {
                lowestScore = opponent.getScore();
            }
            buchholz += opponent.getScore();
        }
        return buchholz - lowestScore;
    }

    public float getScore() {
        return score;
    }

    public float getBuchholz() {
        return buchholz;
    }

    public float getBuchholzCutOne() {
        return buchholzCutOne;
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

    public float getSonnenbornBerger() {
        return sonnenbornBerger;
    }

    public float getAverageEloOpponents() {
        return averageEloOpponents;
    }

    @Override
    public String toString() {
        return "SimulatedPlayer{" +
                "score=" + score +
                ", buchholzCutOne=" + buchholzCutOne +
                ", buchholz=" + buchholz +
                ", averageEloOpponents=" + averageEloOpponents +
                '}';
    }
}

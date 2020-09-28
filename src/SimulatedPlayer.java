import java.util.HashMap;
import java.util.Map;

public class SimulatedPlayer {
    private final Participant participant;
    private final Map<SimulatedPlayer, Float> pastGames;
    private SimulatedTournament simulatedTournament = null;
    private float score;
    private float buchholz;
    private float buchholzCutOne;
    private float sonnenbornBerger;
    private float averageEloOpponents;
    private boolean receivedBye = false;
    private int colorDifference = 0;

    public SimulatedPlayer(Participant participant) {
        this.participant = participant;
        this.pastGames = new HashMap<>();
    }

    public SimulatedPlayer(Participant participant, SimulatedTournament simulatedTournament) {
        this.participant = participant;
        this.score = participant.getScore();
        this.buchholz = participant.getBuchholz();
        this.buchholzCutOne = participant.getBuchholzCutOne();
        this.sonnenbornBerger = participant.getSonnenbornBerger();
        this.averageEloOpponents = participant.getSonnenbornBerger();
        this.simulatedTournament = simulatedTournament;
        this.pastGames = new HashMap<>(participant.getPastGames());
    }

    public static int compareToByScoreThenTieBreak(SimulatedPlayer p1, SimulatedPlayer p2) {
        for (int i = 0; i < Tournament.rankingOrder.size(); i++) {
            int result = switch (Tournament.rankingOrder.get(i)) {
                case SCORE -> -Float.compare(p1.getScore(), p2.getScore());
                case BUCHHOLZ -> -Float.compare(p1.getBuchholz(), p2.getBuchholz());
                case BUCHHOLZCUTONE -> -Float.compare(p1.getBuchholzCutOne(), p2.getBuchholzCutOne());
                case AVERAGEELOOPPONENTS -> -Float.compare(p1.getAverageEloOpponents(), p2.getAverageEloOpponents());
                case SONNENBORNBERGER -> -Float.compare(p1.getSonnenbornBerger(), p2.getScore());
            };
            if (result != 0) {
                return result;
            }
        }
        return -Double.compare(p1.getElo(), p2.getElo());
    }

    public static int compareToByScoreThenElo(SimulatedPlayer p1, SimulatedPlayer p2) {
        int result = -Double.compare(p1.getScore(), p2.getScore());
        return result != 0 ? result : -Double.compare(p1.getElo(), p2.getElo());
    }

    private float calculateBuchholz() {
        float buchholz = 0;
        for (SimulatedPlayer opponent : pastGames.keySet()) {
            buchholz += opponent.getScore();
        }
        return buchholz;
    }

    private float calculateBuchholzCutOne() {
        if (pastGames.size() == 0) {
            return 0;
        }
        float buchholz = 0;
        float lowestScore = Float.MAX_VALUE;
        for (SimulatedPlayer opponent : pastGames.keySet()) {
            if (opponent.getScore() <= lowestScore) {
                lowestScore = opponent.getScore();
            }
            buchholz += opponent.getScore();
        }
        return buchholz - lowestScore;
    }

    private float calculateSonnenbornBerger() {
        float sonnenbornBerger = 0;
        for (SimulatedPlayer opponent : pastGames.keySet()) {
            sonnenbornBerger += opponent.getScore() * pastGames.get(opponent);
        }
        return sonnenbornBerger;
    }

    private float calculateAverageEloOpponents() {
        float sum = 0;
        for (SimulatedPlayer opponent : pastGames.keySet()) {
            sum += opponent.getElo();
        }
        return sum / pastGames.size();
    }

    public void updateTiebreaks() {
        buchholz = calculateBuchholz();
        buchholzCutOne = calculateBuchholzCutOne();
        averageEloOpponents = calculateAverageEloOpponents();
        sonnenbornBerger = calculateSonnenbornBerger();
    }

    public void addGame(SimulatedPlayer opponent, double result) {
        pastGames.put(opponent, (float) result);
        simulatedTournament.addGame(this, opponent);
        score += result;
    }

    public void addGame(SimulatedPlayer opponent, double result, boolean isWhite) {
        pastGames.put(opponent, (float) result);
        simulatedTournament.addGame(this, opponent);
        score += result;
        if (isWhite) {
            colorDifference++;
        } else {
            colorDifference--;
        }
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

    public Map<SimulatedPlayer, Float> getPastGames() {
        return pastGames;
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

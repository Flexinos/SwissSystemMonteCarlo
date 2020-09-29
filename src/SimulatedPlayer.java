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

    public SimulatedPlayer(final Participant participant) {
        this.participant = participant;
        this.pastGames = new HashMap<>();
    }

    public SimulatedPlayer(final Participant participant, final SimulatedTournament simulatedTournament) {
        this.participant = participant;
        this.score = participant.getScore();
        this.buchholz = participant.getBuchholz();
        this.buchholzCutOne = participant.getBuchholzCutOne();
        this.sonnenbornBerger = participant.getSonnenbornBerger();
        this.averageEloOpponents = participant.getSonnenbornBerger();
        this.simulatedTournament = simulatedTournament;
        this.pastGames = new HashMap<>(participant.getPastGames());
    }

    public static int compareToByScoreThenTieBreak(final SimulatedPlayer p1, final SimulatedPlayer p2) {
        for (int i = 0; i < Tournament.rankingOrder.size(); i++) {
            final int result = switch (Tournament.rankingOrder.get(i)) {
                case SCORE -> -Float.compare(p1.score, p2.score);
                case BUCHHOLZ -> -Float.compare(p1.buchholz, p2.buchholz);
                case BUCHHOLZCUTONE -> -Float.compare(p1.buchholzCutOne, p2.buchholzCutOne);
                case AVERAGEELOOPPONENTS -> -Float.compare(p1.averageEloOpponents, p2.averageEloOpponents);
                case SONNENBORNBERGER -> -Float.compare(p1.sonnenbornBerger, p2.score);
            };
            if (result != 0) {
                return result;
            }
        }
        return -Integer.compare(p1.getElo(), p2.getElo());
    }

    public static int compareToByScoreThenElo(final SimulatedPlayer p1, final SimulatedPlayer p2) {
        final int result = -Float.compare(p1.score, p2.score);
        return result != 0 ? result : -Integer.compare(p1.getElo(), p2.getElo());
    }

    private float calculateBuchholz() {
        float buchholz = 0;
        for (final SimulatedPlayer opponent : pastGames.keySet()) {
            buchholz += opponent.score;
        }
        return buchholz;
    }

    private float calculateBuchholzCutOne() {
        if (pastGames.isEmpty()) {
            return 0;
        }
        float buchholz = 0;
        float lowestScore = Float.MAX_VALUE;
        for (final SimulatedPlayer opponent : pastGames.keySet()) {
            if (opponent.score <= lowestScore) {
                lowestScore = opponent.score;
            }
            buchholz += opponent.score;
        }
        return buchholz - lowestScore;
    }

    private float calculateSonnenbornBerger() {
        float sonnenbornBerger = 0;
        for (final Map.Entry<SimulatedPlayer, Float> entry : pastGames.entrySet()) {
            sonnenbornBerger += entry.getKey().score * entry.getValue();
        }
        return sonnenbornBerger;
    }

    private float calculateAverageEloOpponents() {
        float sum = 0;
        for (final SimulatedPlayer opponent : pastGames.keySet()) {
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

    public void addGame(final SimulatedPlayer opponent, final double result) {
        pastGames.put(opponent, (float) result);
        simulatedTournament.addGame(this, opponent);
        score += result;
    }

    public void addGame(final SimulatedPlayer opponent, final double result, final boolean isWhite) {
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

    public void setReceivedBye(final boolean receivedBye) {
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

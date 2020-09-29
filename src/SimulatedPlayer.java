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

    public int compareToByScoreThenTieBreak(final SimulatedPlayer p2) {
        for (int i = 0; i < Tournament.rankingOrder.size(); i++) {
            final int result = switch (Tournament.rankingOrder.get(i)) {
                case SCORE -> -Float.compare(score, p2.score);
                case BUCHHOLZ -> -Float.compare(buchholz, p2.buchholz);
                case BUCHHOLZCUTONE -> -Float.compare(buchholzCutOne, p2.buchholzCutOne);
                case AVERAGEELOOPPONENTS -> -Float.compare(averageEloOpponents, p2.averageEloOpponents);
                case SONNENBORNBERGER -> -Float.compare(sonnenbornBerger, p2.score);
            };
            if (result != 0) {
                return result;
            }
        }
        return -Integer.compare(getElo(), p2.getElo());
    }

    public int compareToByScoreThenElo(final SimulatedPlayer p2) {
        final int result = -Float.compare(score, p2.score);
        return result != 0 ? result : -Integer.compare(getElo(), p2.getElo());
    }

    private void updateBuchholz() {
        float tmpSum = 0f;
        for (final SimulatedPlayer opponent : pastGames.keySet()) {
            tmpSum += opponent.score;
        }
        buchholz = tmpSum;
    }

    private void updateBuchholzCutOne() {
        if (pastGames.isEmpty()) {
            buchholzCutOne = 0f;
        }
        float tmpBuchholz = 0f;
        float lowestScore = Float.MAX_VALUE;
        for (final SimulatedPlayer opponent : pastGames.keySet()) {
            if (opponent.score <= lowestScore) {
                lowestScore = opponent.score;
            }
            tmpBuchholz += opponent.score;
        }
        buchholzCutOne = tmpBuchholz - lowestScore;
    }

    private void updateSonnenbornBerger() {
        float tmpSum = 0.0f;
        for (final Map.Entry<SimulatedPlayer, Float> entry : pastGames.entrySet()) {
            tmpSum += entry.getKey().score * entry.getValue();
        }
        sonnenbornBerger = tmpSum;
    }

    private void updateAverageEloOpponents() {
        float sum = 0.0f;
        for (final SimulatedPlayer opponent : pastGames.keySet()) {
            sum += opponent.getElo();
        }
        averageEloOpponents = sum / pastGames.size();
    }

    public void updateTiebreaks() {
        updateBuchholz();
        updateBuchholzCutOne();
        updateAverageEloOpponents();
        updateSonnenbornBerger();
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

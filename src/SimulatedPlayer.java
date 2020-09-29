import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class SimulatedPlayer {
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

    // todo replace with static factory to return reference to BYE constant in Tournament
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
        this.pastGames = new HashMap<>(participant.getPastResults());
    }

    public boolean playedAgainst(final SimulatedPlayer simulatedPlayer) {
        return this.pastGames.containsKey(simulatedPlayer);
    }

    public int compareToByScoreThenTieBreak(final SimulatedPlayer p2) {
        for (int i = 0; i < Tournament.rankingOrder.size(); i++) {
            final int result = switch (Tournament.rankingOrder.get(i)) {
                case SCORE -> -Float.compare(this.score, p2.score);
                case BUCHHOLZ -> -Float.compare(this.buchholz, p2.buchholz);
                case BUCHHOLZCUTONE -> -Float.compare(this.buchholzCutOne, p2.buchholzCutOne);
                case AVERAGEELOOPPONENTS -> -Float.compare(this.averageEloOpponents, p2.averageEloOpponents);
                case SONNENBORNBERGER -> -Float.compare(this.sonnenbornBerger, p2.score);
            };
            if (result != 0) {
                return result;
            }
        }
        return -Integer.compare(getElo(), p2.getElo());
    }

    public int compareToByScoreThenElo(final SimulatedPlayer p2) {
        final int result = -Float.compare(this.score, p2.score);
        return (result != 0) ? result : -Integer.compare(getElo(), p2.getElo());
    }

    private void updateBuchholz() {
        float tmpSum = 0.0f;
        for (final SimulatedPlayer opponent : this.pastGames.keySet()) {
            tmpSum += opponent.score;
        }
        this.buchholz = tmpSum;
    }

    private void updateBuchholzCutOne() {
        if (this.pastGames.isEmpty()) {
            this.buchholzCutOne = 0.0f;
        }
        float tmpBuchholz = 0.0f;
        float lowestScore = Float.MAX_VALUE;
        for (final SimulatedPlayer opponent : this.pastGames.keySet()) {
            if (opponent.score <= lowestScore) {
                lowestScore = opponent.score;
            }
            tmpBuchholz += opponent.score;
        }
        this.buchholzCutOne = tmpBuchholz - lowestScore;
    }

    private void updateSonnenbornBerger() {
        float tmpSum = 0.0f;
        for (final Entry<SimulatedPlayer, Float> entry : this.pastGames.entrySet()) {
            tmpSum += entry.getKey().score * entry.getValue();
        }
        this.sonnenbornBerger = tmpSum;
    }

    private void updateAverageEloOpponents() {
        int sum = 0;
        for (final SimulatedPlayer opponent : this.pastGames.keySet()) {
            sum += opponent.getElo();
        }
        this.averageEloOpponents = (float) sum / (float) this.pastGames.size();
    }

    public void updateTiebreaks() {
        updateBuchholz();
        updateBuchholzCutOne();
        updateAverageEloOpponents();
        updateSonnenbornBerger();
    }

    public void addGame(final SimulatedPlayer opponent, final float result) {
        this.pastGames.put(opponent, result);
        this.simulatedTournament.addGame(this, opponent);
        this.score += result;
    }

    public void addGame(final SimulatedPlayer opponent, final float result, final boolean isWhite) {
        this.pastGames.put(opponent, result);
        this.simulatedTournament.addGame(this, opponent);
        this.score += result;
        if (isWhite) {
            this.colorDifference++;
        } else {
            this.colorDifference--;
        }
    }

    public void addRankToTable(final int rank) {
        this.participant.addRankToTable(rank);
    }

    public float getScore() {
        return this.score;
    }

    public float getBuchholz() {
        return this.buchholz;
    }

    public float getBuchholzCutOne() {
        return this.buchholzCutOne;
    }

    public SimulatedTournament getSimulatedTournament() {
        return this.simulatedTournament;
    }

    public int getElo() {
        return this.participant.getElo();
    }

    public int getStartingRank() {
        return this.participant.getStartingRank();
    }

    public Map<SimulatedPlayer, Float> getPastGames() {
        return Collections.unmodifiableMap(this.pastGames);
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public boolean hasReceivedBye() {
        return this.receivedBye;
    }

    public void setReceivedBye(final boolean receivedBye) {
        this.receivedBye = receivedBye;
    }

    public float getSonnenbornBerger() {
        return this.sonnenbornBerger;
    }

    public float getAverageEloOpponents() {
        return this.averageEloOpponents;
    }

    @Override
    public String toString() {
        return "SimulatedPlayer{" +
                "score=" + this.score +
                ", buchholzCutOne=" + this.buchholzCutOne +
                ", buchholz=" + this.buchholz +
                ", averageEloOpponents=" + this.averageEloOpponents +
                '}';
    }
}

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class SimulatedPlayer {
    private final Participant participant;
    private final Map<SimulatedPlayer, Float> pastResults;
    private float score;
    private float buchholz;
    private float buchholzCutOne;
    private float sonnenbornBerger;
    private float averageEloOpponents;
    private float performanceRating;
    private boolean hasReceivedBye;
    private int colorDifference = 0;

    public SimulatedPlayer(final Participant participant) {
        this.participant = participant;
        this.pastResults = new HashMap<>(participant.getPastResults());
        this.hasReceivedBye = participant.hasReceivedBye();
        updateScores();
    }

    public boolean hasPlayedAgainst(final SimulatedPlayer simulatedPlayer) {
        return this.pastResults.containsKey(simulatedPlayer);
    }

    public int compareToByScoreThenTieBreak(final SimulatedPlayer p2) {
        for (int i = 0; i < Tournament.getRankingOrder().size(); i++) {
            final int result = switch (Tournament.getRankingOrder().get(i)) {
                case SCORE -> Float.compare(p2.score, this.score);
                case BUCHHOLZ -> Float.compare(p2.buchholz, this.buchholz);
                case BUCHHOLZ_CUT_ONE -> Float.compare(p2.buchholzCutOne, this.buchholzCutOne);
                case AVERAGE_ELO_OPPONENTS -> Float.compare(p2.averageEloOpponents, this.averageEloOpponents);
                case SONNENBORN_BERGER -> Float.compare(p2.score, this.sonnenbornBerger);
            };
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(p2.getElo(), getElo());
    }

    public int compareToByScoreThenElo(final SimulatedPlayer p2) {
        final int result = Float.compare(p2.score, this.score);
        return (result != 0) ? result : Integer.compare(p2.getElo(), getElo());
    }

    private void calculatePerformance() {
        boolean inverted = false;
        float percentage = this.score / this.pastResults.size();
        if (percentage < 0.5) {
            percentage = 1 - percentage;
            inverted = true;
        }
        final int offset = switch ((int) (percentage * 100)) {
            case 100 -> 800;
            case 99 -> 677;
            case 98 -> 589;
            case 97 -> 538;
            case 96 -> 501;
            case 95 -> 470;
            case 94 -> 444;
            case 93 -> 422;
            case 92 -> 401;
            case 91 -> 383;
            case 90 -> 366;
            case 89 -> 351;
            case 88 -> 336;
            case 87 -> 322;
            case 86 -> 309;
            case 85 -> 296;
            case 84 -> 284;
            case 83 -> 273;
            case 82 -> 262;
            case 81 -> 251;
            case 80 -> 240;
            case 79 -> 230;
            case 78 -> 220;
            case 77 -> 211;
            case 76 -> 202;
            case 75 -> 193;
            case 74 -> 184;
            case 73 -> 175;
            case 72 -> 166;
            case 71 -> 158;
            case 70 -> 149;
            case 69 -> 141;
            case 68 -> 133;
            case 67 -> 125;
            case 66 -> 117;
            case 65 -> 110;
            case 64 -> 102;
            case 63 -> 95;
            case 62 -> 87;
            case 61 -> 80;
            case 60 -> 72;
            case 59 -> 65;
            case 58 -> 57;
            case 57 -> 50;
            case 56 -> 43;
            case 55 -> 36;
            case 54 -> 29;
            case 53 -> 21;
            case 52 -> 14;
            case 51 -> 7;
            case 50 -> 0;
            default -> throw new IllegalStateException("Unexpected value: " + (int) (percentage * 100));
        };
        if (inverted) {
            this.performanceRating = this.averageEloOpponents - offset;
        } else {
            this.performanceRating = this.averageEloOpponents + offset;
        }
    }

    private void updateScore() {
        float tmpSum = 0.0f;
        for (final Float result : this.pastResults.values()) {
            tmpSum += result;
        }
        this.score = tmpSum;
    }

    private void updateBuchholz() {
        float tmpSum = 0.0f;
        for (final SimulatedPlayer opponent : this.pastResults.keySet()) {
            tmpSum += opponent.score;
        }
        this.buchholz = tmpSum;
    }

    private void updateBuchholzCutOne() {
        if (this.pastResults.isEmpty()) {
            this.buchholzCutOne = 0.0f;
            return;
        }
        float tmpBuchholz = 0.0f;
        float lowestScore = Float.MAX_VALUE;
        for (final SimulatedPlayer opponent : this.pastResults.keySet()) {
            if (opponent.score <= lowestScore) {
                lowestScore = opponent.score;
            }
            tmpBuchholz += opponent.score;
        }
        this.buchholzCutOne = tmpBuchholz - lowestScore;
    }

    private void updateSonnenbornBerger() {
        float tmpSum = 0.0f;
        for (final Entry<SimulatedPlayer, Float> entry : this.pastResults.entrySet()) {
            tmpSum += entry.getKey().score * entry.getValue();
        }
        this.sonnenbornBerger = tmpSum;
    }

    private void updateAverageEloOpponents() {
        int sum = 0;
        for (final SimulatedPlayer opponent : this.pastResults.keySet()) {
            sum += opponent.getElo();
        }
        this.averageEloOpponents = (float) sum / (float) this.pastResults.size();
    }

    public void updateScores() {
        updateScore();
        updateBuchholz();
        updateBuchholzCutOne();
        updateAverageEloOpponents();
        updateSonnenbornBerger();
    }

    public void addGame(final SimulatedPlayer opponent, final float result) {
        this.pastResults.put(opponent, result);
        this.score += result;
    }

    public void addGame(final SimulatedPlayer opponent, final float result, final boolean isWhite) {
        this.pastResults.put(opponent, result);
        this.score += result;
        if (isWhite) {
            this.colorDifference++;
        } else {
            this.colorDifference--;
        }
    }

    public void giveBye() {
        this.hasReceivedBye = true;
        this.score += 1.0f;
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

    public int getElo() {
        return this.participant.getElo();
    }

    public int getStartingRank() {
        return this.participant.getStartingRank();
    }

    public Map<SimulatedPlayer, Float> getPastResults() {
        return Collections.unmodifiableMap(this.pastResults);
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public boolean hasReceivedBye() {
        return this.hasReceivedBye;
    }

    public float getSonnenbornBerger() {
        return this.sonnenbornBerger;
    }

    public float getAverageEloOpponents() {
        return this.averageEloOpponents;
    }

    public float getPerformanceRating() {
        return this.performanceRating;
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

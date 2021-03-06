import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public final class Participant {
    private static LongAdder[][] rankings;
    private static Map<Integer, LongAdder> topThreeCounter;
    private final List<OpponentWrapper> gameList;
    private final String title;
    private final String name;
    private final String country;
    private final int elo;
    private final String type;
    private final boolean isFemale;
    private final int startingRankNextOpponent;
    private List<Participant> simulatedPlayerList;
    private int startingRank;
    private int numberOfTopThreeFinishes;
    private float score;
    private float buchholz;
    private float buchholzCutOne;
    private float sonnenbornBerger;
    private float averageEloOpponents;
    private float performanceRating = 0;

    public Participant(final int startingRank, final String title, final String name, final String country,
                       final int elo, final String type, final boolean isFemale,
                       final List<OpponentWrapper> gameList, final int startingRankNextOpponent) {
        this.startingRank = startingRank;
        this.title = title;
        this.name = name;
        this.country = country;
        this.elo = elo;
        this.type = type;
        this.isFemale = isFemale;
        this.gameList = new ArrayList<>(gameList);
        this.startingRankNextOpponent = startingRankNextOpponent;
    }

    public static Participant skeletonParticipant(final int startingRank, final String title, final String name,
                                                  final String country, final int elo, final boolean isFemale) {
        return new Participant(startingRank, title, name, country, elo, "", isFemale, new ArrayList<>(), 0);
    }

    public static Participant copyOf(final Participant participant) {
        final Participant participantCopy = new Participant(participant.startingRank, participant.title, participant.name,
                participant.country, participant.elo, participant.type, participant.isFemale,
                participant.gameList, participant.startingRankNextOpponent);
        participantCopy.score = participant.score;
        return participantCopy;
    }

    public static float resultToFloat(Character result) {
        return switch (result) {
            case '1' -> 1.0f;
            case '=' -> 0.5f;
            case '0' -> 0.0f;
            case '+' -> 1.0f;
            case '-' -> 0.0f;
            case 'F' -> 1.0f;
            default -> throw new IllegalStateException("Result is unknown character");
        };
    }

    public static void addRanking(final int startingRank, final int finalRank) {
        //starting rank starts at one, finalRank starts at zero
        rankings[startingRank - 1][finalRank].increment();
    }

    public static void initializeLongAdders(final int size) {
        rankings = new LongAdder[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rankings[i][j] = new LongAdder();
            }
        }
    }

    public static void initializeTopThreeCounterMap(final int numberOfPlayers, final int concurrentThreads) {
        topThreeCounter = new ConcurrentHashMap<>(numberOfPlayers, 0.75f, concurrentThreads);
    }

    public static void addTopThreeRanking(final int startingRank) {
        topThreeCounter.computeIfAbsent(startingRank, (Integer key) -> new LongAdder()).increment();
    }

    public static boolean equalsCheckAllParsed(final Object obj1, final Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if ((obj1 == null) || (obj2 == null)) {
            return false;
        }
        if (!(obj1 instanceof Participant) || !(obj2 instanceof Participant)) {
            return false;
        }
        final Participant participant1 = (Participant) obj1;
        final Participant participant2 = (Participant) obj2;
        //noinspection UnclearExpression
        return participant1.title.equals(participant2.title) &&
                participant1.name.equals(participant2.name) &&
                participant1.country.equals(participant2.country) &&
                participant1.elo == participant2.elo &&
                participant1.type.equals(participant2.type) &&
                participant1.isFemale == participant2.isFemale &&
                participant1.gameList.equals(participant2.gameList) &&
                participant1.startingRankNextOpponent == participant2.startingRankNextOpponent &&
                participant1.startingRank == participant2.startingRank;
    }

    public void addGame(final int opponentStartingRank, final Character result, final Character color) {
        final OpponentWrapper opponentWrapper = new OpponentWrapper(opponentStartingRank, result, color);
        this.gameList.add(opponentWrapper);
        this.score += Participant.resultToFloat(result);
    }

    public List<OpponentWrapper> getGameList() {
        return gameList;
    }

    public int getStartingRank() {
        return this.startingRank;
    }

    public void setStartingRank(final int startingRank) {
        this.startingRank = startingRank;
    }

    public int getElo() {
        return this.elo;
    }

    public String getName() {
        return this.name;
    }

    public int getStartingRankNextOpponent() {
        return this.startingRankNextOpponent;
    }

    public void setSimulatedPlayerList(final List<Participant> simulatedPlayerList) {
        this.simulatedPlayerList = simulatedPlayerList;
    }

    public float getScore() {
        return this.score;
    }

    public boolean hasPlayedAgainst(final Participant simulatedPlayer) {
        for (OpponentWrapper game : simulatedPlayer.gameList) {
            if (game.getOpponentStartingRank() == startingRank) {
                return true;
            }
        }
        return false;
    }

    public int getNumberOfTopThreeFinishes() {
        return this.numberOfTopThreeFinishes;
    }

    public void updateNumberOfTopThreeFinishes() {
        if (topThreeCounter.containsKey(this.startingRank)) {
            this.numberOfTopThreeFinishes = topThreeCounter.get(this.startingRank).intValue();
        } else {
            this.numberOfTopThreeFinishes = 0;
        }
    }

    private void calculatePerformance() {
        boolean inverted = false;
        if (this.gameList.isEmpty()) {
            this.performanceRating = 0;
        }
        float percentage = this.score / (float) this.gameList.size();
        if (percentage < 0.5f) {
            percentage = 1.0f - percentage;
            inverted = true;
        }
        final int offset = switch ((int) (percentage * 100.0f)) {
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
            default -> throw new IllegalStateException("Unexpected value: " + (int) (percentage * 100.0f));
        };
        if (inverted) {
            this.performanceRating = this.averageEloOpponents - (float) offset;
        } else {
            this.performanceRating = this.averageEloOpponents + (float) offset;
        }
    }

    // called after every round
    void updateScore() {
        float tmpSum = 0.0f;
        for (final OpponentWrapper game : this.gameList) {
            tmpSum += Participant.resultToFloat(game.getResult());
        }
        this.score = tmpSum;
    }

    // called at end of simulation
    public void updateScores() {
        if (this.gameList.isEmpty()) {
            this.score = 0.0f;
            this.buchholz = 0.0f;
            this.buchholzCutOne = 0.0f;
            this.sonnenbornBerger = 0.0f;
            this.averageEloOpponents = 0.0f;
            return;
        }
        float tmpScore = 0.0f;
        float tmpBuchholz = 0.0f;
        float tmpSonnenbornBerger = 0.0f;
        int tmpEloOpponentsSum = 0;
        float lowestScore = Float.MAX_VALUE;
        for (final OpponentWrapper game : this.gameList) {
            if (game.getOpponentStartingRank() == 0) {
                tmpScore += Participant.resultToFloat(game.getResult());
            } else {
                final Participant opponent = this.simulatedPlayerList.get(game.getOpponentStartingRank() - 1);
                tmpScore += Participant.resultToFloat(game.getResult());
                tmpBuchholz += opponent.score;
                tmpSonnenbornBerger += opponent.score * Participant.resultToFloat(game.getResult());
                tmpEloOpponentsSum += opponent.elo;
                if (opponent.score <= lowestScore) {
                    lowestScore = opponent.score;
                }
            }
        }
        this.score = tmpScore;
        this.buchholz = tmpBuchholz;
        this.buchholzCutOne = tmpBuchholz - lowestScore;
        this.sonnenbornBerger = tmpSonnenbornBerger;
        this.averageEloOpponents = (float) tmpEloOpponentsSum / this.gameList.size();
        calculatePerformance();
    }

    public float getAverageRank() {
        float sum = 0;
        for (int i = 0, arrayLength = rankings[this.startingRank].length; i < arrayLength; i++) {
            final LongAdder longAdder = rankings[this.startingRank - 1][i];
            sum += longAdder.floatValue() * (i + 1);
        }

        return sum / Main.numberOfSimulations;
    }

    public Character getGender() {
        if (isFemale) {
            return 'w';
        }
        return 'm';
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public int compareToByEloDescending(final Participant p2) {
        return Integer.compare(p2.elo, this.elo);
    }

    public int compareToByScoreThenElo(final Participant p2) {
        final int result = Float.compare(p2.score, this.score);
        return (result != 0) ? result : Integer.compare(p2.elo, this.elo);
    }

    public int compareToByScoreThenTieBreak(final Participant p2) {
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
        return Integer.compare(p2.elo, this.elo);
    }

    public int compareToByTopThreeFinishesDescending(final Participant p2) {
        final int result = Integer.compare(p2.numberOfTopThreeFinishes, this.numberOfTopThreeFinishes);
        return (result != 0) ? result : compareToByEloDescending(p2);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Participant)) {
            return false;
        }
        final Participant other = (Participant) obj;
        return this.startingRank == other.startingRank;
    }

    @Override
    public int hashCode() {
        return this.startingRank;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "name='" + this.name + '\'' +
                ", elo=" + this.elo +
                ", startingRank=" + this.startingRank +
                ", score=" + this.score +
                ", buchholz=" + this.buchholz +
                ", buchholzCutOne=" + this.buchholzCutOne +
                '}';
    }
}

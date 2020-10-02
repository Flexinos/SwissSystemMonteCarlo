import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Participant {
    private final Map<Integer, Float> pastResults;
    private final String title;
    private final String name;
    private final String country;
    private final int elo;
    private final String type;
    private final boolean isFemale;
    private final int pointsByForfeit;
    private final int startingRankNextOpponent;
    private final boolean isWhiteNextGame;
    private List<Participant> simulatedPlayerList;
    private boolean hasReceivedBye;
    private int startingRank;
    private int numberOfTopThreeFinishes;
    private float score;
    private float buchholz;
    private float buchholzCutOne;
    private float sonnenbornBerger;
    private float averageEloOpponents;
    private float performanceRating;
    private int colorDifference = 0;

    public Participant(final int startingRank, final String title, final String name, final String country, final int elo, final String type, final boolean isFemale, final Map<Integer, Float> pastResults, final int pointsByForfeit, final int startingRankNextOpponent, final boolean isWhiteNextGame, final boolean hasReceivedBye) {
        this.startingRank = startingRank;
        this.title = title;
        this.name = name;
        this.country = country;
        this.elo = elo;
        this.type = type;
        this.isFemale = isFemale;
        this.pastResults = new HashMap<>(pastResults);
        this.pointsByForfeit = pointsByForfeit;
        this.startingRankNextOpponent = startingRankNextOpponent;
        this.isWhiteNextGame = isWhiteNextGame;
        this.hasReceivedBye = hasReceivedBye;
    }

    // Customize the output of the simulation results here.
    public static void printSimulationResults(final Collection<Participant> participants) {
        // Set the name of each column here.
        final String[] columnNames = {"Name", "Starting Rank", "Elo", "Top three finishes", "Average rank"};
        final Padding[] columnNamePaddings = {Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT};
        final Padding[] participantFieldsPaddings = {Padding.LEFT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT};
        // Add the functions to produce a participant's entry here.
        final List<String[]> rows = participants.stream().map((Participant participant) -> Stream.of(
                participant.name,
                participant.startingRank,
                participant.elo,
                participant.numberOfTopThreeFinishes,
                participant.getAverageRank()
        ).map(String::valueOf).toArray(String[]::new)).collect(Collectors.toList());
        final int[] columnLengths = getColumnLengths(columnNames, rows);
        printRowSeparator(columnLengths);
        printRow(columnNames, columnLengths, columnNamePaddings);
        printRowSeparator(columnLengths);
        printAllRows(rows, columnLengths, participantFieldsPaddings);
        printRowSeparator(columnLengths);
    }

    private static int[] getColumnLengths(final String[] columnNames, final Iterable<String[]> rows) {
        final int[] maxColumnLengths = new int[columnNames.length];
        for (int columnNumber = 0; columnNumber < columnNames.length; ++columnNumber) {
            maxColumnLengths[columnNumber] = columnNames[columnNumber].length();
        }
        for (final String[] row : rows) {
            for (int columnNumber = 0; columnNumber < columnNames.length; ++columnNumber) {
                final int fieldLength = row[columnNumber].length();
                if (maxColumnLengths[columnNumber] < fieldLength) {
                    maxColumnLengths[columnNumber] = fieldLength;
                }
            }
        }
        return maxColumnLengths;
    }

    private static void printAllRows(final Iterable<String[]> rows, final int[] fieldLengths, final Padding[] paddings) {
        for (final String[] row : rows) {
            printRow(row, fieldLengths, paddings);
        }
    }

    private static void printRow(final String[] row, final int[] fieldLengths, final Padding[] paddings) {
        assert row.length == fieldLengths.length;
        assert row.length == paddings.length;
        System.out.print("|");
        for (int columnNumber = 0; columnNumber < row.length; ++columnNumber) {
            System.out.printf(" %" + paddingToString(paddings[columnNumber]) + fieldLengths[columnNumber] + "s |", row[columnNumber]);
        }
        System.out.println();
    }

    private static String paddingToString(final Padding padding) {
        if (padding == Padding.LEFT) {
            return "-";
        } else {
            return "";
        }
    }

    private static void printRowSeparator(final int[] columnLengths) {
        final int rowLength = Arrays.stream(columnLengths).sum() + (3 * columnLengths.length);
        System.out.println("-".repeat(rowLength + 1));
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
                participant1.pastResults.equals(participant2.pastResults) &&
                participant1.pointsByForfeit == participant2.pointsByForfeit &&
                participant1.startingRankNextOpponent == participant2.startingRankNextOpponent &&
                participant1.isWhiteNextGame == participant2.isWhiteNextGame &&
                participant1.startingRank == participant2.startingRank &&
                participant1.hasReceivedBye == participant2.hasReceivedBye;
    }

    public static Participant copyOf(final Participant participant) {
        return new Participant(participant.startingRank, participant.title, participant.name,
                participant.country, participant.elo, participant.type, participant.isFemale,
                new HashMap<>(participant.getPastResults()), participant.pointsByForfeit,
                participant.startingRankNextOpponent, participant.isWhiteNextGame,
                participant.hasReceivedBye);
    }

    public int getStartingRank() {
        return this.startingRank;
    }

    public void setStartingRank(final int startingRank) {
        this.startingRank = startingRank;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCountry() {
        return this.country;
    }

    public int getElo() {
        return this.elo;
    }

    public String getType() {
        return this.type;
    }

    public boolean isFemale() {
        return this.isFemale;
    }

    public int getNumberOfTopThreeFinishes() {
        return this.numberOfTopThreeFinishes;
    }

    public void setNumberOfTopThreeFinishes(final int numberOfTopThreeFinishes) {
        this.numberOfTopThreeFinishes = numberOfTopThreeFinishes;
    }

    public Map<Integer, Float> getPastResults() {
        return Collections.unmodifiableMap(this.pastResults);
    }

    public boolean hasReceivedBye() {
        return this.hasReceivedBye;
    }

    public int getPointsByForfeit() {
        return this.pointsByForfeit;
    }

    public int getStartingRankNextOpponent() {
        return this.startingRankNextOpponent;
    }

    public List<Participant> getSimulatedPlayerList() {
        return this.simulatedPlayerList;
    }

    public void setSimulatedPlayerList(final List<Participant> simulatedPlayerList) {
        this.simulatedPlayerList = simulatedPlayerList;
    }

    public boolean isHasReceivedBye() {
        return this.hasReceivedBye;
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

    public float getSonnenbornBerger() {
        return this.sonnenbornBerger;
    }

    public float getAverageEloOpponents() {
        return this.averageEloOpponents;
    }

    public float getPerformanceRating() {
        return this.performanceRating;
    }

    public int getColorDifference() {
        return this.colorDifference;
    }

    public boolean isWhiteNextGame() {
        return this.isWhiteNextGame;
    }

    public boolean hasPlayedAgainst(final Participant simulatedPlayer) {
        return this.pastResults.containsKey(simulatedPlayer.startingRank);
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

    public int compareToByScoreThenElo(final Participant p2) {
        final int result = Float.compare(p2.score, this.score);
        return (result != 0) ? result : Integer.compare(p2.elo, this.elo);
    }

    private void calculatePerformance() {
        boolean inverted = false;
        // TODO: make sure to prevent division by 0.
        float percentage = this.score / (float) this.pastResults.size();
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

    private void updateScore() {
        float tmpSum = 0.0f;
        for (final Float result : this.pastResults.values()) {
            tmpSum += result;
        }
        this.score = tmpSum + (float) this.pointsByForfeit;
    }

    private void updateBuchholz() {
        float tmpSum = 0.0f;
        for (final Integer startingRankOpponent : this.pastResults.keySet()) {
            tmpSum += this.simulatedPlayerList.get(startingRankOpponent - 1).score;
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
        for (final Integer startingRankOpponent : this.pastResults.keySet()) {
            if (this.simulatedPlayerList.get(startingRankOpponent - 1).score <= lowestScore) {
                lowestScore = this.simulatedPlayerList.get(startingRankOpponent - 1).score;
            }
            tmpBuchholz += this.simulatedPlayerList.get(startingRankOpponent - 1).score;
        }
        this.buchholzCutOne = tmpBuchholz - lowestScore;
    }

    private void updateSonnenbornBerger() {
        float tmpSum = 0.0f;
        for (final Map.Entry<Integer, Float> entry : this.pastResults.entrySet()) {
            tmpSum += this.simulatedPlayerList.get(entry.getKey() - 1).score * entry.getValue();
        }
        this.sonnenbornBerger = tmpSum;
    }

    private void updateAverageEloOpponents() {
        if (this.pastResults.isEmpty()) {
            this.averageEloOpponents = 0.0f;
        }
        int sum = 0;
        for (final Integer startingRankOpponent : this.pastResults.keySet()) {
            sum += this.simulatedPlayerList.get(startingRankOpponent - 1).elo;
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

    public void addGame(final Participant opponent, final float result) {
        this.pastResults.put(opponent.startingRank, result);
        this.score += result;
    }

    public void addGame(final Participant opponent, final float result, final boolean isWhite) {
        this.pastResults.put(opponent.startingRank, result);
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

    private float getAverageRank() {
        return -1.0f;
    }

    public int compareToByEloDescending(final Participant p2) {
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

    private enum Padding {LEFT, RIGHT}
}

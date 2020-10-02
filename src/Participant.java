import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Participant {
    private final LongAdder[] rankingTable = new LongAdder[Main.numberOfParticipants];
    private final String title;
    private final String name;
    private final String country;
    private final int elo;
    private final String type;
    private final boolean isFemale;
    private final Map<Integer, Float> pastResults;
    private final int pointsByForfeit;
    private final int startingRankNextOpponent;
    private final boolean isWhiteNextGame;
    private int startingRank;
    private int numberOfTopThreeFinishes;
    private final boolean hasReceivedBye;

    public Participant(final String name, final int elo) {
        this(0, "", name, "", elo, "", false, new HashMap<>(), 0, -1, true, false);
    }

    public Participant(final int startingRank, final String title, final String name, final String country, final int elo, final String type, final boolean isFemale, final Map<Integer, Float> pastResults, final int pointsByForfeit, final int startingRankNextOpponent, final boolean isWhiteNextGame, final boolean hasReceivedBye) {
        this.startingRank = startingRank;
        this.title = title;
        this.name = name;
        this.country = country;
        this.elo = elo;
        this.type = type;
        this.isFemale = isFemale;
        this.pastResults = new HashMap<>(pastResults);
        for (int i = 0; i < this.rankingTable.length; i++) {
            this.rankingTable[i] = new LongAdder();
        }
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
        printRow(columnNames, columnLengths, columnNamePaddings);
        printAllRows(rows, columnLengths, participantFieldsPaddings);
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
        for (int columnNumber = 0; columnNumber < row.length; ++columnNumber) {
            System.out.printf("%" + paddingToString(paddings[columnNumber]) + fieldLengths[columnNumber] + "s  ", row[columnNumber]);
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

    public void addRankToTable(final int rank) {
        this.rankingTable[rank].increment();
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

    public boolean isWhiteNextGame() {
        return this.isWhiteNextGame;
    }

    private float getAverageRank() {
        long sum = 0L;
        long longAdderCount = 0L;
        for (int rank = 1; rank <= this.rankingTable.length; rank++) {
            sum += this.rankingTable[rank - 1].longValue() * (long) rank;
            longAdderCount += this.rankingTable[rank - 1].longValue();
        }
        return (float) sum / (float) longAdderCount;
    }

    @Override
    public String toString() {
        return
                "Name: " + this.name + System.lineSeparator() +
                        "Elo: " + this.elo + System.lineSeparator() +
                        "Starting Rank: " + this.startingRank + System.lineSeparator();
    }

    public int compareToByEloDescending(final Participant p2) {
        return Integer.compare(p2.elo, this.elo);
    }

    public int compareToByTopThreeFinishesDescending(final Participant p2) {
        final int result = Integer.compare(p2.numberOfTopThreeFinishes, this.numberOfTopThreeFinishes);
        return (result != 0) ? result : compareToByEloDescending(p2);
    }

    private enum Padding {LEFT, RIGHT}

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
}

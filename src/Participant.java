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
    private final String bundesland;
    private final float score;
    private final float buchholzCutOne;
    private final float buchholz;
    private final float averageEloOpponents;
    private final float sonnenbornBerger;
    private final String type;
    private final boolean isFemale;
    private final Map<SimulatedPlayer, Float> pastResults;
    private int startingRank;
    private int numberOfTopThreeFinishes;

    public Participant(final String name, final int elo) {
        this(0, "", name, "", "", elo, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, "", false, new HashMap<>());
    }

    public Participant(final int startingRank, final String title, final String name, final String country, final String bundesland, final int elo, final float score, final float buchholz, final float buchholzCutOne, final float averageEloOpponents, final float sonnenbornBerger, final String type, final boolean isFemale, final Map<SimulatedPlayer, Float> pastResults) {
        this.startingRank = startingRank;
        this.title = title;
        this.name = name;
        this.country = country;
        this.bundesland = bundesland;
        this.elo = elo;
        this.score = score;
        this.buchholz = buchholz;
        this.buchholzCutOne = buchholzCutOne;
        this.sonnenbornBerger = sonnenbornBerger;
        this.averageEloOpponents = averageEloOpponents;
        this.type = type;
        this.isFemale = isFemale;
        this.pastResults = new HashMap<>(pastResults);
        for (int i = 0; i < this.rankingTable.length; i++) {
            this.rankingTable[i] = new LongAdder();
        }
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

    public float getScore() {
        return this.score;
    }

    public float getBuchholzCutOne() {
        return this.buchholzCutOne;
    }

    public float getBuchholz() {
        return this.buchholz;
    }

    public float getAverageEloOpponents() {
        return this.averageEloOpponents;
    }

    public float getSonnenbornBerger() {
        return this.sonnenbornBerger;
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

    public void addRankToTable(final int rank) {
        this.rankingTable[rank].increment();
    }

    public String getBundesland() {
        return this.bundesland;
    }

    public Map<SimulatedPlayer, Float> getPastResults() {
        return Collections.unmodifiableMap(this.pastResults);
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
                        "Starting Rank: " + this.startingRank + System.lineSeparator() +
                        "Score: " + this.score + System.lineSeparator() +
                        "Tie Break 1: " + this.buchholz + System.lineSeparator() +
                        "Tie Break 2: " + this.buchholzCutOne + System.lineSeparator() +
                        "Tie Break 3: " + this.sonnenbornBerger + System.lineSeparator();
    }

    public int compareToByEloDescending(final Participant p2) {
        return -Integer.compare(this.elo, p2.elo);
    }

    public int compareToByTopThreeFinishesDescending(final Participant p2) {
        final int result = -Integer.compare(this.numberOfTopThreeFinishes, p2.numberOfTopThreeFinishes);
        return (result != 0) ? result : compareToByEloDescending(p2);
    }

    private enum Padding {LEFT, RIGHT}
}

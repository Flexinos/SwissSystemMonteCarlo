import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Participant {
    public final LongAdder[] rankingTable = new LongAdder[Main.numberOfParticipants];
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
    private final Map<SimulatedPlayer, Float> pastGames;
    private int startingRank;
    private int numberOfTopThreeFinishes;

    public Participant(final String name, final int elo) {
        this(0, "", name, "", "", elo, 0, 0, 0, 0, 0, "", false, new HashMap<>());
    }

    public Participant(final int startingRank, final String title, final String name, final String country, final String bundesland, final int elo, final float score, final float buchholz, final float buchholzCutOne, final float averageEloOpponents, final float sonnenbornBerger, final String type, final boolean isFemale, final Map<SimulatedPlayer, Float> pastGames) {
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
        this.pastGames = pastGames;
        for (int i = 0; i < rankingTable.length; i++) {
            rankingTable[i] = new LongAdder();
        }
    }

    // Customize the output of the simulation results here.
    public static void printSimulationResults(final List<Participant> participants) {
        // Set the name of each column here.
        final String[] columnNames = {"Name", "Starting Rank", "Elo", "Top three finishes", "Average rank"};
        final Padding[] columnNamePaddings = {Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT};
        final Padding[] participantFieldsPaddings = {Padding.LEFT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT};
        // Add the functions to produce a participant's entry here.
        final List<String[]> rows = participants.stream().map(participant -> Stream.of(
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

    private static int[] getColumnLengths(final String[] columnNames, final List<String[]> rows) {
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

    private static void printAllRows(final List<String[]> rows, final int[] fieldLengths, final Padding[] paddings) {
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
        return startingRank;
    }

    public void setStartingRank(final int startingRank) {
        this.startingRank = startingRank;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public int getElo() {
        return elo;
    }

    public float getScore() {
        return score;
    }

    public float getBuchholzCutOne() {
        return buchholzCutOne;
    }

    public float getBuchholz() {
        return buchholz;
    }

    public float getAverageEloOpponents() {
        return averageEloOpponents;
    }

    public float getSonnenbornBerger() {
        return sonnenbornBerger;
    }

    public String getType() {
        return type;
    }

    public boolean isFemale() {
        return isFemale;
    }

    public int getNumberOfTopThreeFinishes() {
        return numberOfTopThreeFinishes;
    }

    public void setNumberOfTopThreeFinishes(final int numberOfTopThreeFinishes) {
        this.numberOfTopThreeFinishes = numberOfTopThreeFinishes;
    }

    public void addRankToTable(final int rank) {
        rankingTable[rank].increment();
    }

    public String getBundesland() {
        return bundesland;
    }

    public Map<SimulatedPlayer, Float> getPastGames() {
        return pastGames;
    }

    private float getAverageRank() {
        float sum = 0;
        float longAdderCount = 0;
        for (int rank = 1; rank <= rankingTable.length; rank++) {
            sum += rankingTable[rank - 1].longValue() * rank;
            longAdderCount += rankingTable[rank - 1].longValue();
        }
        return sum / longAdderCount;
    }

    @Override
    public String toString() {
        return
                "Name: " + name + System.lineSeparator() +
                        "Elo: " + elo + System.lineSeparator() +
                        "Starting Rank: " + startingRank + System.lineSeparator() +
                        "Score: " + score + System.lineSeparator() +
                        "Tie Break 1: " + buchholz + System.lineSeparator() +
                        "Tie Break 2: " + buchholzCutOne + System.lineSeparator() +
                        "Tie Break 3: " + sonnenbornBerger + System.lineSeparator();
    }

    public int compareToByEloDescending(final Participant p2) {
        return -Integer.compare(this.elo, p2.elo);
    }

    public int compareToByTopThreeFinishesDescending(final Participant p2) {
        final int result = -Integer.compare(this.numberOfTopThreeFinishes, p2.numberOfTopThreeFinishes);
        return result != 0 ? result : compareToByEloDescending(p2);
    }

    private enum Padding {LEFT, RIGHT}
}

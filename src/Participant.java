import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Participant {
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
    private int startingRank;
    private int numberOfTopThreeFinishes;
    public final LongAdder[] rankingTable = new LongAdder[Main.numberOfParticipants];

    public Participant(String name, int elo) {
        this(0, "", name, "", "", elo, 0, 0, 0, 0, 0, "", false);
    }

    public Participant(int startingRank, String title, String name, String country, String bundesland, int elo, float score, float buchholz, float buchholzCutOne, float averageEloOpponents, float sonnenbornBerger, String type, boolean isFemale) {
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
        for (int i = 0; i < rankingTable.length; i++) {
            rankingTable[i] = new LongAdder();
        }
    }

    public int getStartingRank() {
        return startingRank;
    }

    public void setStartingRank(int startingRank) {
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

    // Customize the output of the simulation results here.
    public static void printSimulationResults(List<Participant> participants) {
        // Set the name of each column here.
        String[] columnNames = {"Name", "Starting Rank", "Elo", "Top three finishes", "Average rank"};
        Padding[] columnNamePaddings = {Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT};
        Padding[] participantFieldsPaddings = {Padding.LEFT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT};
        // Add the functions to produce a participant's entry here.
        List<String[]> rows = participants.stream().map(participant -> Stream.of(
                participant.getName(),
                participant.getStartingRank(),
                participant.getElo(),
                participant.getNumberOfTopThreeFinishes(),
                participant.getAverageRank()
        ).map(String::valueOf).toArray(String[]::new)).collect(Collectors.toList());
        int[] columnLengths = getColumnLengths(columnNames, rows);
        printRow(columnNames, columnLengths, columnNamePaddings);
        printAllRows(rows, columnLengths, participantFieldsPaddings);
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

    public void setNumberOfTopThreeFinishes(int numberOfTopThreeFinishes) {
        this.numberOfTopThreeFinishes = numberOfTopThreeFinishes;
    }

    public void addRankToTable(int rank) {
        rankingTable[rank].increment();
    }

    public String getBundesland() {
        return bundesland;
    }

    private static int[] getColumnLengths(String[] columnNames, List<String[]> rows) {
        int[] maxColumnLengths = new int[columnNames.length];
        for (int columnNumber = 0; columnNumber < columnNames.length; ++columnNumber) {
            maxColumnLengths[columnNumber] = columnNames[columnNumber].length();
        }
        for (String[] row : rows) {
            for (int columnNumber = 0; columnNumber < columnNames.length; ++columnNumber) {
                int fieldLength = row[columnNumber].length();
                if (maxColumnLengths[columnNumber] < fieldLength) {
                    maxColumnLengths[columnNumber] = fieldLength;
                }
            }
        }
        return maxColumnLengths;
    }

    private static void printAllRows(List<String[]> rows, int[] fieldLengths, Padding[] paddings) {
        for (String[] row : rows) {
            printRow(row, fieldLengths, paddings);
        }
    }

    private static void printRow(String[] row, int[] fieldLengths, Padding[] paddings) {
        assert row.length == fieldLengths.length;
        assert row.length == paddings.length;
        for (int columnNumber = 0; columnNumber < row.length; ++columnNumber) {
            System.out.printf("%" + paddingToString(paddings[columnNumber]) + fieldLengths[columnNumber] + "s  ", row[columnNumber]);
        }
        System.out.println();
    }

    private static String paddingToString(Padding padding) {
        if (padding.equals(Padding.LEFT)) {
            return "-";
        } else {
            return "";
        }
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

    public int compareToByEloDescending(Participant p2) {
        return -Double.compare(this.getElo(), p2.getElo());
    }

    public int compareToByTopThreeFinishesDescending(Participant p2) {
        int result = -Integer.compare(this.getNumberOfTopThreeFinishes(), p2.getNumberOfTopThreeFinishes());
        return result != 0 ? result : compareToByEloDescending(p2);
    }

    private enum Padding {LEFT, RIGHT}
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SimulatedTournament {
    private final List<Participant> simulatedPlayerList;
    private final List<List<Pairing>> roundList;
    private final int roundsToBeSimulated;

    public SimulatedTournament(final int roundsToBeSimulated, final Collection<Participant> participants) {
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.simulatedPlayerList = new ArrayList<>(participants.size());
        for (final Participant participant : participants) {
            final Participant simulatedPlayer = Participant.copyOf(participant);
            this.simulatedPlayerList.add(simulatedPlayer);
            simulatedPlayer.setSimulatedPlayerList(this.simulatedPlayerList);
        }
        this.roundList = new ArrayList<>(this.roundsToBeSimulated);
        final List<Pairing> round = new ArrayList<>();
        for (final Participant participant : participants) {
            if (participant.getStartingRankNextOpponent() < 0) {
                break;
            } else if (participant.getStartingRankNextOpponent() == 0) {
                this.simulatedPlayerList.get(participant.getStartingRank() - 1).giveBye();
            } else if (participant.isWhiteNextGame()) {
                round.add(new Pairing(this.simulatedPlayerList.get(participant.getStartingRank() - 1), this.simulatedPlayerList.get(participant.getStartingRankNextOpponent() - 1)));
            }
        }
        if (round.size() > 0) {
            this.roundList.add(round);
        }
        for (final Participant simulatedPlayer : this.simulatedPlayerList) {
            simulatedPlayer.updateScores();
        }
    }

    public void simulateTournament() {
        for (int finishedRounds = 0; finishedRounds < this.roundsToBeSimulated; finishedRounds++) {
            if (this.roundList.size() <= finishedRounds) {
                this.roundList.add(Round.createPairings(this.simulatedPlayerList));
            }
            for (final Pairing pairing : this.roundList.get(finishedRounds)) {
                pairing.simulateResult();
            }
        }
    }

    public void analyseThisSimulatedTournament() {
        this.simulatedPlayerList.forEach(Participant::updateScores);
        this.simulatedPlayerList.sort(Participant::compareToByScoreThenTieBreak);
        for (int i = 0; i < 3; i++) {
            Main.addTopThreeRanking(this.simulatedPlayerList.get(i).getStartingRank());
        }
    }

    private enum Padding {LEFT, RIGHT}

    // Customize the output of the simulation results here.
    public static void printSimulationResults(final Collection<Participant> participants) {
        // Set the name of each column here.
        final String[] columnNames = {"Name", "Starting Rank", "Elo", "Top three finishes", "Average rank"};
        final Padding[] columnNamePaddings = {Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT, Padding.LEFT};
        final Padding[] participantFieldsPaddings = {Padding.LEFT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT, Padding.RIGHT};
        // Add the functions to produce a participant's entry here.
        final List<String[]> rows = participants.stream().map((Participant participant) -> Stream.of(
                participant.getName(),
                participant.getStartingRank(),
                participant.getElo(),
                participant.getNumberOfTopThreeFinishes(),
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
}

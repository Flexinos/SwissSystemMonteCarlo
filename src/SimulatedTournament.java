import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
        if (!round.isEmpty()) {
            this.roundList.add(round);
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
        for (int i = 0, simulatedPlayerListSize = this.simulatedPlayerList.size(); i < simulatedPlayerListSize; i++) {
            Participant.addRanking(this.simulatedPlayerList.get(i).getStartingRank(), i);
        }
    }

    private enum Padding {LEFT, RIGHT}

    public static final class Round {
        private Round() {
        }

        public static List<Pairing> createPairings(final List<Participant> players) {
            final List<Pairing> unorderedPairings = new ArrayList<>();
            final List<Participant> unpairedPlayers = new ArrayList<>(players);
            unpairedPlayers.sort(Participant::compareToByScoreThenElo);
            if ((unpairedPlayers.size() % 2) == 1) {
                giveByeToLastEligiblePlayer(unpairedPlayers); // makes pairing process somewhat easier but not necessarily correct pairing...
            }
            List<Participant> downfloaters = new ArrayList<>();
            final List<Participant> pairedPlayers = new ArrayList<>();
            while (!unpairedPlayers.isEmpty()) {
                final float highestUnpairedScore = unpairedPlayers.get(0).getScore();
                final List<Participant> nextBracket = unpairedPlayers.stream().filter((Participant p) -> Float.compare(p.getScore(), highestUnpairedScore) == 0).sorted(Participant::compareToByScoreThenElo).collect(Collectors.toList());
                nextBracket.addAll(downfloaters);
                nextBracket.sort(Participant::compareToByScoreThenElo);
                pairedPlayers.clear();
                downfloaters = pairBracket(nextBracket, pairedPlayers, unorderedPairings);
                unpairedPlayers.removeAll(pairedPlayers);
                unpairedPlayers.removeAll(downfloaters);
            }
            return unorderedPairings;
        }

        private static List<Participant> pairBracket(final List<Participant> playersInThisBracket, final List<Participant> pairedPlayers, final List<Pairing> unorderedPairings) {
            if (playersInThisBracket.size() < 2) {
                return playersInThisBracket;
            }
            Participant swappedOutPlayer = null;
            outsideLoops:
            for (int i = playersInThisBracket.size() - 1; i >= 0; --i) {
                for (int j = playersInThisBracket.size() - 1; j >= 0; --j) {
                    for (int k = playersInThisBracket.size() - 1; k >= 0; --k) {
                        final boolean proposedPairingIsValid = tryPairBracket(playersInThisBracket, pairedPlayers, unorderedPairings);
                        if (proposedPairingIsValid) {
                            break outsideLoops;
                        }
                        if (swappedOutPlayer != null) {
                            playersInThisBracket.add(swappedOutPlayer);
                        }
                        Collections.swap(playersInThisBracket, j, k);
                        if ((playersInThisBracket.size() % 2) == 1) {
                            swappedOutPlayer = playersInThisBracket.remove(i);
                        }
                    }
                }
            }
            return getDownfloaters(playersInThisBracket, pairedPlayers, swappedOutPlayer);
        }

        private static boolean tryPairBracket(final List<Participant> playersInBracket, final Collection<Participant> pairedPlayers, final Collection<Pairing> unorderedPairings) {
            final Collection<Pairing> provisionalPairings = new ArrayList<>(playersInBracket.size() / 2);
            for (int i = 0; i < (playersInBracket.size() / 2); i++) {
                if (Pairing.canBePaired(playersInBracket.get(i), playersInBracket.get(i + (playersInBracket.size() / 2)))) {
                    provisionalPairings.add(ThreadLocalRandom.current().nextBoolean() ? new Pairing(playersInBracket.get(i + (playersInBracket.size() / 2)), playersInBracket.get(i)) : new Pairing(playersInBracket.get(i), playersInBracket.get(i + (playersInBracket.size() / 2))));
                } else {
                    return false;
                }
            }
            for (final Pairing pairings : provisionalPairings) {
                pairedPlayers.add(pairings.getWhitePlayer());
                pairedPlayers.add(pairings.getBlackPlayer());
            }
            unorderedPairings.addAll(provisionalPairings);
            return true;
        }

        private static List<Participant> getDownfloaters(final Collection<Participant> unpairedPlayersInThisBracket, final Collection<Participant> pairedPlayers, final Participant swappedOutPlayer) {
            final List<Participant> downfloaters = new ArrayList<>();
            if (swappedOutPlayer != null) {
                downfloaters.add(swappedOutPlayer);
            }
            if (unpairedPlayersInThisBracket.size() != pairedPlayers.size()) {
                unpairedPlayersInThisBracket.removeAll(pairedPlayers);
                downfloaters.addAll(unpairedPlayersInThisBracket);
            }
            return downfloaters;
        }

        private static void giveByeToLastEligiblePlayer(final List<Participant> unpairedPlayers) {
            for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
                if (!unpairedPlayers.get(i).hasReceivedBye()) {
                    unpairedPlayers.get(i).giveBye();
                    unpairedPlayers.remove(i);
                }
            }
        }
    }
}

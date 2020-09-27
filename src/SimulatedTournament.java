import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class SimulatedTournament {
    private final Tournament tournament;
    private final List<SimulatedPlayer> simulatedPlayerList;
    private final List<List<Pairing>> listOfRounds;
    private final BitSet gameMatrix;

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;
        this.simulatedPlayerList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(participant -> new SimulatedPlayer(participant, this)).forEachOrdered(simulatedPlayerList::add);
        listOfRounds = new ArrayList<>(tournament.getRoundsToBeSimulated());
        gameMatrix = new BitSet(simulatedPlayerList.size() ^ 2);
        if (tournament.getGivenPairings() != null) {
            List<int[]> givenPairings = tournament.getGivenPairings();
            listOfRounds.add(new ArrayList<>());
            for (int[] givenPairing : givenPairings) {
                listOfRounds.get(0).add(new Pairing(simulatedPlayerList.get(givenPairing[0]), simulatedPlayerList.get(givenPairing[1])));
            }
        }
    }

    public void addGame(SimulatedPlayer player1, SimulatedPlayer player2) {
        gameMatrix.set((player1.getStartingRank() - 1) * simulatedPlayerList.size() + player2.getStartingRank() - 1);
    }

    public boolean haveMet(SimulatedPlayer player1, SimulatedPlayer player2) {
        return gameMatrix.get((player1.getStartingRank() - 1) * simulatedPlayerList.size() + player2.getStartingRank() - 1);
    }

    public void simulateTournament() {
        for (int finishedRounds = 0; finishedRounds < tournament.getRoundsToBeSimulated(); finishedRounds++) {
            if (listOfRounds.size() <= finishedRounds) {
                listOfRounds.add(Round.createPairings(simulatedPlayerList));
            }
            for (Pairing pairing : listOfRounds.get(finishedRounds + 1)) {
                pairing.simulateResult();
            }
        }
    }

    public void analyseThisSimulatedTournament() {
        simulatedPlayerList.forEach(SimulatedPlayer::updateTieBreaks);
        simulatedPlayerList.sort(SimulatedPlayer::compareToByScoreTieBreak);
        IntStream.range(0, 3).forEach(i -> Main.topThreeCounter.computeIfAbsent(simulatedPlayerList.get(i).getParticipant(), k -> new LongAdder()).increment());
        for (int i = 0; i < simulatedPlayerList.size(); i++) {
            simulatedPlayerList.get(i).getParticipant().addRankToTable(i);
        }
    }
}

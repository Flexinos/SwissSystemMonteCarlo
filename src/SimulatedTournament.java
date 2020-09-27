import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class SimulatedTournament {
    private final Tournament tournament;
    private final List<SimulatedPlayer> simulatedPlayerList;
    private BitSet gameMatrix;

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;
        this.simulatedPlayerList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(participant -> new SimulatedPlayer(participant, this)).forEachOrdered(simulatedPlayerList::add);
        gameMatrix = new BitSet(simulatedPlayerList.size() ^ 2);
    }

    public SimulatedTournament(Tournament tournament, boolean isOngoing) {
        this.tournament = tournament;
        this.simulatedPlayerList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(participant -> new SimulatedPlayer(participant, this)).forEachOrdered(simulatedPlayerList::add);
    }

    public void addGame(SimulatedPlayer player1, SimulatedPlayer player2) {
        gameMatrix.set((player1.getStartingRank() - 1) * simulatedPlayerList.size() + player2.getStartingRank() - 1);
    }

    public boolean haveMet(SimulatedPlayer player1, SimulatedPlayer player2) {
        return gameMatrix.get((player1.getStartingRank() - 1) * simulatedPlayerList.size() + player2.getStartingRank() - 1);
    }

    public void simulateTournament() {
        for (int finishedRounds = 0; finishedRounds < tournament.getTotalRounds(); finishedRounds++) {
            Round.createPairings(simulatedPlayerList);
        }
        simulatedPlayerList.forEach(SimulatedPlayer::updateTieBreaks);
        simulatedPlayerList.sort(SimulatedPlayer::compareToByScoreTieBreak);
        IntStream.range(0, 3).forEach(i -> Main.topThreeCounter.computeIfAbsent(simulatedPlayerList.get(i).getParticipant(), k -> new LongAdder()).increment());
        for (int i = 0; i < simulatedPlayerList.size(); i++) {
            simulatedPlayerList.get(i).getParticipant().addRankToTable(i);
        }
    }
}

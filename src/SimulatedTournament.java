import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class SimulatedTournament {
    private final Tournament tournament;
    private int roundsFinished;
    private final List<SimulatedPlayer> simulatedPlayerList;
    private final List<Round> roundList;
    private Ranking rankingByScoreThenEloList;
    private Ranking rankingByScoreThenTieBreakList;
    private BitSet gameMatrix;

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;
        this.roundsFinished = tournament.getFinishedRounds();
        this.roundList = new ArrayList<>(tournament.getTotalRounds());
        this.simulatedPlayerList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(participant -> new SimulatedPlayer(participant, this)).forEachOrdered(simulatedPlayerList::add);
        gameMatrix = new BitSet(simulatedPlayerList.size() ^ 2);
    }

    public SimulatedTournament(Tournament tournament, boolean isOngoing) {
        this.tournament = tournament;
        this.roundsFinished = tournament.getFinishedRounds();
        this.roundList = new ArrayList<>(tournament.getTotalRounds());
        this.simulatedPlayerList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(participant -> new SimulatedPlayer(participant, this)).forEachOrdered(simulatedPlayerList::add);
    }

    public void addGame(SimulatedPlayer player1, SimulatedPlayer player2) {
        gameMatrix.set((player1.getParticipant().getStartingRank() - 1) * simulatedPlayerList.size() + player2.getParticipant().getStartingRank() - 1);
    }

    public boolean haveMet(SimulatedPlayer player1, SimulatedPlayer player2) {
        return gameMatrix.get((player1.getParticipant().getStartingRank() - 1) * simulatedPlayerList.size() + player2.getParticipant().getStartingRank() - 1);
    }

    public void simulateTournament() {
        rankingByScoreThenEloList = new Ranking(simulatedPlayerList, Ranking.TypesOfRanking.ByELO);
        //rankingByScoreThenEloList.get(0).getRanking().stream().map(player -> player.getParticipant().getName() + "\t" + player.getParticipant().getElo()).forEach(System.out::println);
        while (roundsFinished < tournament.getTotalRounds()) {
            getNextRound();
            roundsFinished++;
        }
        createRankingByScoreThenTieBreak();
        IntStream.range(0, 3).forEach(i -> Main.topThreeCounter.computeIfAbsent(rankingByScoreThenTieBreakList.getRanking().get(i).getParticipant(), k -> new LongAdder()).increment());
        for (int i = 0, simulatedPlayerArrayListSize = simulatedPlayerList.size(); i < simulatedPlayerArrayListSize; i++) {
            Main.addRankToTable(simulatedPlayerList.get(i), i);
        }
    }

    private void getNextRound() {
        roundList.add(new Round(tournament, rankingByScoreThenEloList));
        roundList.get(roundList.size() - 1).createPairings();
        rankingByScoreThenEloList = new Ranking(simulatedPlayerList, Ranking.TypesOfRanking.ByELO);
    }

    private void createRankingByScoreThenTieBreak() {
        simulatedPlayerList.forEach(SimulatedPlayer::updateTieBreaks);
        rankingByScoreThenTieBreakList = new Ranking(simulatedPlayerList, Ranking.TypesOfRanking.ByBUCHHOLZ);
    }
}

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class SimulatedTournament {
    private final Tournament tournament;
    private int roundsFinished = 0;
    private final List<SimulatedPlayer> simulatedPlayerArrayList;
    private final List<Round> roundArrayList;
    private final List<Ranking> rankingByScoreThenEloList;
    private final List<Ranking> rankingByScoreThenTieBreakList;
    private BitSet gameMatrix;

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;
        this.roundArrayList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenEloList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenTieBreakList = new ArrayList<>(tournament.getTotalRounds());
        this.simulatedPlayerArrayList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(participant -> new SimulatedPlayer(participant, this)).forEachOrdered(simulatedPlayerArrayList::add);
        gameMatrix = new BitSet(simulatedPlayerArrayList.size() ^ 2);
    }

    public SimulatedTournament(Tournament tournament, boolean isOngoing) {
        this.tournament = tournament;
        this.roundsFinished = tournament.getFinishedRounds();
        this.roundArrayList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenEloList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenTieBreakList = new ArrayList<>(tournament.getTotalRounds());
        this.simulatedPlayerArrayList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(participant -> new SimulatedPlayer(participant, this)).forEachOrdered(simulatedPlayerArrayList::add);
    }

    public void addGame(SimulatedPlayer player1, SimulatedPlayer player2) {
        gameMatrix.set((player1.getParticipant().getStartingRank() - 1) * simulatedPlayerArrayList.size() + player2.getParticipant().getStartingRank() - 1);
    }

    public boolean haveMet(SimulatedPlayer player1, SimulatedPlayer player2) {
        return gameMatrix.get((player1.getParticipant().getStartingRank() - 1) * simulatedPlayerArrayList.size() + player2.getParticipant().getStartingRank() - 1);
    }

    public void simulateTournament() {
        rankingByScoreThenEloList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
        //rankingByScoreThenEloList.get(0).getRanking().stream().map(player -> player.getParticipant().getName() + "\t" + player.getParticipant().getElo()).forEach(System.out::println);
        while (roundsFinished < tournament.getTotalRounds()) {
            getNextRound();
        }
        createRankingByScoreThenTieBreak();
        IntStream.range(0, 3).forEach(i -> Main.topThreeCounter.computeIfAbsent(rankingByScoreThenTieBreakList.get(rankingByScoreThenTieBreakList.size() - 1).getRanking().get(i).getParticipant(), k -> new LongAdder()).increment());
    }

    private void getNextRound() {
        roundArrayList.add(new Round(tournament, rankingByScoreThenEloList.get(roundsFinished++)));
        roundArrayList.get(roundArrayList.size() - 1).createPairings();
        rankingByScoreThenEloList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
    }

    private void createRankingByScoreThenTieBreak() {
        simulatedPlayerArrayList.forEach(SimulatedPlayer::updateTieBreaks);
        rankingByScoreThenTieBreakList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByBUCHHOLZ));
    }
}

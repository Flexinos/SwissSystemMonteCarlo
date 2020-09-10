import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class SimulatedTournament {
    private final Tournament tournament;
    private final List<SimulatedPlayer> simulatedPlayerArrayList;
    private int roundsFinished = 0;
    private final List<Round> roundArrayList;
    private final List<Ranking> rankingByScoreThenEloList;
    private final List<Ranking> rankingByScoreThenTieBreakList;

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;
        this.roundArrayList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenEloList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenTieBreakList = new ArrayList<>(tournament.getTotalRounds());
        this.simulatedPlayerArrayList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(SimulatedPlayer::new).forEachOrdered(simulatedPlayerArrayList::add);
        if (simulatedPlayerArrayList.size() % 2 == 1) {
            simulatedPlayerArrayList.add(this.tournament.getBye());
        }
    }

    public void simulateTournament() {
        rankingByScoreThenEloList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
        while (roundsFinished < tournament.getTotalRounds()) {
            getNextRound();
        }
        createRankingByScoreThenTieBreak();
        IntStream.range(0, 3).forEach(i -> tournament.topThreeCounter.computeIfAbsent(rankingByScoreThenTieBreakList.get(rankingByScoreThenTieBreakList.size() - 1).getRanking().get(i).getParticipant(), k -> new LongAdder()).increment());
    }

    private void getNextRound() {
        roundArrayList.add(new Round(tournament, rankingByScoreThenEloList.get(roundsFinished++)));
        rankingByScoreThenEloList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
    }

    private void createRankingByScoreThenTieBreak() {
        simulatedPlayerArrayList.forEach(SimulatedPlayer::updateBuchholz);
        rankingByScoreThenTieBreakList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByBUCHHOLZ));
    }
}

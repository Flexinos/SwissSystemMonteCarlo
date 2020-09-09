import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

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
            simulatedPlayerArrayList.add(this.tournament.getBYE());
        }
    }

    private void getNextRound() {
        roundArrayList.add(new Round(rankingByScoreThenEloList.get(roundsFinished++)));
        rankingByScoreThenEloList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
        //createRankingByScoreThenTieBreak();
        //System.out.println("\n\nRanking after round: " + roundsFinished);
        //rankingByScoreThenTieBreakArrayList.get(rankingByScoreThenTieBreakArrayList.size() - 1).getRanking().stream().map(player -> player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tBucholz: " + player.getBuchholz() + "\tElo: " + player.getParticipant().getElo()).forEach(System.out::println);
    }

    private void createRankingByScoreThenTieBreak() {
        simulatedPlayerArrayList.forEach(SimulatedPlayer::updateBuchholz);
        rankingByScoreThenTieBreakList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByBUCHHOLZ));
    }

    public void simulateTournament() {
        rankingByScoreThenEloList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
        //System.out.println("Starting rank: ");
        //rankingByScoreThenEloArrayList.get(rankingByScoreThenEloArrayList.size() - 1).getRanking().stream().map(player -> player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tElo: " + player.getParticipant().getElo()).forEach(System.out::println);
        while (roundsFinished < tournament.getTotalRounds()) {
            getNextRound();
        }
        createRankingByScoreThenTieBreak();
        for (int i = 0; i < 3; i++) {
            tournament.topThreeCounter.computeIfAbsent(rankingByScoreThenTieBreakList.get(rankingByScoreThenTieBreakList.size() - 1).getRanking().get(i).getParticipant(), k -> new LongAdder()).increment();
        }
        //rankingByScoreThenTieBreakArrayList.get(rankingByScoreThenTieBreakArrayList.size() - 1).getRanking().stream().map(player -> player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tBuchholz: " + player.getBuchholz() + "\tElo: " + player.getParticipant().getElo()).forEach(System.out::println);
    }
}

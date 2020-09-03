import java.util.ArrayList;
import java.util.List;

public class SimulatedTournament {
    private final Tournament tournament;
    private final List<SimulatedPlayer> simulatedPlayerArrayList;
    private int roundsFinished = 0;
    private final List<Round> roundArrayList;
    private final List<Ranking> rankingByScoreThenEloArrayList;
    private final List<Ranking> rankingByScoreThenTieBreakArrayList;

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;

        this.roundArrayList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenEloArrayList = new ArrayList<>(tournament.getTotalRounds());
        this.rankingByScoreThenTieBreakArrayList = new ArrayList<>(tournament.getTotalRounds());
        this.simulatedPlayerArrayList = new ArrayList<>(tournament.getPlayerArrayList().size());

        tournament.getPlayerArrayList().stream().map(SimulatedPlayer::new).forEachOrdered(simulatedPlayerArrayList::add);
    }

    private void getNextRound() {
        roundArrayList.add(new Round(rankingByScoreThenEloArrayList.get(roundsFinished++)));
        simulatedPlayerArrayList.forEach(SimulatedPlayer::updateBuchholz);
        rankingByScoreThenEloArrayList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
        rankingByScoreThenTieBreakArrayList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByBUCHHOLZ));

        System.out.println("\n\nRanking after round: " + roundsFinished);
        rankingByScoreThenTieBreakArrayList.get(rankingByScoreThenTieBreakArrayList.size() - 1).getRanking().stream().map(player -> player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tBucholz: " + player.getBuchholz() + "\tElo: " + player.getParticipant().getElo()).forEach(System.out::println);
    }

    public void simulateTournament() {
        rankingByScoreThenEloArrayList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));

        System.out.println("Starting rank: ");
        rankingByScoreThenEloArrayList.get(rankingByScoreThenEloArrayList.size() - 1).getRanking().stream().map(player -> player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tElo: " + player.getParticipant().getElo()).forEach(System.out::println);
        System.out.print("\n\n");

        while (roundsFinished < tournament.getTotalRounds()) {
            getNextRound();
        }

        rankingByScoreThenEloArrayList.get(roundsFinished - 1).getRanking().stream().map(player -> player.getParticipant().getName() + " " + player.getScore()).forEach(System.out::println);
    }
}

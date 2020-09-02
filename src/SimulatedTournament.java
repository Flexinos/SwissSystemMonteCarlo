import java.util.ArrayList;

public class SimulatedTournament {
    private final Tournament tournament;
    private ArrayList<SimulatedPlayer> simulatedPlayerArrayList = new ArrayList<>();
    private int roundsFinished = 0;
    private ArrayList<Round> roundArrayList = new ArrayList<>();
    private ArrayList<Ranking> rankingByScoreThenEloArrayList = new ArrayList<>();
    private ArrayList<Ranking> rankingByScoreThenTieBreakArrayList = new ArrayList<>();

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;
        for (Participant participant : tournament.getPlayerArrayList()) {
            simulatedPlayerArrayList.add(new SimulatedPlayer(participant));
        }
    }

    private void getNextRound() {
        roundArrayList.add(new Round(rankingByScoreThenEloArrayList.get(roundsFinished++)));
        rankingByScoreThenEloArrayList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
        System.out.println("\n\nRanking after round: " + roundsFinished);
        for (SimulatedPlayer player : rankingByScoreThenEloArrayList.get(rankingByScoreThenEloArrayList.size() - 1).getRanking()) {
            System.out.println(player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tElo: " + player.getParticipant().getElo());
        }
    }

    public void simulateTournament() {
        rankingByScoreThenEloArrayList.add(new Ranking(simulatedPlayerArrayList, Ranking.TypesOfRanking.ByELO));
        System.out.println("Starting rank: ");
        for (SimulatedPlayer player : rankingByScoreThenEloArrayList.get(rankingByScoreThenEloArrayList.size() - 1).getRanking()) {
            System.out.println(player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tElo: " + player.getParticipant().getElo());
        }
        System.out.print("\n\n");
        while (roundsFinished < tournament.getTotalRounds()) {
            getNextRound();
        }
        for (SimulatedPlayer player : rankingByScoreThenEloArrayList.get(roundsFinished - 1).getRanking()) {
            System.out.println(player.getParticipant().getName() + " " + player.getScore());
        }
    }
}

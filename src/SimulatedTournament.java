import java.util.ArrayList;

public class SimulatedTournament {
    private final Tournament tournament;
    private ArrayList<SimulatedPlayer> simulatedPlayerArrayList;
    private int roundsFinished = 0;
    private ArrayList<Round> roundArrayList;
    private ArrayList<Ranking> rankingArrayList;

    public SimulatedTournament(Tournament tournament) {
        this.tournament = tournament;
        for (Participant participant : tournament.getPlayerArrayList()) {
            simulatedPlayerArrayList.add(new SimulatedPlayer(participant));
        }
        rankingArrayList.add(new Ranking(simulatedPlayerArrayList));
    }

    public void getNextRound() {
        roundArrayList.add(new Round(rankingArrayList.get(roundsFinished++)));
    }
}

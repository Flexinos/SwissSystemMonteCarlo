import java.util.ArrayList;

public class Tournament {
    private final String name;
    private final int totalRounds;
    private final ArrayList<Participant> participantArrayList;
    private ArrayList<SimulatedTournament> simulatedTournamentArrayList;

    public Tournament(String name, int totalRounds, ArrayList<Participant> participantArrayList) {
        this.name = name;
        this.totalRounds = totalRounds;
        participantArrayList.sort(Participant::compareToByElo);
        this.participantArrayList = participantArrayList;
        if (participantArrayList.size() % 2 == 1) {
            participantArrayList.add(new Participant("BYE", 0));
        }
    }

    public String getName() {
        return name;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public ArrayList<Participant> getPlayerArrayList() {
        return participantArrayList;
    }
}

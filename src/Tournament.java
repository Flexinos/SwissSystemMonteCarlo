import java.util.ArrayList;

public class Tournament {
    private final String name;
    private final int rounds;
    private final ArrayList<Participant> participantArrayList;
    private ArrayList<SimulatedTournament> simulatedTournamentArrayList;

    public Tournament(String name, int rounds, ArrayList<Participant> participantArrayList) {
        this.name = name;
        this.rounds = rounds;
        this.participantArrayList = participantArrayList;
    }

    public String getName() {
        return name;
    }

    public int getRounds() {
        return rounds;
    }

    public ArrayList<Participant> getPlayerArrayList() {
        return participantArrayList;
    }
}

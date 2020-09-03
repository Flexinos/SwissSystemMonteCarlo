import java.util.List;

public class Tournament {
    private final String name;
    private final int totalRounds;
    private List<Participant> participantArrayList;
    private List<SimulatedTournament> simulatedTournamentArrayList;

    public Tournament(String name, int totalRounds) {
        this.name = name;
        this.totalRounds = totalRounds;
    }

    public void addParticipants(List<Participant> participants) {
        participants.sort(Participant::compareToByElo);
        this.participantArrayList = participants;
        if (participantArrayList.size() % 2 == 1) {
            participantArrayList.add(new Participant(this, "BYE", 0));
        }
    }

    public String getName() {
        return name;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public List<Participant> getPlayerArrayList() {
        return participantArrayList;
    }
}

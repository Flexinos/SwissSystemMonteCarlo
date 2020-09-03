import java.util.List;

public class Tournament {
    private final int totalRounds;
    private List<Participant> participantArrayList;
    public static final SimulatedPlayer BYE = new SimulatedPlayer(new Participant("BYE", 0));

    public Tournament(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public void addParticipants(List<Participant> participants) {
        participants.sort(Participant::compareToByElo);
        this.participantArrayList = participants;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public SimulatedPlayer getBYE() {
        return BYE;
    }

    public List<Participant> getPlayerArrayList() {
        return participantArrayList;
    }
}

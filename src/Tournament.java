import java.util.List;

public class Tournament {
    private final int totalRounds;
    private List<Participant> participantArrayList;

    public Tournament(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public void addParticipants(List<Participant> participants) {
        participants.sort(Participant::compareToByElo);
        this.participantArrayList = participants;
        if (participantArrayList.size() % 2 == 1) {
            participantArrayList.add(new Participant(this, "BYE", 0));
        }
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public List<Participant> getPlayerArrayList() {
        return participantArrayList;
    }
}

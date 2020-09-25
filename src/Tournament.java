import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private final int totalRounds;
    private int finishedRounds = 0;
    private final List<Participant> participantArrayList = new ArrayList<>();
    private boolean hasBye = false;
    private SimulatedPlayer bye;

    public Tournament(int totalRounds, List<Participant> participants) {
        this.totalRounds = totalRounds;
        this.participantArrayList.addAll(participants);
        this.participantArrayList.sort(Participant::compareToByElo);
        for (int i = 0; i < participantArrayList.size(); ++i) {
            participantArrayList.get(i).setStartingRank(i + 1);
        }
        if (participants.size() % 2 == 1) {
            hasBye = true;
            bye = new SimulatedPlayer(new Participant("BYE", 0));
        }
    }

    public Tournament(int totalRounds, int finishedRounds) {
        this.totalRounds = totalRounds;
        this.finishedRounds = finishedRounds;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public int getFinishedRounds() {
        return finishedRounds;
    }

    public SimulatedPlayer getBye() {
        return bye;
    }

    public List<Participant> getPlayerArrayList() {
        return participantArrayList;
    }

    public boolean hasBye() {
        return hasBye;
    }
}

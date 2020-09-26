import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private final int totalRounds;
    private int finishedRounds = 0;
    private final List<Participant> participantArrayList = new ArrayList<>();
    public static final SimulatedPlayer BYE = new SimulatedPlayer(new Participant("BYE", 0));

    public Tournament(int totalRounds, List<Participant> participants) {
        this.totalRounds = totalRounds;
        this.participantArrayList.addAll(participants);
        this.participantArrayList.sort(Participant::compareToByElo);
        for (int i = 0; i < participantArrayList.size(); ++i) {
            participantArrayList.get(i).setStartingRank(i + 1);
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

    public List<Participant> getPlayerArrayList() {
        return participantArrayList;
    }
}

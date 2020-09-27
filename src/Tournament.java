import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private final int totalRounds;
    private final List<Participant> participantList = new ArrayList<>();
    private List<int[]> givenPairings;
    public static final SimulatedPlayer BYE = new SimulatedPlayer(new Participant("BYE", 0));

    public Tournament(int totalRounds, List<Participant> participants) {
        this.totalRounds = totalRounds;
        this.participantList.addAll(participants);
        this.participantList.sort(Participant::compareToByElo);
        for (int i = 0; i < participantList.size(); ++i) {
            participantList.get(i).setStartingRank(i + 1);
        }
    }

    public Tournament(int totalRounds, List<Participant> participants, List<int[]> givenPairings) {
        this.totalRounds = totalRounds;
        this.participantList.addAll(participants);
        this.participantList.sort(Participant::compareToByElo);
        for (int i = 0; i < participantList.size(); ++i) {
            participantList.get(i).setStartingRank(i + 1);
        }
        this.givenPairings = givenPairings;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public List<int[]> getGivenPairings() {
        return givenPairings;
    }

    public List<Participant> getPlayerArrayList() {
        return participantList;
    }
}

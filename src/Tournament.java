import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private final int roundsToBeSimulated;
    private final List<Participant> participantList = new ArrayList<>();
    private List<int[]> givenPairings;
    public static final SimulatedPlayer BYE = new SimulatedPlayer(new Participant("BYE", 0));

    public Tournament(int roundsToBeSimulated, List<Participant> participants) {
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.participantList.addAll(participants);
        this.participantList.sort(Participant::compareToByElo);
        for (int i = 0; i < participantList.size(); ++i) {
            participantList.get(i).setStartingRank(i + 1);
        }
    }

    public Tournament(int roundsToBeSimulated, List<Participant> participants, List<int[]> givenPairings) {
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.participantList.addAll(participants);
        this.participantList.sort(Participant::compareToByElo);
        for (int i = 0; i < participantList.size(); ++i) {
            participantList.get(i).setStartingRank(i + 1);
        }
        this.givenPairings = givenPairings;
    }

    public int getRoundsToBeSimulated() {
        return roundsToBeSimulated;
    }

    public List<int[]> getGivenPairings() {
        return givenPairings;
    }

    public List<Participant> getPlayerArrayList() {
        return participantList;
    }
}

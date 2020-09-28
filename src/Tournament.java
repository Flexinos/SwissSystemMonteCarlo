import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private final int roundsToBeSimulated;
    private final List<Participant> participantList = new ArrayList<>();
    private List<int[]> givenPairings;
    public static final SimulatedPlayer BYE = new SimulatedPlayer(new Participant("BYE", 0));
    public static final List<rankingBy> rankingOrder = new ArrayList<>(List.of(rankingBy.SCORE, rankingBy.BUCHHOLZCUTONE, rankingBy.BUCHHOLZ, rankingBy.AVERAGEELOOPPONENTS));

    public Tournament(int roundsToBeSimulated, List<Participant> participants) {
        if (roundsToBeSimulated < 0) {
            throw new IllegalArgumentException("roundsToBeSimulated must be non-negative");
        }
        if (participants.size() < 2) {
            throw new IllegalArgumentException("participants must contain two or more players");
        }
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.participantList.addAll(participants);
        this.participantList.sort(Participant::compareToByEloDescending);
        for (int i = 0; i < participantList.size(); ++i) {
            participantList.get(i).setStartingRank(i + 1);
        }
    }

    public Tournament(int roundsToBeSimulated, List<Participant> participants, List<int[]> givenPairings) {
        if (roundsToBeSimulated < 0) {
            throw new IllegalArgumentException("roundsToBeSimulated must be non-negative");
        }
        if (participants.size() < 2) {
            throw new IllegalArgumentException("participants must contain two or more players");
        }
        if (givenPairings.size() < 1) {
            throw new IllegalArgumentException("givenPairings must contain one or more pairings");
        }
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.participantList.addAll(participants);
        this.participantList.sort(Participant::compareToByEloDescending);
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

    public enum rankingBy {SCORE, BUCHHOLZ, BUCHHOLZCUTONE, SONNENBORNBERGER, AVERAGEELOOPPONENTS}
}

import java.util.ArrayList;
import java.util.List;

public class Tournament {
    public static final List<rankingBy> rankingOrder = new ArrayList<>(List.of(rankingBy.SCORE, rankingBy.BUCHHOLZCUTONE, rankingBy.BUCHHOLZ, rankingBy.AVERAGEELOOPPONENTS));
    private final int roundsToBeSimulated;
    private final List<Participant> participantList = new ArrayList<>();
    private List<int[]> givenPairings;

    public Tournament(final int roundsToBeSimulated, final List<Participant> participants) {
        if (roundsToBeSimulated < 0) {
            throw new IllegalArgumentException("roundsToBeSimulated must be non-negative");
        }
        if (participants.size() < 2) {
            throw new IllegalArgumentException("participants must contain two or more players");
        }
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.participantList.addAll(participants);
        this.participantList.sort(Participant::compareToByEloDescending);
        for (int i = 0; i < this.participantList.size(); ++i) {
            this.participantList.get(i).setStartingRank(i + 1);
        }
    }

    public Tournament(final int roundsToBeSimulated, final List<Participant> participants, final List<int[]> givenPairings) {
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
        for (int i = 0; i < this.participantList.size(); ++i) {
            this.participantList.get(i).setStartingRank(i + 1);
        }
        this.givenPairings = givenPairings;
    }

    public int getRoundsToBeSimulated() {
        return this.roundsToBeSimulated;
    }

    public List<int[]> getGivenPairings() {
        return this.givenPairings;
    }

    public List<Participant> getPlayerArrayList() {
        return this.participantList;
    }

    public enum rankingBy {SCORE, BUCHHOLZ, BUCHHOLZCUTONE, SONNENBORNBERGER, AVERAGEELOOPPONENTS}
}

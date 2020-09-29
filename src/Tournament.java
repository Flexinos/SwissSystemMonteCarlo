import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Tournament {
    private static final List<RankingBy> rankingOrder = new ArrayList<>(List.of(RankingBy.SCORE, RankingBy.BUCHHOLZ_CUT_ONE, RankingBy.BUCHHOLZ, RankingBy.AVERAGE_ELO_OPPONENTS));
    private final int roundsToBeSimulated;
    private final List<Participant> participantList;
    private List<int[]> givenPairings;

    public Tournament(final int roundsToBeSimulated, final Collection<Participant> participants) {
        if (roundsToBeSimulated < 0) {
            throw new IllegalArgumentException("roundsToBeSimulated must be non-negative");
        }
        if (participants.size() < 2) {
            throw new IllegalArgumentException("participants must contain two or more players");
        }
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.participantList = new ArrayList<>(participants);
        this.participantList.sort(Participant::compareToByEloDescending);
        for (int i = 0; i < this.participantList.size(); ++i) {
            this.participantList.get(i).setStartingRank(i + 1);
        }
    }

    public Tournament(final int roundsToBeSimulated, final Collection<Participant> participants, final List<int[]> givenPairings) {
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
        this.participantList = new ArrayList<>(participants);
        this.participantList.sort(Participant::compareToByEloDescending);
        for (int i = 0; i < this.participantList.size(); ++i) {
            this.participantList.get(i).setStartingRank(i + 1);
        }
        this.givenPairings = new ArrayList<>(givenPairings);
    }

    public static List<RankingBy> getRankingOrder() {
        return Collections.unmodifiableList(rankingOrder);
    }

    public int getRoundsToBeSimulated() {
        return this.roundsToBeSimulated;
    }

    public List<int[]> getGivenPairings() {
        if (this.givenPairings == null) {
            return null;
        }
        return Collections.unmodifiableList(this.givenPairings);
    }

    public List<Participant> getPlayerArrayList() {
        return Collections.unmodifiableList(this.participantList);
    }

    public enum RankingBy {SCORE, BUCHHOLZ, BUCHHOLZ_CUT_ONE, SONNENBORN_BERGER, AVERAGE_ELO_OPPONENTS}
}

import java.util.HashSet;

public class Round {
    private final Ranking rankingBeforeRound;
    private HashSet<Pairing> pairings;

    public Round(Ranking rankingBeforeRound) {
        this.rankingBeforeRound = rankingBeforeRound;
        createPairings();
    }

    public boolean addPairing(Pairing pairing) {
        return pairings.add(pairing);
    }

    public HashSet<Pairing> getPairings() {
        return pairings;
    }

    private void createPairings() {

    }
}

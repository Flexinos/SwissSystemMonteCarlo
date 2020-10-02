import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SimulatedTournament {
    private final List<Participant> participantList;
    private final List<Participant> simulatedPlayerList;
    private final List<List<Pairing>> roundList;
    private final int roundsToBeSimulated;

    public SimulatedTournament(final int roundsToBeSimulated, final Collection<Participant> participants) {
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.participantList = new ArrayList<>(participants);
        this.simulatedPlayerList = new ArrayList<>(participants.size());
        for (final Participant participant : participants) {
            final Participant simulatedPlayer = Participant.copyOf(participant);
            this.simulatedPlayerList.add(simulatedPlayer);
            simulatedPlayer.setSimulatedPlayerList(this.simulatedPlayerList);
        }
        this.roundList = new ArrayList<>(this.roundsToBeSimulated);
        final List<Pairing> round = new ArrayList<>();
        for (final Participant participant : participants) {
            if (participant.getStartingRankNextOpponent() < 0) {
                break;
            }
            if (participant.getStartingRankNextOpponent() == 0) {
                this.simulatedPlayerList.get(participant.getStartingRank() - 1).giveBye();
            }
            if (participant.isWhiteNextGame()) {
                round.add(new Pairing(this.simulatedPlayerList.get(participant.getStartingRank() - 1), this.simulatedPlayerList.get(participant.getStartingRankNextOpponent() - 1)));
            }
        }
        this.roundList.add(round);
        for (final Participant simulatedPlayer : this.simulatedPlayerList) {
            simulatedPlayer.updateScores();
        }
    }

    public void simulateTournament() {
        for (int finishedRounds = 0; finishedRounds < this.roundsToBeSimulated; finishedRounds++) {
            if (this.roundList.size() <= finishedRounds) {
                this.roundList.add(Round.createPairings(this.simulatedPlayerList));
            }
            for (final Pairing pairing : this.roundList.get(finishedRounds)) {
                pairing.simulateResult();
            }
        }
    }

    public void analyseThisSimulatedTournament() {
        this.simulatedPlayerList.forEach(Participant::updateScores);
        this.simulatedPlayerList.sort(Participant::compareToByScoreThenTieBreak);
        for (int i1 = 0; i1 < 3; i1++) {
            Main.addTopThreeRanking(this.participantList.get(this.simulatedPlayerList.get(i1).getStartingRank() - 1));
        }
    }
}

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public final class SimulatedTournament {
    private final List<SimulatedPlayer> simulatedPlayerList;
    private final List<List<Pairing>> roundList;
    private final int roundsToBeSimulated;

    public SimulatedTournament(final int roundsToBeSimulated, final Collection<Participant> participants) {
        this.roundsToBeSimulated = roundsToBeSimulated;
        this.simulatedPlayerList = new ArrayList<>(participants.size());
        for (final Participant participant1 : participants) {
            final SimulatedPlayer player = new SimulatedPlayer(participant1, this.simulatedPlayerList);
            this.simulatedPlayerList.add(player);
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
        for (final SimulatedPlayer simulatedPlayer : this.simulatedPlayerList) {
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
        this.simulatedPlayerList.forEach(SimulatedPlayer::updateScores);
        this.simulatedPlayerList.sort(SimulatedPlayer::compareToByScoreThenTieBreak);
        IntStream.range(0, 3).mapToObj((int i) -> this.simulatedPlayerList.get(i).getParticipant()).forEach(Main::addTopThreeRanking);
        for (int i = 0; i < this.simulatedPlayerList.size(); i++) {
            this.simulatedPlayerList.get(i).addRankToTable(i);
        }
    }
}

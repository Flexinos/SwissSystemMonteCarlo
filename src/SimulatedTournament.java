import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class SimulatedTournament {
    private final Tournament tournament;
    private final List<SimulatedPlayer> simulatedPlayerList;
    private final List<List<Pairing>> roundList;

    public SimulatedTournament(final Tournament tournament) {
        this.tournament = tournament;
        this.simulatedPlayerList = new ArrayList<>(tournament.getPlayerArrayList().size());
        tournament.getPlayerArrayList().stream().map(SimulatedPlayer::new).forEachOrdered(this.simulatedPlayerList::add);
        this.roundList = new ArrayList<>(tournament.getRoundsToBeSimulated());
        if (tournament.getGivenPairings() != null) {
            final List<int[]> givenPairings = tournament.getGivenPairings();
            final List<Pairing> round = new ArrayList<>();
            for (final int[] givenPairing : givenPairings) {
                if (givenPairing[1] == 0) {
                    this.simulatedPlayerList.get(givenPairing[0] - 1).giveBye();
                } else {
                    round.add(new Pairing(this.simulatedPlayerList.get(givenPairing[0] - 1), this.simulatedPlayerList.get(givenPairing[1] - 1)));
                }
            }
            this.roundList.add(round);
        }
    }

    public void simulateTournament() {
        for (int finishedRounds = 0; finishedRounds < this.tournament.getRoundsToBeSimulated(); finishedRounds++) {
            if (this.roundList.size() <= finishedRounds) {
                this.roundList.add(Round.createPairings(this.simulatedPlayerList));
            }
            for (final Pairing pairing : this.roundList.get(finishedRounds)) {
                pairing.simulateResult();
            }
        }
    }

    public void analyseThisSimulatedTournament() {
        //simulatedPlayerList.forEach(SimulatedPlayer::updateTiebreaks);
        this.simulatedPlayerList.sort(SimulatedPlayer::compareToByScoreThenTieBreak);
        IntStream.range(0, 3).mapToObj((int i) -> this.simulatedPlayerList.get(i).getParticipant()).forEach(Main::addTopThreeRanking);
        for (int i = 0; i < this.simulatedPlayerList.size(); i++) {
            this.simulatedPlayerList.get(i).addRankToTable(i);
        }
    }
}

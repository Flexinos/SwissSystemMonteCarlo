import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Round {

    private final List<SimulatedPlayer> players;

    public Round(List<SimulatedPlayer> players) {
        this.players = players;
    }

    public void createPairings() {
        //change to static method in future
        //maybe change datatype of unpairedPlayers to treeset, allows faster filtering and faster removal
        List<SimulatedPlayer> unpairedPlayers = new ArrayList<>(players);
        unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        List<SimulatedPlayer> pairedPlayers = new ArrayList<>();
        if (unpairedPlayers.size() % 2 == 1) {
            giveByeToLastEligiblePlayer(unpairedPlayers, pairedPlayers); // makes pairing process somewhat easier but not necessarily correct pairing...
        }
        List<SimulatedPlayer> downfloatersFromPreviousBracket = new ArrayList<>();
        List<SimulatedPlayer> downfloatersToNextBracket = new ArrayList<>();
        while (unpairedPlayers.size() > 0) {
            double highestUnpairedScore = unpairedPlayers.get(0).getScore();
            List<SimulatedPlayer> nextBracket = unpairedPlayers.stream().filter(p -> p.getScore() == highestUnpairedScore).sorted(SimulatedPlayer::compareToByScoreThenElo).collect(Collectors.toList());
            pairBracket(nextBracket, downfloatersFromPreviousBracket, downfloatersToNextBracket, pairedPlayers);
            downfloatersFromPreviousBracket.clear();
            downfloatersFromPreviousBracket.addAll(downfloatersToNextBracket);
            downfloatersToNextBracket.clear();
            unpairedPlayers.removeAll(pairedPlayers);
            unpairedPlayers.removeAll(downfloatersToNextBracket);
            unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        }
    }

    private void pairBracket(List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromPreviousBracket, List<SimulatedPlayer> downfloatersToNextBracket, List<SimulatedPlayer> pairedPlayers) {
        List<SimulatedPlayer> unpairedPlayersInThisBracket = new ArrayList<>(nonDownfloaters);
        //printPlayersInBracket(unpairedPlayersInThisBracket, nonDownfloaters, downfloatersFromPreviousBracket);
        unpairedPlayersInThisBracket.addAll(downfloatersFromPreviousBracket);
        unpairedPlayersInThisBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
        List<Pairing> proposedPairings = new ArrayList<>(unpairedPlayersInThisBracket.size() / 2);
        for (int i = unpairedPlayersInThisBracket.size() - 1; i >= 0; i--) {
            for (int j = unpairedPlayersInThisBracket.size() - 1; j >= 0; j--) {
                boolean proposedPairingIsValid = tryPairBracket(proposedPairings, pairedPlayers, unpairedPlayersInThisBracket);
                if (proposedPairingIsValid) {
                    getDownfloater(unpairedPlayersInThisBracket, pairedPlayers, downfloatersToNextBracket);
                    return;
                }
                if (!downfloatersToNextBracket.isEmpty()) {
                    unpairedPlayersInThisBracket.add(downfloatersToNextBracket.remove(0));
                }
                Collections.swap(unpairedPlayersInThisBracket, i, j);
                if (unpairedPlayersInThisBracket.size() % 2 == 1) {
                    downfloatersToNextBracket.add(unpairedPlayersInThisBracket.remove(unpairedPlayersInThisBracket.size() - 1));
                }
            }
        }
        downfloatersToNextBracket.addAll(unpairedPlayersInThisBracket);
    }

    private boolean tryPairBracket(List<Pairing> proposedPairings, List<SimulatedPlayer> pairedPLayers, List<SimulatedPlayer> playersInBracket) {
        List<Pairing> provisionalPairings = new ArrayList<>(playersInBracket.size() / 2);
        Random random = new Random();
        for (int i = 0; i < playersInBracket.size() / 2; i++) {
            if (Pairing.pairingAllowed(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2))) {
                if (random.nextBoolean()) {
                    provisionalPairings.add(new Pairing(playersInBracket.get(i + playersInBracket.size() / 2), playersInBracket.get(i)));
                } else {
                    provisionalPairings.add(new Pairing(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2)));
                }
                pairedPLayers.add(playersInBracket.get(i));
                pairedPLayers.add(playersInBracket.get(i + playersInBracket.size() / 2));
            } else {
                pairedPLayers.clear();
                return false;
            }
        }
        proposedPairings.clear();
        proposedPairings.addAll(provisionalPairings);
        proposedPairings.forEach(Pairing::simulateResult);
        return true;
    }

    private void getDownfloater(List<SimulatedPlayer> unpairedPlayersInThisBracket, List<SimulatedPlayer> pairedPlayers, List<SimulatedPlayer> downfloatersToNextBracket) {
        if (unpairedPlayersInThisBracket.size() != pairedPlayers.size()) {
            unpairedPlayersInThisBracket.removeAll(pairedPlayers);
            downfloatersToNextBracket.addAll(unpairedPlayersInThisBracket);
        }
    }

    private void giveByeToLastEligiblePlayer(List<SimulatedPlayer> unpairedPlayers, List<SimulatedPlayer> pairedPlayers) {
        for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
            if (!unpairedPlayers.get(i).hasReceivedBye()) {
                Pairing.giveBye(unpairedPlayers.get(i));
                pairedPlayers.add(unpairedPlayers.get(i));
            }
        }

    }

    private enum typeOfBracket {
        DOWNFLOATERS,
        NONDOWNFLOATERS,
        TOTAL
    }
}

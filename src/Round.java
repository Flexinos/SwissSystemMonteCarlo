import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Round {

    private final List<SimulatedPlayer> players;
    private final List<Pairing> unorderedPairings;

    public Round(List<SimulatedPlayer> players) {
        this.players = players;
        this.unorderedPairings = new ArrayList<>(players.size() + 1);
    }

    public void createPairings() {
        //change to static method in future
        //maybe change datatype of unpairedPlayers to treeset, allows faster filtering and faster removal
        List<SimulatedPlayer> unpairedPlayers = new ArrayList<>(players);
        unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        if (unpairedPlayers.size() % 2 == 1) {
            giveByeToLastEligiblePlayer(unpairedPlayers); // makes pairing process somewhat easier but not necessarily correct pairing...
        }
        List<SimulatedPlayer> downfloatersFromPreviousBracket = new ArrayList<>();
        List<SimulatedPlayer> downfloatersToNextBracket = new ArrayList<>();
        while (unpairedPlayers.size() > 0) {
            double highestUnpairedScore = unpairedPlayers.get(0).getScore();
            List<SimulatedPlayer> nextBracket = unpairedPlayers.stream().filter(p -> p.getScore() == highestUnpairedScore).sorted(SimulatedPlayer::compareToByScoreThenElo).collect(Collectors.toList());
            downfloatersToNextBracket.clear();
            pairBracket(nextBracket, downfloatersFromPreviousBracket, downfloatersToNextBracket);
            downfloatersFromPreviousBracket.clear();
            downfloatersFromPreviousBracket.addAll(downfloatersToNextBracket);
            for (Pairing pairing : unorderedPairings) {
                unpairedPlayers.remove(pairing.getPlayer1());
                unpairedPlayers.remove(pairing.getPlayer2());
            }
            unpairedPlayers.removeAll(downfloatersToNextBracket);
            unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        }
    }

    private void pairBracket(List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromPreviousBracket, List<SimulatedPlayer> downfloatersToNextBracket) {
        List<SimulatedPlayer> unpairedPlayersInThisBracket = new ArrayList<>(nonDownfloaters);
        //printPlayersInBracket(unpairedPlayersInThisBracket, nonDownfloaters, downfloatersFromPreviousBracket);
        unpairedPlayersInThisBracket.addAll(downfloatersFromPreviousBracket);
        unpairedPlayersInThisBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
        List<Pairing> proposedPairings = new ArrayList<>(unpairedPlayersInThisBracket.size() / 2);
        List<SimulatedPlayer> pairedPlayers = new ArrayList<>(unpairedPlayersInThisBracket.size());
        for (int i = unpairedPlayersInThisBracket.size() - 1; i >= 0; i--) {
            for (int j = unpairedPlayersInThisBracket.size() - 1; j >= 0; j--) {
                boolean proposedPairingIsValid = tryPairBracket(proposedPairings, pairedPlayers, unpairedPlayersInThisBracket);
                if (proposedPairingIsValid) {
                    getDownfloater(unpairedPlayersInThisBracket, pairedPlayers, downfloatersToNextBracket);
                    unorderedPairings.addAll(proposedPairings);
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

    private void printPlayersInBracket(List<SimulatedPlayer> unpairedPlayersInBracket, List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromUpperBracket) {
        if (!downfloatersFromUpperBracket.isEmpty()) {
            printPlayersInBracket(typeOfBracket.DOWNFLOATERS, downfloatersFromUpperBracket);
            printPlayersInBracket(typeOfBracket.NONDOWNFLOATERS, nonDownfloaters);
            printPlayersInBracket(typeOfBracket.TOTAL, unpairedPlayersInBracket);
        } else {
            System.out.println("\nNo downfloaters");
            printPlayersInBracket(typeOfBracket.NONDOWNFLOATERS, nonDownfloaters);
        }
    }

    private void printPlayersInBracket(typeOfBracket type, List<SimulatedPlayer> playersInBracket) {
        System.out.println();
        if (type == typeOfBracket.TOTAL) {
            System.out.println("Total Players in this bracket: " + playersInBracket.size());
        } else if (type == typeOfBracket.DOWNFLOATERS) {
            System.out.println("Downfloaters from upper bracket with score " + playersInBracket.get(playersInBracket.size() - 1).getScore() + ": " + playersInBracket.size());
        } else {
            System.out.println("Players in this bracket for score " + playersInBracket.get(playersInBracket.size() - 1).getScore() + ": " + playersInBracket.size());
        }
        for (SimulatedPlayer player : playersInBracket) {
            System.out.print(player.getParticipant().getName() + ", ");
        }
    }

    private void giveByeToLastEligiblePlayer(List<SimulatedPlayer> unpairedPlayers) {
        for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
            if (!unpairedPlayers.get(i).hasReceivedBye()) {
                unorderedPairings.add(new Pairing(unpairedPlayers.get(i), Tournament.BYE, true));
                unpairedPlayers.get(i).setReceivedBye(true);
            }
        }

    }

    private enum typeOfBracket {
        DOWNFLOATERS,
        NONDOWNFLOATERS,
        TOTAL
    }
}

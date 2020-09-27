import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Round {

    public static List<Pairing> createPairings(List<SimulatedPlayer> players) {
        List<Pairing> unorderedPairings = new ArrayList<>();
        List<SimulatedPlayer> unpairedPlayers = new ArrayList<>(players);
        unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        if (unpairedPlayers.size() % 2 == 1) {
            giveByeToLastEligiblePlayer(unpairedPlayers); // makes pairing process somewhat easier but not necessarily correct pairing...
        }
        List<SimulatedPlayer> downfloaters = new ArrayList<>();
        List<SimulatedPlayer> pairedPlayers = new ArrayList<>();
        while (unpairedPlayers.size() > 0) {
            double highestUnpairedScore = unpairedPlayers.get(0).getScore();
            List<SimulatedPlayer> nextBracket = unpairedPlayers.stream().filter(p -> p.getScore() == highestUnpairedScore).sorted(SimulatedPlayer::compareToByScoreThenElo).collect(Collectors.toList());
            nextBracket.addAll(downfloaters);
            nextBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
            pairedPlayers.clear();
            downfloaters = pairBracket(nextBracket, pairedPlayers, unorderedPairings);
            unpairedPlayers.removeAll(pairedPlayers);
            unpairedPlayers.removeAll(downfloaters);
        }
        return unorderedPairings;
    }

    private static List<SimulatedPlayer> pairBracket(List<SimulatedPlayer> playersInThisBracket, List<SimulatedPlayer> pairedPlayers, List<Pairing> unorderedPairings) {
        if (playersInThisBracket.size() < 2) {
            return playersInThisBracket;
        }
        SimulatedPlayer swappedOutPlayer = null;
        outsideLoops:
        for (int i = playersInThisBracket.size() - 1; i >= 0; --i) {
            for (int j = playersInThisBracket.size() - 1; j >= 0; --j) {
                for (int k = playersInThisBracket.size() - 1; k >= 0; --k) {
                    boolean proposedPairingIsValid = tryPairBracket(playersInThisBracket, pairedPlayers, unorderedPairings);
                    if (proposedPairingIsValid) {
                        break outsideLoops;
                    }
                    if (swappedOutPlayer != null) {
                        playersInThisBracket.add(swappedOutPlayer);
                    }
                    Collections.swap(playersInThisBracket, j, k);
                    if (playersInThisBracket.size() % 2 == 1) {
                        swappedOutPlayer = playersInThisBracket.remove(i);
                    }
                }
            }
        }
        return getDownfloaters(playersInThisBracket, pairedPlayers, swappedOutPlayer);
    }

    private static boolean tryPairBracket(List<SimulatedPlayer> playersInBracket, List<SimulatedPlayer> pairedPlayers, List<Pairing> unorderedPairings) {
        List<Pairing> provisionalPairings = new ArrayList<>(playersInBracket.size() / 2);
        for (int i = 0; i < playersInBracket.size() / 2; i++) {
            if (Pairing.pairingAllowed(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2))) {
                provisionalPairings.add(ThreadLocalRandom.current().nextBoolean() ? new Pairing(playersInBracket.get(i + playersInBracket.size() / 2), playersInBracket.get(i)) : new Pairing(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2)));
            } else {
                return false;
            }
        }
        for (Pairing pairings : provisionalPairings) {
            pairedPlayers.add(pairings.getPlayer1());
            pairedPlayers.add(pairings.getPlayer2());
        }
        unorderedPairings.addAll(provisionalPairings);
        return true;
    }

    private static List<SimulatedPlayer> getDownfloaters(List<SimulatedPlayer> unpairedPlayersInThisBracket, List<SimulatedPlayer> pairedPlayers, SimulatedPlayer swappedOutPlayer) {
        List<SimulatedPlayer> downfloaters = new ArrayList<>();
        if (swappedOutPlayer != null) {
            downfloaters.add(swappedOutPlayer);
        }
        if (unpairedPlayersInThisBracket.size() != pairedPlayers.size()) {
            unpairedPlayersInThisBracket.removeAll(pairedPlayers);
            downfloaters.addAll(unpairedPlayersInThisBracket);
        }
        return downfloaters;
    }

    private static void giveByeToLastEligiblePlayer(List<SimulatedPlayer> unpairedPlayers) {
        for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
            if (!unpairedPlayers.get(i).hasReceivedBye()) {
                Pairing.giveBye(unpairedPlayers.get(i));
                unpairedPlayers.get(i).setReceivedBye(true);
                unpairedPlayers.remove(i);
            }
        }
    }
}

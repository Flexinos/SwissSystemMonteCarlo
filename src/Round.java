import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Round {
    private static final Random random = new Random();

    public static void createPairings(List<SimulatedPlayer> players) {
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
            downfloaters = pairBracket(nextBracket, pairedPlayers);
            unpairedPlayers.removeAll(pairedPlayers);
            unpairedPlayers.removeAll(downfloaters);
        }
    }

    private static List<SimulatedPlayer> pairBracket(List<SimulatedPlayer> playersInThisBracket, List<SimulatedPlayer> pairedPlayers) {
        if (playersInThisBracket.size() < 2) {
            return playersInThisBracket;
        }
        SimulatedPlayer swappedOutPlayer = null;
        outsideLoops:
        for (int i = playersInThisBracket.size() - 1; i >= 0; --i) {
            for (int j = playersInThisBracket.size() - 1; j >= 0; --j) {
                for (int k = playersInThisBracket.size() - 1; k >= 0; --k) {
                    boolean proposedPairingIsValid = tryPairBracket(playersInThisBracket, pairedPlayers);
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

    private static boolean tryPairBracket(List<SimulatedPlayer> playersInBracket, List<SimulatedPlayer> pairedPlayers) {
        List<Pairing> provisionalPairings = new ArrayList<>(playersInBracket.size() / 2);
        for (int i = 0; i < playersInBracket.size() / 2; i++) {
            if (Pairing.pairingAllowed(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2))) {
                provisionalPairings.add(random.nextBoolean() ? new Pairing(playersInBracket.get(i + playersInBracket.size() / 2), playersInBracket.get(i)) : new Pairing(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2)));
            } else {
                return false;
            }
        }
        for (Pairing pairings : provisionalPairings) {
            pairedPlayers.add(pairings.getPlayer1());
            pairedPlayers.add(pairings.getPlayer2());
        }
        provisionalPairings.forEach(Pairing::simulateResult);
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

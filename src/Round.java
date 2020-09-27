import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Round {
    private static final Random random = new Random();

    public static void createPairings(List<SimulatedPlayer> players) {
        List<SimulatedPlayer> unpairedPlayers = new ArrayList<>(players); // maybe change datatype of unpairedPlayers to treeset, allows faster filtering and faster removal
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
            downfloaters = pairBracket(nextBracket, pairedPlayers);
            unpairedPlayers.removeAll(pairedPlayers);
            unpairedPlayers.removeAll(downfloaters);
        }
    }

    private static List<SimulatedPlayer> pairBracket(List<SimulatedPlayer> unpairedPlayersInThisBracket, List<SimulatedPlayer> pairedPlayers) {
        SimulatedPlayer swappedOutPlayer = null;
        outsideLoops:
        for (int i = unpairedPlayersInThisBracket.size() - 1; i >= 0; --i) {
            for (int j = unpairedPlayersInThisBracket.size() - 1; j >= 0; --j) {
                for (int k = unpairedPlayersInThisBracket.size() - 1; k >= 0; --k) {
                    boolean proposedPairingIsValid = tryPairBracket(unpairedPlayersInThisBracket, pairedPlayers);
                    if (proposedPairingIsValid) {
                        break outsideLoops;
                    }
                    if (swappedOutPlayer != null) {
                        unpairedPlayersInThisBracket.add(swappedOutPlayer);
                    }
                    Collections.swap(unpairedPlayersInThisBracket, j, k);
                    if (unpairedPlayersInThisBracket.size() % 2 == 1) {
                        swappedOutPlayer = unpairedPlayersInThisBracket.remove(i);
                    }
                }
            }
        }
        return getDownfloaters(unpairedPlayersInThisBracket, pairedPlayers, swappedOutPlayer);
    }

    private static boolean tryPairBracket(List<SimulatedPlayer> playersInBracket, List<SimulatedPlayer> pairedPlayers) {
        List<Pairing> provisionalPairings = new ArrayList<>(playersInBracket.size() / 2);
        for (int i = 0; i < playersInBracket.size() / 2; i++) {
            if (Pairing.pairingAllowed(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2))) {
                if (random.nextBoolean()) {
                    provisionalPairings.add(new Pairing(playersInBracket.get(i + playersInBracket.size() / 2), playersInBracket.get(i)));
                } else {
                    provisionalPairings.add(new Pairing(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2)));
                }
                pairedPlayers.add(playersInBracket.get(i));
                pairedPlayers.add(playersInBracket.get(i + playersInBracket.size() / 2));
            } else {
                pairedPlayers.clear();
                return false;
            }
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
            }
        }
    }
}

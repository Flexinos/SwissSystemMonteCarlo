import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Round {
    private static final Random random = new Random();

    public static void createPairings(List<SimulatedPlayer> players) {
        List<Pairing> unorderedPairings = new ArrayList<>();
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
            downfloaters = pairBracket(nextBracket, downfloaters, pairedPlayers, unorderedPairings);
            for (Pairing pairing : unorderedPairings) {
                unpairedPlayers.remove(pairing.getPlayer1());
                unpairedPlayers.remove(pairing.getPlayer2());
            }
            unpairedPlayers.removeAll(downfloaters);
            unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        }
    }

    private static List<SimulatedPlayer> pairBracket(List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromPreviousBracket, List<SimulatedPlayer> pairedPlayers, List<Pairing> unorderedPairings) {
        List<SimulatedPlayer> unpairedPlayersInThisBracket = new ArrayList<>(nonDownfloaters);
        unpairedPlayersInThisBracket.addAll(downfloatersFromPreviousBracket);
        unpairedPlayersInThisBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
        List<Pairing> proposedPairings = new ArrayList<>(unpairedPlayersInThisBracket.size() / 2);
        List<SimulatedPlayer> downfloatersToNextBracket = new ArrayList<>();
        for (int i = unpairedPlayersInThisBracket.size() - 1; i >= 0; i--) {
            for (int j = unpairedPlayersInThisBracket.size() - 1; j >= 0; j--) {
                boolean proposedPairingIsValid = tryPairBracket(proposedPairings, pairedPlayers, unpairedPlayersInThisBracket);
                if (proposedPairingIsValid) {
                    getDownfloaters(unpairedPlayersInThisBracket, pairedPlayers, downfloatersToNextBracket);
                    unorderedPairings.addAll(proposedPairings);
                    return downfloatersToNextBracket;
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
        return downfloatersToNextBracket;
    }

    private static boolean tryPairBracket(List<Pairing> proposedPairings, List<SimulatedPlayer> pairedPlayers, List<SimulatedPlayer> playersInBracket) {
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
        proposedPairings.clear();
        proposedPairings.addAll(provisionalPairings);
        proposedPairings.forEach(Pairing::simulateResult);
        return true;
    }

    private static void getDownfloaters(List<SimulatedPlayer> unpairedPlayersInThisBracket, List<SimulatedPlayer> pairedPlayers, List<SimulatedPlayer> downfloatersToNextBracket) {
        if (unpairedPlayersInThisBracket.size() != pairedPlayers.size()) {
            unpairedPlayersInThisBracket.removeAll(pairedPlayers);
            downfloatersToNextBracket.addAll(unpairedPlayersInThisBracket);
        }
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

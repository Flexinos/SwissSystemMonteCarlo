import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class Round {
    private Round() {
    }

    public static List<Pairing> createPairings(final List<SimulatedPlayer> players) {
        final List<Pairing> unorderedPairings = new ArrayList<>();
        final List<SimulatedPlayer> unpairedPlayers = new ArrayList<>(players);
        unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        if ((unpairedPlayers.size() % 2) == 1) {
            giveByeToLastEligiblePlayer(unpairedPlayers); // makes pairing process somewhat easier but not necessarily correct pairing...
        }
        List<SimulatedPlayer> downfloaters = new ArrayList<>();
        final List<SimulatedPlayer> pairedPlayers = new ArrayList<>();
        while (!unpairedPlayers.isEmpty()) {
            final float highestUnpairedScore = unpairedPlayers.get(0).getScore();
            final List<SimulatedPlayer> nextBracket = unpairedPlayers.stream().filter((SimulatedPlayer p) -> Float.compare(p.getScore(), highestUnpairedScore) == 0).sorted(SimulatedPlayer::compareToByScoreThenElo).collect(Collectors.toList());
            nextBracket.addAll(downfloaters);
            nextBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
            pairedPlayers.clear();
            downfloaters = pairBracket(nextBracket, pairedPlayers, unorderedPairings);
            unpairedPlayers.removeAll(pairedPlayers);
            unpairedPlayers.removeAll(downfloaters);
        }
        return unorderedPairings;
    }

    private static List<SimulatedPlayer> pairBracket(final List<SimulatedPlayer> playersInThisBracket, final List<SimulatedPlayer> pairedPlayers, final List<Pairing> unorderedPairings) {
        if (playersInThisBracket.size() < 2) {
            return playersInThisBracket;
        }
        SimulatedPlayer swappedOutPlayer = null;
        outsideLoops:
        for (int i = playersInThisBracket.size() - 1; i >= 0; --i) {
            for (int j = playersInThisBracket.size() - 1; j >= 0; --j) {
                for (int k = playersInThisBracket.size() - 1; k >= 0; --k) {
                    final boolean proposedPairingIsValid = tryPairBracket(playersInThisBracket, pairedPlayers, unorderedPairings);
                    if (proposedPairingIsValid) {
                        break outsideLoops;
                    }
                    if (swappedOutPlayer != null) {
                        playersInThisBracket.add(swappedOutPlayer);
                    }
                    Collections.swap(playersInThisBracket, j, k);
                    if ((playersInThisBracket.size() % 2) == 1) {
                        swappedOutPlayer = playersInThisBracket.remove(i);
                    }
                }
            }
        }
        return getDownfloaters(playersInThisBracket, pairedPlayers, swappedOutPlayer);
    }

    private static boolean tryPairBracket(final List<SimulatedPlayer> playersInBracket, final Collection<SimulatedPlayer> pairedPlayers, final Collection<Pairing> unorderedPairings) {
        final Collection<Pairing> provisionalPairings = new ArrayList<>(playersInBracket.size() / 2);
        for (int i = 0; i < (playersInBracket.size() / 2); i++) {
            if (Pairing.canBePaired(playersInBracket.get(i), playersInBracket.get(i + (playersInBracket.size() / 2)))) {
                provisionalPairings.add(ThreadLocalRandom.current().nextBoolean() ? new Pairing(playersInBracket.get(i + (playersInBracket.size() / 2)), playersInBracket.get(i)) : new Pairing(playersInBracket.get(i), playersInBracket.get(i + (playersInBracket.size() / 2))));
            } else {
                return false;
            }
        }
        for (final Pairing pairings : provisionalPairings) {
            pairedPlayers.add(pairings.getPlayer1());
            pairedPlayers.add(pairings.getPlayer2());
        }
        unorderedPairings.addAll(provisionalPairings);
        return true;
    }

    private static List<SimulatedPlayer> getDownfloaters(final Collection<SimulatedPlayer> unpairedPlayersInThisBracket, final Collection<SimulatedPlayer> pairedPlayers, final SimulatedPlayer swappedOutPlayer) {
        final List<SimulatedPlayer> downfloaters = new ArrayList<>();
        if (swappedOutPlayer != null) {
            downfloaters.add(swappedOutPlayer);
        }
        if (unpairedPlayersInThisBracket.size() != pairedPlayers.size()) {
            unpairedPlayersInThisBracket.removeAll(pairedPlayers);
            downfloaters.addAll(unpairedPlayersInThisBracket);
        }
        return downfloaters;
    }

    private static void giveByeToLastEligiblePlayer(final List<SimulatedPlayer> unpairedPlayers) {
        for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
            if (!unpairedPlayers.get(i).hasReceivedBye()) {
                unpairedPlayers.get(i).giveBye();
                unpairedPlayers.remove(i);
            }
        }
    }
}

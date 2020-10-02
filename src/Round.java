import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class Round {
    private Round() {
    }

    public static List<Pairing> createPairings(final List<Participant> players) {
        final List<Pairing> unorderedPairings = new ArrayList<>();
        final List<Participant> unpairedPlayers = new ArrayList<>(players);
        unpairedPlayers.sort(Participant::compareToByScoreThenElo);
        if ((unpairedPlayers.size() % 2) == 1) {
            giveByeToLastEligiblePlayer(unpairedPlayers); // makes pairing process somewhat easier but not necessarily correct pairing...
        }
        List<Participant> downfloaters = new ArrayList<>();
        final List<Participant> pairedPlayers = new ArrayList<>();
        while (!unpairedPlayers.isEmpty()) {
            final float highestUnpairedScore = unpairedPlayers.get(0).getScore();
            final List<Participant> nextBracket = unpairedPlayers.stream().filter((Participant p) -> Float.compare(p.getScore(), highestUnpairedScore) == 0).sorted(Participant::compareToByScoreThenElo).collect(Collectors.toList());
            nextBracket.addAll(downfloaters);
            nextBracket.sort(Participant::compareToByScoreThenElo);
            pairedPlayers.clear();
            downfloaters = pairBracket(nextBracket, pairedPlayers, unorderedPairings);
            unpairedPlayers.removeAll(pairedPlayers);
            unpairedPlayers.removeAll(downfloaters);
        }
        return unorderedPairings;
    }

    private static List<Participant> pairBracket(final List<Participant> playersInThisBracket, final List<Participant> pairedPlayers, final List<Pairing> unorderedPairings) {
        if (playersInThisBracket.size() < 2) {
            return playersInThisBracket;
        }
        Participant swappedOutPlayer = null;
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

    private static boolean tryPairBracket(final List<Participant> playersInBracket, final Collection<Participant> pairedPlayers, final Collection<Pairing> unorderedPairings) {
        final Collection<Pairing> provisionalPairings = new ArrayList<>(playersInBracket.size() / 2);
        for (int i = 0; i < (playersInBracket.size() / 2); i++) {
            if (Pairing.canBePaired(playersInBracket.get(i), playersInBracket.get(i + (playersInBracket.size() / 2)))) {
                provisionalPairings.add(ThreadLocalRandom.current().nextBoolean() ? new Pairing(playersInBracket.get(i + (playersInBracket.size() / 2)), playersInBracket.get(i)) : new Pairing(playersInBracket.get(i), playersInBracket.get(i + (playersInBracket.size() / 2))));
            } else {
                return false;
            }
        }
        for (final Pairing pairings : provisionalPairings) {
            pairedPlayers.add(pairings.getWhitePlayer());
            pairedPlayers.add(pairings.getBlackPlayer());
        }
        unorderedPairings.addAll(provisionalPairings);
        return true;
    }

    private static List<Participant> getDownfloaters(final Collection<Participant> unpairedPlayersInThisBracket, final Collection<Participant> pairedPlayers, final Participant swappedOutPlayer) {
        final List<Participant> downfloaters = new ArrayList<>();
        if (swappedOutPlayer != null) {
            downfloaters.add(swappedOutPlayer);
        }
        if (unpairedPlayersInThisBracket.size() != pairedPlayers.size()) {
            unpairedPlayersInThisBracket.removeAll(pairedPlayers);
            downfloaters.addAll(unpairedPlayersInThisBracket);
        }
        return downfloaters;
    }

    private static void giveByeToLastEligiblePlayer(final List<Participant> unpairedPlayers) {
        for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
            if (!unpairedPlayers.get(i).hasReceivedBye()) {
                unpairedPlayers.get(i).giveBye();
                unpairedPlayers.remove(i);
            }
        }
    }
}

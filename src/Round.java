import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Round {
    private final Ranking rankingByEloBeforeRound;
    private final HashSet<Pairing> pairings = new HashSet<>();

    public Round(Ranking rankingByEloBeforeRound) {
        this.rankingByEloBeforeRound = rankingByEloBeforeRound;
        createPairings();
    }

    private PairingIsValidTuple tryPairBracket(int board, List<SimulatedPlayer> playersInBracket) {
        List<PossiblePairing> provisionalPairings = new ArrayList<>();
        List<Pairing> returnedPairings = new ArrayList<>();
        for (int i = 0; i < playersInBracket.size() / 2; i++) {
            if (Pairing.pairingAllowed(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2))) {
                //todo add considerations to color
                provisionalPairings.add(new PossiblePairing(board++, playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2)));
            } else {
                return new PairingIsValidTuple(returnedPairings, false);
            }
        }
        System.out.println("\nGames:");
        for (PossiblePairing possiblePairing : provisionalPairings) {
            returnedPairings.add(new Pairing(possiblePairing));
        }
        return new PairingIsValidTuple(returnedPairings, true);
    }

    private PairingDownfloaterPair pairBracket(int board, List<SimulatedPlayer> playersInBracket, List<SimulatedPlayer> downfloatersFromUpperBracket) {
        if (downfloatersFromUpperBracket.isEmpty()) {
            System.out.println("\nNo downfloaters");
        } else {
            System.out.println("\nDownfloaters from upper bracket: ");
            for (SimulatedPlayer player : downfloatersFromUpperBracket) {
                System.out.print(player.getParticipant().getName() + ", ");
            }
            System.out.println();
        }
        System.out.println("Players in bracket for score " + playersInBracket.get(0).getScore() + ": " + playersInBracket.size());
        for (SimulatedPlayer player : playersInBracket) {
            System.out.print(player.getParticipant().getName() + ", ");
        }
        System.out.println();

        List<SimulatedPlayer> unpairedPlayersInBracket = new ArrayList<>(playersInBracket);
        unpairedPlayersInBracket.addAll(downfloatersFromUpperBracket);
        unpairedPlayersInBracket.sort(SimulatedPlayer::compareToByScoreThenElo);

        if (!downfloatersFromUpperBracket.isEmpty()) {
            System.out.println("Total players in bracket: " + unpairedPlayersInBracket.size());
            for (SimulatedPlayer player : unpairedPlayersInBracket) {
                System.out.print(player.getParticipant().getName() + ", ");
            }
            System.out.println();
        }

        List<SimulatedPlayer> downfloatersToNextBracket = new ArrayList<>();
        PairingIsValidTuple proposedPairing = tryPairBracket(board, unpairedPlayersInBracket);
        if (proposedPairing.getIsValid()) {
            List<Pairing> proposedPairingPairings = proposedPairing.getPairings();
            for (Pairing pairing : proposedPairingPairings) {
                unpairedPlayersInBracket.remove(pairing.getPlayer1());
                unpairedPlayersInBracket.remove(pairing.getPlayer2());
            }
            if (unpairedPlayersInBracket.size() % 2 == 1) {
                downfloatersToNextBracket.add(unpairedPlayersInBracket.remove(unpairedPlayersInBracket.size() - 1));
            }
            for (SimulatedPlayer player : downfloatersToNextBracket) {
                System.out.println(player.getParticipant().getName());
            }
            return new PairingDownfloaterPair(proposedPairing.getPairings(), downfloatersToNextBracket);
        } else {
            for (int i = playersInBracket.size() - 1; i >= 0; i--) {
                for (int j = playersInBracket.size() - 1; j >= 0; j--) {
                    if (!downfloatersToNextBracket.isEmpty()) {
                        unpairedPlayersInBracket.add(downfloatersToNextBracket.remove(0));
                    }
                    Collections.swap(unpairedPlayersInBracket, i, j);
                    if (unpairedPlayersInBracket.size() % 2 == 1) {
                        downfloatersToNextBracket.add(unpairedPlayersInBracket.remove(unpairedPlayersInBracket.size() - 1));
                    }
                    proposedPairing = tryPairBracket(board, unpairedPlayersInBracket);
                    if (proposedPairing.getIsValid()) {
                        for (Pairing pairing : proposedPairing.getPairings()) {
                            unpairedPlayersInBracket.remove(pairing.getPlayer1());
                            unpairedPlayersInBracket.remove(pairing.getPlayer2());
                        }
                        downfloatersToNextBracket.addAll(unpairedPlayersInBracket);
                        return new PairingDownfloaterPair(proposedPairing.getPairings(), downfloatersToNextBracket);
                    }
                    if (!downfloatersToNextBracket.isEmpty()) {
                        unpairedPlayersInBracket.add(downfloatersToNextBracket.remove(0));
                        unpairedPlayersInBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
                    }
                    //Collections.swap(unpairedPlayersInBracket, i, j); slows down simulation, but may be closer to fide regulations
                }
            }
        }
        downfloatersToNextBracket.addAll(unpairedPlayersInBracket);
        return new PairingDownfloaterPair(new ArrayList<>(), downfloatersToNextBracket);
    }

    private void createPairings() {
        int board = 1;
        List<SimulatedPlayer> unpairedPlayers = rankingByEloBeforeRound.getRanking();
        List<SimulatedPlayer> downfloatersFromPreviousBracket = new ArrayList<>();
        while (unpairedPlayers.size() > 0) {
            double highestUnpairedScore = unpairedPlayers.get(0).getScore();
            List<SimulatedPlayer> nextBracket = unpairedPlayers.stream()
                    .filter(p -> p.getScore() == highestUnpairedScore).sorted(SimulatedPlayer::compareToByScoreThenElo).collect(Collectors.toList());
            PairingDownfloaterPair proposedPairings = pairBracket(board, nextBracket, downfloatersFromPreviousBracket);
            downfloatersFromPreviousBracket.clear();
            pairings.addAll(proposedPairings.getPairings());
            board += proposedPairings.getPairings().size();
            downfloatersFromPreviousBracket.addAll(proposedPairings.getDownfloater());
            for (Pairing pairing : proposedPairings.getPairings()) {
                unpairedPlayers.remove(pairing.getPlayer1());
                unpairedPlayers.remove(pairing.getPlayer2());
            }
            for (SimulatedPlayer player : proposedPairings.getDownfloater()) {
                unpairedPlayers.remove(player);
            }
            unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        }
    }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Round {

    private final Ranking rankingByScoreThenEloBeforeRound;
    private final List<Pairing> unorderedPairings;

    public Round(Ranking rankingByScoreThenEloBeforeRound) {
        this.rankingByScoreThenEloBeforeRound = rankingByScoreThenEloBeforeRound;
        this.unorderedPairings = new ArrayList<>(rankingByScoreThenEloBeforeRound.getRanking().size() + 1);
        createPairings();
    }

    private PairingDownfloaterPair pairBracket(int board, List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromUpperBracket) {
        boolean downfloatersPresent = !downfloatersFromUpperBracket.isEmpty();
        List<SimulatedPlayer> unpairedPlayersInBracket = new ArrayList<>(nonDownfloaters);
        List<SimulatedPlayer> downfloatersToNextBracket = new ArrayList<>();
        printPlayersInBracketWithPossibleDownfloaters(nonDownfloaters, downfloatersFromUpperBracket, downfloatersPresent, unpairedPlayersInBracket);

        PairingIsValidTuple proposedPairing = tryPairBracket(board, unpairedPlayersInBracket);

        if (proposedPairing.getIsValid()) {
            for (Pairing pairing : proposedPairing.getPairings()) {
                unpairedPlayersInBracket.remove(pairing.getPlayer1());
                unpairedPlayersInBracket.remove(pairing.getPlayer2());
            }
            //todo check if this is only scenario with downfloaters
            if (unpairedPlayersInBracket.size() % 2 == 1) {
                downfloatersToNextBracket.add(unpairedPlayersInBracket.remove(unpairedPlayersInBracket.size() - 1));
            }
            return new PairingDownfloaterPair(proposedPairing.getPairings(), downfloatersToNextBracket);
        } else {
            for (int i = unpairedPlayersInBracket.size() - 1; i >= 0; i--) {
                for (int j = unpairedPlayersInBracket.size() - 1; j >= 0; j--) {
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

    private void printPlayersInBracketWithPossibleDownfloaters(List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromUpperBracket, boolean downfloatersPresent, List<SimulatedPlayer> unpairedPlayersInBracket) {
        if (downfloatersPresent) {
            printPlayersInBracket(typeOfBracket.DOWNFLOATERS, downfloatersFromUpperBracket);
            printPlayersInBracket(typeOfBracket.NONDOWNFLOATERS, nonDownfloaters);
            unpairedPlayersInBracket.addAll(downfloatersFromUpperBracket);
            unpairedPlayersInBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
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

    private void createPairings() {
        //todo give lowest player bye
        int board = 1;
        List<SimulatedPlayer> unpairedPlayers = rankingByScoreThenEloBeforeRound.getRanking();
        List<SimulatedPlayer> downfloatersFromPreviousBracket = new ArrayList<>();
        if (unpairedPlayers.size() % 2 == 1) {
            int lastBoard = unpairedPlayers.size() / 2 + 1;
            for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
                if (!unpairedPlayers.get(i).receivedBye()) {
                    unorderedPairings.add(new Pairing(new PossiblePairing(lastBoard, unpairedPlayers.get(i), Tournament.BYE)));
                }
            }
        }
        while (unpairedPlayers.size() > 0) {
            double highestUnpairedScore = unpairedPlayers.get(0).getScore();
            List<SimulatedPlayer> nextBracket = unpairedPlayers.stream().filter(p -> p.getScore() == highestUnpairedScore).sorted(SimulatedPlayer::compareToByScoreThenElo).collect(Collectors.toList());
            PairingDownfloaterPair proposedPairings = pairBracket(board, nextBracket, downfloatersFromPreviousBracket);
            downfloatersFromPreviousBracket.clear();
            unorderedPairings.addAll(proposedPairings.getPairings());
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

    private enum typeOfBracket {
        DOWNFLOATERS,
        NONDOWNFLOATERS,
        TOTAL
    }
}

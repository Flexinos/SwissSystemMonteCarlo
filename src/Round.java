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

    private void pairBracket(int board, List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromPreviousBracket, List<SimulatedPlayer> downfloatersToNextBracket) {
        boolean downfloatersPresent = !downfloatersFromPreviousBracket.isEmpty();
        List<SimulatedPlayer> unpairedPlayersInThisBracket = new ArrayList<>(nonDownfloaters);
        printAndUpdateBracket(nonDownfloaters, downfloatersFromPreviousBracket, downfloatersPresent, unpairedPlayersInThisBracket);

        List<Pairing> proposedPairings = new ArrayList<>();
        //todo check if this makes sense

        for (int i = unpairedPlayersInThisBracket.size() - 1; i >= 0; i--) {
            for (int j = unpairedPlayersInThisBracket.size() - 1; j >= 0; j--) {
                boolean proposedPairingIsValid = tryPairBracket(proposedPairings, board, unpairedPlayersInThisBracket);
                if (proposedPairingIsValid) {
                    getDownfloater(unpairedPlayersInThisBracket, downfloatersToNextBracket, proposedPairings);
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

    private void getDownfloater(List<SimulatedPlayer> unpairedPlayersInThisBracket, List<SimulatedPlayer> downfloatersToNextBracket, List<Pairing> proposedPairings) {
        for (Pairing pairing : proposedPairings) {
            unpairedPlayersInThisBracket.remove(pairing.getPlayer1());
            unpairedPlayersInThisBracket.remove(pairing.getPlayer2());
        }
        //only adds one player. not sure if scenario with multiple downfloaters can exist (probably can)
        if (!unpairedPlayersInThisBracket.isEmpty()) {
            downfloatersToNextBracket.add(unpairedPlayersInThisBracket.remove(unpairedPlayersInThisBracket.size() - 1));
        }
    }

    private boolean tryPairBracket(List<Pairing> proposedPairings, int board, List<SimulatedPlayer> playersInBracket) {
        List<PossiblePairing> provisionalPairings = new ArrayList<>();
        for (int i = 0; i < playersInBracket.size() / 2; i++) {
            if (Pairing.pairingAllowed(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2))) {
                //todo add considerations to color
                provisionalPairings.add(new PossiblePairing(board++, playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2)));
            } else {
                return false;
            }
        }
        System.out.println("\nGames:");
        proposedPairings.clear();
        for (PossiblePairing possiblePairing : provisionalPairings) {
            proposedPairings.add(new Pairing(possiblePairing));
        }
        return true;
    }

    private void printAndUpdateBracket(List<SimulatedPlayer> nonDownfloaters, List<SimulatedPlayer> downfloatersFromUpperBracket, boolean downfloatersPresent, List<SimulatedPlayer> unpairedPlayersInBracket) {
        //todo remove side effects! method should only print
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
        List<SimulatedPlayer> unpairedPlayers = rankingByScoreThenEloBeforeRound.getRanking();
        giveByeIfNecessary(unpairedPlayers);
        List<SimulatedPlayer> downfloatersFromPreviousBracket = new ArrayList<>();
        List<SimulatedPlayer> downfloatersToNextBracket = new ArrayList<>();
        while (unpairedPlayers.size() > 0) {
            double highestUnpairedScore = unpairedPlayers.get(0).getScore();
            int board = unorderedPairings.size() + 1;
            List<SimulatedPlayer> nextBracket = unpairedPlayers.stream().filter(p -> p.getScore() == highestUnpairedScore).sorted(SimulatedPlayer::compareToByScoreThenElo).collect(Collectors.toList());
            downfloatersToNextBracket.clear();
            pairBracket(board, nextBracket, downfloatersFromPreviousBracket, downfloatersToNextBracket);
            downfloatersFromPreviousBracket.clear();
            downfloatersFromPreviousBracket.addAll(downfloatersToNextBracket);
            for (Pairing pairing : unorderedPairings) {
                unpairedPlayers.remove(pairing.getPlayer1());
                unpairedPlayers.remove(pairing.getPlayer2());
            }
            for (SimulatedPlayer player : downfloatersToNextBracket) {
                unpairedPlayers.remove(player);
            }
            unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
        }
    }

    private void giveByeIfNecessary(List<SimulatedPlayer> unpairedPlayers) {
        if (unpairedPlayers.size() % 2 == 1) {
            int lastBoard = unpairedPlayers.size() / 2 + 1;
            for (int i = unpairedPlayers.size() - 1; i > 0; i--) {
                if (!unpairedPlayers.get(i).receivedBye()) {
                    unorderedPairings.add(new Pairing(new PossiblePairing(lastBoard, unpairedPlayers.get(i), Tournament.BYE)));
                }
            }
        }
    }

    private enum typeOfBracket {
        DOWNFLOATERS,
        NONDOWNFLOATERS,
        TOTAL
    }
}

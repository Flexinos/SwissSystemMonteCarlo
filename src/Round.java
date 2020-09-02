import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Round {
    private final Ranking rankingByEloBeforeRound;
    private HashSet<Pairing> pairings = new HashSet<>();

    public Round(Ranking rankingByEloBeforeRound) {
        this.rankingByEloBeforeRound = rankingByEloBeforeRound;
        createPairings();
    }

    public HashSet<Pairing> getPairings() {
        return pairings;
    }

    private PairingValidpairingPair tryPairBracket(int board, List<SimulatedPlayer> playersInBracket) {
        ArrayList<PossiblePairing> possiblePairings = new ArrayList<>();
        ArrayList<Pairing> pairings = new ArrayList<>();
        boolean isValid = true;
        if (playersInBracket.size() == 0) {
            System.out.println("pairing empty list");
            return new PairingValidpairingPair(pairings, true);
        }
        for (int i = 0; i < playersInBracket.size() / 2; i++) {
            if (Pairing.pairingAllowed(playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2))) {
                //todo add considerations to color
                possiblePairings.add(new PossiblePairing(board++, playersInBracket.get(i), playersInBracket.get(i + playersInBracket.size() / 2)));
            } else {
                isValid = false;
                break;
            }
        }
        if (isValid) {
            System.out.println("\nGames:");
            for (PossiblePairing possiblePairing : possiblePairings) {
                pairings.add(new Pairing(possiblePairing));
            }
        }
        return new PairingValidpairingPair(pairings, isValid);
    }

    private PairingDownfloaterPair pairBracket(int board, List<SimulatedPlayer> playersInBracket, ArrayList<SimulatedPlayer> downfloatersFromUpperBracket) {
        if (!downfloatersFromUpperBracket.isEmpty()) {
            System.out.println("\nDownfloaters from upper bracket: ");
            for (SimulatedPlayer player : downfloatersFromUpperBracket) {
                System.out.print(player.getParticipant().getName() + ", ");
            }
            System.out.println();
        } else {
            System.out.println("\nNo downfloaters");
        }
        System.out.println("Players in bracket for score " + String.valueOf(playersInBracket.get(0).getScore()) + ": " + playersInBracket.size());
        for (SimulatedPlayer player : playersInBracket) {
            System.out.print(player.getParticipant().getName() + ", ");
        }
        System.out.println();

        List<SimulatedPlayer> tmpList = new ArrayList<>(playersInBracket);
        tmpList.addAll(downfloatersFromUpperBracket);
        tmpList.sort(SimulatedPlayer::compareToByScoreThenElo);
        if (!downfloatersFromUpperBracket.isEmpty()) {
            System.out.println("Total players in bracket: " + tmpList.size());
            for (SimulatedPlayer player : tmpList) {
                System.out.print(player.getParticipant().getName() + ", ");
            }
            System.out.println();
        }

        ArrayList<SimulatedPlayer> downfloatersToNextBracket = new ArrayList<>();
        PairingValidpairingPair proposedPairing = tryPairBracket(board, tmpList);
        if (proposedPairing.getIsValid()) {
            for (Pairing pairing : proposedPairing.getPairings()) {
                tmpList.remove(pairing.getPlayer1());
                tmpList.remove(pairing.getPlayer2());
            }
            if (tmpList.size() % 2 == 1) {
                downfloatersToNextBracket.add(tmpList.remove(tmpList.size() - 1));
            }
            System.out.println("Downfloaters to next bracket: ");
            for (SimulatedPlayer player : downfloatersToNextBracket) {
                System.out.println(player.getParticipant().getName());
            }
            return new PairingDownfloaterPair(proposedPairing.getPairings(), downfloatersToNextBracket);
        } else {
            System.out.println();
            for (int i = playersInBracket.size() - 1; i >= 0; i--) {
                for (int j = playersInBracket.size() - 1; j >= 0; j--) {
                    if (!downfloatersToNextBracket.isEmpty()) {
                        tmpList.add(downfloatersToNextBracket.remove(0));
                    }
                    Collections.swap(tmpList, i, j);
                    System.out.println("swapping list");
                    if (tmpList.size() % 2 == 1) {
                        downfloatersToNextBracket.add(tmpList.remove(tmpList.size() - 1));
                    }
                    proposedPairing = tryPairBracket(board, tmpList);
                    if (!downfloatersToNextBracket.isEmpty()) {
                        tmpList.add(downfloatersToNextBracket.remove(0));
                        tmpList.sort(SimulatedPlayer::compareToByScoreThenElo);
                    }
                    if (proposedPairing.getIsValid()) {
                        for (Pairing pairing : proposedPairing.getPairings()) {
                            tmpList.remove(pairing.getPlayer1());
                            tmpList.remove(pairing.getPlayer2());
                        }
                        downfloatersToNextBracket.addAll(tmpList);
                        return new PairingDownfloaterPair(proposedPairing.getPairings(), downfloatersToNextBracket);
                    }
                }
            }
        }
        downfloatersToNextBracket.addAll(tmpList);
        return new PairingDownfloaterPair(new ArrayList<Pairing>(), downfloatersToNextBracket);
        //todo baaadd
        //throw new RuntimeException("Pairings kinda not possible (won't fix)");
    }

    private void createPairings() {
        int board = 1;
        ArrayList<SimulatedPlayer> unpairedPlayers = rankingByEloBeforeRound.getRanking();
        ArrayList<SimulatedPlayer> downfloaters = new ArrayList<>();
        while (unpairedPlayers.size() > 0) {
            double highestScore = unpairedPlayers.get(0).getScore();
            List<SimulatedPlayer> nextBracket = unpairedPlayers.stream()
                    .filter(p -> p.getScore() == highestScore).collect(Collectors.toList());
            nextBracket.sort(SimulatedPlayer::compareToByScoreThenElo);
            PairingDownfloaterPair proposedPairings = pairBracket(board, nextBracket, downfloaters);
            downfloaters.clear();
            pairings.addAll(proposedPairings.getPairings());
            downfloaters.addAll(proposedPairings.getDownfloater());
            unpairedPlayers.sort(SimulatedPlayer::compareToByScoreThenElo);
            board += proposedPairings.getPairings().size();
            for (Pairing pairing : proposedPairings.getPairings()) {
                unpairedPlayers.remove(pairing.getPlayer1());
                unpairedPlayers.remove(pairing.getPlayer2());
            }
            for (SimulatedPlayer player : proposedPairings.getDownfloater()) {
                unpairedPlayers.remove(player);
            }
        }
    }
}

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static final int numberOfParticipants = 200;
    public static final int numberOfRounds = 9;
    public static final int numberOfSimulations = 10000;
    public static final int numberOfConcurrentThreads = 4;

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        final Random random = new Random();
        Tournament myTournament = new Tournament(numberOfRounds);

        List<Participant> participants = IntStream.range(0, numberOfParticipants).mapToObj(i -> new Participant("player " + i, 1000 + random.nextInt(1600))).collect(Collectors.toList());
        myTournament.addParticipants(participants);

        for (int i = 0; i < numberOfSimulations; i++) {
            SimulatedTournament mySimulatedTournament = new SimulatedTournament(myTournament);
            mySimulatedTournament.simulateTournament();
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }

        Set<Participant> topThreeCounterKeySet = myTournament.topThreeCounter.keySet();
        List<Participant> participantsWithTopThreeRanking = new ArrayList<>(topThreeCounterKeySet);
        myTournament.topThreeCounter.forEach((participant, longAdder) -> participantsWithTopThreeRanking.get(participantsWithTopThreeRanking.indexOf(participant)).setNumberOfTopThreeFinishes(longAdder.intValue()));
        participantsWithTopThreeRanking.sort(Participant::compareToByTopThreeFinishes);
        System.out.println("\nNumber of top three finishes:\n");
        for (Participant participant : participantsWithTopThreeRanking) {
            System.out.println(participant.getName() + "\t" + participant.getElo() + "\t" + participant.getNumberOfTopThreeFinishes());
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("\nTotal runtime: " + duration + "ms");
    }
}

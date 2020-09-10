import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static final int numberOfParticipants = 100;
    public static final int numberOfRounds = 9;
    public static final int numberOfSimulations = 10000;
    public static final int numberOfConcurrentThreads = 6;

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();
        final Random random = new Random();
        Tournament myTournament = new Tournament(numberOfRounds, IntStream.range(0, numberOfParticipants).mapToObj(i -> new Participant("player " + i, 1000 + random.nextInt(1600))).collect(Collectors.toList()));

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < numberOfConcurrentThreads; i++) {
            WorkerThread workerThread = new WorkerThread(myTournament, (int) Math.ceil((double) numberOfSimulations / numberOfConcurrentThreads));
            Thread thread = new Thread(workerThread);
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        myTournament.topThreeCounter.forEach((participant, longAdder) -> participant.setNumberOfTopThreeFinishes(longAdder.intValue()));
        List<Participant> participantsWithTopThreeRanking = new ArrayList<>(myTournament.topThreeCounter.keySet());
        participantsWithTopThreeRanking.sort(Participant::compareToByTopThreeFinishes);
        System.out.println("\n\nNumber of top three finishes:\n");
        participantsWithTopThreeRanking.stream().map(participant -> participant.getName() + "\t" + "starting rank: " + participant.getStartingRank() + "\t" + "Elo: " + participant.getElo() + "\t" + participant.getNumberOfTopThreeFinishes()).forEach(System.out::println);

        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println("\nTotal runtime: " + duration / 1000 + "." + duration % 1000 + "s");
    }
}

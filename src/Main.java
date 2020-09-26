import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static final int numberOfParticipants = 100;
    public static final int numberOfRounds = 9;
    public static final int numberOfSimulations = 100000;
    public static final int numberOfConcurrentThreads = 6;
    public static final int minElo = 1000;
    public static final int maxElo = 2600;
    private static int finished_simulations = 0;
    public static final Map<Participant, LongAdder> topThreeCounter =
            new ConcurrentHashMap<>(numberOfParticipants, 0.75f, numberOfConcurrentThreads);

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Specify the location of the lookup table as the only argument.");
            System.exit(1);
        }
        Timer entireProcessTimer = new Timer();
        String lookupTableFile = args[0];
        // ATTENTION: The lookupTableFile's contents must match the variables LOWEST_ELO and HIGHEST_ELO.
        LookupTable.createLookupTable(lookupTableFile);
        System.out.println("Time spent creating lookupTable: " +
                millisecondsToSecondsString(entireProcessTimer.elapsedMilliSeconds()) + System.lineSeparator());

        Tournament myTournament = createTournament();

        Timer simulationsTimer = new Timer();

        simulateTournament(myTournament);

        System.out.println("Simulation runtime: " +
                millisecondsToSecondsString(simulationsTimer.elapsedMilliSeconds()) + System.lineSeparator());

        showResults();

        System.out.println(System.lineSeparator() + "Total runtime: " +
                millisecondsToSecondsString(entireProcessTimer.elapsedMilliSeconds()));
    }

    private static Tournament createTournament() {
        final Random random = new Random();
        return new Tournament(numberOfRounds, IntStream.range(0, numberOfParticipants)
                .mapToObj(i -> new Participant("player " + i, minElo + random.nextInt(maxElo - minElo)))
                .collect(Collectors.toList()));
    }

    private static void simulateTournament(Tournament tournament) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(numberOfConcurrentThreads);
        for (int i = 0; i < numberOfConcurrentThreads; ++i) {
            WorkerThread workerThread = new WorkerThread(tournament, numberOfSimulations);
            pool.execute(workerThread);
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
    }

    private static void showResults() {
        topThreeCounter.forEach((participant, longAdder) -> participant.setNumberOfTopThreeFinishes(longAdder.intValue()));
        List<Participant> participantsWithTopThreeRanking = new ArrayList<>(topThreeCounter.keySet());
        participantsWithTopThreeRanking.sort(Participant::compareToByTopThreeFinishes);
        Participant.printSimulationResults(participantsWithTopThreeRanking);
    }

    synchronized public static int getSimulationTicket() {
        return ++finished_simulations;
    }

    private static class Timer {
        private final long startTime;

        public Timer() {
            this.startTime = System.nanoTime();
        }

        public long elapsedMilliSeconds() {
            return (System.nanoTime() - startTime) / 1000000;
        }
    }

    private static String millisecondsToSecondsString(long milliseconds) {
        return milliseconds / 1000 + "." + milliseconds % 1000 + "s";
    }
}
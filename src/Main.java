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
    public static final Map<Participant, LongAdder> topThreeCounter = new ConcurrentHashMap<>(numberOfParticipants, 0.75f, numberOfConcurrentThreads);
    public static final LongAdder[][] rankingTable = new LongAdder[numberOfParticipants][numberOfParticipants];

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();
        LookupTable.createLookupTable();
        long timeSpentCreatingLookupTable = (System.nanoTime() - startTime) / 1000000;
        System.out.println("Time spent creating lookupTable: " + timeSpentCreatingLookupTable / 1000 + "." + timeSpentCreatingLookupTable % 1000 + "s\n");
        initializeRankingTable();

        final Random random = new Random();
        Tournament myTournament = new Tournament(numberOfRounds, IntStream.range(0, numberOfParticipants).mapToObj(i -> new Participant("player " + i, minElo + random.nextInt(maxElo - minElo))).collect(Collectors.toList()));

        long timeBeforeSimulations = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(numberOfConcurrentThreads);
        for (int i = 0; i < numberOfConcurrentThreads; ++i) {
            WorkerThread workerThread = new WorkerThread(myTournament, numberOfSimulations);
            pool.execute(workerThread);
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);

        long timeAfterSimulations = (System.nanoTime() - timeBeforeSimulations) / 1000000;
        System.out.println("Simulation runtime: " + timeAfterSimulations / 1000 + "." + timeAfterSimulations % 1000 + "s\n");

        topThreeCounter.forEach((participant, longAdder) -> participant.setNumberOfTopThreeFinishes(longAdder.intValue()));
        List<Participant> participantsWithTopThreeRanking = new ArrayList<>(topThreeCounter.keySet());
        participantsWithTopThreeRanking.sort(Participant::compareToByTopThreeFinishes);
        Participant.printSimulationResults(participantsWithTopThreeRanking);

        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println("\nTotal runtime: " + duration / 1000 + "." + duration % 1000 + "s");
    }

    private static void initializeRankingTable() {
        for (int row = 0; row < rankingTable.length; ++row) {
            for (int column = 0; column < rankingTable.length; ++column) {
                rankingTable[row][column] = new LongAdder();
            }
        }
    }

    public static void addRankToTable(SimulatedPlayer p, int rank) {
        rankingTable[p.getParticipant().getStartingRank() - 1][rank].increment();
    }

    synchronized public static int getSimulationTicket() {
        return finished_simulations++;
    }
}
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
    public static final int numberOfSimulations = 2;
    public static final int numberOfConcurrentThreads = 1;
    public static final int minElo = 1000;
    public static final int maxElo = 2600;
    private static int finished_simulations = 0;
    public static final Map<Participant, LongAdder> topThreeCounter = new ConcurrentHashMap<>((int) (numberOfParticipants / 0.75), (float) 0.75, Main.numberOfConcurrentThreads);
    public static final LongAdder[][] rankingTable = new LongAdder[numberOfParticipants][numberOfParticipants];

    public static void main(String[] args) throws InterruptedException {
        LookupTable.createLookupTable();
        initializeRankingTable();
        long startTime = System.nanoTime();
        final Random random = new Random();
        Tournament myTournament = new Tournament(numberOfRounds, IntStream.range(0, numberOfParticipants).mapToObj(i -> new Participant("player " + i, minElo + random.nextInt(maxElo - minElo))).collect(Collectors.toList()));

        ExecutorService pool = Executors.newFixedThreadPool(numberOfConcurrentThreads);
        for (int i = 0; i < numberOfConcurrentThreads; i++) {
            WorkerThread workerThread = new WorkerThread(myTournament, numberOfSimulations);
            pool.execute(workerThread);
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);

        topThreeCounter.forEach((participant, longAdder) -> participant.setNumberOfTopThreeFinishes(longAdder.intValue()));
        List<Participant> participantsWithTopThreeRanking = new ArrayList<>(topThreeCounter.keySet());
        participantsWithTopThreeRanking.sort(Participant::compareToByTopThreeFinishes);
        participantsWithTopThreeRanking.stream().map(participant -> participant.getName() + "\t" + "starting rank: " + participant.getStartingRank() + "\t" + "Elo: " + participant.getElo() + "\t" + "Top three finishes: " + participant.getNumberOfTopThreeFinishes()).forEach(System.out::println);

        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println("\nTotal runtime: " + duration / 1000 + "." + duration % 1000 + "s");
    }

    private static void initializeRankingTable(){
        for(int row = 0; row < rankingTable.length; ++row) {
            for (int column = 0; column < rankingTable.length; ++column) {
                rankingTable[row][column] = new LongAdder();
            }
        }
    }

    public static void addRankToTable(SimulatedPlayer p, int i) {
        rankingTable[p.getParticipant().getStartingRank() - 1][i].increment();
    }

    synchronized public static int getSimulationTicket(){
        return finished_simulations++;
    }
}

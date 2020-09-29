import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class Main {
    // Variables for configuration
    public static final int numberOfParticipants = 100;
    public static final int numberOfRounds = 9;
    public static final int numberOfSimulations = 100000;
    public static final int numberOfConcurrentThreads = 6;
    // Used for randomly created participants
    public static final int minElo = 1000;
    public static final int maxElo = 2600;
    // End of configuration
    public static final Map<Participant, LongAdder> topThreeCounter =
            new ConcurrentHashMap<>(numberOfParticipants, 0.75f, numberOfConcurrentThreads);
    private static int finished_simulations = 0;

    public static void main(final String[] args) throws InterruptedException, IOException {
        if (args.length != 1) {
            System.out.println("Specify the location of the lookup table as the only argument.");
            System.exit(1);
        }
        final Timer entireProcessTimer = new Timer();
        final String lookupTableFile = args[0];
        // ATTENTION: The lookupTableFile's contents must match the variables LOWEST_ELO and HIGHEST_ELO.
        LookupTable.createLookupTable(lookupTableFile);
        entireProcessTimer.printElapsedSecondsMessage("Time spent creating lookupTable: ", System.lineSeparator());
        final Tournament myTournament = createTournament();
        final Timer simulationsTimer = new Timer();
        simulateTournament(myTournament);
        simulationsTimer.printElapsedSecondsMessage("Simulation runtime: ", System.lineSeparator());
        showResults();
        entireProcessTimer.printElapsedSecondsMessage(System.lineSeparator() + "Total runtime: ", "");
    }

    private static Tournament createTournament() throws IOException {
        return new Tournament(1, XLSXParser.getParticipantsFromRanking("https://chess-results.com/tnr507448.aspx?lan=0&zeilen=0&art=1&rd=8&turdet=YES&flag=30&prt=4&excel=2010"), ChessDataParser.getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=9&turdet=YES&flag=NO"));
    }

    private static void simulateTournament(final Tournament tournament) throws InterruptedException {
        final ExecutorService pool = Executors.newFixedThreadPool(numberOfConcurrentThreads);
        for (int i = 0; i < numberOfConcurrentThreads; ++i) {
            final WorkerThread workerThread = new WorkerThread(tournament, numberOfSimulations);
            pool.execute(workerThread);
        }
        pool.shutdown();
        pool.awaitTermination(1L, TimeUnit.DAYS);
    }

    private static void showResults() {
        topThreeCounter.forEach((participant, longAdder) -> participant.setNumberOfTopThreeFinishes(longAdder.intValue()));
        final List<Participant> participantsWithTopThreeRanking = new ArrayList<>(topThreeCounter.keySet());
        participantsWithTopThreeRanking.sort(Participant::compareToByTopThreeFinishesDescending);
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

        private long elapsedMilliSeconds() {
            return (System.nanoTime() - startTime) / 1000000L;
        }

        private void printElapsedSecondsMessage(final String beforeTime, final String afterTime) {
            System.out.println(beforeTime + millisecondsToSecondsString(elapsedMilliSeconds()) + afterTime);
        }

        private static String millisecondsToSecondsString(final long milliseconds) {
            return milliseconds / 1000L + "." + milliseconds % 1000L + "s";
        }
    }
}
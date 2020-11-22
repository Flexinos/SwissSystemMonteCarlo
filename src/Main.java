import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Main {
    // Variables for configuration
    public static final int numberOfRounds = 3;
    public static final int numberOfSimulations = 1;
    public static final int numberOfConcurrentThreads = 6;
    // End of configuration
    private static int finishedSimulationsCounter = 0;

    private Main() {
    }

    public static void main(final String[] args) throws InterruptedException {
        /*
        if (args.length != 1) {
            System.out.println("Specify the location of the lookup table as the only argument.");
            System.exit(1);
        }
        */
        final Timer entireProcessTimer = new Timer();
        // final String lookupTableFile = args[0];
        // ATTENTION: The lookupTableFile's contents must match the variables LOWEST_ELO and HIGHEST_ELO.
        // LookupTable.createLookupTable(lookupTableFile);
        // entireProcessTimer.printElapsedSecondsMessage("Time spent creating lookupTable: ", System.lineSeparator());
        final Tournament myTournament = createTournament();
        final Timer simulationsTimer = new Timer();
        simulateTournament(myTournament);
        simulationsTimer.printElapsedSecondsMessage("Simulation runtime: ", System.lineSeparator());
        showResults(myTournament);
        entireProcessTimer.printElapsedSecondsMessage(System.lineSeparator() + "Total runtime: ", "");
    }

    private static Tournament createTournament() {
        final List<Participant> participants = TournamentDataParser.getTournamentDataFromLink(
                "https://chess-results.com/tnr507448.aspx?lan=0&zeilen=0&art=1&rd=8&turdet=YES&flag=30&prt=4&excel=2010");
        for (final Participant participant : participants) {
            participant.updateScore();
        }
        Participant.initializeTopThreeCounterMap(participants.size(), numberOfConcurrentThreads);
        Participant.initializeLongAdders(participants.size());
        return new Tournament(numberOfRounds, participants);
    }

    private static void simulateTournament(final Tournament tournament) throws InterruptedException {
        final ExecutorService pool = Executors.newFixedThreadPool(numberOfConcurrentThreads);
        for (int i = 0; i < numberOfConcurrentThreads; ++i) {
            final Runnable workerThread = new WorkerThread(tournament, numberOfSimulations);
            pool.execute(workerThread);
        }
        pool.shutdown();
        pool.awaitTermination(1L, TimeUnit.DAYS);
    }

    private static void showResults(final Tournament myTournament) {
        final List<Participant> participantsWithTopThreeRanking = new ArrayList<>();
        for (final Participant participant : myTournament.getParticipantList()) {
            participant.updateNumberOfTopThreeFinishes();
            final int numberOfTopThreeFinishes = participant.getNumberOfTopThreeFinishes();
            if (numberOfTopThreeFinishes > 0) {
                participantsWithTopThreeRanking.add(participant);
            }
        }
        participantsWithTopThreeRanking.sort(Participant::compareToByTopThreeFinishesDescending);
        SimulatedTournament.printSimulationResults(participantsWithTopThreeRanking);
    }

    public static int getSimulationTicket() {
        synchronized (Main.class) {
            return ++finishedSimulationsCounter;
        }
    }

    private static final class Timer {
        private final long startTime;

        private Timer() {
            this.startTime = System.nanoTime();
        }

        private static String millisecondsToSecondsString(final long milliseconds) {
            return (milliseconds / 1000L) + "." + (milliseconds % 1000L) + "s";
        }

        private long elapsedMilliSeconds() {
            return (System.nanoTime() - this.startTime) / 1000000L;
        }

        private void printElapsedSecondsMessage(final String beforeTime, final String afterTime) {
            System.out.println(beforeTime + millisecondsToSecondsString(elapsedMilliSeconds()) + afterTime);
        }
    }
}
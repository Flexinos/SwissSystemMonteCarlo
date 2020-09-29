public class WorkerThread implements Runnable {
    private static final int simulationsPerProgressMessage = 1000;
    private final Tournament tournament;
    private final int numberOfSimulations;
    private final int numberOfSimulationsStringLength;

    public WorkerThread(final Tournament tournament, final int numberOfSimulations) {
        this.tournament = tournament;
        this.numberOfSimulations = numberOfSimulations;
        this.numberOfSimulationsStringLength = Integer.toString(numberOfSimulations).length();
    }

    @Override
    public void run() {
        int simulationTicket = Main.getSimulationTicket();
        while (simulationTicket <= numberOfSimulations) {
            final SimulatedTournament simulatedTournament = new SimulatedTournament(tournament);
            simulatedTournament.simulateTournament();
            simulatedTournament.analyseThisSimulatedTournament();
            if (simulationTicket % simulationsPerProgressMessage == 0) {
                System.out.printf(
                        "Completed %" + numberOfSimulationsStringLength +
                                "d / %" + numberOfSimulationsStringLength +
                                "d simulations." + System.lineSeparator(),
                        simulationTicket, numberOfSimulations);
            }
            simulationTicket = Main.getSimulationTicket();
        }
    }
}

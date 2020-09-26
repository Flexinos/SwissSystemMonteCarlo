public class WorkerThread implements Runnable {
    private final int simulationsPerProgressMessage = 10000;
    private final Tournament tournament;
    private final int numberOfSimulations;
    private final int numberOfSimulationsStringLength;

    public WorkerThread(Tournament tournament, int numberOfSimulations) {
        this.tournament = tournament;
        this.numberOfSimulations = numberOfSimulations;
        this.numberOfSimulationsStringLength = Integer.toString(numberOfSimulations).length();
    }

    @Override
    public void run() {
        int simulationTicket = Main.getSimulationTicket();
        while(simulationTicket < numberOfSimulations) {
            SimulatedTournament simulatedTournament = new SimulatedTournament(tournament);
            simulatedTournament.simulateTournament();
            if (++simulationTicket % simulationsPerProgressMessage == 0) {
                System.out.printf(
                        "Completed %" + numberOfSimulationsStringLength +
                                "d / %" +  numberOfSimulationsStringLength +
                                "d simulations." + System.lineSeparator(),
                        simulationTicket, numberOfSimulations);
            }
            simulationTicket = Main.getSimulationTicket();
        }
    }
}

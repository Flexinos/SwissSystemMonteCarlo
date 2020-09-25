public class WorkerThread implements Runnable {
    private final Tournament tournament;
    private final int numberOfSimulations;

    public WorkerThread(Tournament tournament, int numberOfSimulations) {
        this.tournament = tournament;
        this.numberOfSimulations = numberOfSimulations;
    }

    @Override
    public void run() {
        int simulationTicket = Main.getSimulationTicket();
        while(simulationTicket < numberOfSimulations) {
            SimulatedTournament simulatedTournament = new SimulatedTournament(tournament);
            simulatedTournament.simulateTournament();
            if (simulationTicket % 10000 == 0) {
                System.out.println(simulationTicket);
            }
            simulationTicket = Main.getSimulationTicket();
        }
    }
}

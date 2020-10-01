import java.util.ArrayList;

public final class WorkerThread implements Runnable {
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
        while (simulationTicket <= this.numberOfSimulations) {
            final SimulatedTournament simulatedTournament = new SimulatedTournament(this.tournament.getRoundsToBeSimulated(), new ArrayList<>(this.tournament.getPlayerArrayList()), this.tournament.getGivenPairings());
            simulatedTournament.simulateTournament();
            simulatedTournament.analyseThisSimulatedTournament();
            if ((simulationTicket % simulationsPerProgressMessage) == 0) {
                System.out.printf(
                        "Completed %" + this.numberOfSimulationsStringLength +
                                "d / %" + this.numberOfSimulationsStringLength +
                                "d simulations." + System.lineSeparator(),
                        simulationTicket, this.numberOfSimulations);
            }
            simulationTicket = Main.getSimulationTicket();
        }
    }
}

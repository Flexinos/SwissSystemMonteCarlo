public class WorkerThread implements Runnable {
    private final Tournament tournament;
    private final int numberOfSimulations;

    public WorkerThread(Tournament tournament, int numberOfSimulations) {
        this.tournament = tournament;
        this.numberOfSimulations = numberOfSimulations;
    }

    @Override
    public void run() {
        for (int j = 0; j < numberOfSimulations; j++) {
            SimulatedTournament simulatedTournament = new SimulatedTournament(tournament);
            simulatedTournament.simulateTournament();
            Main.finishedSimulations.increment();
            if (Main.finishedSimulations.intValue() % 10000 == 0) {
                System.out.println(Main.finishedSimulations.intValue());
            }
        }
    }
}

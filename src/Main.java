import java.security.SecureRandom;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Participant> participants = new ArrayList<>();
        final SecureRandom random = new SecureRandom();

        for (int i = 0; i < 32; i++) {
            participants.add(new Participant("player " + i, 800 + random.nextInt(1000)));
        }
        Tournament myTournament = new Tournament("myTournament", 5, participants);
        SimulatedTournament mySimulatedTournament = new SimulatedTournament(myTournament);

        mySimulatedTournament.simulateTournament();
    }
}

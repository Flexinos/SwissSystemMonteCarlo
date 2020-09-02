import java.security.SecureRandom;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Participant> participants = new ArrayList<>();
        final SecureRandom random = new SecureRandom();

        for (int i = 0; i < 200; i++) {
            participants.add(new Participant("player " + i, 1000 + random.nextInt(1600)));
        }
        Tournament myTournament = new Tournament("myTournament", 9, participants);
        SimulatedTournament mySimulatedTournament = new SimulatedTournament(myTournament);

        mySimulatedTournament.simulateTournament();
    }
}

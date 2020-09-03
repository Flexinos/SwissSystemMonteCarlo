import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final SecureRandom random = new SecureRandom();
        Tournament myTournament = new Tournament("myTournament", 11);

        List<Participant> participants = new ArrayList<>();
        for (int i = 0; i < 151; i++) {
            participants.add(new Participant(myTournament, "player " + i, 1000 + random.nextInt(1600)));
        }

        SimulatedTournament mySimulatedTournament = new SimulatedTournament(myTournament);

        long startTime = System.nanoTime();
        mySimulatedTournament.simulateTournament();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println(duration);
    }
}

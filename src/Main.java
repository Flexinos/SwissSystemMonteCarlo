import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static final int numberOfParticipants = 20;
    public static final int numberOfRounds = 1;

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        final Random random = new Random();
        Tournament myTournament = new Tournament(numberOfRounds);

        List<Participant> participants = IntStream.range(0, numberOfParticipants).mapToObj(i -> new Participant("player " + i, 1000 + random.nextInt(1600))).collect(Collectors.toList());
        myTournament.addParticipants(participants);

        for (int i = 0; i < 100000; i++) {
            SimulatedTournament mySimulatedTournament = new SimulatedTournament(myTournament);
            mySimulatedTournament.simulateTournament();
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println(duration);
    }
}

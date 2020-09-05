import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static final int numberOfParticipants = 1000;
    public static final int numberOfRounds = 11;

    public static void main(String[] args) {
        final SecureRandom random = new SecureRandom();
        Tournament myTournament = new Tournament(numberOfRounds);

        List<Participant> participants = IntStream.range(0, numberOfParticipants).mapToObj(i -> new Participant("player " + i, 1000 + random.nextInt(1600))).collect(Collectors.toList());
        myTournament.addParticipants(participants);

        SimulatedTournament mySimulatedTournament = new SimulatedTournament(myTournament);

        long startTime = System.nanoTime();
        mySimulatedTournament.simulateTournament();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println(duration);
    }
}

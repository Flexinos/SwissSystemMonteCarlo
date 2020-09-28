import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatedPlayerTest {
    private final Participant participant1 = new Participant("", 1000);
    private final Participant participant2 = new Participant("", 0);
    private final Participant participant3 = new Participant("", 1000);
    private final Participant participant4 = new Participant("", 2000);
    private final Participant participant5 = new Participant("", 1600);
    private final Participant participant6 = new Participant("", 1890);
    private final Participant participant7 = new Participant("", 2120);
    private final Participant participant8 = new Participant("", 2670);
    private final List<Participant> participants = new ArrayList<>(List.of(participant1, participant2, participant3, participant4, participant5, participant6, participant7, participant8));
    private final List<SimulatedPlayer> simulatedPlayers = new ArrayList<>();
    Tournament tournament = new Tournament(0, participants);
    SimulatedTournament simulatedTournament = new SimulatedTournament(tournament);
    SimulatedPlayer simulatedPlayer1;
    SimulatedPlayer simulatedPlayer2;
    SimulatedPlayer simulatedPlayer3;
    SimulatedPlayer simulatedPlayer4;
    SimulatedPlayer simulatedPlayer5;
    SimulatedPlayer simulatedPlayer6;
    SimulatedPlayer simulatedPlayer7;
    SimulatedPlayer simulatedPlayer8;

    @BeforeEach
    private void createSimulatedPlayers() {
        simulatedPlayer1 = new SimulatedPlayer(participant1, simulatedTournament);
        simulatedPlayer2 = new SimulatedPlayer(participant2, simulatedTournament);
        simulatedPlayer3 = new SimulatedPlayer(participant3, simulatedTournament);
        simulatedPlayer4 = new SimulatedPlayer(participant4, simulatedTournament);
        simulatedPlayer5 = new SimulatedPlayer(participant5, simulatedTournament);
        simulatedPlayer6 = new SimulatedPlayer(participant6, simulatedTournament);
        simulatedPlayer7 = new SimulatedPlayer(participant7, simulatedTournament);
        simulatedPlayer8 = new SimulatedPlayer(participant8, simulatedTournament);
        simulatedPlayers.addAll(List.of(simulatedPlayer1, simulatedPlayer2, simulatedPlayer3, simulatedPlayer4, simulatedPlayer5, simulatedPlayer6, simulatedPlayer7, simulatedPlayer8));
    }


    @Test
    void compareToByScoreTieBreak() {
        simulatedPlayer1.addGame(simulatedPlayer5, 1);
        simulatedPlayer2.addGame(simulatedPlayer6, 1);
        simulatedPlayer3.addGame(simulatedPlayer7, 0.5);
        simulatedPlayer4.addGame(simulatedPlayer8, 0);

        simulatedPlayer5.addGame(simulatedPlayer1, 0);
        simulatedPlayer6.addGame(simulatedPlayer2, 0);
        simulatedPlayer7.addGame(simulatedPlayer3, 0.5);
        simulatedPlayer8.addGame(simulatedPlayer4, 1);


        assertEquals(simulatedPlayer1.getScore(), 1);
        assertEquals(simulatedPlayer2.getScore(), 1);
        assertEquals(simulatedPlayer3.getScore(), 0.5);
        assertEquals(simulatedPlayer4.getScore(), 0);
        assertEquals(simulatedPlayer5.getScore(), 0);
        assertEquals(simulatedPlayer6.getScore(), 0);
        assertEquals(simulatedPlayer7.getScore(), 0.5);
        assertEquals(simulatedPlayer8.getScore(), 1);


        simulatedPlayer1.addGame(simulatedPlayer2, 1);
        simulatedPlayer8.addGame(simulatedPlayer3, 0.5);
        simulatedPlayer7.addGame(simulatedPlayer4, 0.5);
        simulatedPlayer5.addGame(simulatedPlayer6, 1);

        simulatedPlayer2.addGame(simulatedPlayer1, 0);
        simulatedPlayer3.addGame(simulatedPlayer8, 0.5);
        simulatedPlayer4.addGame(simulatedPlayer7, 0.5);
        simulatedPlayer6.addGame(simulatedPlayer5, 0);


        assertEquals(simulatedPlayer1.getScore(), 2);
        assertEquals(simulatedPlayer2.getScore(), 1);
        assertEquals(simulatedPlayer3.getScore(), 1);
        assertEquals(simulatedPlayer4.getScore(), 0.5);
        assertEquals(simulatedPlayer5.getScore(), 1);
        assertEquals(simulatedPlayer6.getScore(), 0);
        assertEquals(simulatedPlayer7.getScore(), 1);
        assertEquals(simulatedPlayer8.getScore(), 1.5);

        for (SimulatedPlayer simulatedPlayer : simulatedPlayers) {
            simulatedPlayer.updateTiebreaks();
        }

        assertEquals(simulatedPlayer1.getBuchholz(), 2);
        assertEquals(simulatedPlayer2.getBuchholz(), 2);
        assertEquals(simulatedPlayer3.getBuchholz(), 2.5);
        assertEquals(simulatedPlayer4.getBuchholz(), 2.5);
        assertEquals(simulatedPlayer5.getBuchholz(), 2);
        assertEquals(simulatedPlayer6.getBuchholz(), 2);
        assertEquals(simulatedPlayer7.getBuchholz(), 1.5);
        assertEquals(simulatedPlayer8.getBuchholz(), 1.5);

        assertEquals(simulatedPlayer1.getBuchholzCutOne(), 1);
        assertEquals(simulatedPlayer2.getBuchholzCutOne(), 2);
        assertEquals(simulatedPlayer3.getBuchholzCutOne(), 1.5);
        assertEquals(simulatedPlayer4.getBuchholzCutOne(), 1.5);
        assertEquals(simulatedPlayer5.getBuchholzCutOne(), 2);
        assertEquals(simulatedPlayer6.getBuchholzCutOne(), 1);
        assertEquals(simulatedPlayer7.getBuchholzCutOne(), 1);
        assertEquals(simulatedPlayer8.getBuchholzCutOne(), 1);

        assertEquals(simulatedPlayer1.getSonnenbornBerger(), 2);
        assertEquals(simulatedPlayer2.getSonnenbornBerger(), 0);
        assertEquals(simulatedPlayer3.getSonnenbornBerger(), 1.25);
        assertEquals(simulatedPlayer4.getSonnenbornBerger(), 0.5);
        assertEquals(simulatedPlayer5.getSonnenbornBerger(), 0);
        assertEquals(simulatedPlayer6.getSonnenbornBerger(), 0);
        assertEquals(simulatedPlayer7.getSonnenbornBerger(), 0.75);
        assertEquals(simulatedPlayer8.getSonnenbornBerger(), 1);

        List<SimulatedPlayer> rankingByScoreThenTiebreak = new ArrayList<>();
        rankingByScoreThenTiebreak.add(simulatedPlayer1);
        rankingByScoreThenTiebreak.add(simulatedPlayer8);
        rankingByScoreThenTiebreak.add(simulatedPlayer5);
        rankingByScoreThenTiebreak.add(simulatedPlayer2);
        rankingByScoreThenTiebreak.add(simulatedPlayer3);
        rankingByScoreThenTiebreak.add(simulatedPlayer7);
        rankingByScoreThenTiebreak.add(simulatedPlayer4);
        rankingByScoreThenTiebreak.add(simulatedPlayer6);
        simulatedPlayers.sort(SimulatedPlayer::compareToByScoreThenTieBreak);
        assertEquals(simulatedPlayers, rankingByScoreThenTiebreak);
    }

    @Test
    void compareToByScoreThenElo() {
    }
}
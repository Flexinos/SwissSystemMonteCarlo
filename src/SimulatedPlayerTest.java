import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatedPlayerTest {
    private final Participant participant1 = new Participant("", 1000);
    private final Participant participant2 = new Participant("", 0);
    private final Participant participant3 = new Participant("", 1000);
    private final Participant participant4 = new Participant("", 2000);
    private final Participant participant5 = new Participant("", 2000);
    private final Participant participant6 = new Participant("", 2000);
    private final Participant participant7 = new Participant("", 2000);
    private final Participant participant8 = new Participant("", 2000);
    private final List<Participant> participantList = new ArrayList<>();

    @Test
    void compareToByScoreTieBreak() {
    }

    @Test
    void compareToByScoreThenElo() {
        participantList.add(participant1);
        participantList.add(participant2);
        participantList.add(participant3);
        participantList.add(participant4);
        participantList.add(participant5);
        participantList.add(participant6);
        participantList.add(participant7);
        participantList.add(participant8);
        Tournament tournament = new Tournament(0, participantList);
        SimulatedTournament simulatedTournament = new SimulatedTournament(tournament);
        SimulatedPlayer simulatedPlayer1 = new SimulatedPlayer(participant1, simulatedTournament);
        SimulatedPlayer simulatedPlayer2 = new SimulatedPlayer(participant2, simulatedTournament);
        SimulatedPlayer simulatedPlayer3 = new SimulatedPlayer(participant3, simulatedTournament);
        SimulatedPlayer simulatedPlayer4 = new SimulatedPlayer(participant4, simulatedTournament);
        SimulatedPlayer simulatedPlayer5 = new SimulatedPlayer(participant5, simulatedTournament);
        SimulatedPlayer simulatedPlayer6 = new SimulatedPlayer(participant6, simulatedTournament);
        SimulatedPlayer simulatedPlayer7 = new SimulatedPlayer(participant7, simulatedTournament);
        SimulatedPlayer simulatedPlayer8 = new SimulatedPlayer(participant8, simulatedTournament);


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

        // test tie breaks
    }
}
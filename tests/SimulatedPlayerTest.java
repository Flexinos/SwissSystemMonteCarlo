import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SimulatedPlayerTest {
    private final Participant participant1 = new Participant("", 1000);
    private final Participant participant2 = new Participant("", 0);
    private final Participant participant3 = new Participant("", 1000);
    private final Participant participant4 = new Participant("", 2000);
    private final Participant participant5 = new Participant("", 1600);
    private final Participant participant6 = new Participant("", 1890);
    private final Participant participant7 = new Participant("", 2120);
    private final Participant participant8 = new Participant("", 2670);
    private final List<Participant> participants = new ArrayList<>(List.of(this.participant1, this.participant2, this.participant3, this.participant4, this.participant5, this.participant6, this.participant7, this.participant8));
    private final List<SimulatedPlayer> simulatedPlayers = new ArrayList<>();
    Tournament tournament = new Tournament(0, this.participants);
    SimulatedTournament simulatedTournament = new SimulatedTournament(this.tournament);
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
        this.simulatedPlayer1 = new SimulatedPlayer(this.participant1, this.simulatedTournament);
        this.simulatedPlayer2 = new SimulatedPlayer(this.participant2, this.simulatedTournament);
        this.simulatedPlayer3 = new SimulatedPlayer(this.participant3, this.simulatedTournament);
        this.simulatedPlayer4 = new SimulatedPlayer(this.participant4, this.simulatedTournament);
        this.simulatedPlayer5 = new SimulatedPlayer(this.participant5, this.simulatedTournament);
        this.simulatedPlayer6 = new SimulatedPlayer(this.participant6, this.simulatedTournament);
        this.simulatedPlayer7 = new SimulatedPlayer(this.participant7, this.simulatedTournament);
        this.simulatedPlayer8 = new SimulatedPlayer(this.participant8, this.simulatedTournament);
        this.simulatedPlayers.addAll(List.of(this.simulatedPlayer1, this.simulatedPlayer2, this.simulatedPlayer3, this.simulatedPlayer4, this.simulatedPlayer5, this.simulatedPlayer6, this.simulatedPlayer7, this.simulatedPlayer8));
    }


    @Test
    void compareToByScoreTieBreak() {
        this.simulatedPlayer1.addGame(this.simulatedPlayer5, 1.0f);
        this.simulatedPlayer2.addGame(this.simulatedPlayer6, 1.0f);
        this.simulatedPlayer3.addGame(this.simulatedPlayer7, 0.5f);
        this.simulatedPlayer4.addGame(this.simulatedPlayer8, 0.0f);

        this.simulatedPlayer5.addGame(this.simulatedPlayer1, 0.0f);
        this.simulatedPlayer6.addGame(this.simulatedPlayer2, 0.0f);
        this.simulatedPlayer7.addGame(this.simulatedPlayer3, 0.5f);
        this.simulatedPlayer8.addGame(this.simulatedPlayer4, 1.0f);


        assertEquals(this.simulatedPlayer1.getScore(), 1.0f);
        assertEquals(this.simulatedPlayer2.getScore(), 1.0f);
        assertEquals(this.simulatedPlayer3.getScore(), 0.5f);
        assertEquals(this.simulatedPlayer4.getScore(), 0.0f);
        assertEquals(this.simulatedPlayer5.getScore(), 0.0f);
        assertEquals(this.simulatedPlayer6.getScore(), 0.0f);
        assertEquals(this.simulatedPlayer7.getScore(), 0.5f);
        assertEquals(this.simulatedPlayer8.getScore(), 1.0f);


        this.simulatedPlayer1.addGame(this.simulatedPlayer2, 1.0f);
        this.simulatedPlayer8.addGame(this.simulatedPlayer3, 0.5f);
        this.simulatedPlayer7.addGame(this.simulatedPlayer4, 0.5f);
        this.simulatedPlayer5.addGame(this.simulatedPlayer6, 1.0f);

        this.simulatedPlayer2.addGame(this.simulatedPlayer1, 0.0f);
        this.simulatedPlayer3.addGame(this.simulatedPlayer8, 0.5f);
        this.simulatedPlayer4.addGame(this.simulatedPlayer7, 0.5f);
        this.simulatedPlayer6.addGame(this.simulatedPlayer5, 0.0f);


        assertEquals(this.simulatedPlayer1.getScore(), 2.0f);
        assertEquals(this.simulatedPlayer2.getScore(), 1.0f);
        assertEquals(this.simulatedPlayer3.getScore(), 1.0f);
        assertEquals(this.simulatedPlayer4.getScore(), 0.5f);
        assertEquals(this.simulatedPlayer5.getScore(), 1.0f);
        assertEquals(this.simulatedPlayer6.getScore(), 0.0f);
        assertEquals(this.simulatedPlayer7.getScore(), 1.0f);
        assertEquals(this.simulatedPlayer8.getScore(), 1.5f);

        for (final SimulatedPlayer simulatedPlayer : this.simulatedPlayers) {
            simulatedPlayer.updateTiebreaks();
        }

        assertEquals(this.simulatedPlayer1.getBuchholz(), 2.0f);
        assertEquals(this.simulatedPlayer2.getBuchholz(), 2.0f);
        assertEquals(this.simulatedPlayer3.getBuchholz(), 2.5f);
        assertEquals(this.simulatedPlayer4.getBuchholz(), 2.5f);
        assertEquals(this.simulatedPlayer5.getBuchholz(), 2.0f);
        assertEquals(this.simulatedPlayer6.getBuchholz(), 2.0f);
        assertEquals(this.simulatedPlayer7.getBuchholz(), 1.5f);
        assertEquals(this.simulatedPlayer8.getBuchholz(), 1.5f);

        assertEquals(this.simulatedPlayer1.getBuchholzCutOne(), 1.0f);
        assertEquals(this.simulatedPlayer2.getBuchholzCutOne(), 2.0f);
        assertEquals(this.simulatedPlayer3.getBuchholzCutOne(), 1.5f);
        assertEquals(this.simulatedPlayer4.getBuchholzCutOne(), 1.5f);
        assertEquals(this.simulatedPlayer5.getBuchholzCutOne(), 2.0f);
        assertEquals(this.simulatedPlayer6.getBuchholzCutOne(), 1.0f);
        assertEquals(this.simulatedPlayer7.getBuchholzCutOne(), 1.0f);
        assertEquals(this.simulatedPlayer8.getBuchholzCutOne(), 1.0f);

        assertEquals(this.simulatedPlayer1.getSonnenbornBerger(), 2.0f);
        assertEquals(this.simulatedPlayer2.getSonnenbornBerger(), 0.0f);
        assertEquals(this.simulatedPlayer3.getSonnenbornBerger(), 1.25f);
        assertEquals(this.simulatedPlayer4.getSonnenbornBerger(), 0.5f);
        assertEquals(this.simulatedPlayer5.getSonnenbornBerger(), 0.0f);
        assertEquals(this.simulatedPlayer6.getSonnenbornBerger(), 0.0f);
        assertEquals(this.simulatedPlayer7.getSonnenbornBerger(), 0.75f);
        assertEquals(this.simulatedPlayer8.getSonnenbornBerger(), 1.0f);

        final Collection<SimulatedPlayer> rankingByScoreThenTiebreak = new ArrayList<>();
        rankingByScoreThenTiebreak.add(this.simulatedPlayer1);
        rankingByScoreThenTiebreak.add(this.simulatedPlayer8);
        rankingByScoreThenTiebreak.add(this.simulatedPlayer5);
        rankingByScoreThenTiebreak.add(this.simulatedPlayer2);
        rankingByScoreThenTiebreak.add(this.simulatedPlayer3);
        rankingByScoreThenTiebreak.add(this.simulatedPlayer7);
        rankingByScoreThenTiebreak.add(this.simulatedPlayer4);
        rankingByScoreThenTiebreak.add(this.simulatedPlayer6);
        this.simulatedPlayers.sort(SimulatedPlayer::compareToByScoreThenTieBreak);
        assertEquals(this.simulatedPlayers, rankingByScoreThenTiebreak);
    }

    @Test
    void compareToByScoreThenElo() {
        this.simulatedPlayer1.addGame(this.simulatedPlayer5, 1.0f);
        this.simulatedPlayer2.addGame(this.simulatedPlayer6, 1.0f);
        this.simulatedPlayer3.addGame(this.simulatedPlayer7, 0.5f);
        this.simulatedPlayer4.addGame(this.simulatedPlayer8, 0.0f);

        this.simulatedPlayer5.addGame(this.simulatedPlayer1, 0.0f);
        this.simulatedPlayer6.addGame(this.simulatedPlayer2, 0.0f);
        this.simulatedPlayer7.addGame(this.simulatedPlayer3, 0.5f);
        this.simulatedPlayer8.addGame(this.simulatedPlayer4, 1.0f);


        this.simulatedPlayer1.addGame(this.simulatedPlayer2, 1.0f);
        this.simulatedPlayer8.addGame(this.simulatedPlayer3, 0.5f);
        this.simulatedPlayer7.addGame(this.simulatedPlayer4, 0.5f);
        this.simulatedPlayer5.addGame(this.simulatedPlayer6, 1.0f);

        this.simulatedPlayer2.addGame(this.simulatedPlayer1, 0.0f);
        this.simulatedPlayer3.addGame(this.simulatedPlayer8, 0.5f);
        this.simulatedPlayer4.addGame(this.simulatedPlayer7, 0.5f);
        this.simulatedPlayer6.addGame(this.simulatedPlayer5, 0.0f);

        assertEquals(this.simulatedPlayer1.compareToByScoreThenElo(this.simulatedPlayer1), 0);
        assertEquals(this.simulatedPlayer1.compareToByScoreThenElo(this.simulatedPlayer2), -1);
        assertEquals(this.simulatedPlayer1.compareToByScoreThenElo(this.simulatedPlayer3), -1);
        assertEquals(this.simulatedPlayer1.compareToByScoreThenElo(this.simulatedPlayer4), -1);

        assertEquals(this.simulatedPlayer2.compareToByScoreThenElo(this.simulatedPlayer3), 1);
        assertEquals(this.simulatedPlayer2.compareToByScoreThenElo(this.simulatedPlayer1), 1);
        assertEquals(this.simulatedPlayer5.compareToByScoreThenElo(this.simulatedPlayer7), 1);
        assertEquals(this.simulatedPlayer7.compareToByScoreThenElo(this.simulatedPlayer5), -1);
    }
}
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedTournamentTest {
    Participant p1;
    Participant p2;

    @BeforeEach
    void setUp() {
        p1 = Participant.skeletonParticipant(1, "g", "Mirzoev Azer", "AZE", 2527, false);
        p2 = Participant.skeletonParticipant(2, "m", "Argandona Riveiro Inigo", "ESP", 2408, false);
        Collection<Participant> participants = new ArrayList<>(List.of(p1, p2));
        SimulatedTournament tournament = new SimulatedTournament(3, participants);
        List<Participant> playerlist = tournament.getSimulatedPlayerList();
        StringBuilder string = new StringBuilder(playerlist.size() * 100 + tournament.getRoundsToBeSimulated() * 8);
    }

    @Test
    void createPlayerDataStringTRF_TestJavafoSample() {
        assertEquals(SimulatedTournament.createPlayerDataStringTRF(1, p1),
                "001    1 m  g Mirzoev Azer                      2527 AZE     1234567 1978        0.0    1");
        assertEquals(SimulatedTournament.createPlayerDataStringTRF(2, p2),
                "001    2 m  m Argandona Riveiro Inigo           2408 ESP     1234567 1978        0.0    2");
    }

    @Test
    void createOpponentsStringTRF_TestJavafoSample() {
        p1.addGame(p2.getStartingRank(), '1', 'w');
        p2.addGame(p1.getStartingRank(), '0', 'b');
        assertEquals(SimulatedTournament.createOpponentsStringTRF(p1.getGameList()), "     2 w 1");
        assertEquals(SimulatedTournament.createOpponentsStringTRF(p2.getGameList()), "     1 b 0");
    }
    /*
    @Test
    void createTRF_TestJavafoSample() {
        p1.addGame(26,1,true);
        assertEquals();
    }
    */
}
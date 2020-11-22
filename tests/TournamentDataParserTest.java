/*
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Map.Entry;

class TournamentDataParserTest {
    private static final class ParsingData {
        private final String link;
        private final int tournamentNumber;
        private final Map<Integer, Participant> participantsToTest;

        private ParsingData(final String link, final int tournamentNumber, final Map<Integer, Participant> participantsToTest) {
            this.link = link;
            this.tournamentNumber = tournamentNumber;
            this.participantsToTest = participantsToTest;
        }
    }

    private static final Collection<ParsingData> testData = new ArrayList<>();

    @BeforeAll
    private static void initializeFields() {
        testData.add(new ParsingData("https://chess-results.com/tnr507448.aspx?lan=0&art=0&turdet=NO&flag=NO&prt=7", 507448,
                Map.ofEntries(
                        Map.entry(1, new Participant(1, "GM", "Meshkovs Nikita",
                                "LAT", 2585, "", false, Map.ofEntries(
                                Map.entry(45, 0.5f), Map.entry(57, 1.0f), Map.entry(24, 0.0f),
                                Map.entry(38, 1.0f), Map.entry(80, 1.0f), Map.entry(16, 1.0f),
                                Map.entry(18, 1.0f), Map.entry(9, 1.0f), Map.entry(2, 0.5f)),
                                0, 0, false, false)),
                        Map.entry(41, new Participant(41, "", "Ghiotto Marco Luigi",
                                "ITA", 2151, "U18", false, Map.ofEntries(
                                Map.entry(86, 1.0f), Map.entry(22, 0.5f), Map.entry(20, 0.0f),
                                Map.entry(28, 0.5f), Map.entry(55, 0.0f), Map.entry(62, 0.0f),
                                Map.entry(74, 1.0f), Map.entry(66, 0.0f), Map.entry(73, 1.0f)
                        ), 0, 0, false, false)),
                        Map.entry(88, new Participant(88, "", "Jetzl Julian",
                                "AUT", 1802, "U14", false, Map.ofEntries(
                                Map.entry(42, 0.0f), Map.entry(66, 0.0f), Map.entry(65, 0.0f),
                                Map.entry(69, 0.0f), Map.entry(76, 1.0f), Map.entry(46, 0.0f),
                                Map.entry(75, 1.0f)
                        ), 0, 0, false, true)),
                        Map.entry(89, new Participant(89, "", "Dorner Gnther",
                                "AUT", 1787, "S65", false, Map.ofEntries(
                                Map.entry(43, 0.0f), Map.entry(36, 0.0f), Map.entry(64, 0.0f),
                                Map.entry(70, 1.0f), Map.entry(78, 0.5f), Map.entry(56, 0.0f),
                                Map.entry(65, 0.5f)
                        ), 1, 0, false, true))
                )
        ));
    }

    @Test
    final void getTournamentData() {
        for (final ParsingData parsingData : testData) {
            System.out.println("Testing tournament " + parsingData.tournamentNumber);
            final List<Participant> participantsFromLink = TournamentDataParser.getTournamentDataFromLink(parsingData.link);
            final List<Participant> participantsFromTournamentNumber = TournamentDataParser.getTournamentDataFromTournamentNumber(parsingData.tournamentNumber);
            Assertions.assertEquals(participantsFromLink, participantsFromTournamentNumber);
            for (final Entry<Integer, Participant> entry : parsingData.participantsToTest.entrySet()) {
                System.out.println("Testing player " + entry.getValue().getStartingRank() + ", " + entry.getValue().getName());
                Assertions.assertTrue(Participant.equalsCheckAllParsed(entry.getValue(), participantsFromLink.get(entry.getKey() - 1)));
                Assertions.assertTrue(Participant.equalsCheckAllParsed(entry.getValue(), participantsFromTournamentNumber.get(entry.getKey() - 1)));
            }
        }
    }
}
 */
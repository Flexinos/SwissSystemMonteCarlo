import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipantTest {
    Participant participant1 = new Participant("", 1000);
    Participant participant2 = new Participant("", 0);
    Participant participant3 = new Participant("", 1000);
    Participant participant4 = new Participant("", 2000);

    @org.junit.jupiter.api.Test
    void compareToByElo() {
        assertEquals(participant1.compareToByEloDescending(participant1), 0);
        assertEquals(participant1.compareToByEloDescending(participant2), -1);
        assertEquals(participant1.compareToByEloDescending(participant3), 0);
        assertEquals(participant1.compareToByEloDescending(participant4), 1);

        assertEquals(participant1.compareToByEloDescending(participant1), 0);
        assertEquals(participant2.compareToByEloDescending(participant1), 1);
        assertEquals(participant3.compareToByEloDescending(participant1), 0);
        assertEquals(participant4.compareToByEloDescending(participant1), -1);
    }

    @org.junit.jupiter.api.Test
    void compareToByTopThreeFinishes() {
        participant1.setNumberOfTopThreeFinishes(3);
        participant2.setNumberOfTopThreeFinishes(3);
        participant3.setNumberOfTopThreeFinishes(0);
        participant4.setNumberOfTopThreeFinishes(5);

        assertEquals(participant1.compareToByTopThreeFinishesDescending(participant1), 0);
        assertEquals(participant1.compareToByTopThreeFinishesDescending(participant2), -1);
        assertEquals(participant1.compareToByTopThreeFinishesDescending(participant3), -1);
        assertEquals(participant1.compareToByTopThreeFinishesDescending(participant4), 1);

        assertEquals(participant4.compareToByTopThreeFinishesDescending(participant1), -1);

        assertEquals(participant2.compareToByTopThreeFinishesDescending(participant1), 1);

        assertEquals(participant1.compareToByTopThreeFinishesDescending(participant1), 0);
        assertEquals(participant2.compareToByTopThreeFinishesDescending(participant2), 0);
        assertEquals(participant3.compareToByTopThreeFinishesDescending(participant3), 0);
        assertEquals(participant4.compareToByTopThreeFinishesDescending(participant4), 0);
    }
}
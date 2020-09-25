import java.util.ArrayList;
import java.util.List;

public class Participant {
    private final String title;
    private final String name;
    private final String country;
    private final int elo;
    private final String bundesland;
    private final double score;
    private final double tieBreak1;
    private final double tieBreak2;
    private final double tieBreak3;
    private final String type;
    private final boolean isFemale;
    private int startingRank;
    private int numberOfTopThreeFinishes;

    public Participant(String name, int elo) {
        this(0, "", name, "", "", elo, 0, 0, 0, 0, "", false);
    }

    public Participant(int startingRank, String title, String name, String country, String bundesland, int elo, double score, double tieBreak1, double tieBreak2, double tieBreak3, String type, boolean isFemale) {
        this.startingRank = startingRank;
        this.title = title;
        this.name = name;
        this.country = country;
        this.bundesland = bundesland;
        this.elo = elo;
        this.score = score;
        this.tieBreak1 = tieBreak1;
        this.tieBreak2 = tieBreak2;
        this.tieBreak3 = tieBreak3;
        this.type = type;
        this.isFemale = isFemale;
    }

    public int getStartingRank() {
        return startingRank;
    }

    public void setStartingRank(int startingRank) {
        this.startingRank = startingRank;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public int getElo() {
        return elo;
    }

    public double getScore() {
        return score;
    }

    public double getTieBreak1() {
        return tieBreak1;
    }

    public double getTieBreak2() {
        return tieBreak2;
    }

    public double getTieBreak3() {
        return tieBreak3;
    }

    public String getType() {
        return type;
    }

    public boolean isFemale() {
        return isFemale;
    }

    public int getNumberOfTopThreeFinishes() {
        return numberOfTopThreeFinishes;
    }

    public void setNumberOfTopThreeFinishes(int numberOfTopThreeFinishes) {
        this.numberOfTopThreeFinishes = numberOfTopThreeFinishes;
    }

    public int compareToByElo(Participant p2) {
        return -1 * (Double.compare(this.getElo(), p2.getElo()));
    }

    public int compareToByTopThreeFinishes(Participant p2) {
        int result = Integer.compare(this.getNumberOfTopThreeFinishes(), p2.getNumberOfTopThreeFinishes());
        return result != 0 ? result : compareToByElo(p2);
    }

    @Override
    public String toString() {
        return "Starting rank: " + startingRank +
                " Name: " + name +
                " Elo: " + elo +
                " score: " + score +
                " tieBreak1: " + tieBreak1 +
                " tieBreak2: " + tieBreak2 +
                " tieBreak3: " + tieBreak3;
    }

    public static void printSimulationResults(List<Participant> participants) {
        String[] columnNames = {"Name", "Starting Rank", "Elo", "Top three finishes"};
        int[] fieldLengths = new int[columnNames.length];
        for (int fieldNumber = 0; fieldNumber < columnNames.length; ++fieldNumber) {
            fieldLengths[fieldNumber] = columnNames[fieldNumber].length();
        }
        List<String[]> allParticipantsEntries = new ArrayList<>();
        for (Participant participant : participants) {
            String[] participantEntries = new String[columnNames.length];
            int currentFieldNumber = 0;
            participantEntries[currentFieldNumber++] = participant.getName();
            participantEntries[currentFieldNumber++] = Integer.toString(participant.getStartingRank());
            participantEntries[currentFieldNumber++] = Integer.toString(participant.getElo());
            participantEntries[currentFieldNumber] = Integer.toString(participant.getNumberOfTopThreeFinishes());
            for (int fieldNumber = 0; fieldNumber < columnNames.length; ++fieldNumber) {
                if (participantEntries[fieldNumber].length() > fieldLengths[fieldNumber]) {
                    fieldLengths[fieldNumber] = participantEntries[fieldNumber].length();
                }
            }
            allParticipantsEntries.add(participantEntries);
        }
        for (int fieldNumber = 0; fieldNumber < columnNames.length; ++fieldNumber) {
            System.out.printf("%-" + fieldLengths[fieldNumber] + "s  ", columnNames[fieldNumber]);
        }
        System.out.println();
        for (String[] participantEntries : allParticipantsEntries) {
            for (int fieldNumber = 0; fieldNumber < columnNames.length; ++fieldNumber) {
                System.out.printf("%" + fieldLengths[fieldNumber] + "s  ", participantEntries[fieldNumber]);
            }
            System.out.println();
        }

    }
}

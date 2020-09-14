public class Participant {
    private final String title;
    private final String name;
    private final String country;
    private final double points;
    private final int elo;
    private final double tieBreak1;
    private final double tieBreak2;
    private final double tieBreak3;
    private final String type;
    private final boolean isFemale;
    private int startingRank;
    private int numberOfTopThreeFinishes;

    public Participant(String name, int elo) {
        this(0, "", name, "", elo, 0, 0, 0, 0, "", false);
    }

    public Participant(int startingRank, String title, String name, String country, int elo, double points, double tieBreak1, double tieBreak2, double tieBreak3, String type, boolean isFemale) {
        this.startingRank = startingRank;
        this.title = title;
        this.name = name;
        this.country = country;
        this.elo = elo;
        this.points = points;
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

    public double getPoints() {
        return points;
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
        return -1 * (Integer.compare(this.getNumberOfTopThreeFinishes(), p2.getNumberOfTopThreeFinishes()));
    }
}

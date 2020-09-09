public class Participant {
    private final String name;
    private final int elo;
    private int numberOfTopThreeFinishes;

    public Participant(String name, int elo) {
        this.name = name;
        this.elo = elo;
    }

    public String getName() {
        return name;
    }

    public int getElo() {
        return elo;
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

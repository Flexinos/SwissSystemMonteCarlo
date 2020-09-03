public class Participant {
    private final String name;
    private final int elo;

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

    public int compareToByElo(Participant p2) {
        return -1 * (Double.compare(this.getElo(), p2.getElo()));
    }
}

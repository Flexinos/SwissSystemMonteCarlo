public class Participant {
    private final String name;
    private int elo;

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

    public void setElo(int elo) {
        this.elo = elo;
    }
}

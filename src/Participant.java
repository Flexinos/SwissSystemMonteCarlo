public class Participant {
    private final String name;
    private final int elo;
    private final Tournament tournament;

    public Participant(Tournament tournament, String name, int elo) {
        this.tournament = tournament;
        this.name = name;
        this.elo = elo;
    }

    public String getName() {
        return name;
    }

    public int getElo() {
        return elo;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public int compareToByElo(Participant p2) {
        return -1 * (Double.compare(this.getElo(), p2.getElo()));
    }
}

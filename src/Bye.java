public class Bye extends Participant {
    private String name;
    private int elo;

    public Bye(String name, int elo) {
        super(name, elo);
        this.name = "BYE";
        this.elo = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getElo() {
        return elo;
    }
}

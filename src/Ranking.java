import java.util.ArrayList;

public class Ranking {
    private ArrayList<SimulatedPlayer> ranking;

    public Ranking(ArrayList<SimulatedPlayer> simulatedPlayerArrayList) {
        ArrayList<SimulatedPlayer> tmpList = new ArrayList<>(simulatedPlayerArrayList);
        tmpList.sort(SimulatedPlayer::compareTo);
        this.ranking = tmpList;
    }

    public ArrayList<SimulatedPlayer> getRanking() {
        return ranking;
    }
}

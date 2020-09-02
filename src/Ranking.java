import java.util.ArrayList;

public class Ranking {
    private ArrayList<SimulatedPlayer> ranking;

    public Ranking(ArrayList<SimulatedPlayer> simulatedPlayerArrayList, TypesOfRanking type) {
        if (type == TypesOfRanking.ByELO) {
            RankingByScoreThenElo(simulatedPlayerArrayList);
        } else {
            RankingByScoreThenTieBreak(simulatedPlayerArrayList);
        }
    }

    public void RankingByScoreThenElo(ArrayList<SimulatedPlayer> simulatedPlayerArrayList) {
        ArrayList<SimulatedPlayer> tmpList = new ArrayList<>(simulatedPlayerArrayList);
        tmpList.sort(SimulatedPlayer::compareToByScoreThenElo);
        this.ranking = tmpList;
    }

    public void RankingByScoreThenTieBreak(ArrayList<SimulatedPlayer> simulatedPlayerArrayList) {
        ArrayList<SimulatedPlayer> tmpList = new ArrayList<>(simulatedPlayerArrayList);
        tmpList.sort(SimulatedPlayer::compareToByTieBreak);
        this.ranking = tmpList;
    }

    public enum TypesOfRanking {
        ByELO, ByBUCHHOLZ
    }

    public ArrayList<SimulatedPlayer> getRanking() {
        return ranking;
    }
}

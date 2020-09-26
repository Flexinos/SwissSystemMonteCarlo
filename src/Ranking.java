import java.util.ArrayList;
import java.util.List;

public class Ranking {
    private List<SimulatedPlayer> ranking;

    public Ranking(List<SimulatedPlayer> simulatedPlayerArrayList, TypesOfRanking type) {
        if (type == TypesOfRanking.ByELO) {
            RankingByScoreThenElo(simulatedPlayerArrayList);
        } else {
            RankingByScoreThenTieBreak(simulatedPlayerArrayList);
        }
    }

    private void RankingByScoreThenElo(List<SimulatedPlayer> simulatedPlayerArrayList) {
        List<SimulatedPlayer> tmpList = new ArrayList<>(simulatedPlayerArrayList);
        tmpList.sort(SimulatedPlayer::compareToByScoreThenElo);
        this.ranking = tmpList;
    }

    private void RankingByScoreThenTieBreak(List<SimulatedPlayer> simulatedPlayerArrayList) {
        List<SimulatedPlayer> tmpList = new ArrayList<>(simulatedPlayerArrayList);
        tmpList.sort(SimulatedPlayer::compareToByTieBreak);
        this.ranking = tmpList;
    }

    public enum TypesOfRanking {
        ByELO, ByBUCHHOLZ
    }

    public List<SimulatedPlayer> getRanking() {
        return ranking;
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder("Rang:\tName:\tElo:\tScore:\tTieBreak1:\tTieBreak2:\tTieBreak3:");
        for (int i = 0, rankingSize = ranking.size(); i < rankingSize; ++i) {
            SimulatedPlayer player = ranking.get(i);
            tmp.append(i).append(". ").append(player.toString());
        }
        return tmp.toString();
    }
}
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

    public void RankingByScoreThenElo(List<SimulatedPlayer> simulatedPlayerArrayList) {
        List<SimulatedPlayer> tmpList = new ArrayList<>(simulatedPlayerArrayList);
        tmpList.sort(SimulatedPlayer::compareToByScoreThenElo);
        this.ranking = tmpList;
    }

    public void RankingByScoreThenTieBreak(List<SimulatedPlayer> simulatedPlayerArrayList) {
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
}
//todo write toString
//rankingByScoreThenTieBreakArrayList.get(rankingByScoreThenTieBreakArrayList.size() - 1).getRanking().stream().map(player -> player.getParticipant().getName() + "\tScore: " + player.getScore() + "\tBuchholz: " + player.getBuchholz() + "\tElo: " + player.getParticipant().getElo()).forEach(System.out::println);
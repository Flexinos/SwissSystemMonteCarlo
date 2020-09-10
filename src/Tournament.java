import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class Tournament {
    private final int totalRounds;
    private final List<Participant> participantArrayList = new ArrayList<>();
    public static final SimulatedPlayer BYE = new SimulatedPlayer(new Participant("BYE", 0));
    private final LongAdder finishedSimulations = new LongAdder();
    public Map<Participant, LongAdder> topThreeCounter;

    public Tournament(int totalRounds, List<Participant> participants) {
        this.totalRounds = totalRounds;
        this.participantArrayList.addAll(participants);
        this.participantArrayList.sort(Participant::compareToByElo);
        for (int i = 0; i < participantArrayList.size(); i++) {
            participantArrayList.get(i).setStartingRank(i + 1);
        }
        this.topThreeCounter = new ConcurrentHashMap<>((int) (participantArrayList.size() / 0.75), (float) 0.75, Main.numberOfConcurrentThreads);
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public SimulatedPlayer getBYE() {
        return BYE;
    }

    public List<Participant> getPlayerArrayList() {
        return participantArrayList;
    }

    public LongAdder getFinishedSimulations() {
        return finishedSimulations;
    }

    public void addToLongAdder(int x) {
        finishedSimulations.add(x);
    }
}

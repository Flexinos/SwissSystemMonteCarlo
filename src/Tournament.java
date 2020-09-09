import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class Tournament {
    private final int totalRounds;
    private final List<Participant> participantArrayList = new ArrayList<>();
    public static final SimulatedPlayer BYE = new SimulatedPlayer(new Participant("BYE", 0));
    public AbstractMap<Participant, LongAdder> topThreeCounter;

    public Tournament(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public void addParticipants(List<Participant> participants) {
        participantArrayList.addAll(participants);
        participants.sort(Participant::compareToByElo);
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
}

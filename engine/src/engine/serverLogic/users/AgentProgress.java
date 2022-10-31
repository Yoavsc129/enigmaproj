package engine.serverLogic.users;

public class AgentProgress {

    private final String name;
    private int inQueue = 0, fromServer = 0, finished = 0, candidates = 0;

    private int missionCount;

    public AgentProgress(String name) {
        this.name = name;
    }

    public void updateProgress(int inQueue, int fromServer, int finished, int candidates) {
        this.inQueue = inQueue;
        this.fromServer = fromServer;
        this.finished = finished;
        this.candidates = candidates;
    }

    public int getInQueue() {
        return inQueue;
    }

    public int getFromServer() {
        return fromServer;
    }

    public int getFinished() {
        return finished;
    }

    public int getCandidates() {
        return candidates;
    }

    public String getName() {
        return name;
    }
}

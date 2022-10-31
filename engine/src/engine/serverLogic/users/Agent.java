package engine.serverLogic.users;

public class Agent extends User{

    private int threadCount;

    private int missionCount;

    private boolean updateReady = false;

    private AgentProgress agentProgress;

    public Agent(String name, String type) {
        super(name, type);
        agentProgress = new AgentProgress(name);
    }

    public void setAgentDetails(int threadCount, int missionCount){
        this.threadCount = threadCount;
        this.missionCount = missionCount;
    }

    public synchronized void updateAgentProgress(int inQueue, int fromServer, int finished, int candidates){
        agentProgress.updateProgress(inQueue, fromServer, finished, candidates);
        updateReady = true;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getMissionCount() {
        return missionCount;
    }

    public synchronized AgentProgress getAgentProgress() {
        if(updateReady){
            updateReady = false;
            return agentProgress;
        }
        else{
            return null;
        }
    }
}

package engine.serverLogic.users;

public class Ally extends User{

    private int missionSize;
    private int agentsCount = 0;
    public Ally(String name, String type) {
        super(name, type);
    }

    public void setMissionSize(int missionSize) {
        this.missionSize = missionSize;
    }

    public void addAgent(){agentsCount+=1;}

    public int getMissionSize() {
        return missionSize;
    }

    public int getAgentsCount() {
        return agentsCount;
    }
}

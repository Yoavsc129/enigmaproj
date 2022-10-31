package uBoat.client.component.main.tabs.contestTab;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TeamRow {



    private final SimpleStringProperty name;
    private final SimpleIntegerProperty agentsCount;
    private final SimpleIntegerProperty missionSize;

    public TeamRow(String name, int agentsCount, int missionSize) {
        this.name = new SimpleStringProperty(name);
        this.agentsCount = new SimpleIntegerProperty(agentsCount);
        this.missionSize = new SimpleIntegerProperty(missionSize);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getAgentsCount() {
        return agentsCount.get();
    }

    public SimpleIntegerProperty agentsCountProperty() {
        return agentsCount;
    }

    public void setAgentsCount(int agentsCount) {
        this.agentsCount.set(agentsCount);
    }

    public int getMissionSize() {
        return missionSize.get();
    }

    public SimpleIntegerProperty missionSizeProperty() {
        return missionSize;
    }

    public void setMissionSize(int missionSize) {
        this.missionSize.set(missionSize);
    }
}

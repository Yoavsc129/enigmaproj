package client.component.tabs.contestTab.alliesTable;

import javafx.beans.property.SimpleStringProperty;

public class AlliesTableRow {
    private final SimpleStringProperty name;
    private final SimpleStringProperty agentCount;
    private final SimpleStringProperty missionSize;

    public AlliesTableRow(String name, int agentCount, int missionSize) {
        this.name = new SimpleStringProperty(name);
        this.agentCount = new SimpleStringProperty(Integer.toString(agentCount));
        this.missionSize = new SimpleStringProperty(Integer.toString(missionSize));
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getAgentCount() {
        return agentCount.get();
    }

    public SimpleStringProperty agentCountProperty() {
        return agentCount;
    }

    public String getMissionSize() {
        return missionSize.get();
    }

    public SimpleStringProperty missionSizeProperty() {
        return missionSize;
    }
}

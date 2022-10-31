package client.component.candidatesTable;

import javafx.beans.property.SimpleStringProperty;

public class TableRow {
    private final SimpleStringProperty candidate, configuration;

    public TableRow(String candidate, String configuration) {
        this.candidate = new SimpleStringProperty(candidate);
        this.configuration = new SimpleStringProperty(configuration);
    }

    public String getCandidate() {
        return candidate.get();
    }

    public SimpleStringProperty candidateProperty() {
        return candidate;
    }

    public String getConfiguration() {
        return configuration.get();
    }

    public SimpleStringProperty configurationProperty() {
        return configuration;
    }
}

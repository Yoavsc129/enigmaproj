package engine.bruteForce.tasks;

public class TaskResult {

    private final String candidate;

    private final String configuration;

    public TaskResult(String candidate, String configuration) {
        this.candidate = candidate;
        this.configuration = configuration;
    }

    public String getCandidate() {
        return candidate;
    }

    public String getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return candidate;
    }
}

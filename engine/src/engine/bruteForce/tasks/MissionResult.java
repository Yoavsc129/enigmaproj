package engine.bruteForce.tasks;

import java.util.List;

public class MissionResult {
    private List<TaskResult> results;
    private int tasksDone;
    private int candidatesFound;

    public MissionResult(List<TaskResult> results, int tasksDone) {
        this.results = results;
        this.tasksDone = tasksDone;
        this.candidatesFound = results.size();
    }

    public List<TaskResult> getResults() {
        return results;
    }

    public int getTasksDone() {
        return tasksDone;
    }

    public int getCandidatesFound() {
        return candidatesFound;
    }
}

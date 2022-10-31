package engine.serverLogic.candidates;

public class Candidate {
    private final String candidate;

    private final String configuration;

    private final String allyTeam;

    private final String agent;
    public Candidate(String candidate, String configuration, String allyTeam, String agent) {
        this.candidate = candidate;
        this.configuration = configuration;
        this.allyTeam = allyTeam;
        this.agent = agent;
    }

    public String getCandidate() {
        return candidate;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getAllyTeam() {
        return allyTeam;
    }

    public String getAgent() {
        return agent;
    }
}

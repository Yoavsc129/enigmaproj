package engine.serverLogic.candidates;

import java.util.ArrayList;
import java.util.List;

public class CandidateManager {

    private final List<Candidate> candidates;

    public CandidateManager() {
        candidates = new ArrayList<>();
    }

    public synchronized void addCandidates(List<Candidate> newCandidates, String ally, String agent){
        for(Candidate candidate: newCandidates){
            //might need another way
            candidates.add(new Candidate(candidate.getCandidate(), candidate.getConfiguration(), ally, agent));

        }
    }

    public synchronized List<Candidate> getCandidates(int fromIndex){
        if (fromIndex < 0 || fromIndex > candidates.size()) {
            fromIndex = 0;
        }
        return candidates.subList(fromIndex, candidates.size());
    }

    public int getVersion(){return candidates.size();}
}

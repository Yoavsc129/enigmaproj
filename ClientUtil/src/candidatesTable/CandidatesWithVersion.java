package candidatesTable;

import java.util.List;

public class CandidatesWithVersion {
     private List<Candidate> candidates;
     private int version;

     public void setProperties(){
         for(Candidate candidate: candidates)
             candidate.setProperties();
     }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public int getVersion() {
        return version;
    }
}

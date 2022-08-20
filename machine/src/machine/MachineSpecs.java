package machine;

import java.util.List;

public class MachineSpecs {
    private List<Integer> rotorIds;
    private List<Integer> rotorNotches;
    private List<Character> positions;
    private String reflectorId;
    private List<Character> plugs;

    public MachineSpecs(List<Integer> rotorIds, List<Integer> rotorNotches, List<Character> positions, String reflectorId, List<Character> plugs) {
        this.rotorIds = rotorIds;
        this.rotorNotches = rotorNotches;
        this.positions = positions;
        this.reflectorId = reflectorId;
        this.plugs = plugs;
    }

    public List<Integer> getRotorIds() {
        return rotorIds;
    }

    public List<Integer> getRotorNotches() {
        return rotorNotches;
    }

    public List<Character> getPositions() {
        return positions;
    }

    public String getReflectorId() {
        return reflectorId;
    }

    public List<Character> getPlugs() {
        return plugs;
    }
}

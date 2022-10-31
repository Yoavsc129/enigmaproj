package engine;

import machine.MachineSpecs;

public class MachineDetails {

    private int usedRotors;

    private int totalRotors;

    private int totalReflectors;

    private int messages;

    private MachineSpecs specs = null;


    public MachineDetails(int usedRotors, int totalRotors, int totalReflectors, int messages) {
        this.usedRotors = usedRotors;
        this.totalRotors = totalRotors;
        this.totalReflectors = totalReflectors;
        this.messages = messages;
    }

    public int getUsedRotors() {
        return usedRotors;
    }

    public int getTotalRotors() {
        return totalRotors;
    }

    public int getTotalReflectors() {
        return totalReflectors;
    }

    public int getMessages() {
        return messages;
    }

    public MachineSpecs getSpecs() {
        return specs;
    }
}

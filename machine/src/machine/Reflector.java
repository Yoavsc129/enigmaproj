package machine;

import machine.generated.CTEReflect;

import java.util.List;

public class Reflector {
    public ReflectorID getID() {
        return ID;
    }

    public enum ReflectorID {
        I, II, III, IV, V



    }
    private ReflectorID ID;
    private int[] reflection;

    private int size;

    public Reflector(ReflectorID id, int size, List<CTEReflect> cteReflects) {
        ID = id;
        reflection = new int[size];
        for(CTEReflect reflect : cteReflects){
            reflection[reflect.getInput() - 1] = reflect.getOutput() - 1;
            reflection[reflect.getOutput() - 1] = reflect.getInput() - 1;
        };
    }

    public int reflect(int input){
        return reflection[input];
    }


}

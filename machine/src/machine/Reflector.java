package machine;

import machine.generated.CTEReflect;

import java.io.Serializable;
import java.util.List;

public class Reflector implements Serializable {
    public ReflectorID getID() {
        return ID;
    }

    public enum ReflectorID {
        I, II, III, IV, V
    }
    private final ReflectorID ID;
    private final int[] reflection;


    public Reflector(ReflectorID id, int size, List<CTEReflect> cteReflects) {
        ID = id;
        reflection = new int[size];
        for(CTEReflect reflect : cteReflects){
            reflection[reflect.getInput() - 1] = reflect.getOutput() - 1;
            reflection[reflect.getOutput() - 1] = reflect.getInput() - 1;
        }
    }

    public int reflect(int input){
        return reflection[input];
    }


}

package machine;

import java.io.Serializable;

public class MachineSpecs implements Serializable {
    private String rotors;
    private String positions;
    private String reflector;
    private String plugs;


    public MachineSpecs(String rotors, String positions, String reflector, String plugs) {
        this.rotors = rotors;
        this.positions = positions;
        this.reflector = reflector;
        this.plugs = plugs;
    }

    public MachineSpecs(MachineSpecs item){
        rotors = item.rotors;
        positions = item.positions;
        reflector = item.reflector;
        plugs = item.plugs;
    }

    public String format(){
        StringBuilder format = new StringBuilder();
        format.append(String.format("<%s><%s><%s>", rotors, positions, reflector));
        if(plugs != null) {
            format.append('<');
            for (int i = 0; i < plugs.length(); i+=2) {
                format.append(String.format("%c|%c,", plugs.charAt(i), plugs.charAt(i + 1)));
            }
            format.setCharAt(format.length() - 1, '>');
        }
        return format.toString();
    }
}

package machine;
import machine.generated.CTEReflector;
import machine.generated.CTERotor;

import java.io.Serializable;
import java.util.*;

public class Machine implements Serializable {
    private final int rotorsCount;
    private final String ABC;

    private final Map<Integer, Rotor> allRotors;
    private List<Rotor> usedRotors;
    private final Map<machine.Reflector.ReflectorID, Reflector> reflectors;
    private Reflector usedReflector;
    private Plugs plugs;

    private List<Rotor> tempRotors;

    private Plugs tempPlugs;

    private Reflector tempReflector;

    private MachineSpecs machineSpecs = null;

    public Machine(int rotorsCount, String abc, List<CTERotor> cteRotor, List<CTEReflector> cteReflector) {
        this.rotorsCount = rotorsCount;
        ABC = abc;
        allRotors = new HashMap<>();
        reflectors = new HashMap<>();
        usedRotors = new ArrayList<>();
        for(CTERotor rotor : cteRotor)
            allRotors.put(rotor.getId(),new Rotor(rotor.getId(), rotor.getNotch(),abc.length(), rotor.getCTEPositioning()));

        for (CTEReflector reflector : cteReflector) {
            reflectors.put(Reflector.ReflectorID.valueOf(reflector.getId()),new Reflector(Reflector.ReflectorID.valueOf(reflector.getId()), abc.length(), reflector.getCTEReflect()));
        }

        usedRotors.add(new Rotor());
        plugs = new Plugs(ABC);
        tempRotors = usedRotors;
        tempPlugs = plugs;
    }


    public void pickTempRotors(List<Integer> rotorsIDs){
        tempRotors = new ArrayList<>();
        tempRotors.add(new Rotor());
        for(int id : rotorsIDs)
            tempRotors.add(allRotors.get(id).clone());

    }

    public void pickTempReflector(String ID){tempReflector = reflectors.get(Reflector.ReflectorID.valueOf(ID));}


    public void setInitialTempRotorsPosition(String positions){
        int i = 0;
        for(Rotor rotor : tempRotors){
            if(rotor.getId() > 0){
                rotor.setInitialPosition(positions.charAt(i));
                i++;
            }
        }
    }

    public void setupTempPlugs(){tempPlugs = new Plugs(ABC);}

    public void setTempPlugs(char c1, char c2){tempPlugs.setPlugs(c1, c2);}


    public char getPlugs(char c){
        return plugs.getPlug(c);
    }

    public void applyChanges(){
        usedRotors = tempRotors;
        usedReflector = tempReflector;
        plugs = tempPlugs;

    }

    public  char encodeChar(char c){
        return getPlugs(ABC.charAt(encode(ABC.indexOf(getPlugs(c)) + 1) - 1));
    }

    private char encodeCharNoPlugs(char c){return ABC.charAt(encode(ABC.indexOf(c) + 1) - 1);}

    public String encodeStringNoPlugs(String input){
        StringBuilder res = new StringBuilder();
        char[] inputAsArray = input.toCharArray();
        for(char c: inputAsArray)
            res.append(encodeCharNoPlugs(c));

        return res.toString();
    }

    private int encode(int index){
        int res = index;
        for (int i = 0; i < usedRotors.size(); i++) {
            if(i + 1 < usedRotors.size())
                usedRotors.get(i).rotateNext(usedRotors.get(i + 1));
            if(usedRotors.get(i).getId() > 0)
                res=usedRotors.get(i).getLeftIndex(res);

        }
        res = usedReflector.reflect(res - 1) + 1;
        for (int i = usedRotors.size() - 1; i >= 0; i--)
            if(usedRotors.get(i).getId() > 0)
                res=usedRotors.get(i).getRightIndex(res);

        return res;
    }

    public String getABC(){return ABC;}

    public int getRotorsCount() {
        return rotorsCount;
    }

    public int getRotorsTotal(){
        return allRotors.size();
    }

    public int getReflectorsTotal(){
        return reflectors.size();
    }

    public List<Rotor> getUsedRotors() {
        return usedRotors;
    }

    public String createFormatMachineSpecs(){
        StringBuilder res = new StringBuilder("<");
        StringBuilder pos = new StringBuilder("<");
        int i = 2;
        for(Rotor r : usedRotors){
            if(r.getId() > 0){
                res.insert(1, r.createRotorSpecs());
                pos.insert(1, r.getCurrPosition());
                if(i < usedRotors.size())
                    res.insert(1, ',');
                i++;
            }
        }
        res.append('>');
        pos.append('>');
        res.append(pos);
        res.append(String.format("<%s>", usedReflector.getID()));
        if(plugs.getInUse())
            res.append(plugs.createPlugsSpecs());


        return res.toString();
    }


    public void resetRotors(){
        for(Rotor r : usedRotors)
            if(r.getId()>0)
                r.resetRotor();
    }

    public MachineSpecs createMachineSpecs() {
        StringBuilder rotors = new StringBuilder();
        StringBuilder positions = new StringBuilder();
        String positionsFormat;
        int i = 2;
        for(Rotor r : usedRotors){
            if(r.getId() > 0){
                rotors.insert(0, r.getId());
                positionsFormat = String.format("%c(%d)", r.getCurrPosition(), r.getNotch() - 1);
                //positions.insert(1, r.getCurrPosition());
                positions.insert(0, positionsFormat);
                if(i < usedRotors.size()) {
                    rotors.insert(0, ',');
                    positions.insert(0, ',');
                }
                i++;
            }
        }
        String plugsStr = null;
        if(plugs.getInUse())
            plugsStr = plugs.getAllPlugs();


        return new MachineSpecs(rotors.toString(), positions.toString(), usedReflector.getID().toString(), plugsStr);
    }

    public void setMachineSpecs() {
       machineSpecs = createMachineSpecs();
    }

    public MachineSpecs getMachineSpecs(){
        return machineSpecs = createMachineSpecs();
    }
}

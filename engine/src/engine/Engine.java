package engine;

import engine.exception.*;
import machine.Machine;
import machine.Plugs;
import machine.Reflector;
import machine.Rotor;
import machine.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Engine {

    private final static String JAXB_PACKAGE_NAME = "machine.generated";
    private InputStream inputStream;

    private Machine enigma;

    private boolean machineLoaded = false;

    private int messagesCount = 0;

    private String initialSpecs = null;

    private List<Stats> stats;

    private Stats currStats;

    private void openNewFile(String fileName) throws FileNotFoundException {
        InputStream temp = null;
        try {
            temp = new FileInputStream(fileName);
        }catch (FileNotFoundException e){
            throw e;
        }
        inputStream = temp;
    }
    private void createXMLEnigma() throws RotorCountTooLowException, ABCNotEvenException, NotUniqueIdException, DoubleMappingException, NotchOutOfRangeException, NotEnoughRotorsException, RotorIdOutOfScopeException {
        try {
            CTEEnigma cteEnigma = deserializeForm(inputStream);
            CTEMachine cteMachine = cteEnigma.getCTEMachine();
            checkCTEnigma(cteMachine);
            enigma = new Machine(cteMachine.getRotorsCount(), cteMachine.getABC().trim(),
                    cteMachine.getCTERotors().getCTERotor(),
                    cteMachine.getCTEReflectors().getCTEReflector());
            machineLoaded = true;
            stats = new ArrayList<>();
            initialSpecs = null;
            messagesCount = 0;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
            //something nicer
        } catch (Exception e){
            throw e;
        }

    }

    private static CTEEnigma deserializeForm(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (CTEEnigma) u.unmarshal(in);
    }

    private void checkCTEnigma(CTEMachine cteMachine) throws ABCNotEvenException, RotorCountTooLowException, NotEnoughRotorsException, NotUniqueIdException, DoubleMappingException, NotchOutOfRangeException, RotorIdOutOfScopeException {
        if(cteMachine.getABC().trim().length()%2 != 0)
            throw new ABCNotEvenException(cteMachine.getABC().trim().length());

        if(cteMachine.getRotorsCount() < 2)
            throw new RotorCountTooLowException();

        if(cteMachine.getRotorsCount() > cteMachine.getCTERotors().getCTERotor().size())
            throw new NotEnoughRotorsException(cteMachine.getRotorsCount(),cteMachine.getCTERotors().getCTERotor().size());
        try{
            checkCTERotors(cteMachine.getCTERotors().getCTERotor(), cteMachine.getABC().trim().length());
        }catch (Exception e){
            throw e;
        }

    }
    private void checkCTERotors(List<CTERotor> CTERotors, int abcLen) throws RotorIdOutOfScopeException, NotUniqueIdException, NotchOutOfRangeException, DoubleMappingException {
        int size = CTERotors.size();
        boolean[] countIds = new boolean[size];
        Arrays.fill(countIds, false);
        int id;
        Set<Character> abcCounterRight = new HashSet<>();
        Set<Character> abcCounterLeft = new HashSet<>();
        char right;
        char left;

        for(CTERotor r: CTERotors){
            id = r.getId();
            if(id< 1 || id>size)
                throw new RotorIdOutOfScopeException(id, size);
            if(countIds[id - 1])
                throw new NotUniqueIdException(id);
            countIds[id-1] = true;
            if(r.getNotch() < 1 || r.getNotch() > abcLen)
                throw new NotchOutOfRangeException(id, r.getNotch(), abcLen);

            for(CTEPositioning p : r.getCTEPositioning()){
                abcCounterLeft.clear();
                abcCounterRight.clear();
                right = p.getRight().charAt(0);
                left = p.getLeft().charAt(0);
                if(abcCounterLeft.contains(left))
                    throw new DoubleMappingException(left, id, "left");
                else abcCounterLeft.add(left);
                if(abcCounterRight.contains(right))
                    throw new DoubleMappingException(right, id, "right");
                else abcCounterRight.add(right);
            }
        }
    }

    public void createMachineFromFile(String fileName) throws FileNotFoundException, RotorCountTooLowException, ABCNotEvenException, NotUniqueIdException, DoubleMappingException, NotchOutOfRangeException, NotEnoughRotorsException, RotorIdOutOfScopeException {
        try{
            openNewFile(fileName);
            createXMLEnigma();
        } catch(FileNotFoundException e){
            throw e;
        } catch (Exception e){
            throw e;
        }
    }

    public int getReflectorsTotal(){return enigma.getReflectorsTotal();}

    public String getMachineSpecs(){
        StringBuilder res = new StringBuilder();
        res.append(String.format("used rotors/total rotors: %d/%d \ntotal reflectors: %d\nmessages: %d",
                enigma.getRotorsCount(), enigma.getRotorsTotal(),
                enigma.getReflectorsTotal(), messagesCount));
        if(initialSpecs != null){
            res.append(String.format("\n%s\n", initialSpecs));
            res.append(enigma.createMachineSpecs());
        }
        return res.toString();
    }
    public String pickRotors(String rotorIds){
        String[] idsString = rotorIds.split(",");
        List<Integer> ids = new ArrayList<>();
        int id;
        for(String num : idsString) {
            try{
                id = Integer.parseInt(num.trim());
                if(idsString.length != enigma.getRotorsCount())
                    return String.format("%d rotors are required but %d were entered", enigma.getRotorsCount(), idsString.length);
                if(id<1 || id> enigma.getRotorsTotal())
                    return String.format("The ID %d doesn't match any of the rotors.", id);
                if(ids.contains(id))
                    return String.format("The ID %d was chosen at least twice. please enter different IDs.", id);
                ids.add(id);
            }catch (NumberFormatException e){
                return String.format("Please only enter numbers.");
            }

        }
        Collections.reverse(ids);
        //enigma.pickRotors(ids);
        enigma.pickTempRotors(ids);
        return null;
    }
    public String pickInitialRotorsPos(String pos){
        String abc = enigma.getABC();
        if(pos.length() != enigma.getRotorsCount())
            return String.format("%d positions are required but %d were entered", enigma.getRotorsCount(), pos.length());
        for(Character c : pos.toCharArray())
            if(!abc.contains(c.toString()))
                return String.format("The character %c is not part of the ABC.", c);

        enigma.setInitialTempRotorsPosition(pos);
        return null;
    }
    public void pickReflector(int ID){
        enigma.pickTempReflector(Reflector.ReflectorID.values()[ID].name());
    }

    public String pickPlugs(String plugs){
        enigma.setupTempPlugs();
        if(plugs.length() == 0)
            return null;
        char[] letters = plugs.toUpperCase().toCharArray();
        if((letters.length%2) !=0)
            return "Please input an even number of letters";
        String plugsCheck = checkPlugsString(letters);
        if(plugsCheck != null)
            return plugsCheck;
        int i = 0;
        while(i<plugs.length()){
            enigma.setTempPlugs(letters[i], letters[i + 1]);
            i+=2;
        }
        return null;
    }
    private String checkPlugsString(char[] letters){
        String abc = enigma.getABC();
        Set<Character> lettersCount = new HashSet<>();
        for(Character c : letters){
            if(!abc.contains(c.toString()))
                return String.format("The letter %c is not part of the machine's ABC.", c);
            if(lettersCount.contains(c))
                return String.format("The letter %c was given at least twice.", c);
            lettersCount.add(c);
        }

        return null;
    }

    public void applyChanges(){
        enigma.applyChanges();
    }
    public void randMachineSpecs(){
        Random rng = new Random();
        List<Integer> rotorIds = new ArrayList<>();

        String ABC = enigma.getABC();
        StringBuilder pos = new StringBuilder();
        StringBuilder plugs = new StringBuilder();
        for (int i = 0; i < enigma.getRotorsTotal(); i++){
            rotorIds.add(i + 1);
            pos.append(ABC.charAt(rng.nextInt(ABC.length())));
        }
        Collections.shuffle(rotorIds);
        int reflectorID = rng.nextInt(enigma.getReflectorsTotal());
        int plugsCount = rng.nextInt(ABC.length()/2 + 1)*2;
        List<Integer> abcIndexes = new ArrayList<>();
        for (int i = 0; i < ABC.length(); i++) 
            abcIndexes.add(i);
        Collections.shuffle(abcIndexes);
        for (int i = 0; i < plugsCount; i++)
            plugs.append(ABC.charAt(abcIndexes.get(i)));

        //enigma.reinitializeMachine();
        enigma.pickTempRotors(rotorIds.subList(0,enigma.getRotorsCount()));
        enigma.setInitialTempRotorsPosition(pos.toString());
        pickReflector(reflectorID);
        pickPlugs(plugs.toString());
        enigma.applyChanges();

        initialSpecs = enigma.createMachineSpecs();
        createStat();

    }


    public void setInitialSpecs(){
        initialSpecs = enigma.createMachineSpecs();
    }

    public String getInitialSpecs(){return initialSpecs;}

    public void createStat(){
        currStats = new Stats(initialSpecs);
        stats.add(currStats);
    }
    public void resetRotors(){
        enigma.resetRotors();
    }

    public String checkAbc(String msg) {
        String abc = enigma.getABC();
        for (Character c : msg.toCharArray()) {
            if (!abc.contains(c.toString()))
                return String.format("The letter %c is not part of the machine's ABC.", c);
        }
        return null;
    }
    public String decodeMsg(String msg){
        StringBuilder res = new StringBuilder();
        String check = checkAbc(msg);
        if(check != null)
            return check;
        long start = System.nanoTime();
        for(char c: msg.toCharArray())
            res.append(enigma.encodeChar(c));

        long end = System.nanoTime();
        currStats.addMessage(msg, res.toString(), end - start);
        messagesCount+=1;

        return res.toString();
    }

    public String getStats(){
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for(Stats s : stats){
            if(first){
                res.append(s.getSpecs());
                res.append(s.getMessages());
                first = false;
            }
            else{
                res.append("\n" + s.getSpecs());
                res.append(s.getMessages());
            }
        }
        return res.toString();
    }

    public boolean isMachineLoaded() {
        return machineLoaded;
    }
}

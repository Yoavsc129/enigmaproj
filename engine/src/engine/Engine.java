package engine;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import engine.bruteForce.Decipher;
import engine.bruteForce.Difficulty;
import engine.exception.*;
import machine.Machine;
import machine.MachineSpecs;
import machine.Reflector;
import machine.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Engine {

    private final static String JAXB_PACKAGE_NAME = "machine.generated";

    private final static int AGENT_COUNT = 20;
    private transient InputStream inputStream;

    private Machine enigma;

    private Decipher decipher;

    public MachineDetails getDetails() {
        return details;
    }

    private MachineDetails details;

    private boolean machineLoaded = false;

    private int messagesCount = 0;

    private String initialSpecs = null;

    private List<Stats> stats;

    private Stats currStats;

    public MachineSpecs getInitialMachineSpecs() {
        return initialMachineSpecs;
    }

    public MachineSpecs getCurrMachineSpecs() {
        return currMachineSpecs = enigma.getMachineSpecs();
    }

    private MachineSpecs initialMachineSpecs;

    private MachineSpecs currMachineSpecs;

    private String savedMachine;

    private boolean newDisMsg = true;

    private int alliesReq;

    private List<Integer> pickedRotors;

    private int pickedReflector;

    public Engine() {
        pickedRotors = new ArrayList<>();
    }

    public List<Integer> getPickedRotors() {
        return pickedRotors;
    }

    public int getPickedReflector() {
        return pickedReflector;
    }

    public int getAlliesReq() {
        return alliesReq;
    }

    public void setAlliesReq(int alliesReq) {
        this.alliesReq = alliesReq;
    }

    private void openNewFile(String fileName) throws FileNotFoundException {
        InputStream temp;
        try {
            temp = new FileInputStream(fileName);
        }catch (FileNotFoundException e){
            throw e;
        }
        inputStream = temp;
    }
    public void createXMLEnigma(CTEEnigma cteEnigma) throws RotorCountTooLowException, ABCNotEvenException, NotUniqueIdException, DoubleMappingException, NotchOutOfRangeException, NotEnoughRotorsException, RotorIdOutOfScopeException, BadReflectorIdException, NotUniqueRefIdException, BadReflectException, AgentCountOutOfRangeException {
        try {
            CTEMachine cteMachine = cteEnigma.getCTEMachine();
            CTEDecipher cteDecipher = cteEnigma.getCTEDecipher();
            /*if(cteDecipher.getAgents() < 2 || cteDecipher.getAgents() > 50)
                throw new AgentCountOutOfRangeException(cteDecipher.getAgents());*/
            checkCTEnigma(cteMachine);
            enigma = new Machine(cteMachine.getRotorsCount(), cteMachine.getABC().trim(),
                    cteMachine.getCTERotors().getCTERotor(),
                    cteMachine.getCTEReflectors().getCTEReflector());
            decipher = new Decipher(AGENT_COUNT, cteDecipher.getCTEDictionary().getWords(),
                    cteDecipher.getCTEDictionary().getExcludeChars());
            machineLoaded = true;
            stats = new ArrayList<>();
            initialSpecs = null;
            messagesCount = 0;
            details = new MachineDetails(enigma.getRotorsCount(), enigma.getRotorsTotal(),
                    enigma.getReflectorsTotal(), messagesCount);
        }catch (Exception e){
            throw e;
        }

    }


    public static CTEEnigma deserializeForm(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (CTEEnigma) u.unmarshal(in);
    }

    private void checkCTEnigma(CTEMachine cteMachine) throws ABCNotEvenException, RotorCountTooLowException, NotEnoughRotorsException, NotUniqueIdException, DoubleMappingException, NotchOutOfRangeException, RotorIdOutOfScopeException, BadReflectorIdException, NotUniqueRefIdException, BadReflectException {
        if(cteMachine.getABC().trim().length()%2 != 0)
            throw new ABCNotEvenException(cteMachine.getABC().trim().length());

        if(cteMachine.getRotorsCount() < 2)
            throw new RotorCountTooLowException();

        if(cteMachine.getRotorsCount() > cteMachine.getCTERotors().getCTERotor().size())
            throw new NotEnoughRotorsException(cteMachine.getRotorsCount(),cteMachine.getCTERotors().getCTERotor().size());
        try{
            checkCTERotors(cteMachine.getCTERotors().getCTERotor(), cteMachine.getABC().trim().length());
            checkCTEReflectors(cteMachine.getCTEReflectors().getCTEReflector());
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

            abcCounterLeft.clear();
            abcCounterRight.clear();
            for(CTEPositioning p : r.getCTEPositioning()){

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

    private void checkCTEReflectors(List<CTEReflector> CTEReflectors) throws BadReflectorIdException, NotUniqueRefIdException, BadReflectException {
        Reflector.ReflectorID[] ids = Reflector.ReflectorID.values();
        List<String> idsStrings = new ArrayList<>();
        String id;
        for(Reflector.ReflectorID i : ids)
            idsStrings.add(i.name());
        idsStrings = idsStrings.subList(0,CTEReflectors.size());
        Set<String> usedIds = new HashSet<>();
        for(CTEReflector r : CTEReflectors){
            id = r.getId();
            if(!idsStrings.contains(id))
                throw new BadReflectorIdException(id, idsStrings.get(idsStrings.size() - 1));
            if(usedIds.contains(r.getId()))
                throw new NotUniqueRefIdException(id);
            usedIds.add(id);
            for(CTEReflect t: r.getCTEReflect())
                if(t.getInput() == t.getOutput())
                    throw new BadReflectException(id, t.getInput());
        }
    }


    public void createMachineFromFile(String fileName) throws FileNotFoundException, RotorCountTooLowException, ABCNotEvenException, NotUniqueIdException, DoubleMappingException, NotchOutOfRangeException, NotEnoughRotorsException, RotorIdOutOfScopeException, BadReflectorIdException, NotUniqueRefIdException, BadReflectException, AgentCountOutOfRangeException {
        try{
            openNewFile(fileName);
            CTEEnigma cteEnigma = deserializeForm(inputStream);
            createXMLEnigma(cteEnigma);
        }catch(JAXBException e){
            throw new RuntimeException();
        }catch(Exception e){
            throw e;
        }
    }



    public int getReflectorsTotal(){return enigma.getReflectorsTotal();}

    public int getRotorsCount(){return enigma.getRotorsCount();}

    public int getRotorsTotal(){return enigma.getRotorsTotal();}
    public int getMessagesCount(){return messagesCount;}

    public String getMachineSpecs(){
        StringBuilder res = new StringBuilder();
        res.append(String.format("used rotors/total rotors: %d/%d \ntotal reflectors: %d\nmessages: %d",
                enigma.getRotorsCount(), enigma.getRotorsTotal(),
                enigma.getReflectorsTotal(), messagesCount));
        if(initialSpecs != null){
            res.append(String.format("\n%s\n", initialSpecs));
            res.append(enigma.createFormatMachineSpecs());
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
                return "Please only enter numbers.";
            }

        }
        Collections.reverse(ids);
        enigma.pickTempRotors(ids);
        pickedRotors = ids;
        return null;
    }
    public String pickInitialRotorsPos(String pos){
        String abc = enigma.getABC();
        if(pos.length() != enigma.getRotorsCount())
            return String.format("%d positions are required but %d were entered", enigma.getRotorsCount(), pos.length());
        for(Character c : pos.toCharArray())
            if(!abc.contains(c.toString()))
                return String.format("The character %c is not part of the ABC.", c);
        char[] letters = pos.toCharArray();
        String reversedPos = "";
        for (int i = pos.length() - 1; i >= 0; i--) {
            reversedPos = reversedPos + letters[i];
        }
        enigma.setInitialTempRotorsPosition(reversedPos);
        return null;
    }
    public void pickReflector(int ID){
        pickedReflector = ID;
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
        enigma.setMachineSpecs();
        initialMachineSpecs = new MachineSpecs(enigma.getMachineSpecs());
        currMachineSpecs = enigma.getMachineSpecs();
        createStat();
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
        pickedRotors = rotorIds.subList(0,enigma.getRotorsCount());
        enigma.pickTempRotors(pickedRotors);
        enigma.setInitialTempRotorsPosition(pos.toString());
        pickedReflector = reflectorID;
        pickReflector(reflectorID);
        pickPlugs(plugs.toString());
        applyChanges();
        enigma.setMachineSpecs();
        initialMachineSpecs = new MachineSpecs(enigma.getMachineSpecs());
        currMachineSpecs = enigma.getMachineSpecs();

        initialSpecs = enigma.createFormatMachineSpecs();

    }

    public void writeMachineToString() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(enigma);
        out.close();
        decipher.setSavedEnigma(Base64.getEncoder().encodeToString(bytes.toByteArray()));
    }

    public void readMachineFromString(String machine){
        try {
            enigma = Decipher.readMachineFromString(machine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void setInitialSpecs(){
        initialSpecs = enigma.createFormatMachineSpecs();
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
        msg = msg.toUpperCase();
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

    public char decodeChar(char c){
        long start = System.nanoTime();
        String res = String.format("%c",enigma.encodeChar(c));
        long end = System.nanoTime();
        if(newDisMsg) {
            currStats.addMessage(String.format("%c", c), res, end - start);
            newDisMsg = false;
        }
        else{
            currStats.updateLastMsg(String.format("%c", c), res, end - start);
        }

        return res.charAt(0);
    }

    public String decodeMsgBT(String msg){
        return enigma.encodeStringNoPlugs(msg.toUpperCase());
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
                res.append("\n").append(s.getSpecs());
                res.append(s.getMessages());
            }
        }
        return res.toString();
    }

    public Msg getLastMsg(){
        return currStats.getLastMsg();
    }

    public void discardLastMsg(){
        currStats.discardLastMsg();
    }


    public boolean isMachineLoaded() {
        return machineLoaded;
    }

    public String getABC(){return enigma.getABC();}

    public boolean inDictionary(String word){
        return decipher.inDictionary(word);
    }

    public int getAgentsCount() {
        return decipher.getAgentsCount();
    }

    public List<String> getWordsList(){
        return decipher.getWordsList();
    }

    public List<String> dictionarySuggest(String prefix) {
        List<String> list = decipher.dictionarySuggest(prefix);
        return list;
    }

    public Decipher getDecipher() {
        return decipher;
    }

    public void discreteMsgDone(){
        newDisMsg = true;
    }

    private static class EngineDeserializer implements JsonDeserializer<Engine>{
        @Override
        public Engine deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return null;
        }
    }

    public Machine getEnigma() {
        return enigma;
    }
}

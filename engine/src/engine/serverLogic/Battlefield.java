package engine.serverLogic;

import engine.Engine;
import engine.bruteForce.Difficulty;
import engine.exception.AlliesCountLowException;
import engine.exception.InvalidDifficultyException;
import engine.serverLogic.candidates.CandidateManager;
import engine.serverLogic.users.Ally;
import machine.Machine;
import machine.generated.CTEBattlefield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Battlefield {

    private String name;
    private int alliesCount;

    private int alliesCurr = 0;
    private String difficulty;
    private Engine engine;
    private String uBoat;

    private String uBoatInput;

    private int totalTasks;

    private int posPermutations;

    private String status = "Waiting";
    List<Ally> allies;

    private Map<String, DM> DMs;

    private CandidateManager candidateManager;

    private boolean codeSet = false;

    private boolean endContest = false;

    private String winner = "";

    public Battlefield(CTEBattlefield cteBattlefield, Engine engine, String uBoat) {
        this.name = cteBattlefield.getBattleName();
        this.alliesCount = cteBattlefield.getAllies();
        this.difficulty = cteBattlefield.getLevel().toUpperCase();
        this.engine = engine;
        this.uBoat = uBoat;
        allies = new ArrayList<>();
        DMs = new HashMap<>();
        candidateManager = new CandidateManager();
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setuBoatInput(String uBoatInput) {
        this.uBoatInput = uBoatInput;
    }

    public void setStatus() {
        status = "In Progress";
    }

    public String getName() {
        return name;
    }

    public int getAlliesCount() {
        return alliesCount;
    }

    public int getAlliesCurr() {
        return alliesCurr;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Engine getEngine() {
        return engine;
    }

    public String getUBoat() {
        return uBoat;
    }

    public String getStatus() {
        return status;
    }

    public List<Ally> getAllies() {
        return allies;
    }

    public String getuBoatInput() {
        return uBoatInput;
    }

    public CandidateManager getCandidateManager() {
        if(!endContest)
            return candidateManager;
        else return null;

    }

    public void checkBattlefield() throws InvalidDifficultyException, AlliesCountLowException {
        /*if(alliesCount < 2)
            throw new AlliesCountLowException();*/
        Difficulty[] difficulties = Difficulty.values();
        for(Difficulty d: difficulties){
            if(difficulty.contains(d.name()))
                return;
        }
        throw new InvalidDifficultyException(difficulty);
    }

    public void addAlly(Ally ally){
        alliesCurr++;
        allies.add(ally);
    }

    public boolean isFull(){
        return alliesCurr == alliesCount;
    }

    public void startContest(){
        createDMs();
        startDMs();
    }

    public void setupConfiguration(String rotors, int reflector){
        if(!codeSet){
            rotors = rotors.replace("[", "").replace("]", "");
            String[] sepRotors = rotors.split(",", -1);
            List<Integer> rotorIDs = new ArrayList<>();
            for (int i = 0; i < engine.getRotorsCount(); i++) {
                rotorIDs.add(Integer.parseInt(sepRotors[i].trim()));
            }
            engine.getEnigma().pickTempRotors(rotorIDs);
            engine.pickReflector(reflector);
            engine.applyChanges();
            codeSet = true;
        }
    }

    private void createDMs(){
        calculateTotalTasks(Difficulty.valueOf(difficulty));
        for(Ally ally : allies){
            DMs.put(ally.getName(), new DM(ally.getName(), engine, ally.getMissionSize(), totalTasks, posPermutations, Difficulty.valueOf(difficulty), uBoatInput));
        }
    }

    private void startDMs(){
        for(DM dm: DMs.values())
            dm.produceTasks();
    }

    public void endContest(String winner){
        endContest = true;
        this.winner = winner;
        for(DM dm: DMs.values())
            dm.endContest();

    }

    public String getWinner() {
        return winner;
    }

    public boolean isEndContest() {
        return endContest;
    }

    public synchronized DM getDm(String name){
        if(endContest){
            return null;
        }
        else
            return DMs.get(name);
    }


    private void calculateTotalTasks(Difficulty difficulty){
        Machine enigma = engine.getEnigma();

        totalTasks = (int)Math.pow(enigma.getABC().length(), enigma.getRotorsCount());
        posPermutations = totalTasks;

        if(difficulty != Difficulty.EASY) {
            totalTasks *= enigma.getReflectorsTotal();
            if (difficulty != Difficulty.MEDIUM){
                totalTasks *= factorial(enigma.getRotorsCount());
                if(difficulty != Difficulty.HARD)
                    totalTasks *= binomi(enigma.getRotorsTotal(), enigma.getRotorsCount());
            }

        }
    }

    public static long factorial(int number) {
        int result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }

    public static int binomi(int n, int k) {
        if ((n == k) || (k == 0))
            return 1;
        else
            return binomi(n - 1, k) + binomi(n - 1, k - 1);
    }
}

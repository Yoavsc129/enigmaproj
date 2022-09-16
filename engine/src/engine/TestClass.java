package engine;

import engine.bruteForce.Difficulty;

public class TestClass {

    public static void main(String[] args) {
        Engine engine = new Engine();
        try {
            engine.createMachineFromFile("C:\\Users\\YOAV\\IdeaProjects\\enigmaproj\\engine\\src\\resources\\ex2-basic.xml");
            engine.pickRotors("3, 2, 1");
            engine.pickReflector(1);
            engine.applyChanges();
            engine.pickInitialRotorsPos("AAG");
            String userInput = engine.decodeMsgBT("item");
            engine.resetRotors();
            System.out.println(engine.decodeMsgBT(userInput));
            engine.resetRotors();
            engine.setMissionScope(10);
            engine.bruteForce(userInput, Difficulty.MEDIUM);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}

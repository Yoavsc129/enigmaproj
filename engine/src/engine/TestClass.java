package engine;

import java.io.FileNotFoundException;

public class TestClass {
    public static void main(String[] args) {

        Engine e = new Engine();
        try {
            e.createMachineFromFile("engine/src/resources/ex1-sanity-paper-enigma.xml");
        } catch(FileNotFoundException ex){
            System.out.println(" File not found");
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        System.out.println(e.getMachineSpecs());
        e.randMachineSpecs();
        String input = "cake";
        String output = e.decodeMsg(input.toUpperCase());
        System.out.println(output);
        System.out.println(e.getMachineSpecs());
        e.resetRotors();
        System.out.println(e.decodeMsg(output));
        //need to initialize again, depends on checks timing
        e.randMachineSpecs();
        input = "ThisIsAVeryLongMessageIThink";
        output = e.decodeMsg(input.toUpperCase());
        System.out.println(output);
        System.out.println(e.getMachineSpecs());
        e.resetRotors();
        System.out.println(e.decodeMsg(output));
        System.out.println(e.getStats());

    }
}

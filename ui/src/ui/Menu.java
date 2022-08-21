package ui;

import engine.Engine;
import machine.Reflector;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {

    private Scanner scanner;
    private Engine engine;

    private final static String FILE_NAME_SUFFIX = ".xml";

    public void menu(){
        scanner = new Scanner(System.in);
        boolean run = true;
        int menuOption = 0;
        boolean validInput;
        engine = new Engine();

        while(run){
            System.out.println("Select an option by entering its number:");
            for(MenuOptions o: MenuOptions.values())
                System.out.println("\n" + o.toString());
            do{
                try{
                    menuOption = scanner.nextInt();
                    if(menuOption >= 1 && menuOption <= MenuOptions.values().length)
                        validInput = true;
                    else{
                        System.out.println("Please enter a valid option");
                        validInput = false;
                    }
                }catch (InputMismatchException e){
                    System.out.println("Please enter a number");
                    validInput = false;
                    scanner.nextLine();
                }
            }while(!validInput);
            switch (MenuOptions.values()[menuOption - 1]){
                case READ_FILE:
                    readFile();
                    break;

                case MACHINE_SPECS:
                    if(engine.isMachineLoaded())
                        System.out.println(engine.getMachineSpecs());
                    else System.out.println("A machine should be loaded first");
                    break;

                case CHOOSE_CONFIG:
                    if(engine.isMachineLoaded())
                        setConfiguration();
                    else System.out.println("A machine should be loaded first");
                    break;

                case RANDOM_CONFIG:
                    if(engine.isMachineLoaded()){
                        engine.randMachineSpecs();
                        System.out.println(engine.getInitialSpecs());
                    }
                    else System.out.println("A machine should be loaded first");
                    break;
                    
                case ENCODE:
                    if(engine.isMachineLoaded() && engine.getInitialSpecs() != null)
                        encodeMessage();
                    else if (!engine.isMachineLoaded()) {
                        System.out.println("A machine should be loaded first");
                    }
                    else System.out.println("An initial configuration was not created");
                    break;

                case RESET:
                    if(engine.isMachineLoaded() && engine.getInitialSpecs() != null)
                        engine.resetRotors();
                    else if (!engine.isMachineLoaded()) {
                        System.out.println("A machine should be loaded first\n");
                    }
                    else System.out.println("An initial configuration was not created");
                    break;
                case STATS:
                    if(engine.isMachineLoaded() && engine.getInitialSpecs() != null)
                        System.out.println(engine.getStats());
                    else if (!engine.isMachineLoaded()) {
                        System.out.println("A machine should be loaded first");
                    }
                    else System.out.println("An initial configuration was not created");
                    break;

                case EXIT:
                    run = false;
                    break;

            }
        }
    }
    public void readFile(){
        scanner.nextLine();
        boolean validInput;
        String fileName;
        System.out.println("Please enter the full path for an XML file containing the enigma's specifications:");
        do{
            fileName = scanner.nextLine();
            if(fileName.endsWith(FILE_NAME_SUFFIX))
                validInput = true;
            else{
                System.out.println("File name must end with .xml");
                validInput = false;
            }
        }while(!validInput);
        try{
            engine.createMachineFromFile(fileName);
            System.out.println("Machine created successfully\n");
        }catch(FileNotFoundException e){
            System.out.println("File not found\n");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setConfiguration(){
        String check;

        check = configRotors();
        if(check != null){
            System.out.println(check);
            return;
        }
        check = configPositions();
        if(check != null){
            System.out.println(check);
            return;
        }
        configReflector();
        check = configPlugs();
        if(check != null){
            System.out.println(check);
            return;
        }
        engine.applyChanges();
        engine.setInitialSpecs();
        engine.createStat();

        System.out.println("Enigma configured successfully.");
    }

    private String configRotors(){
        String input;
        String check;
        System.out.println("Please enter the IDs of the rotors to be used. IDs should be given as list divided by ','.\n" +
                "The order of the IDs matches the order of the rotors in the machine.\n" +
                "Example: for 23,542,231,545 the rightmost rotor would be 545:");
        input = scanner.next();
        check = engine.pickRotors(input);

        return check;
    }

    private String configPositions(){
        String input;
        String check;
        System.out.println("Please enter the letters for the initial position of each rotor as a consecutive list of letters (no dividers).\n" +
                "The order of the letters should match the order of the rotors in the machine.\n" +
                "Example: for A8D4 the rightmost rotor would be set to 4:");
        input = scanner.next();
        check = engine.pickInitialRotorsPos(input.toUpperCase());

        return check;
    }

    private void configReflector(){
        int inputInt = 0;
        boolean validInput;

        System.out.println("Please pick the reflector for the machine by entering the number of the desired option:");
        for (int i = 0; i < engine.getReflectorsTotal(); i++)
            System.out.printf("%d.'%s'\n%n", i + 1, Reflector.ReflectorID.values()[i]);
        do{
            try{
                inputInt = scanner.nextInt();
                if(inputInt >=1 && inputInt<= engine.getReflectorsTotal())
                    validInput = true;
                else{
                    System.out.println("Please enter a valid option");
                    validInput = false;
                }
            }catch (InputMismatchException e){
                System.out.println("Please enter a number");
                validInput = false;
                scanner.nextLine();
            }
        }while(!validInput);
        engine.pickReflector(inputInt - 1);
    }

    private String configPlugs(){
        String input;
        String check;

        System.out.println("If you want, please choose the plugs by entering a consecutive list of pairs of letters.\n" +
                "Each pair will be matched in the plugs board.\n" +
                "else, press enter.");

        scanner.nextLine();
        input = scanner.nextLine();
        check = engine.pickPlugs(input);

        return check;
    }
    
    public void encodeMessage(){
        System.out.println("Please enter the message you wish to encode/decode.\n" +
                "Only letters that are part of the ABC should be entered.");
        String input = scanner.next();
        System.out.println(engine.decodeMsg(input.toUpperCase()));
    }



}

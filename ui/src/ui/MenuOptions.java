package ui;

public enum MenuOptions{
    READ_FILE("1. Read and create an enigma machine from file"),
    MACHINE_SPECS("2. Show enigma's specifications"),
    CHOOSE_CONFIG("3. Choose initial code configuration"),
    RANDOM_CONFIG("4. Generate initial code configuration randomly"),
    ENCODE("5. Process message"),
    RESET("6. Reset rotors to last chosen configuration"),
    STATS("7. Show history and statistics for current machine"),
    EXIT("8. Exit");

    private final String name;


    MenuOptions(String name) {
        this.name = name;
    }

    @Override
    public String toString(){return name;}
}

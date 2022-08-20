package ui;

public enum MenuOptions{
    READFILE("1. Read machine file"),
    MACHINESPECS("2. Show enigma's specifications"),
    CHOOSECCONFIG("3. Choose initial code configuration"),
    RANDCONFIG("4. Generate initial code configuration randomly"),
    ENCODE("5. Process message"),
    RESET("6. Reset rotors to last chosen configuration"),
    STATS("7. Show history and statistics for current machine"),
    EXIT("8. Exit");

    private String name;


    MenuOptions(String name) {
        this.name = name;
    }

    @Override
    public String toString(){return name;}
}

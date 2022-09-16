package engine;

import java.util.ArrayList;
import java.util.List;

public class Stats {
    private final String specs;



    private final List<Msg> messages;

    public Stats(String specs) {
        this.specs = specs;
        messages = new ArrayList<>();
    }

    public void addMessage(String input, String output, long time){
        messages.add(new Msg(input, output, time));
    }

    public String getMessages(){
        StringBuilder res = new StringBuilder();
        for(Msg m : messages){
            res.append("\n").append(m.getMessage());
        }
        return res.toString();
    }

    public Msg getLastMsg(){
        return messages.get(messages.size() - 1);
    }


    public String getSpecs() {
        return specs;
    }
}

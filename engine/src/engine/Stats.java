package engine;

import java.util.ArrayList;
import java.util.List;

public class Stats {
    private String specs;



    private List<Msg> messages;

    public Stats(String specs) {
        this.specs = specs;
        messages = new ArrayList<>();
    }

    public void addMessage(String input, String output, long time){
        messages.add(new Msg(input, output, time));
    }

    public String getMessages(){
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for(Msg m : messages){
            /*if(first){
                res.append(m.getMessage());
                first = false;
            }
            else res.append("\n" + m.getMessage());*/
            res.append("\n" + m.getMessage());
        }
        return res.toString();
    }


    public String getSpecs() {
        return specs;
    }
}

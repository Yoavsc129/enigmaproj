package engine;

public class Msg{
    String input;
    String output;
    long time;

    public Msg(String input, String output, long time){
        this.input = input;
        this.output = output;
        this.time = time;
    }

    public String getMessage(){
        return String.format("<%s> --> <%s> %d ns", input, output, time);
    }

}

package engine.serverLogic.users;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String name;
    private final String type;

    private String parent;

    private List<String> children;

    private List<String> waitingChildren;

    private boolean ready;

    private boolean inContest;

    public User(String name, String type) {
        this.name = name;
        this.type = type;
        children = new ArrayList<>();
        waitingChildren = new ArrayList<>();
        ready = false;
        inContest = false;
    }

    public void setInContest(boolean inContest) {
        this.inContest = inContest;
    }

    public void ready(){
        ready = true;
    }

    public boolean isReady() {
        return ready;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }


    public void addChild(String child){
        if(!isReady())
            children.add(child);
        else waitingChildren.add(child);
    }

    public void removeChild(String child){
        children.remove(child);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getParent() {
        return parent;
    }


    public List<String> getChildren() {
        return children;
    }

    public boolean isInContest() {
        return inContest;
    }
}

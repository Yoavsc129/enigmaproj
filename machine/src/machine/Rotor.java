package machine;
import machine.generated.CTEPositioning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rotor implements Cloneable{
    private final int id;
    private int notch;

    private final int ABClen;

    private char initialLetter;

    private Map<Character, Integer> rightCol;
    private Map<Character, Integer> leftCol;



    public Rotor(int id, int notch, int abClen, List<CTEPositioning> ctePositioning) {
        this.id = id;
        this.notch = notch;
        ABClen = abClen;
        rightCol = new HashMap<>();
        leftCol = new HashMap<>();
        for (int i = 0; i < ctePositioning.size(); i++) {
            rightCol.put(ctePositioning.get(i).getRight().charAt(0), i + 1) ;
            leftCol.put(ctePositioning.get(i).getLeft().charAt(0), i + 1);
        }

    }

    public Rotor(){
        id = -1;
        notch = 1;
        ABClen = 0;
        rightCol = null;
        leftCol = null;
        initialLetter = ' ';
    }
    @Override
    public Rotor clone(){
        try{
            Rotor newRotor = (Rotor) super.clone();
            newRotor.leftCol = new HashMap<>(leftCol);
            newRotor.rightCol = new HashMap<>(rightCol);
            return newRotor;

        }catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public void rotate(int times){
        int newval;
        for(Character k : leftCol.keySet()){
            newval = leftCol.get(k) - times;
            if(newval <=0)
                newval += ABClen;
            leftCol.put(k, newval);
        }
        for(Character k : rightCol.keySet()){
            newval = rightCol.get(k) - times;
            if(newval <=0)
                newval += ABClen;
            rightCol.put(k, newval);
        }
        notch -= times;
        if(notch <=0)
            notch += ABClen;
    }

    public void setInitialPosition(char c){
        rotate(rightCol.get(c) - 1);
        initialLetter = c;
    }
    public void rotateNext(Rotor next){
        if(notch == 1)
            next.rotate(1);
    }

    public int getLeftIndex(int right){
        char c = ' ';
        for(Map.Entry<Character, Integer> e : rightCol.entrySet())
            if(e.getValue() == right)
                c = e.getKey();

        return leftCol.get(c);
    }

    public int getRightIndex(int left){
        char c = ' ';
        for(Map.Entry<Character, Integer> e : leftCol.entrySet())
            if(e.getValue() == left)
                c = e.getKey();

        return rightCol.get(c);
    }

    public void resetRotor(){
        rotate(rightCol.get(initialLetter) - 1);
    }

    public int getId() {
        return id;
    }

    public int getNotch() {
        return notch;
    }

    public char getInitialLetter() {
        return initialLetter;
    }

    public char getCurrPosition(){
        for(Character key : rightCol.keySet())
            if(rightCol.get(key) == 1)
                return key;
        return ' ';
    }

    public String createRotorSpecs(){
        String res;
        res = String.format("%d(%d)", id, notch - 1);


        return res;
    }
}

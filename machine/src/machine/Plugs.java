package machine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plugs {
    private String ABC;
    private Map<Character, Character> plugs;

    private Boolean inUse = false;

    public Plugs(String abc) {
        ABC = abc;
        plugs = new HashMap<>();
        for(char c : ABC.toCharArray()){
            plugs.put(c, c);
        }
    }

    public void setPlugs(char c1, char c2){
        char temp;
        if(c1 != c2){
            inUse = true;
            temp = plugs.get(c1);
            if(temp != c1)
                plugs.put(temp, temp);
            plugs.put(c1, c2);
            temp = plugs.get(c2);
            if(temp != c2)
                plugs.put(temp, temp);
            plugs.put(c2, c1);
        }

    }
    public char getPlug(char c){
        char res = plugs.get(c);
        /*if(res == c)
            return ' ';*/
        //else
        return res;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public String createPlugsSpecs(){
        StringBuilder res = new StringBuilder("<");
        Map<Character, Boolean> usedChars = new HashMap<>();
        char temp;
        boolean first = true;
        for(char c : ABC.toCharArray()){
            usedChars.put(c, false);
        }
        for(char key : plugs.keySet()){
            temp = plugs.get(key);
            if(temp != key)
                if(!usedChars.get(temp)){
                    usedChars.put(key, true);
                    if(first)
                        first = false;
                    else res.append(',');
                    res.append(String.format("%c|%c", key, temp));
                }
        }
        res.append('>');

        return res.toString();
    }
}

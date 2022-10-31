package engine.serverLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BattlefieldManager {
    private List<Battlefield> battlefieldDataList;

    public BattlefieldManager() {battlefieldDataList = new ArrayList<>();}

    public synchronized void addBattlefield(Battlefield battlefield){
        battlefieldDataList.add(battlefield);
    }

    public List<Battlefield> getBattlefieldDataList() {
        return battlefieldDataList;
    }

    public Battlefield getBattlefield(String name){
        Battlefield res = null;
        for(Battlefield bf: battlefieldDataList)
            if(name.equals(bf.getName()))
                res = bf;
        return res;
    }

    public boolean isBattlefieldExists(String battlefield){
        for(Battlefield b : battlefieldDataList)
            if(Objects.equals(b.getName(), battlefield))
                return true;
        return false;
    }

    public void removeBattlefield(String battlefield){
        for(Battlefield bf: battlefieldDataList)
            if(bf.getName().equals(battlefield)){
                battlefieldDataList.remove(bf);
                break;
            }
    }
}

package dsAgents.dsPerceptionModule.dsSyntax;

import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import eis.iilang.Percept;

import java.util.HashMap;

public class DSPercepts {

    static HashMap<String,Integer> PBlockTypes=new HashMap<String, Integer>();
    static int PNextBlockType=0;

    // uchovava i nazvy bloku, mapuje je na int, nemusi platit b1=1 apod.!
    public static int blockTypeByName(String blockName){
        if(PBlockTypes.containsKey(blockName))
            return(PBlockTypes.get(blockName));
        PBlockTypes.put(blockName,PNextBlockType);

        PNextBlockType++;
        return(PNextBlockType-1);
    }

    public static int dispenserTypeByName(String blockName){
        return(blockTypeByName(blockName));
    }


    public static int perceptParam2Int(Percept percept, int noParam) {
        int i=Integer.parseInt(percept.getParameters().get(noParam).toString());
        return(i);
    }

    public static int perceptParam2Type(Percept percept, int noParam, String teamName) {
        int type = 0;
        String team;
        String st = percept.getParameters().get(noParam).toString();
        if (st.contentEquals("entity")) {
            team = percept.getParameters().get(noParam + 1).toString();
            if (team.contentEquals(teamName))
                type = DSCell.__DSEntity_Friend;
            else
                type = DSCell.__DSEntity_Enemy;
        }
        if(st.contentEquals("block")){
            String dispenserName = percept.getParameters().get(noParam + 1).toString();
            int dType = blockTypeByName(dispenserName);
            type = DSCell.__DSBlock+dType;
        }
        if(st.contentEquals("dispenser")) {
            String dispenserName = percept.getParameters().get(noParam + 1).toString();
            int dType = dispenserTypeByName(dispenserName);
            type = DSCell.__DSDispenser+dType;
        }
        if(st.contentEquals("marker"))
            type= DSCell.__DSMarker;
        return(type);
    }

    public static void initBlocks(){
        PBlockTypes.put("b0",0);
        PBlockTypes.put("b1",1);
        PBlockTypes.put("b2",2);
        PBlockTypes.put("b3",3);
        PNextBlockType=4;

    }

}

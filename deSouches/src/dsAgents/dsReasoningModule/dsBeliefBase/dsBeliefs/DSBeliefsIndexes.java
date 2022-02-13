package dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs;

import java.util.HashMap;
import java.util.Map;

public class DSBeliefsIndexes {

    public final static int __simStart=0;
    public final static int __name=1;
    public final static int __team=2;
    public final static int __teamSize=3;
    public final static int __steps=4;
    public final static int __role=5;
    public final static int __actionID=6;
    public final static int __timestamp=7;
    public final static int __deadline=8;
    public final static int __step=9;
    public final static int __lastAction=10;
    public final static int __lastActionParams=11;
    public final static int __lastActionResult=12;
    public final static int __score=13;
    public final static int __thing=14;
    public final static int __task=15;
    public final static int __attached=16;
    public final static int __energy=17;
    public final static int __deactivated=18;
    public final static int __roleZone=19;
    public final static int __goalZone=20;
    public final static int __violation=21;
    public final static int __norm=22;
    public final static int __surveyed=23;
    public final static int __hit=24;
    public final static int __ranking=25;



    static Map<String, Integer> _beliefMap=new HashMap<String, Integer>()
    {{
        put("simStart",__simStart);
        put("name",__name);
        put("team",__team);
        put("teamSize",__teamSize);
        put("steps",__steps);
        put("role",__role);
        put("actionID",__actionID);
        put("timestamp",__timestamp);
        put("deadline",__deadline);
        put("step",__step);
        put("lastAction",__lastAction);
        put("lastActionParams",__lastActionParams);
        put("lastActionResult",__lastActionResult);
        put("score",__score);
        put("thing",__thing);
        put("task",__task);
        put("attached",__attached);
        put("energy",__energy);
        put("deactivated",__deactivated);
        put("roleZone",__roleZone);
        put("goalZone",__goalZone);
        put("violation",__violation);
        put("norm",__norm);
        put("surveyed",__surveyed);
        put("hit",__hit);
        put("ranking",__ranking);
    }};

public static int getIndex(String beliefName){
    if(_beliefMap.containsKey(beliefName))
        return(_beliefMap.get(beliefName));
    return(-1);
}

}




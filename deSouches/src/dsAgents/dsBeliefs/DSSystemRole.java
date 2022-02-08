package dsAgents.dsBeliefs;

import java.util.HashMap;
import java.util.Map;

import static javax.swing.UIManager.put;

public class DSSystemRole {
    private int PVision;
    private int PClearDistance;
    private int PClearChance;
    public static int _rotate=0;
    public static int _move=1;
    public static int _attach=2;
    public static int _detach=3;
    public static int _connect=4;
    public static int _disconnect=5;
    public static int _request=6;
    public static int _submit=7;
    public static int _clear=8;
    public static int _survey=9;
    private static Map<String,Integer> PActionMap=new HashMap<String, Integer>()
    {   {
            put("rotate",0);
            put("move",1);
            put("attach",2);
            put("detach",3);
            put("connect",4);
            put("disconnect",5);
            put("request",6);
            put("submit",7);
            put("clear",8);
            put("survey",9);
        }
    };

    private int[] PSpeeds;

    private boolean[] PActionEnable = {false,false,false,false,false,false,false,false,false,false};

    // actions Skip and Adopt are always possible for any role

    public boolean can_perform(int action){
        return(PActionEnable[action]);
    }

    public int get_clear_chance() {
        return PClearChance;
    }

    public int get_clear_distance() {
        return PClearDistance;
    }

    public int get_speed(int blocks){
        int length=PSpeeds.length;
        if(blocks>length){
            return(PSpeeds[length-1]);
        }
        return(PSpeeds[blocks-1]);
    }

    public DSSystemRole(){}

}

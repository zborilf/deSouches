package dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs;

import dsAgents.dsExecutionModule.dsActions.*;
import eis.iilang.Parameter;
import eis.iilang.Percept;

import java.util.*;


public class DSRole {

    private final String PRoleName;
    int PVision;
    List<Integer> PSpeeds; // TODO rychlost pri nakladu (input)
    List<String> PActions;
    int   PClearChance;
    int   PClearDistance;


    /*
        Role in percept
            role(
                    String name
                    Int vision
                    List<String> actions
                    List<Integer> speed (for 0, 1, 2 ... block attached)
                    Float clearChance
                    Int clearMaxDistance
     */

    public DSRole(Percept percept){
        String aa=percept.getName();
        String bb=percept.getParameters().toString();
        PRoleName=percept.getParameters().get(0).toString();
        PVision=Integer.valueOf(percept.getParameters().get(1).toString());
        String pactions=percept.getParameters().get(2).toString();
        String pactions2=pactions.substring(1,pactions.length()-1);
        PActions = Arrays.asList(pactions2.split("\\s*,\\s*"));
        String pspeeds=percept.getParameters().get(3).toString();
        String pspeeds2=pspeeds.substring(1,pspeeds.length()-1);
        List<String> pspeedsS=Arrays.asList(pspeeds2.split("\\s*,\\s*"));
        PSpeeds=new LinkedList<Integer>();
        for(String sp:pspeedsS)
            PSpeeds.add(Integer.valueOf(sp));


        PClearChance=Math.round(Float.valueOf(percept.getParameters().get(4).toString())*100);
        PClearDistance=Integer.valueOf(percept.getParameters().get(5).toString());
        System.out.println(bb);
    }
}

package dsAgents.dsBeliefBase.dsBeliefs;

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

    public String getRoleName(){
        return(PRoleName);
    }

    public int getVision(){
        return(PVision);
    }

    public DSRole(Collection<Parameter> parameters){
        Iterator i= parameters.iterator();

        PRoleName=i.next().toString();
        PVision=Integer.valueOf(i.next().toString());

        String pactions=i.next().toString();
        String pactions2=pactions.substring(1,pactions.length()-1);
        PActions = Arrays.asList(pactions2.split("\\s*,\\s*"));
        String pspeeds=i.next().toString();
        String pspeeds2=pspeeds.substring(1,pspeeds.length()-1);
        List<String> pspeedsS=Arrays.asList(pspeeds2.split("\\s*,\\s*"));
        PSpeeds=new LinkedList<Integer>();
        for(String sp:pspeedsS)
            PSpeeds.add(Integer.valueOf(sp));


        PClearChance=Math.round(Float.valueOf(i.next().toString())*100);
        PClearDistance=Integer.valueOf(i.next().toString());

    }
}

package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSHybridPathPlanner;

import java.awt.*;

public class DSGGetBeside extends DSGGoal{
    int PObjectType;
    int PTimeout;

    Point PPreferedDestination=null;

    public String getGoalDescription(){
        return("getBeside "+PObjectType);
    }

    public boolean revisePlans(DSAgent agent) {
        /*
               check whether PObjectPosition is still empty
                    if not or it is null
                        Find new preferedPosition:
                            take four possible positions beside the destination
                            randomly select one
                       if there is no free position
                            wait ? or take step somewhere, if possible??
         */
        Point objectPosition = agent.getOutlook().seenNearest(PObjectType);
        if (objectPosition == null) {
            // object not seeen -> goal failed
            agent.printOutput("!!! Chci get beside a nevidim v outlooku objekt "+PObjectType);
            setGoalToFail();
            return (true);
        }
        if (Math.abs(objectPosition.x) + Math.abs(objectPosition.y) <= 1){
            setPlansToSuccess();
        // agent is beside it
            return(true);
        }

        Point objectMapPosition= new Point (agent.getMapPosition().x + objectPosition.x,
                                    agent.getMapPosition().y + objectPosition.y);

        agent.printOutput("Estim. object position "+objectPosition+" at map "+objectMapPosition);

        DSPlan plan= DSHybridPathPlanner.getOneStep("get beside", agent.getMap(),1, agent,
                                    objectMapPosition, agent.getBody(),PTimeout-agent.getStep(), false);

        if(plan!=null) {
            PPlans.put(plan.getName(), plan);
            return (true);
        }
        return(false);
    }

    public DSGGetBeside(int objectType, int timeout){
        PTimeout = timeout;
        PObjectType=objectType;
    };
}

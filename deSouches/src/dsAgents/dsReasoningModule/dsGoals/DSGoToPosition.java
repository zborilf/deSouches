package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

public class DSGoToPosition extends DSGoal {

    Point PDestination;
    DSBody PBody=null;

    public String getGoalName(){
        return("goToPosition");
    }


    public boolean revisePlans(DSAgent agent) {

        if(PPlans.containsKey("goToPosition"))
            return false;

        if (PDestination == null) {
            return (false);
        }

        if(PBody==null)
                PBody=agent.getBody();

        Point agentPos=agent.getPosition();
        //    PPlan = new DSAStar().
        //    computePath(agent.getGroup().getGroupMap(), agent.getMap().getAgentPos(), PPosition, PBody,100, agent);

        DSPlan plan = astarGroup("goToPosition",1,agent, PDestination, PBody);
        if(plan==null)
            return(false);

        PPlans.put("goToPosition",plan);
        return(true);

    }


    public DSGoToPosition(Point position) {
        super();
        PDestination=position;
    }


    public DSGoToPosition(Point position, DSBody body) {
        super();
        PBody=body;
        PDestination=position;
    }

}

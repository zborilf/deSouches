package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSAgentOutlook;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSHybridPathPlanner;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSOneStepGreedy;

import java.awt.*;
import java.util.LinkedList;

public class DSGApproachTypes extends DSGGoal {

    int PTimeout;
    Point PSupposedPosition;
    LinkedList<Integer> PDesiredTypes;


    @Override
    public String getGoalDescription() {
        return ("approachGoal "+PDesiredTypes+" pos "+PSupposedPosition);
    }

    @Override
    public boolean revisePlans(DSAgent agent) {

        /*
                1, if sees some from the desired types in outlook, finishes -> creates empty 'terminating' plan
                2, if does not see desired type and it is at position, fails -> creates terminating plan
                                                                                                with 'fail' action
                3, does one stem toward the position (should be improved by A^, solving deadlocks)
         */
        DSAgentOutlook outlook = agent.getOutlook();

        /*
                1
         */
/*
        for(Integer t:PDesiredTypes)
            if(outlook.sees(t)) {
            setPlansToSuccess();
            return(true);
        }
*/
        if ((Math.abs(agent.getMapPosition().x - PSupposedPosition.x) +
                Math.abs(agent.getMapPosition().y - PSupposedPosition.y)) < 2){
            setPlansToSuccess();
            return(true);
        }

        /*
                2
         */

        if(agent.getMapPosition()==PSupposedPosition){
            setGoalToFail();
            return(false);
        }

        /*
                3
         */

        DSPlan plan= DSHybridPathPlanner.getOneStep("approach", agent.getMap(),1, agent,
                PSupposedPosition, agent.getBody(), PTimeout-agent.getStep(), false);

        if(plan!=null) {
            PPlans.put(plan.getName(), plan);
            return (true);
        }
        return(false);

    }

    public DSGApproachTypes(LinkedList<Integer> what, Point where, int timeout){
        PTimeout = timeout;
        PSupposedPosition = where;
        PDesiredTypes = what;
    }
}

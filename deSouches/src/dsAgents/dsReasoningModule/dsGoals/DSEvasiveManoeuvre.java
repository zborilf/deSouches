package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSHybridPathPlanner;

import java.awt.*;

public class DSEvasiveManoeuvre extends DSGGoal{

    Point PDestination;
    DSBody PBody;
    final static int _nOAttempts=10;
    final static int _distance=8;

    public String getGoalDescription() {
        return ("evasiveManoeuvre");
    }


    public String getGoalParametersDescription(){
        return(" to "+PDestination);
    }

    public boolean revisePlans(DSAgent agent) {

        if (PPlans.containsKey("evasiveManoeuvre")) return false;

        DSPlan plan=null;

        for(int i=0; i<_nOAttempts;i++) {

            int dx = (int) Math.round(Math.random() * _distance-(_distance/2));
            int dy = (int) Math.round(Math.random() * _distance-(_distance/2));

            PDestination = new Point(agent.getMapPosition().x + dx, agent.getMapPosition().x + dy);

            PBody = agent.getBody();

            // evas man priority 2

            plan = DSHybridPathPlanner.getOneStep("evasiveManoeuvre", agent.getMap(), 2, agent,
                    PDestination, PBody, 10, true);

            if(plan!=null)
                break;
        }

        if (plan == null) return (false);

        PPlans.put("evasiveManoeuvre", plan);
        return (true);
    }

    public DSEvasiveManoeuvre() {
        super();
    }
}

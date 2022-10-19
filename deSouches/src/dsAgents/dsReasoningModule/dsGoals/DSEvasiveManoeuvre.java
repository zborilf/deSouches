package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSHybridPathPlanner;

import java.awt.*;

public class DSEvasiveManoeuvre extends DSGGoal{

    Point PDestination;
    DSBody PBody;
    final static int _nOAttempts=10;
    final static int _distance=6;
    Point _destination;

    public String getGoalDescription() {
        return ("evasiveManoeuvre");
    }


    public String getGoalParametersDescription(){
        return(" to "+PDestination);
    }

    public boolean revisePlans(DSAgent agent) {

        Point o=agent.getOutlook().seenNearest(DSCell.__DSObstacle);
        if(o!=null)
            if((Math.abs(o.x)+Math.abs(o.y))==1)
            {
                DSPlan plan=new DSPlan("manoeuvre shot",2, false);
                plan.appendAction(new DSClear(o));
                PPlans.put(plan.getName(),plan);
                return(true);
            }

        if (PPlans.containsKey("evasiveManoeuvre")) return false;

        DSPlan plan=null;

        for(int i=0; i<_nOAttempts;i++) {


            PDestination = agent.getMap().shiftPosition(agent, _destination);

            PBody = agent.getBody();

            // evas man priority 2

//            plan = DSHybridPathPlanner.getOneStep("evasiveManoeuvre", agent.getMap(), 2, agent,
//                    PDestination, PBody, 10, true);

            plan = DSHybridPathPlanner.getOneStep("evasiveManoeuvre", agent.getMap(), 2, agent,
                    PDestination, PBody, 10, true);


            if(plan!=null)
                break;

            int dx = (int) Math.round(Math.random() * _distance-(_distance/2));
            int dy = (int) Math.round(Math.random() * _distance-(_distance/2));
            _destination=new Point(dx,dy);
        }

        if (plan == null) return (false);

        PPlans.put("evasiveManoeuvre", plan);
        return (true);
    }

    public DSEvasiveManoeuvre() {
        super();
        int dx = (int) Math.round(Math.random() * _distance-(_distance/2));
        int dy = (int) Math.round(Math.random() * _distance-(_distance/2));
        _destination=new Point(dx,dy);
    }


    public DSEvasiveManoeuvre(int distance) {
        super();
        int dx = (int) Math.round(Math.random() * distance-(distance/2));
        int dy = (int) Math.round(Math.random() * distance-(distance/2));
        _destination=new Point(dx,dy);
    }

}

package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSHybridPathPlanner;

import java.awt.*;
import java.util.LinkedList;

public class DSGGoAndShoot extends DSGGoal {

    Point PSupposedPosition;
    int PTimeout;

    @Override
    public String getGoalDescription() {
        return ("go and shoot " + PSupposedPosition);
    }


    @Override
    public boolean revisePlans(DSAgent agent) {

        // try to shoot
        if(agent.getActualRole().contentEquals("digger")){
            LinkedList<DSCell> attackables=agent.getOutlook().getCells().getAttackables();
            if(attackables!=null) {
                if(agent.getNotRecentlyAttacked(attackables)!=null) {
                    Point p =
                            agent.getNotRecentlyAttacked(attackables).getPosition();
                    DSPlan plan = new DSPlan("attack", 2, false);
                    plan.appendAction(new DSClear(p));
                    PPlans.put(plan.getName(), plan);
                    return (true);
                }
            }
        }

        if ((Math.abs(agent.getMapPosition().x - PSupposedPosition.x) +
                Math.abs(agent.getMapPosition().y - PSupposedPosition.y)) < 2) {
            setPlansToSuccess();
            return(true);
        }


            if (agent.getMapPosition() == PSupposedPosition) {
                setPlansToSuccess();
                return (true);
            }

            DSPlan plan = DSHybridPathPlanner.getOneStep("go ahead", agent.getMap(), 1, agent,
                    PSupposedPosition, agent.getBody(), PTimeout - agent.getStep(), false);

            if (plan != null) {
                PPlans.put(plan.getName(), plan);
                return (true);
            }



        return (false);
    }

    public DSGGoAndShoot(Point where, int timeout) {
        PTimeout=timeout;
        PSupposedPosition = where;
    }
}

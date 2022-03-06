
package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSAction;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsExecutionModule.dsActions.DSRotateCW;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

public class DSClearGoal extends DSGoal {

    Point PDirection;

    @Override
    public String getGoalName() {
        return ("clearGoal");
    }

    @Override
    public boolean revisePlans(DSAgent agent) {
        Point direction=agent.getMap().objectAroundCell(agent.getPosition(), DSCell.__DSObstacle);
        if(direction==null)
            return(false);
        System.out.println(agent.getEntityName()+"("+agent.getStep()+") strikes once again");
        Point relDirection=new Point(direction.x-agent.getPosition().x,direction.y-agent.getPosition().y);
        DSPlan plan = new DSPlan("justClear", 1);
        DSAction clear = new DSClear(agent.getEI(), relDirection);
        plan.appendAction(clear);
        DSRotateCW anotherAction = new DSRotateCW(agent.getEI());
        plan.appendAction(anotherAction);
        PPlans.put("justClear", plan);
        return (true);
    }

    public DSClearGoal(Point direction){
        super();
        PDirection=direction;
    }

}

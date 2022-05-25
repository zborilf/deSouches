package dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods;

import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSMove;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

public final class DSStraightPath {
    public  DSPlan computeStraightPath(DSAgent agent, String planName, Point from, Point to, int priority){
        DSPlan path = new DSPlan(planName, priority);
        while((from.x!=to.x)||(from.y!=to.y)){
            if(Math.abs(from.x-to.x)>Math.abs(from.y-to.y))
                    if(from.x>to.x){
                        path.appendAction(new DSMove(agent.getEI(), "w"));
                        from.x=from.x-1;
                    }
            else
                    {
                        path.appendAction(new DSMove(agent.getEI(), "e"));
                        from.x=from.x+1;
                    }
                else
                    if(from.y>to.y){
                        path.appendAction(new DSMove(agent.getEI(), "n"));
                        from.y=from.y-1;
                    }
                    else
                    {
                        path.appendAction(new DSMove(agent.getEI(), "s"));
                        from.y=from.y+1;
                    }
        }
        return(path);
    }

}

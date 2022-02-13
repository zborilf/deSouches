package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Numeral;

import java.awt.*;

public class DSClear extends DSAction {

    Point Pp;

    @Override
    public DSGoal execute(DSAgent agent) {

        Action a = new Action("clear", new Numeral(Pp.x), new Numeral(Pp.y));
        try {
            System.out.println(agent.getEntityName()+" BUM (step "+agent.getStep()+"):"+Pp);
            agent.getEI().performAction(agent.getJADEAgentName(), a);
        } catch (ActException e) {
            return(new DSGoalFalse());
        }
        return(new DSGoalTrue());
    }


    public void succeededEffect(DSAgent agent){
    // odstrani napojene veci na body
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    @Override
    public String actionText() {
        return("clear");
    }

    @Override
    public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
        return null;
    }

    public DSClear(EnvironmentInterfaceStandard ei, Point direction)
    {
        super(ei);
        Pp=direction;
    }
}


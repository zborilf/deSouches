package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;

public class DSRequest extends DSAction {

    String PDirection;

    @Override
    public DSGoal execute(DSAgent agent) {
        Action a = new Action("request", new Identifier(PDirection));
        try {
            agent.getEI().performAction(agent.getJADEAgentName(), a);

        } catch (ActException e) {
            return(new DSGoalFalse());
        }
        return(new DSGoalTrue());
    }

    @Override
    public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
        return null;
    }

    @Override
    public boolean isExternal(){return(true);}

    @Override
    public String actionText() {
        return("Request "+PDirection);
    }


    public DSRequest(EnvironmentInterfaceStandard ei, String direction){
        super(ei);
        PDirection=direction;
    }

}

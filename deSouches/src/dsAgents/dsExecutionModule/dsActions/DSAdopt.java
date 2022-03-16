package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;

public class DSAdopt extends DSAction {

    String PRole;

    @Override
    public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
        return(null);
    }

    @Override
    public DSGoal execute(DSAgent agent){

        Action a = new Action("adopt", new Identifier(PRole));
        try {
            agent.getEI().performAction(agent.getJADEAgentName(), a);

        } catch (ActException e) {
            return(new DSGoalFalse());
        }
        return(new DSGoalTrue());
    }

    @Override
    public void succeededEffect(DSAgent agent) {
        agent.setActualRole(PRole);
    }

    @Override
    public boolean isExternal(){
        return(true);
    }

    @Override
    public String actionText() {
        return("Adopt");
    }


    public DSAdopt(EnvironmentInterfaceStandard ei, String role) {
        super(ei);
        PRole=role;
    }

}

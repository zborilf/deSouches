package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.EnvironmentInterfaceStandard;

public class DSRotate extends DSAction{

    @Override
    public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
        return(null);
    }


    @Override
    public DSGoal execute(DSAgent agent){
        return(null);
    }

    @Override
    public boolean isExternal(){
        return(true);
    }

    @Override
    public String actionText() {
        return("Rotate");
    }

    public DSRotate(EnvironmentInterfaceStandard ei) {
        super(ei);
    }
}

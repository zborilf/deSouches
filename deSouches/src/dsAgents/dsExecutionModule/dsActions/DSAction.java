package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.EnvironmentInterfaceStandard;

public abstract class DSAction {

    EnvironmentInterfaceStandard PEI;

    public abstract DSGoal execute(DSAgent agent);

    public abstract boolean isExternal();

    public abstract String actionText();

    public void succeededEffect(DSAgent agent) { return;} // after suc. feedback

    public abstract DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody body, int step);

    public DSAction(EnvironmentInterfaceStandard ei){
        PEI=ei;
    }

}

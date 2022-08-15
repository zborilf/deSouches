package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;

public class DSSkip extends DSAction {
  @Override
  public DSGGoal execute(DSAgent agent) {
    Action a = new Action("skip");
    try {
      agent.getEI().performAction(agent.getJADEAgentName(), a);

    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  @Override
  public boolean isExternal() {
    return true;
  }

  @Override
  public String actionText() {
    return ("skip");
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody body, int step) {
    return null;
  }
}

package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;

public class DSRequest extends DSAction {

  String PDirection;

  @Override
  public DSGGoal execute(DSAgent agent) {
    Action a = new Action("request", new Identifier(PDirection));
    try {

      try {
        agent.getOutput().write("Request action: " + a.toProlog()+" direction "+PDirection+"\n");
        agent.getOutput().flush();
      } catch (Exception e){};

      agent.getEI().performAction(agent.getJADEAgentName(), a);

    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    return null;
  }

  @Override
  public boolean isExternal() {
    return (true);
  }

  @Override
  public String actionText() {
    return ("Request " + PDirection);
  }

  public DSRequest(String direction) {
    PDirection = direction;
  }
}

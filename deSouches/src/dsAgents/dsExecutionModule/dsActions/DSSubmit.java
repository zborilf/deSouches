package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;
import java.awt.*;

public class DSSubmit extends DSAction {

  String PTaskName;

  @Override
  public void succeededEffect(DSAgent agent) {
    Point direction = DSPerceptor.getPositionFromDirection("s");
    agent.getBody().removePart(direction);
  }

  @Override
  public DSGoal execute(DSAgent agent) {
    Action a = new Action("submit", new Identifier(PTaskName));
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
    return ("Submitting " + PTaskName);
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    return null;
  }

  public DSSubmit(String taskName) {
    PTaskName = taskName;
  }
}

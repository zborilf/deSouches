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

public class DSDetach extends DSAction {

  String PDirection;

  @Override
  public void succeededEffect(DSAgent agent) {
    System.out.println(
        "Detach: agent "
            + agent.getEntityName()
            + " v "
            + PDirection
            + " body "
            + agent.getBody().bodyToString());
    Point direction = DSPerceptor.getPositionFromDirection(PDirection);
    agent.getBody().removePart(direction);
    System.out.println(
        "Detach POST : agent "
            + agent.getEntityName()
            + " v "
            + PDirection
            + " body "
            + agent.getBody().bodyToString());
  }

  @Override
  public DSGoal execute(DSAgent agent) {
    Action a = new Action("detach", new Identifier(PDirection));
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
    return ("Detach " + PDirection);
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    return null;
  }

  public DSDetach(String direction) {
    PDirection = direction;
  }
}

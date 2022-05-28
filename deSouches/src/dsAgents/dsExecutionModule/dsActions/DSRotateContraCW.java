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
import java.awt.Point;

public class DSRotateContraCW extends DSAction {

  public DSRotateContraCW(EnvironmentInterfaceStandard ei) {
    super(ei);
  }

  @Override
  public void succeededEffect(DSAgent agent) {
    agent.getBody().rotateBody(false);
  }

  @Override
  public DSGoal execute(DSAgent agent) {
    Action a = new Action("rotate", new Identifier("ccw"));

    try {
      agent.getEI().performAction(agent.getJADEAgentName(), a);
    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  @Override
  public String actionText() {
    return ("Rotate CCW");
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    Point point = item.getPosition();
    DSBody body = item.getBody().cloneBody();
    body.rotateBody(false);
    if (map.isObstacleAt(item.getPosition(), agentBody, body, step)) return (null);
    else return (new DSAStarItem(item.getPosition(), item, this, body, 0, 0));
  }

  @Override
  public boolean isExternal() {
    return (true);
  }
}

package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;
import java.awt.*;

public class DSAttach extends dsAgents.dsExecutionModule.dsActions.DSAction {
  private final String PDirection;
  int PBlockType;

  @Override
  public void succeededEffect(DSAgent agent) {
    Point direction = DSPerceptor.getPositionFromDirection(PDirection);
    Point agentPosition = agent.getMap().getOwnerAgentPos();
    DSCell cell = new DSCell(direction.x, direction.y, DSCell.__DSBlock + PBlockType, 0);
    agent.getBody().addCell(cell);
    agent.holdsBlock(PBlockType);

  }

  @Override
  public DSGGoal execute(DSAgent agent) {
    Action a = new Action("attach", new Identifier(PDirection));
    try {
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
    return ("Attach " + PDirection);
  }

  public DSAttach(String direction, int type) {
    PDirection = direction;
    PBlockType = type;
  }
}

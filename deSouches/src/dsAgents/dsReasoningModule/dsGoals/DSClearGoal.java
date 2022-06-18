package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSAction;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.awt.*;

public class DSClearGoal extends DSGoal {

  Point PDirection;

  @Override
  public String getGoalDescription() {
    return ("clearGoal");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    Point direction;
    if (PDirection == null)
      direction = agent.getMap().objectAroundCell(agent.getMapPosition(), DSCell.__DSObstacle);
    else direction = PDirection;

    if (direction == null) return (false);

    DSPlan plan = new DSPlan("justClear", 1);
    DSAction clear = new DSClear(direction);
    plan.appendAction(clear);
    PPlans.put("justClear", plan);
    return (true);
  }

  public DSClearGoal(Point direction) {
    super();
    PDirection = direction;
  }

  public DSClearGoal() {
    super();
    PDirection = null;
  }
}

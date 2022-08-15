package dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsExecutionModule.dsActions.DSMove;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.awt.*;


// obsolete, one step greedy search does this

public final class DSStraightPath {
  // path through obstacles -> only digger and default
  // step is not really necessary for now as both roles have speed 1
  public DSPlan computeStraightPath(
      DSAgent agent, String planName, Point from, Point to, int priority) {
    DSPlan path = new DSPlan(planName, priority);
    while ((from.x != to.x) || (from.y != to.y)) {
      if (Math.abs(from.x - to.x) > Math.abs(from.y - to.y)) {
        if (from.x > to.x) {
          makeObstacleFree(path, agent, from.x, from.y, "w");
          path.appendAction(new DSMove("w"));
          from.x -= 1;
        } else {
          makeObstacleFree(path, agent, from.x, from.y, "e");
          path.appendAction(new DSMove("e"));
          from.x += 1;
        }
      } else if (from.y > to.y) {
        makeObstacleFree(path, agent, from.x, from.y, "n");
        path.appendAction(new DSMove("n"));
        from.y -= 1;
      } else {
        makeObstacleFree(path, agent, from.x, from.y, "s");
        path.appendAction(new DSMove("s"));
        from.y += 1;
      }

      if (AgentObstacle(agent, from.x, from.y)) {
        return null;
      }
    }
    return (path);
  }

  // checks given vector for obstacles and add clear actions if necessary
  private void makeObstacleFree(DSPlan plan, DSAgent agent, int x, int y, String direction) {
    DSBody body = agent.getBody();
    Point dig = DSPerceptor.getPositionFromDirection(direction);
    if (agent.getMap().isObstacleAt(new Point(x + dig.x, y + dig.y), body, body, agent.getStep())) {
      plan.appendAction(new DSClear(dig));
    }
  }

  private boolean AgentObstacle(DSAgent agent, int x, int y) {
    Point p = new Point(x, y);
    return (agent.getMap().getMap().getKeyType(p, DSCell.__DSAgent) != null
        || agent.getMap().getMap().getKeyType(p, DSCell.__DSEntity_Enemy) != null
        || agent.getMap().getMap().getKeyType(p, DSCell.__DSEntity_Friend) != null);
  }
}

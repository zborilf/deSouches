package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStar;
import java.awt.*;

public class DSGoToGoal extends DSGoal {
  @Override
  public String getGoalDescription() {
    return ("goToGoal");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {

    if (PPlans.containsKey("goToGoal")) return false; // plan exists,  no revision

    Point point = agent.getNearestGoal();
    if (point == null) {
      return (false);
    }

    DSPlan plan =
        new DSAStar()
            .computePath(
                "goToGoal",
                1,
                agent.getMap(),
                agent.getMap().getAgentPos(agent),
                point,
                agent.getBody(),
                5000,
                agent);
    if (plan == null) return (false);
    // bastl, jen pro legraci. Volne sousedni agenta
    PPlans.put("goToGoal", plan);
    return (true);
  }
}

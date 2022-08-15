package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSPAStar;
import java.awt.*;

public class DSGoToGoal extends DSGGoal {
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
        new DSPAStar()
            .computePath(
                "goToGoal",
                1,
                agent.getMap(),
                agent.getMap().getAgentPos(agent),
                point,
                agent.getBody(),
                5000,
                agent, true);

    if (plan == null) return (false);
    // bastl, jen pro legraci. Volne sousedni agenta
    PPlans.put("goToGoal", plan);
    return (true);
  }
}

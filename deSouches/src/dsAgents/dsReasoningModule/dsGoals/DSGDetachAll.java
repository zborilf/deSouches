package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSDetach;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.util.LinkedList;

public class DSGDetachAll extends DSGoal {
  @Override
  public String getGoalDescription() {
    return ("detachAllGoal");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    LinkedList<String> directions = agent.getBody().getAllDirectionsAttached();
    DSPlan detachAllPlan = new DSPlan("detachAllaGoal", 2);
    DSDetach detach;
    if (PPlans.containsKey("detachAllGoal")) {
      return false;
    }
    if (directions.size() == 0) { // nothing to detach -> immedieate success
      setPlansToSuccess();
      return (true);
    }
    System.out.println(
        "?? - agent "
            + agent.getEntityName()
            + " mel neco attachle minimalne na "
            + directions.getFirst());
    for (String direction : directions) {
      detach = new DSDetach(direction);
      detachAllPlan.insertAction(detach);
    }
    PPlans.put("detachAllGoal", detachAllPlan);
    return true;
  }
}

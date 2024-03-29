package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSSubmit;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

public class DSGSubmitGoal extends DSGGoal {

  private String PTaskName;

  @Override
  public String getGoalDescription() {
    return ("submitGoal");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    if (PPlans.containsKey("submitGoal")) return false;
    DSPlan plan = new DSPlan("submit plan", 1);
    plan.insertAction(new DSSubmit(PTaskName));
    PPlans.put("submitGoal", plan);
    return (true);
  }

  public DSGSubmitGoal(String taskName) {
    PTaskName = taskName;
  }
}

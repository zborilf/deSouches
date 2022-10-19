package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsMultiagent.dsTasks.DSTaskType;

public class DSGConnectGoal extends DSGGoal {

  private DSPlan PPlan;
  private DSTaskType PTaskType;
  private String PTaskName;
  private int PNOLeutnant;

  @Override
  public String getGoalDescription() {
    return ("customGoal");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    if (PPlans.containsKey("customGoal")) return false;
    DSPlan plan = PTaskType.dancePlan(agent, PTaskName);
   // plan.setTerminating(false);
    if (plan != null) {
      PPlans.put("customGoal", plan);
      return (true);
    }
    return (false);
  }

  public DSGConnectGoal(DSTaskType taskType, int nOLeutnant, String taskName) {
    PNOLeutnant = nOLeutnant;
    PTaskType = taskType;
    PTaskName = taskName;
  }
}

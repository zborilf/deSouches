package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;

public class DSGoalFalse extends DSGGoal {
  @Override
  public String getGoalDescription() {
    return ("false");
  }

  @Override
  public int goalStatus() {
    return (DSGGoal.__DSGExecutionFailed);
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    return false;
  }
}

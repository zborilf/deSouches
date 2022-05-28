package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;

public class DSGoalTrue extends DSGoal {
  @Override
  public String getGoalDescription() {
    return ("true");
  }

  @Override
  public int goalStatus() {
    return (DSGoal.__DSGGoalAchieved);
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    return false;
  }
}

package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;

public class DSGoalTrue extends DSGGoal {
  @Override
  public String getGoalDescription() {
    return ("true");
  }

  @Override
  public int goalStatus() {
    return (DSGGoal.__DSGGoalAchieved);
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    return false;
  }
}

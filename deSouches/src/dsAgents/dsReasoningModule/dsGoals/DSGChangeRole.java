package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSAdopt;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

public class DSGChangeRole extends DSGGoal {

  String PRole;

  public boolean goalAchieved() {
    return (PLastStatus == __DSGGoalAchieved);
  }

  @Override
  public String getGoalDescription() {
    return ("changeRole");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {
    if (agent.getActualRole().compareTo(PRole) == 0) {
      PLastStatus = __DSGGoalAchieved;
    }
    DSPlan plan=new DSPlan("changeRole",1);
    plan.appendAction(new DSAdopt("worker"));
    PPlans.put(plan.getName(),plan);

    //        if(if(PPlans.containsKey("changeRole"))
    return false;
  }

  public DSGChangeRole(String role) {
    PRole = role;
  }
}

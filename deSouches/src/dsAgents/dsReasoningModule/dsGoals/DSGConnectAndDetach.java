package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSAction;
import dsAgents.dsExecutionModule.dsActions.DSConnect;
import dsAgents.dsExecutionModule.dsActions.DSDetach;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

public class DSGConnectAndDetach extends DSGoal {

  String PDirection;
  String PPartnerName;

  @Override
  public String getGoalDescription() {
    return ("connectAndDetach");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {

    if (PPlans.containsKey("dance1")) return false; // plan exists,  no revision

    DSPlan plan = new DSPlan("dance1", 1);
    DSAction connect = new DSConnect(agent.getEI(), PDirection, PPartnerName);
    DSAction detach = new DSDetach(agent.getEI(), PDirection);
    plan.insertAction(detach);
    plan.insertAction(connect);

    PPlans.put("dance1", plan);
    return true;
  }

  public DSGConnectAndDetach(String direction, String partnerName) {
    PPartnerName = partnerName;
    PDirection = direction;
  }
}

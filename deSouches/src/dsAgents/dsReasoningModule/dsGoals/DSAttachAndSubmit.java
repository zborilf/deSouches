package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSAction;
import dsAgents.dsExecutionModule.dsActions.DSAttach;
import dsAgents.dsExecutionModule.dsActions.DSSubmit;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

public class DSAttachAndSubmit extends DSGGoal {

  String PTaskName;
  String PDirection;
  int PBlockType;

  @Override
  public String getGoalDescription() {
    return ("attachAndSubmit");
  }

  @Override
  public boolean revisePlans(DSAgent agent) {

    if (PPlans.containsKey("attachAndSubmit")) return false; // plan exists,  no revision

    DSPlan plan = new DSPlan("attachAndSubmit", 1);
    DSAction submit = new DSSubmit(PTaskName);
    plan.insertAction(submit);
    DSAttach newAttach = new DSAttach(PDirection, PBlockType);
    plan.insertAction(newAttach);
    PPlans.put("attachAndSubmit", plan);
    return (true);
  }

  public DSAttachAndSubmit(String taskName, String direction, int blockType) {
    PTaskName = taskName;
    PDirection = direction;
    PBlockType = blockType;
  }
}

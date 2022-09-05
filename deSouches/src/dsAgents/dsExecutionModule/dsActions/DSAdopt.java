package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;

public class DSAdopt extends DSAction {

  String PRole;

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    return (null);
  }

  @Override
  public DSGGoal execute(DSAgent agent) {

    Action a = new Action("adopt", new Identifier(PRole));
    agent.printOutput("Adopt action: " + a.toProlog()+"\n");


    try {
      agent.getEI().performAction(agent.getJADEAgentName(), a);

    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  @Override
  public void succeededEffect(DSAgent agent) {
  }

  @Override
  public boolean isExternal() {
    return (true);
  }

  @Override
  public String actionText() {
    return ("Adopt");
  }

  public DSAdopt(String role) {
    PRole = role;
  }
}

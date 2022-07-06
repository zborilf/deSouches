package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Numeral;
import java.awt.*;

public class DSClear extends DSAction {

  Point Pp;

  @Override
  public DSGoal execute(DSAgent agent) {

    Action a = new Action("clear", new Numeral(Pp.x), new Numeral(Pp.y));
    try {
      agent.getEI().performAction(agent.getJADEAgentName(), a);
    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  public void succeededEffect(DSAgent agent) {
    // odstrani napojene veci na body
  }

  @Override
  public boolean isExternal() {
    return true;
  }

  @Override
  public String actionText() {
    return ("clear "+ Pp);
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    return null;
  }

  public DSClear(Point direction) {
    Pp = direction;
  }
}

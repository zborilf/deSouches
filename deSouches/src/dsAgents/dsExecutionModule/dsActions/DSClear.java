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
import eis.iilang.Numeral;
import java.awt.*;

public class DSClear extends DSAction {



  Point PTarget;


  @Override
  public DSGGoal execute(DSAgent agent) {

    Action a = new Action("clear", new Numeral(PTarget.x), new Numeral(PTarget.y));

    agent.printOutput("Clear action: " + a.toProlog()+"\n");


    try {
      agent.getEI().performAction(agent.getJADEAgentName(), a);
    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  public void succeededEffect(DSAgent agent) {
    agent.clearPerformed(PTarget);
    // odstrani napojene veci na body
  }

  @Override
  public boolean isExternal() {
    return true;
  }

  @Override
  public String actionText() {
    return ("clear "+ PTarget);
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    return null;
  }

  public DSClear(Point direction) {
    PTarget = direction;
  }


}

package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import java.awt.*;

public class DSConnect extends DSAction {

  String PDirection;
  String PPartnerName;

  @Override
  public DSGGoal execute(DSAgent agent) {

    Point Pp = DSPerceptor.getPositionFromDirection(PDirection);
    Action a =
            new Action("connect", new Identifier(PPartnerName), new Numeral(Pp.x), new Numeral(Pp.y));
    agent.printOutput("Connect action: " + a.toProlog()+"\n");

    try {
      agent.getEI().performAction(agent.getJADEAgentName(), a);

    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  @Override
  public boolean isExternal() {
    return true;
  }

  @Override
  public String actionText() {

    Point Pp = DSPerceptor.getPositionFromDirection(PDirection);
    return ("Connect " + " / " + PPartnerName + " / " + Pp);
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    return null;
  }

  public DSConnect(String direction, String partnerName) {
    PDirection = direction;
    PPartnerName = partnerName;
  }
}

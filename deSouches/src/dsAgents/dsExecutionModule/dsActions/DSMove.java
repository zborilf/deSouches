package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSAStarItem;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DSMove extends dsAgents.dsExecutionModule.dsActions.DSAction {
  private ArrayList<String> PDirection = new ArrayList<>();
  int PDx = 0;
  int PDy = 0;
  int PDxO = 0;
  int PDyO = 0;

  boolean PPartial=false;
  int PLAStep = 0;

  @Override
  public DSGGoal execute(DSAgent agent) {
    Action a =
        new Action(
            "move",
            PDirection.stream()
                .map(Identifier::new)
                .collect(Collectors.toCollection(ArrayList::new)));
    PLAStep = agent.getStep();
    agent.printOutput("Moving action: " + a.toProlog()+"\n");

    try {
      agent.getEI().performAction(agent.getJADEAgentName(), a);
    } catch (ActException e) {
      return (new DSGoalFalse());
    }
    return (new DSGoalTrue());
  }

  @Override
  public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
    // TODO Auto-generated method stub
    Point point = item.getPosition();
    DSBody body = item.getBody();
    Point newPosition = new Point(map.centralizeXCoords(point.x + PDx),
                                map.centralizeYCoords(point.y + PDy));
    if (map.isObstacleAt(newPosition, agentBody, body, step)) return (null);
    return (new DSAStarItem(newPosition, item, this, body, 0, 0));
  }

  public synchronized void succeededEffect(DSAgent agent) {
    if ((agent.getStep() - PLAStep) > 1)
      System.out.println(
          "!!! "
              + agent.getEntityName()
              + " v kroku "
              + agent.getStep()
              + " prodleva "
              + (agent.getStep() - PLAStep));
    if (PPartial) {
      agent.getMap().moveBy(agent, PDxO, PDyO);
    }else
    agent.getMap().moveBy(agent, PDx, PDy);

  }

  public void setPartial(){
    PPartial=true;
  }

  @Override
  public boolean isExternal() {
    return (true);
  }

  @Override
  public String actionText() {
    return ("Move [" + PDx + "," + PDy + "]");
  }

  public DSMove(String direction) {
    PDirection.add(direction);
    Point pos = DSPerceptor.getPositionFromDirection(direction);
    PDx = pos.x;
    PDy = pos.y;
  }

  public void addDirection(String direction) {
    PDirection.add(direction);
    Point pos = DSPerceptor.getPositionFromDirection(direction);
    PDxO = pos.x;
    PDyO = pos.y;
    PDx += pos.x;
    PDy += pos.y;
  }

  public void AstarReverseOrder() {
    ArrayList<String> tempList = new ArrayList<String>();
    for (int i = PDirection.size() - 1; i >= 0; i--) {
      tempList.add(PDirection.get(i));
    }
    PDirection = tempList;
  }

  public String getPlannedDirection() {
    return PDirection.get(0);
  }

  public String getPlannedDirections() {
    return String.join("->", PDirection);
  }
}

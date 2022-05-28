package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import java.awt.*;

public class DSODispenserGoalMission {
  public Point PDispenserPosition;
  public Point PGoalPosition;
  public int PDispenserType;
  public DSAgent PAgent;
  private static final int _distanceMax = 9999;

  public int getLength() {
    if (PAgent.getBody().blockAttached(DSCell.__DSBlock + PDispenserType) != null)
      return (DSMap.distance(PAgent.getMapPosition(), PGoalPosition));
    if (PAgent.getBody().blockAttached(DSCell.__DSBlock + PDispenserType) == null)
      return (DSMap.distance(PAgent.getMapPosition(), PDispenserPosition)
          + DSMap.distance(PDispenserPosition, PGoalPosition));
    return (_distanceMax);
  }

  public int getDispenserType() {
    return (PDispenserType);
  }

  public DSAgent getAgent() {
    return (PAgent);
  }

  public Point getDispenserPosition() {
    return (PDispenserPosition);
  }

  public void printDGM(int type) {
    System.out.println(
        "-: shortest for "
            + type
            + " dispenser at "
            + PDispenserPosition
            + " and goal at "
            + PGoalPosition
            + " is with agent "
            + PAgent.getEntityName()
            + " at position "
            + PAgent.getMapPosition()
            + "/n TOTAL LENGTH is "
            + getLength());
  }

  public DSODispenserGoalMission(
      DSAgent agent, Point dPosition, Point goalPosition, int dispenserType) {
    PAgent = agent;
    PDispenserType = dispenserType;
    PDispenserPosition = dPosition;
    PGoalPosition = goalPosition;
  }
}

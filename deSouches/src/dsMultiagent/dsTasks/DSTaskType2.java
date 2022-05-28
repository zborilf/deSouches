package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import java.awt.*;

public class DSTaskType2 extends DSTaskType {

  DSBody PFormationBody;

  @Override
  public DSBody getSoldierGoalBody() {
    return null;
  }

  /*@Override
  public DSBody getSoldierGoalBody(DSAgent agent){
      return(DSBody.getDoubleBody(new Point(0,1)));
  }*/

  @Override
  public DSBody getTaskBody() {
    return null;
  }

  public Point formationPosition(DSAgent agent, Point position) {
    if (agent == PMaster) return (new Point(position.x, position.y));
    if (agent == PLeutnant1) return (new Point(position.x + 1, position.y));
    return null;
  }

  @Override
  public Point blockPosition(int agent) {
    if (agent == 1) return (new Point(0, 1));
    if (agent == 2) return (new Point(1, 1));
    return (null);
  }

  @Override
  public Point blockPosition(DSAgent agent) {
    if (agent == PMaster) return (blockPosition(1));
    if (agent == PLeutnant1)
      ;
    return (blockPosition(2));
  }

  public DSTaskType2() {
    super();
    PTaskTypeNumber = 2;
    PTaskArea.addCell(new DSCell(0, 1, 0, 0));
    PTaskArea.addCell(new DSCell(1, 0, 0, 0));
    PTaskArea.addCell(new DSCell(1, 1, 0, 0));
  }
}

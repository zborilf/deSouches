package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSConnect;
import dsAgents.dsExecutionModule.dsActions.DSDetach;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.awt.*;

public class DSTaskType14 extends DSTaskType {

  @Override
  DSPlan makePlanL1() {
    DSPlan plan = new DSPlan("Leutnant1, task14", 2);
    DSConnect connect = new DSConnect(getConnectDirection(PLeutnant1), PLeutnant2.getEntityName());
    plan.appendAction(connect);
    connect = new DSConnect(getConnectDirection(PLeutnant1), PMaster.getEntityName());
    plan.appendAction(connect);
    DSDetach detach = new DSDetach(getConnectDirection(PLeutnant1));
    plan.appendAction(detach);
    return (plan);
  };

  @Override
  DSPlan makePlanL2() {
    DSPlan plan = new DSPlan("Leutnant2, task14", 2);
    DSConnect connect = new DSConnect(getConnectDirection(PLeutnant2), PLeutnant3.getEntityName());
    plan.appendAction(connect);
    connect = new DSConnect(getConnectDirection(PLeutnant2), PLeutnant1.getEntityName());
    plan.appendAction(connect);
    DSDetach detach = new DSDetach(getConnectDirection(PLeutnant2));
    plan.appendAction(detach);
    return (plan);
  }
  ;

  @Override
  DSPlan makePlanL3() {
    DSPlan plan = new DSPlan("Leutnant3, task14", 2);
    DSConnect connect = new DSConnect(getConnectDirection(PLeutnant3), PLeutnant2.getEntityName());
    plan.appendAction(connect);
    DSDetach detach = new DSDetach(getConnectDirection(PLeutnant3));
    plan.appendAction(detach);
    return (plan);
  }
  ;

  /*
  public DSBody getSoldierGoalBody() {
    return null;
  }
   */


  public DSBody getTaskBody() {
    return null;
  }


  @Override
  public Point blockPosition(int agent) {
    if (agent == 1) return (new Point(0, 1));
    if (agent == 2) return (new Point(-1, 1));
    if (agent == 3) return (new Point(-2, +1));
    if (agent == 4) return (new Point(-3, +1));
    return (null);
  }

  @Override
  public Point formationPosition(DSAgent agent, Point position) {
    if (agent == PMaster) return (new Point(position.x, position.y));
    if (agent == PLeutnant1) return (new Point(position.x - 1, position.y));
    if (agent == PLeutnant2) return (new Point(position.x - 2, position.y));
    if (agent == PLeutnant3) return (new Point(position.x - 3, position.y));
    return (null);
  }

  public DSTaskType14() {
    super();
    PTaskTypeNumber = 14;
    PTaskArea.addCell(new DSCell(0, 0, 0, 0));
    PTaskArea.addCell(new DSCell(0, 1, 0, 0));
    PTaskArea.addCell(new DSCell(-1, 0, 0, 0));
    PTaskArea.addCell(new DSCell(-1, 1, 0, 0));
    PTaskArea.addCell(new DSCell(-2, 0, 0, 0));
    PTaskArea.addCell(new DSCell(-2, 1, 0, 0));
    PTaskArea.addCell(new DSCell(-3, 0, 0, 0));
    PTaskArea.addCell(new DSCell(-3, 1, 0, 0));
  }
}

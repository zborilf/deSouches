package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.*;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.awt.*;

/*
    A    XXX
   OO    XXX
    O     XX
           X


   Mpos	L1	    L2		Mpos	C	G
   0	    -1,0	1,2		L1	    C	D
			        L2	    S	S	CW	C	D

*/

public class DSTaskType9 extends DSTaskType {

  @Override
  protected DSPlan makePlanMaster(String taskName) {
    DSPlan plan = new DSPlan("Master, connect and submit", 2);
    DSConnect connect = new DSConnect("s", PLeutnant1.getEntityName());
    plan.appendAction(connect);
    connect = new DSConnect("s", PLeutnant2.getEntityName());
    plan.appendAction(connect);
    return (plan);
  }

  DSPlan makePlanL1() {
    DSPlan plan = new DSPlan("Leutnant1, task9", 2);
    DSConnect connect = new DSConnect("s", PMaster.getEntityName());
    plan.appendAction(connect);
    DSDetach detach = new DSDetach("s");
    plan.appendAction(detach);
    return (plan);
  }
  ;

  DSPlan makePlanL2() {
    DSPlan plan = new DSPlan("Leutnant2, task9", 2);
    DSConnect connect = new DSConnect("w", PMaster.getEntityName());
    plan.appendAction(connect);
    DSDetach detach = new DSDetach("w");
    plan.appendAction(detach);
    return (plan);
  }
  ;

  /*

  public DSBody getSoldierGoalBody() {
      return null;
  }

  @Override
  public DSBody getSoldierGoalBody(DSAgent agent) {
      if(agent==PLeutnant2)
          return(DSBody.getDoubleBody(new Point(-1,0)));
      return(DSBody.getDoubleBody(new Point(0,1)));
  }


  @Override
  public String getConnectDirection(DSAgent agent){
      if(agent==PLeutnant1)
          return("s");
      if(agent==PLeutnant2)
          return("w");
      return("");
  }

   */

  @Override
  public DSBody getTaskBody() {
    return null;
  }

  @Override
  public Point formationPosition(DSAgent agent, Point position) {
    if (agent == PMaster) return (new Point(position.x, position.y));
    if (agent == PLeutnant1) return (new Point(position.x - 1, position.y));
    if (agent == PLeutnant2) return (new Point(position.x + 1, position.y + 2));
    return (null);
  }

  @Override
  public Point blockPosition(int agent) {
    if (agent == 1) return (new Point(0, 1));
    if (agent == 2) return (new Point(-1, 1));
    if (agent == 3) return (new Point(0, +2));
    return (null);
  }

  @Override
  public Point blockPosition(DSAgent agent) {
    if (agent == PMaster) return (blockPosition(1));
    if (agent == PLeutnant1) return (blockPosition(2));
    if (agent == PLeutnant2) return (blockPosition(3));
    return (null);
  }

  public DSTaskType9() {
    super();
    PTaskTypeNumber = 9;
    PTaskArea.addCell(new DSCell(-1, 0, 0, 0));
    PTaskArea.addCell(new DSCell(-1, 1, 0, 0));
    PTaskArea.addCell(new DSCell(0, 0, 0, 0));
    PTaskArea.addCell(new DSCell(0, 1, 0, 0));
    PTaskArea.addCell(new DSCell(0, 2, 0, 0));
    PTaskArea.addCell(new DSCell(1, 0, 0, 0));
    PTaskArea.addCell(new DSCell(1, 1, 0, 0));
    PTaskArea.addCell(new DSCell(1, 2, 0, 0));
    PTaskArea.addCell(new DSCell(1, 3, 0, 0));
  }
}

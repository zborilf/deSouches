package dsAgents.dsReasoningModule.dsGoals;

import deSouches.utils.HorseRider;
import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSAttach;
import dsAgents.dsExecutionModule.dsActions.DSRequest;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.awt.*;

public class DSGetBlock extends DSGoal {

  private static final String TAG = "DSGoToDispenser";
  int PBlockType;
  Point PDispenserLocation;

  // napad, kdyz nevidi depod, sestavi plan jit k nejblizsimu 'neprekazkovemu' okraji sve mapy
  // pokud depot vidi, snazi se k nemu dojit, sestavi cestu k nemu, pokud by nahodou spadl,
  // sestavuje cestu znovu

  public String getGoalDescription() {
    return ("goToDispenser");
  }

  /*
         Plan "Get Block from a Dispenser" (defaultne, pokud nevidí volný blok)
  */

  DSPlan makePlanGetFromDispenser(DSAgent agent) {

    if (PDispenserLocation == null) PDispenserLocation = agent.getNearestDispenser(PBlockType);

    if (PDispenserLocation != null) { // nevidi dispenser

      //      PPlan = new DSAStar().computePath(agent.getMap(), agent.getMap().getAgentPos(),
      // PDestination, agent.getBody(),300, agent);

      String mv1 = agent.getMap().getFreeNeighbour(PDispenserLocation, agent);
      String mv2 = agent.getMap().oppositeDirection(mv1);

      Point out = DSPerceptor.getPositionFromDirection(mv1);

      Point attachFrom = new Point(PDispenserLocation.x + out.x, PDispenserLocation.y + out.y);
      DSPlan newPlan = astarGroup("goToDispenser", 1, agent, attachFrom, agent.getBody());
      HorseRider.inform(
          TAG,
          "makePlan: "
              + agent.getEntityName()
              + " pronasleduje dispenser na "
              + PDispenserLocation);

      if (newPlan != null) {
        newPlan.setPriority(1000 - newPlan.getLength());
        DSRequest newRequest = new DSRequest(mv2);
        newPlan.appendAction(newRequest);
        DSAttach newAttach = new DSAttach(mv2, PBlockType);
        newPlan.appendAction(newAttach);

        return (newPlan);
      }
    }
    return (null);
  }

  /*11111111111
         Plan "Go For a Block"
  */

  DSPlan makePlanGoForBlock(DSAgent agent) {

    Point blockAt = agent.nearestFreeBlock(PBlockType);

    if (blockAt == null) return (null);

    // Make plan
    String neighbPos = agent.getMap().getFreeNeighbour(blockAt, agent);
    String neighbPosOp = agent.getMap().oppositeDirection(neighbPos);
    Point out = DSPerceptor.getPositionFromDirection(neighbPos);
    Point position = new Point(blockAt.x + out.x, blockAt.y + out.y);
    DSPlan newPlan = astarGroup("goForBlock", 2, agent, position, agent.getBody());
    if (newPlan != null) {
      newPlan.setPriority(1000 - newPlan.getLength());
      DSAttach newAttach = new DSAttach(neighbPosOp, PBlockType);
      newPlan.appendAction(newAttach);

      return (newPlan);
    }
    return (null);
  }

  DSPlan makePlanGrabBlock(DSAgent agent) {

    return (null);
    /*
            String direction=agent.getMap().getFreeNeighbourObject(DSCell.__DSBlock+PBlockType, agent);
            if(direction=="")
                return(null);
            // Make plan
            DSPlan newPlan=new DSPlan("grabBlock",3);
            DSAttach newAttach = new DSAttach( direction, PBlockType);
                newPlan.appendAction(newAttach);
                return (newPlan);
    */
  }

  /*
         Plan revision
  */

  @Override
  public boolean revisePlans(DSAgent agent) { // true -> plans modified

    boolean modified = false;

    if (agent.getBody().blockAttached(PBlockType) != null) {
      setPlansToSuccess();
      return (true);
    }

    if (!PPlans.containsKey("goToDispenser")) {
      DSPlan gtdPlan = makePlanGetFromDispenser(agent);
      if (gtdPlan != null) {
        modified = true;
        PPlans.put("goToDispenser", gtdPlan);
      }
    }

    if (!PPlans.containsKey("goForBlock")) {
      DSPlan gbPlan = makePlanGoForBlock(agent);
      if (gbPlan != null) {
        modified = true;
        PPlans.put("goForBlock", gbPlan);
      }
    }

    if (!PPlans.containsKey("grabBlock")) {
      DSPlan gbPlan = makePlanGrabBlock(agent);
      if (gbPlan != null) {
        modified = true;
        PPlans.put("grabBlock", gbPlan);
      }
    }

    return (modified);
  }

  public DSGetBlock(int blockType) {
    super();
    PBlockType = blockType;
    PDispenserLocation = null;
  }

  public DSGetBlock(int dispenserType, Point dispenserLocation) {
    super();
    PBlockType = dispenserType;
    PDispenserLocation = dispenserLocation;
  }
}

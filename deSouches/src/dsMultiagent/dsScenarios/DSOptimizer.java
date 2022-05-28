package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.dsTasks.DSTask;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class DSOptimizer {
  private final DSTask PTask;
  private final LinkedList<DSAgent> PAgents;
  private final DSMap PMap;
  private final Point PGoalPosition;
  private final HashMap<Integer, LinkedList<Point>> PDispenserPositions;

  // nejkratsi cesta pro jeden ktery typ

  LinkedList<Point> getAgentPositions() {
    LinkedList<Point> agentPositions = new LinkedList<Point>();
    for (DSAgent agent : PAgents) agentPositions.add(agent.getMapPosition());
    return (agentPositions);
  }

  HashMap<Integer, LinkedList<Point>> getDispensersPositions() {
    HashMap<Integer, LinkedList<Point>> dispensersPositions =
        new HashMap<Integer, LinkedList<Point>>();
    for (int type : PTask.getTypesNeeded()) {
      if (!dispensersPositions.keySet().contains(type))
        dispensersPositions.put(type, PMap.allObjects(DSCell.__DSDispenser + type));
    }
    return (dispensersPositions);
  }

  int countDistance(Point agent, Point dispenser) {
    return (DSMap.distance(agent, dispenser) + DSMap.distance(dispenser, PGoalPosition));
  }

  DSODispenserGoalMission shortestDistanceAgentDispenser(DSAgent agent, int dispenserType) {
    DSODispenserGoalMission optimalDGM, DGM;
    LinkedList<Point> dispenserPositions = PDispenserPositions.get(dispenserType);
    if (dispenserPositions == null) return (null);
    if (dispenserPositions.isEmpty()) return (null);
    optimalDGM =
        new DSODispenserGoalMission(
            agent, dispenserPositions.getFirst(), PGoalPosition, dispenserType);
    for (Point dispenserPosition : dispenserPositions) {
      DGM = new DSODispenserGoalMission(agent, dispenserPosition, PGoalPosition, dispenserType);
      if (DGM.getLength() < optimalDGM.getLength()) optimalDGM = DGM;
    }
    return (optimalDGM);
  }

  DSODispenserGoalMission shortestDGM(int dispenserType) {
    DSODispenserGoalMission optimalDGM, DGM;
    if (PAgents.isEmpty()) return (null);
    optimalDGM = shortestDistanceAgentDispenser(PAgents.getFirst(), dispenserType);
    if (optimalDGM == null) return (null);
    for (DSAgent agent : PAgents) {
      DGM = shortestDistanceAgentDispenser(agent, dispenserType);
      if (DGM.getLength() < optimalDGM.getLength()) optimalDGM = DGM;
    }
    return (optimalDGM);
  }

  public LinkedList<DSODispenserGoalMission> getOptimalAgents() {
    LinkedList<DSODispenserGoalMission> missions = new LinkedList<DSODispenserGoalMission>();
    for (int taskType : PTask.getTypesNeeded()) {
      DSODispenserGoalMission mission = shortestDGM(taskType);
      if (PAgents != null) PAgents.remove(mission.getAgent());
      missions.add(mission);
      mission.printDGM(PTask.getTaskType().getTaskType());
    }
    return (missions);
  }

  /*
  Jen jedoduche, beru typ po typu a pro nej hledám optimální pár agentxdispenser
  */

  public DSOptimizer(LinkedList<DSAgent> freeAgents, Point goalPosition, DSTask task, DSMap map) {
    PTask = task;
    PMap = map;
    PAgents = freeAgents;
    PGoalPosition = goalPosition;
    PDispenserPositions = getDispensersPositions();
  }
}

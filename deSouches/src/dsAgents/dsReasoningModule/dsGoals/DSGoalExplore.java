package dsAgents.dsReasoningModule.dsGoals;

/*
       2022 - should be initiated with assigned area on the map
               it should
                       1, go randomly (with prob __random_walk higher priority than 2 and 3)
                       2, go to nearest known obstacle if known
                       3, go to role area if are not digger and role area is known
                       4, clear obstacles, if in touch
                       5, switch to digger if it is at role zone and is not digger
              Terminates when
                       Area is fully explored (by this agent during this behaviour)
                       Agent joins another group (should catch event 'group attached' or something ... )
*/

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.DSAdopt;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsExecutionModule.dsActions.DSMove;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSStraightPath;
import java.awt.Point;
import java.util.Comparator;
import java.util.LinkedList;

public class DSGoalExplore extends DSGoal {
  private static final String TAG = "DSGoalRoam";

  int PDistance;
  private final int ROAM_PRIORITY = 1;

  public String getGoalDescription() {
    if (highestPriorityPlan() == null) return "Goal explore";
    return ("Goal explore / " + highestPriorityPlan().getName());
  }

  public boolean revisePlans(DSAgent agent) {

    // TODO:l imo pouze worth pokud diger uvidim
    if (!PPlans.containsKey("clear")
        && agent.getActualRole().contentEquals("digger")
        && (Math.random() < 0.6)) {
      Point obstacleAt = agent.getMap().nearestObject(DSCell.__DSObstacle, agent.getMapPosition());
      int range = agent.getVisionRange();
      DSPlan plan = new DSPlan("clear", 4);
      Point direction =
          new Point(
              obstacleAt.x - agent.getMapPosition().x, obstacleAt.y - agent.getMapPosition().y);

      DSClear clearAction = new DSClear(agent.getEI(), direction);
      plan.appendAction(clearAction);
      PPlans.put(plan.getName(), plan);
    }

    if (agent.getActualRole().compareTo("default") == 0) {
      if (agent.getMap().getMap().getKeyType(agent.getMapPosition(), DSCell.__DSRoleArea) != null) {
        // standing at role zone and is not 'digger', make high priority plan to change it

        String role = agent.getCommander().roleNeeded(agent);

        DSPlan plan = new DSPlan("set role " + role, 3);
        DSAdopt adoptAction = new DSAdopt(agent.getEI(), role);
        plan.appendAction(adoptAction);
        PPlans.put(plan.getName(), plan);
        return (true);
      }

      if (!PPlans.containsKey("goToRoleZone") && (Math.random() < 0.5)) {
        Point dp = (agent.getMap().nearestObject(DSCell.__DSRoleArea, agent.getMapPosition()));
        if (dp != null) {
          DSPlan plan =
              new DSStraightPath()
                  .computeStraightPath(
                      agent,
                      "reachRoleZone",
                      (Point) agent.getMapPosition().clone(),
                      new Point(dp.x, dp.y),
                      2);
          PPlans.put("goToRoleZone", plan);
        }
      }
    }

    Point destination = null;
    String destinationS = "";

    // TODO:l priority priblizeni k ruznym typu bloku na feromony + posilit pro roly digger
    // todo:l ciste reaktivne nextPoint nebo vytvorit treba trasu delky 3 / PDistance -> ted
    // nextPoint dle vetsi vzdalenosti

    LinkedList<Point> neigbours =
        agent.getMap().getNeighboursExactDistance(agent.getMapPosition(), agent.getVisionRange());

    int curStep = agent.getStep();
    DSCell maxCell = null;
    double maxCellPhero = -100;
    // TODO:l delete vision output
    if (agent.getEntityName().contains("agentA1")) {
      System.err.print("A1 vis:");
    }
    for (Point p : neigbours) {
      DSCell cell = agent.getMap().getMap().getNewestAt(p);
      if (cell != null) {

        // TODO:l pokud symetrie drz smer
        if (agent.getEntityName().contains("agentA1")) {
          System.err.print(
              "  (" + p.x + "," + p.y + ") ->" + (int) cell.getVisiblePheromone(curStep));
        }
        maxCell = cell.getVisiblePheromone(curStep) > maxCellPhero ? cell : maxCell;
        maxCellPhero = maxCell.getVisiblePheromone(curStep);
      }
    }
    if (agent.getEntityName().contains("agentA1")) {
      System.err.println(" ." + agent.getStep());
    }
    System.err.flush();

    if (maxCell != null) {
      destination = maxCell.getPosition();
    } else {
      System.err.println("CELL HAS NO NEIGHBOURS step: " + agent.getStep());
    }

    if (destination == null) return false;

    // TODO:l proc DSSTRAIGHT a ne ASTAR
    DSPlan plan =
        new DSStraightPath()
            .computeStraightPath(
                agent, "roam", (Point) agent.getMapPosition().clone(), destination, ROAM_PRIORITY);
    final int UNREASONABLY_LARGE_N = 100;

    // plan = new
    // DSAStar().computePath("astarPlan",ROAM_PRIORITY,agent.getMap(),agent.getMapPosition(),destination,agent.getBody(), UNREASONABLY_LARGE_N, agent);

    // only first step of plan may include clear
    DSPlan stepPlan = new DSPlan("roamAnt", ROAM_PRIORITY);
    DSMove firstAction;
    try {
      firstAction = (DSMove) plan.getAction();
    } catch (Exception e) {
      System.err.println("EXPLORE PATH NOT AVAILABLE step: " + agent.getStep());
      return false;
    }

    Point dir = DSPerceptor.getPositionFromDirection(firstAction.getPlannedDirection());
    Point p = new Point(agent.getMapPosition().x + dir.x, agent.getMapPosition().y + dir.y);
    var alternativeP = agent.getMap().getNeighboursExactDistance(agent.getMapPosition(), 1);
    Point finalDestination = destination;
    alternativeP.sort(Comparator.comparingInt(sortP -> DSMap.distance(sortP, finalDestination)));

    // agent may be in way -> try other cells
    // todo:l maybe lower prior
    for (int i = 0; !alternativeP.isEmpty(); i++) {

      if (agent.getMap().isObstacleAt(p, agent.getBody(), agent.getBody(), agent.getStep())) {
        // TODO:l astar asi resi obstacle
        // TODO:l zatim zbytecne kontroluje range -> muzu zkusit nexstep clearovat?
        int range = 1;
        if (agent.getActualRole().contentEquals("digger")) range = agent.getVisionRange();
        if (DSMap.distance(agent.getMapPosition(), p) <= range) {
          DSPlan plan1 = new DSPlan("clear", ROAM_PRIORITY);
          Point direction =
              new Point(p.x - agent.getMapPosition().x, p.y - agent.getMapPosition().y);

          DSClear clearAction = new DSClear(agent.getEI(), direction);
          plan1.appendAction(clearAction);
          PPlans.put(plan1.getName(), plan1);
          return true;
        }
      } else if (agent.getMap().getMap().getKeyType(p, DSCell.__DSAgent) != null
          || agent.getMap().getMap().getKeyType(p, DSCell.__DSEntity_Enemy) != null
          || agent.getMap().getMap().getKeyType(p, DSCell.__DSEntity_Friend) != null) {
        // try other change direction
        // TODO:l due to synchronization problems temporary problems may arise
        p = alternativeP.pop();
        firstAction = new DSMove(agent.getEI(), DSPerceptor.getDirectionFromPosition(p));
      } else {
        // regular walk
        stepPlan.appendAction(firstAction);
        PPlans.put("roamAnt", stepPlan);
        return true;
      }
    }

    System.err.println(agent.getEntityName() + " NO PATH AVAILABLE");
    return false;
  }

  public DSGoalExplore(int distance) {
    PDistance = distance;
  }
}

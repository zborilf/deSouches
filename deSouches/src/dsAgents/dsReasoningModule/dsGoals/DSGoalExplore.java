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
import dsAgents.dsBeliefBase.dsBeliefs.DSRoles;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCells;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.DSAdopt;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSPAStar;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSStraightPath;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DSGoalExplore extends DSGGoal {

  int PDistance;

  public String getGoalDescription() {
    if (highestPriorityPlan() == null) return "Goal explore";
    return ("Goal explore / " + highestPriorityPlan().getName());
  }

  public boolean revisePlans(DSAgent agent) {
    final int ROAM_PRIORITY = 2, PRIORITY_BOOST = 2, LARGE_N = 300;
    final double ALLOWED_SIMILAR = 0.9;

    // exploration with attachments doesn't really make sense as its slower;
    if (agent.getBody().getBodyList().size() > 1) return false;

    List<Point> destinations = new ArrayList<>();
    int local_priority = ROAM_PRIORITY;

    if (agent.getActualRole().compareTo(DSRoles._roleDefault) == 0) {
      // try to change role if viable
      // if (agent.getMap().getMap().getKeyType(agent.getMapPosition(), DSCell.__DSRoleArea) != null) {

      if (agent.getOutlook().getCells().getKeyType(new Point(0,0), DSCell.__DSRoleArea) != null) {

        // standing at role zone -> set role
        String role = agent.getCommander().roleNeeded(agent);
        if(!role.contentEquals("")) {
          DSPlan plan = new DSPlan("set role " + role, 5);
          DSAdopt adoptAction = new DSAdopt(role);
          plan.appendAction(adoptAction);
          PPlans.put(plan.getName(), plan);
          return true;
        }
      }

    //  Point dp = agent.getOutlook().seenNearest(DSCell.__DSRoleArea);
      Point roleAreaPosition=agent.getMap().nearestObject(DSCell.__DSRoleArea, agent.getMapPosition());

      if (roleAreaPosition != null) {
        // zone in near proximity might as well go there -> first in list, the highest prior
     //   Point rolePosition=agent.getMap().shiftPosition(agent,dp);
        destinations.add(roleAreaPosition);
        local_priority += PRIORITY_BOOST;
      }


    }

    // try to shoot
    if(agent.getActualRole().contentEquals(DSRoles._roleDigger)){
      LinkedList<DSCell> attackables=agent.getOutlook().getCells().getAttackables();
      if(attackables!=null) {
          if(agent.getNotRecentlyAttacked(attackables)!=null) {
            Point p =
                    agent.getNotRecentlyAttacked(attackables).getPosition();
            DSPlan plan = new DSPlan("attack", 3, false);
            plan.appendAction(new DSClear(p));
            PPlans.put(plan.getName(), plan);
            return (true);
          }
        }
      }



    // SMAZ NIZE, POKUD SE TO ROZBILO

    if(agent.getGroup().getMap().sizeEstimated())
    {
      setSubgoal(new DSEvasiveManoeuvre(10));
      return(true);
    }


      LinkedList<Point> neigbours =
        agent.getMap().getNeighboursExactDistance(agent.getMapPosition(), agent.getVisionRange());
    List<DSCell> neigbourCells =
        neigbours.stream()
            .map(c -> agent.getMap().getMapCells().getNewestAt(c))
            .filter(Objects::nonNull)
            .toList();

    if (neigbourCells.isEmpty())
      System.err.println("CELL HAS NO NEIGHBOURS step: " + agent.getStep());

    else {


      double min =
          neigbourCells.stream()
              .min(Comparator.comparingDouble(DSCell::getPheromone))
              .orElse(null)
              .getPheromone();

      // add sorted viable cells
      neigbourCells.stream()
          .filter(
              c ->
                  c.getPheromone() >= (min * ALLOWED_SIMILAR)
                      && c.getPheromone() <= (min + min * (1 - ALLOWED_SIMILAR)))
          .sorted(Comparator.comparingDouble(DSCell::getPheromone))
          .map(DSCell::getPosition)
          .forEach(destinations::add);
    }

    if (destinations.isEmpty()) return false;

    // ASTAR - avoids obstacles -> default and explorer role
    // STRAIGHTPATH - throught obstacles -> digger

    for (Point candidate : destinations) {
      DSPlan plan =
          new DSStraightPath()
              .computeStraightPath(
                  agent,
                  "explore StraightPath",
                  (Point) agent.getMapPosition().clone(),
                  candidate,
                  local_priority);

      if (agent.getActualRole().compareTo("explorer") == 0) {
        plan =
            new DSPAStar()
                .computePath(
                    "explore Astar",
                    local_priority,
                    agent.getMap(),
                    agent.getMapPosition(),
                    candidate,
                    agent.getBody(),
                    LARGE_N,
                    agent, true);
      }

      /*
              End Ant Map
       */




      if (plan != null && plan.getLength() != 0) {
        // shorter plan only from first action for more reactive approach
        DSPlan stepPlan = new DSPlan("roamAnt", local_priority, false);   // TODO, vyhodit posledni false
        stepPlan.appendAction(plan.getAction());
        PPlans.put("roamAnt", stepPlan);
        return true;
      }
    }

    return false;
  }

  public DSGoalExplore(int distance) {
    PDistance = distance;
  }
}

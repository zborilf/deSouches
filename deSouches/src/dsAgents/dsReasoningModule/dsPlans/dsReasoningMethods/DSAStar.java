package dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.*;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;

public final class DSAStar {

  private static final String TAG = "DSAStar";

  private static int heuristic(Point from, Point to) {
    return DSMap.distance(from, to);
  }

  private static void printList(String agentName, LinkedList<DSAStarItem> lst) {
    StringBuilder message = new StringBuilder(agentName + "::");
    for (DSAStarItem item : lst) {
      if (item.PAction != null) {
        message
            .append("[")
            .append(item.PAction.actionText())
            .append(" ; ")
            .append("/")
            .append(item.getPosition().x)
            .append(",")
            .append(item.getPosition().y)
            .append("/:")
            .append(item.getCost())
            .append("/")
            .append(item.getHeuristic())
            .append("] ");
      }
    }
    System.out.println("printList: " + message);
  }

  private static void printStructures(
      String agentName, LinkedList<DSAStarItem> open, LinkedList<DSAStarItem> close, int steps) {
    //        HorseRider.inquire(TAG, "printStructures: "+steps + ">> Open: "+open.size());
    //        printList(open);

    System.out.println("printStructures: " + agentName + " / Close: " + close.size());
    printList(agentName, close);
  }

  private static DSAStarItem getCheapest(LinkedList<DSAStarItem> open) {

    DSAStarItem cheapest = open.get(0);
    for (DSAStarItem item : open) {
      if ((item.getCost() + item.getHeuristic()) < (cheapest.getCost() + cheapest.getHeuristic())) {
        cheapest = item;
      }
    }
    return cheapest;
  }

  private static LinkedList<DSAction> createActionList(DSAgent agent) {
    LinkedList<DSAction> actionList = new LinkedList<DSAction>();
    actionList.add(new DSRotateCW());
    actionList.add(new DSRotateContraCW());
    actionList.add(new DSMove("n"));
    actionList.add(new DSMove("s"));
    actionList.add(new DSMove("w"));
    actionList.add(new DSMove("e"));
    return (actionList);
  }

  private static boolean isInClosed(Point position, DSBody body, LinkedList<DSAStarItem> closed) {
    for (DSAStarItem dsaStarItem : closed) {
      if (dsaStarItem.getPosition().equals(position)) {
        if (dsaStarItem.getBody().matchBody(body)) return true;
      }
    }
    return false;
  }

  /**
   * inserts new value skips if better present, removes old with same cost and sorts list from
   * lowest.
   *
   * @param open sorted list
   * @param newItem new item to be inserted
   */
  private void insertCheapest(LinkedList<DSAStarItem> open, DSAStarItem newItem) {
    boolean placed = false;
    boolean removed = false;

    ListIterator<DSAStarItem> i = open.listIterator();
    while (i.hasNext()
        && (!placed
            || !removed)) { // iterate through items until placed and removed or ran out of elements
      DSAStarItem item = i.next();
      if (!removed && item.getPosition().equals(newItem.getPosition())) { // same position
        removed = true;
        if ((newItem.getCost() + newItem.getHeuristic())
            < (item.getCost() + item.getHeuristic())) { // new one is better
          i.remove();
        } else { // old one is better
          placed = true;
        }
      }
      if (!placed
          && (item.getCost() + item.getHeuristic())
              >= (newItem.getCost() + newItem.getHeuristic())) { // only worse values after this
        i.add(newItem); // add item before current
        placed = true;
      }
    }

    if (!placed) { // adds item at the end if it was not placed
      open.add(newItem);
    }
  }

  private void expandItem(
      DSMap map,
      DSAStarItem item,
      DSBody agentBody,
      LinkedList<DSAction> actions,
      Point to,
      LinkedList<DSAStarItem> open,
      LinkedList<DSAStarItem> closed,
      int step) {

    while (!actions.isEmpty()) {
      DSAction action = actions.pop();
      DSAStarItem newItem =
          action.simulate(map, item, agentBody, step); // TODO ta nula na konci je provizorka
      if (newItem != null) {
        if (!isInClosed(newItem.getPosition(), newItem.getBody(), closed)) {
          int newHeuristic = heuristic(newItem.getPosition(), to);
          newItem.setHeuristic(newHeuristic);
          newItem.setGCost(item.getGCost() + 1);
          insertCheapest(open, newItem);
        }
      }
    }
  }

  private DSPlan extractPath(DSPlan plan, DSAStarItem goal, DSAgent agent) {
    while (goal != null && goal.getAction() != null) {
      DSAction ac = goal.getAction();
      if (ac.actionText().contains("Move")) {
        DSMove mv = (DSMove) ac;

        for (int i = agent.getSpeed() - 1;
            i > 0 && goal.getPrevious() != null && goal.getPrevious().getAction() != null;
            i--) {
          goal = goal.getPrevious();
          DSAction nextAc = goal.getAction();
          if (nextAc.actionText().contains("Move")) {
            DSMove nextMv = (DSMove) nextAc;
            mv.addDirection(nextMv.getPlannedDirection());
          }
        }
        mv.AstarReverseOrder();
        plan.insertAction(mv);
      } else {
        plan.insertAction(ac);
      }
      goal = goal.getPrevious();
    }
    return plan;
  }

  private DSAStarItem aStarIterate(
      DSMap map,
      LinkedList<DSAStarItem> open,
      LinkedList<DSAStarItem> closed,
      Point to,
      DSBody finalBody,
      int steps,
      DSAgent agent,
      DSBody agentBody) {

    while (steps > 0) {
      //	printStructures(open,close,steps);
      if (open.isEmpty()) return null;
      DSAStarItem item = getCheapest(open);
      open.remove(item);
      //      System.out.println("A* dlja "+agent.getEntityName()+" dava do cosed "+item.PPosition);
      closed.add(item);

      if (item.getPosition().equals(to)) if (item.getBody().matchBody(finalBody)) return item;
      // expanding actions

      LinkedList<DSAction> actions = createActionList(agent);
      expandItem(map, item, agentBody, actions, to, open, closed, agent.getStep());

      steps--;
    }
    return null;
  }

  private DSPlan aStar(
      String planName,
      int priority,
      DSMap map,
      LinkedList<DSAStarItem> open,
      LinkedList<DSAStarItem> close,
      Point to,
      DSBody finalBody,
      int steps,
      DSAgent agent,
      DSBody agentBody) {
    DSAStarItem goal;
    Point from = open.getFirst().getPosition();
    String AN = agent.getEntityName();
    goal = aStarIterate(map, open, close, to, finalBody, steps, agent, agentBody);

    if (goal != null) {
      DSPlan path = new DSPlan(planName, priority);
      //         printStructures(agent.getEntityName(),open,close,0);
      return (extractPath(path, goal, agent));
    } else {
      //     System.out.println( "aStar: "+"Astar failed for " + agent.getEntityName()+" from
      // ["+from.x+","+
      //             from.y+"], to ["+to.x+","+to.y+"] agent body "+agentBody.bodyToString());
      //       map.printMap("Failed A* for "+agent.getEntityName());
    }
    return null;
  }

  public DSPlan computePath(
      String planName,
      int priority,
      DSMap map,
      Point from,
      Point to,
      DSBody finalBody,
      int steps,
      DSAgent agent) {
    // od; do; limit pocet kroku; struktura, kterou agent tahne (on+veci)
    LinkedList<DSAStarItem> open = new LinkedList<DSAStarItem>();
    LinkedList<DSAStarItem> close = new LinkedList<DSAStarItem>();
    //    HorseRider.inform(TAG, "computePath: agent "+agent.getEntityName()+" body
    // "+agent.getBody().bodyToString());
    // posunout na spravnou pozici agentovo telo na mape skupiny
    DSBody agentBodyShifted = DSBody.shiftBody(from, agent.getBody());
    //   HorseRider.inform(TAG, "computePath: agent "+agent.getEntityName()+" body shifted
    // "+agentBodyShifted.bodyToString()+" disp "+agent.getGroup().getDisplacement(agent));;

    open.add(new DSAStarItem(from, null, null, agent.getBody(), 0, heuristic(from, to)));
    //   HorseRider.inform(TAG, "computePath: "+agent.getEntityName()+" final body
    // "+finalBody.bodyToString());
    return (aStar(
        planName, priority, map, open, close, to, finalBody, steps, agent, agentBodyShifted));
  }
}

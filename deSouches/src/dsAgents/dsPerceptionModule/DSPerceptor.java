package dsAgents.dsPerceptionModule;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.DSBeliefBase;
import dsAgents.dsBeliefBase.dsBeliefs.DSBeliefsIndexes;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.*;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
import eis.PerceptUpdate;
import eis.iilang.*;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DSPerceptor {

  private static final String TAG = "DSProcessPercepts";
  LinkedList<Point> PFriendsSeen;
  DSPercepts PPercepts;

  public boolean disabled(Collection<Percept> percepts) {
    try {
      if (percepts.stream()
          .filter(prc -> prc.getName().equals("deactivated"))
          .iterator()
          .next()
          .getParameters()
          .get(0)
          .toString()
          .contentEquals("true")) return (true);
    } catch (Exception e) {
    }
    ;
    return (false);
  }

  /*
         Friends (agents) seen
  */

  void clearFriendsList() {
    PFriendsSeen.clear();
  }

  public static Point getPositionFromDirection(String direction) {
    Point directionPosition;
    int x = 0, y = 0;
    if (direction.contentEquals("n")) y = -1;
    else if (direction.contentEquals("s")) y = 1;
    else if (direction.contentEquals("w")) x = -1;
    else if (direction.contentEquals("e")) x = 1;
    return (new Point(x, y));
  }

  public static String getDirectionFromPosition(Point position) {
    if (position.x == 0) {
      if (position.y == 1) return ("s");
      if (position.y == -1) return ("n");
    }
    if (position.y == 0) {
      if (position.x == 1) return ("e");
      if (position.x == -1) return ("w");
    }
    return ("");
  }

  public boolean seesBlocksInBody(DSAgentOutlook outlook) {
        return(outlook.seesBlockBy(new Point(0,0),DSCell.__DSBlock, DSCell.__DSBlock+20));
  }



  public synchronized void actualizeMap(
          DSMap map,
          DSAgentOutlook outlook,
          Point agentPos,
          int vision,
          String PTeamName,
          int step,
          DSAgent agent) {

    clearFriendsList();

    DSCells newOutlook = outlook.getCells();
    for (int i = -vision; i <= vision; i++)
      for (int j = -vision + Math.abs(i); Math.abs(j) + Math.abs(i) <= vision; j++) {
        if (Math.abs(i) + Math.abs(j) > vision) continue;

        List<DSCell> old = new LinkedList<>();


        var allAt = map.getMapCells().getAllAt(map.shiftPosition(agent, new Point(i,j)));
        if (allAt != null && !allAt.isEmpty()) {
          old = allAt.stream().filter(x -> x.getTimestamp() <= step).toList();
        }

        map.removeOlder(map.shiftPosition(agent, new Point(i,j)), step);


        LinkedList<DSCell> cells = newOutlook.getAllAt(new Point(i, j));
        if (cells != null) {
          for (DSCell cell : cells) {
            DSCell newCell =
                new DSCell(
                        map.shiftPosition(agent, cell.getPosition()).x,
                        map.shiftPosition(agent, cell.getPosition()).y,
                        cell.getType(),
                        cell.getTimestamp(),
                        agent);

            var ncell =
                old.stream()
                    .filter(
                        c ->
                            newCell.getPosition().equals(c.getPosition())
                                && c.getType() == newCell.getType())
                    .toList();
            if (!ncell.isEmpty()) {
              newCell.setPheromone(ncell.get(0).getPheromone());
              newCell.setTimestamp(ncell.get(0).getTimestamp());
            }
            map.updateCell(newCell);
          }
        }
      }

    // adding clears
    for (int x = -vision; x <= vision; x++)
      for (int y = -vision + Math.abs(x); Math.abs(y) + Math.abs(x) <= vision; y++) {
        if (Math.abs(x) + Math.abs(y) > vision) continue;

        DSCell clearCell =
            new DSCell(map.shiftPosition(agent, new Point(x,y)).x,
                      map.shiftPosition(agent, new Point(x,y)).y,
                      DSCell.__DSClear, step, agent);

        LinkedList<DSCell> cells = map.getMapCells().getAllAt(clearCell.getPosition());
        if (cells == null
            || cells.stream()
                .noneMatch(
                    pCell ->
                        (pCell.getType() == DSCell.__DSObstacle)
                            || ((pCell.getType() >= DSCell.__DSBlock)
                                && (pCell.getType() < DSCell.__DSDispenser)))) {
          map.updateCell(clearCell);
        } else {
          map.getMapCells().removeCell(clearCell.getX(), clearCell.getY(), DSCell.__DSClear);
        }
      }
  }

  /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
   *
   *       Update BB for percepts
   */

  public static void processPercepts(DSBeliefBase BB, PerceptUpdate percepts) {

    /*
        for delete list
    */

    Iterator<Percept> newDeletePercepts = percepts.getDeleteList().iterator();
    BB.clearDeleteOutlook();

    Percept percept;
    String perceptName;
    Collection<Parameter> perceptParams;

    while (newDeletePercepts.hasNext()) {
      percept = newDeletePercepts.next();
      perceptName = percept.getName();
      perceptParams = percept.getParameters();

      switch (DSBeliefsIndexes.getIndex(perceptName)) {
        case DSBeliefsIndexes.__thing:
          BB.deleteThingFromOutlook(perceptParams);
          break;

        case DSBeliefsIndexes.__name:
          break;
        case DSBeliefsIndexes.__team:
          break;
        case DSBeliefsIndexes.__teamSize:
          break;
        case DSBeliefsIndexes.__steps:
          break;
        case DSBeliefsIndexes.__step:
          break;

        case DSBeliefsIndexes.__roleZone:
          BB.leavesRoleZone(perceptParams);
          break;

        case DSBeliefsIndexes.__goalZone:
          BB.leavesGoalZone(perceptParams);
          break;

        case DSBeliefsIndexes.__lastAction:
          break;
        case DSBeliefsIndexes.__lastActionParams:
          break;

        case DSBeliefsIndexes.__lastActionResult:
          break;

        case DSBeliefsIndexes.__task:
          BB.removeTask(perceptParams);
          break;

        case DSBeliefsIndexes.__attached:
          BB.removeAttached(perceptParams);
          break;

        case DSBeliefsIndexes.__energy:
          break;
        case DSBeliefsIndexes.__score:
      }
    }

    Iterator<Percept> newAddPercepts = percepts.getAddList().iterator();

    //  System.out.println(newAddPercepts);

    while (newAddPercepts.hasNext()) {
      percept = newAddPercepts.next();
      perceptName = percept.getName();
      perceptParams = percept.getParameters();

      switch (DSBeliefsIndexes.getIndex(perceptName)) {
        case DSBeliefsIndexes.__thing:
          BB.addThingToOutlook(perceptParams);
          break;

        case DSBeliefsIndexes.__name:
          BB.setName(perceptParams);
          break;

        case DSBeliefsIndexes.__team:
          break;

        case DSBeliefsIndexes.__teamSize:
          BB.setTeamSize(perceptParams);
          break;

        case DSBeliefsIndexes.__steps:

        case DSBeliefsIndexes.__role:
          BB.processRole(perceptParams);
          break;

        case DSBeliefsIndexes.__roleZone:
          BB.addRoleZoneToOutlook(perceptParams);
          break;

        case DSBeliefsIndexes.__goalZone:
          BB.addGoleZoneToOutlook(perceptParams);
          break;

        case DSBeliefsIndexes.__step:
          BB.setStep(perceptParams);
          break;

        case DSBeliefsIndexes.__lastAction:
          BB.setLastAction(perceptParams);
          break;

        case DSBeliefsIndexes.__lastActionParams:
          BB.setLastActionParams(perceptParams);
          break;

        case DSBeliefsIndexes.__lastActionResult:
          BB.setLastActionResult(perceptParams);
          break;

        case DSBeliefsIndexes.__task:
          BB.setTask(perceptParams);
          break;

        case DSBeliefsIndexes.__attached:
          BB.addAttached(perceptParams);
          break;


        case DSBeliefsIndexes.__energy:
          BB.setEnergy(perceptParams);
          break;

        case DSBeliefsIndexes.__score:
          BB.setScore(perceptParams);
          break;
        case DSBeliefsIndexes.__ranking:
          BB.getCommander().printOutput(" FINAL RANKING: "+perceptParams.iterator().next().toString());
          //System.exit(0);
      }
    }

    if (BB.getGUIFocus()) {
      String outlookString = BB.getOutlook().stringOutlook(BB.getVision(), BB.getName());
      BB.getGUI().writeTextOutlook("OUTLOOK:\n" + outlookString);
      BB.getGUI()
          .writePheroOutlook(
              "PHEROMONE OUTLOOK:\n" + BB.getOutlook().stringPheroOutLook(BB.getVision()));
    }
  }

  public DSPerceptor() {
    PPercepts = new DSPercepts();
    PFriendsSeen = new LinkedList<>();
  }
}

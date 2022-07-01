package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

/*
   2022, actual outlook of an agent (process add/delete lists)

*/

import dsAgents.DSAgent;
import java.awt.*;
import java.util.LinkedList;

public class DSAgentOutlook {
  DSCells POutlook;
  int PStep;

  public int getStep() {
    return (PStep);
  }

  public void processAddThing(int x, int y, String type, String params, int step, DSAgent agent) {
    PStep = step;
    DSCell cell = new DSCell(x, y, type, params, step, agent);
    POutlook.put(cell);
  }

  public void processDeleteThing(int x, int y, String type, String params, int step) {
    PStep = step;
    POutlook.removeCell(x, y, DSCell.getThingTypeIndex(type, params));
  }

  public synchronized DSCells getCells() {
    return (POutlook);
  }

  // list of friend agents in actual outlook

  public LinkedList<Point> getFriendsSeen(int vision) {
    LinkedList<Point> flist = new LinkedList<Point>();
    for (int i = -vision; i <= vision; i++)
      for (int j = -vision; j <= vision; j++) {
        Point point = new Point(i, j);
        if (POutlook.getKeyType(point, DSCell.__DSEntity_Friend) != null) flist.add(point);
      }
    return (flist);
  }

  public synchronized String stringOutlook(int vision, String agentname) {

    String so = "";

    for (int j = -vision; j <= vision; j++) {
      for (int i = -vision; i <= vision; i++) {
        if (Math.abs(i) + Math.abs(j) > vision) so = so + "   ";
        else {
          if ((j == 0) && (i == 0)) so = so + " AA";
          else {
            if (POutlook.containsKey(new Point(i, j))) {
              DSCell cell = POutlook.getOneAt(new Point(i, j));
              if (cell == null || cell.getType() == DSCell.__DSClear) so += " ..";
              else so += DSCell.getTypeSign(cell.getType());
            }
          }
        }
      }
      so += '\n';
    }
    return (so);
  }

  public DSAgentOutlook() {
    POutlook = new DSCells();
  }

  public String stringPheroOutLook(int vision) {
    StringBuilder so = new StringBuilder();

    for (int x = -vision; x <= vision; x++) {
      for (int y = -vision; y <= vision; y++) {
        if (Math.abs(x) + Math.abs(y) > vision) so.append("   ");
        else {
          DSCell regulC = POutlook.getNewestAt(new Point(x, y));

          if (regulC != null) {
            // only MSB to outlook for compact look
            int phero = (int) regulC.getPheromone();
            String hex = Integer.toHexString(phero).toUpperCase();
            hex = hex.length() == 1 ? '0' + hex : hex;
            if (phero >= 255 || phero < 0) System.err.println(" ERROR: PHEROMONE OVERFLOW");
            so.append(hex + " ");
          } else {
            so.append("?? ");
          }
        }
      }
      so.append('\n');
    }
    return so.toString();
  }
}

package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

/*
   2022, actual outlook of an agent (process add/delete lists)

*/

import dsAgents.DSAgent;
import java.awt.*;
import java.util.Comparator;
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
        if (Math.abs(x) + Math.abs(y) > vision) so.append(" ");
        else {
          var regulC = POutlook.getNewestAt(new Point(x, y));

          if (regulC != null) {
            var p =
                POutlook.getAllAt(new Point(x, y)).stream()
                    .max(Comparator.comparingInt(foo -> (int) foo.getPheromone()))
                    .orElse(null);
            int pPhero = (int) p.getPheromone();
            int regulPhero = (int) regulC.getPheromone();
            if (pPhero != regulPhero) {
              System.err.println("CHYBA PHERO OUTLOOK" + pPhero + "=/=" + regulPhero);
            }
            int phero = pPhero / 10;
            char printVal = (char) (phero + '0');
            if (phero >= 10) {
              printVal = (char) (phero - 10 + 'A');
            }
            so.append(printVal);
          } else {
            so.append("?");
          }
        }
      }
      so.append('\n');
    }
    return so.toString();
  }
}

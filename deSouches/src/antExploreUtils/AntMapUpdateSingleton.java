package antExploreUtils;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.DSGroup;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class AntMapUpdateSingleton {
  /*
  each group treated separately, keeping step count so map gets updated only once per step
   */

  private static volatile AntMapUpdateSingleton singleton;

  private AntMapUpdateSingleton() {}

  public static synchronized AntMapUpdateSingleton getInstance() {

    if (singleton == null) singleton = new AntMapUpdateSingleton();

    return singleton;
  }

  HashMap<DSGroup, Integer> agentStep = new HashMap<>();

  // each group separately
  // TODO:l synchronized jde predelat na semafor
  public synchronized void updateMap(DSAgent agent) {
    DSGroup g = agent.getGroup();

    // 1st pass ?
    if (agentStep.containsKey(g) && agentStep.get(g) == agent.getStep()) {
      return;
    }

    agentStep.put(g, agent.getStep());

    DSMap groupMap = agent.getMap();
    Point tlc = groupMap.getMap().getTLC();
    Point brc = groupMap.getMap().getBRC();

    if (tlc == null || brc == null) {
      System.err.println("ANT: map empty");
      return;
    }

    // systematic pass
    for (int x = tlc.x; x <= brc.x; x++) {
      for (int y = tlc.y; y <= brc.y; y++) {
        Point p = new Point(x, y);
        LinkedList<DSCell> cellsAtPoint = groupMap.getMap().getAllAt(p);
        if (cellsAtPoint == null || cellsAtPoint.isEmpty()) {
          continue;
        }

        for (DSCell cell : cellsAtPoint) {
          cell.evaporate();
        }

        // in theory all at same point should have same pheromone level
        // TODO:l check first vs newest

        // DSCell newestAtp = cellsAtPoint.getFirst();
        DSCell newestAtp = groupMap.getMap().getNewestAt(p);

        // diffusion to neighbourhood
        for (int xSurround = p.x - 1; xSurround <= p.x + 1; xSurround++) {
          for (int ySurround = p.y - 1; ySurround <= p.y + 1; ySurround++) {
            if (xSurround < 0 || xSurround > tlc.x || ySurround < 0 || ySurround > tlc.y) {
              // TODO:l resit overflow
              continue;
            }

            Point pSurround = new Point(xSurround, ySurround);
            LinkedList<DSCell> cellsAtSurroundPoint = groupMap.getMap().getAllAt(pSurround);

            for (DSCell cell : cellsAtSurroundPoint) {
              cell.addPheromone(newestAtp.getPheromone(agent.getStep()));
            }
          }
        }
      }
    }
  }
}

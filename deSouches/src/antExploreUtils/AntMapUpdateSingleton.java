package antExploreUtils;

import antExploreUtils.stats.AntMapStatistics;
import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.DSGroup;
import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class AntMapUpdateSingleton {
  /*
  each group treated separately, keeping step count so map gets updated only once per step
   */

  private static volatile AntMapUpdateSingleton singleton;

  public static synchronized AntMapUpdateSingleton getInstance() {

    if (singleton == null) singleton = new AntMapUpdateSingleton();

    return singleton;
  }

  private AntMapStatistics stat = new AntMapStatistics();

  private AntMapUpdateSingleton() {}

  ConcurrentHashMap<DSGroup, Integer> agentStep = new ConcurrentHashMap<>();

  public void updateMap(DSAgent agent) {
    DSGroup g = agent.getGroup();

    // groups work separately but only first agent of group calculates stats
    synchronized (this) {
      if (!agentStep.containsKey(g) || agentStep.get(g) == agent.getStep()) {
        agentStep.put(agent.getGroup(), agent.getStep());
        return;
      }
      agentStep.put(g, agent.getStep());
    }

    DSMap groupMap = agent.getMap();
    Point tlc = groupMap.getMap().getTLC();
    Point brc = groupMap.getMap().getBRC();

    if (tlc == null || brc == null) {
      System.err.println("ANT: map empty");
      return;
    }

    // to propagate from  I need to keep track of their pheromones
    // keeping copy of all cells, so they are not deleted also adding undiscovered cells
    HashMap<Point, DSCell> emptyCells = new HashMap<>();
    HashMap<Point, Double> nextGenPheromone = new HashMap<>();

    final double EVAP_COEFICIENT = 0.9, PHERO_LOW = 0.0;

    for (int x = tlc.x; x <= brc.x; x++) {
      for (int y = tlc.y; y <= brc.y; y++) {
        Point p = new Point(x, y);
        DSCell nCell = getNewestCellAt(groupMap, emptyCells, p);

        if (nCell != null) {
          emptyCells.put(p, nCell);
        } else {
          DSCell cell = new DSCell(x, y, DSCell.__DSBorder, 0, agent);
          cell.setPheromone(PHERO_LOW);
          emptyCells.put(p, cell);
        }
      }
    }

    // systematic pass - calculate
    for (int x = tlc.x; x <= brc.x; x++) {
      for (int y = tlc.y; y <= brc.y; y++) {
        // pheromone evaporation is automatic as it is calculated from discovery timestamp

        Point p = new Point(x, y);

        DSCell newestAtp = getNewestCellAt(groupMap, emptyCells, p);

        if (newestAtp == null) {
          continue;
        }

        // diffusion from direct neighbours
        LinkedList<Point> neigh = groupMap.getNeighboursExactDistance(p, 1);
        neigh.addAll(groupMap.getNeighboursExactDistance(p, 2));
        // results
        double neighCount = 1, sum = newestAtp.getPheromone();

        for (Point nP : neigh) {
          DSCell nCell = getNewestCellAt(groupMap, emptyCells, nP);
          if (nCell == null) continue;

          neighCount++;
          sum += nCell.getPheromone();
        }

        // ( neigh_size instead of neighCount boosts edges)
        double res = sum / (neigh.size()+1);
        // evaporation
        res *= EVAP_COEFICIENT;
        nextGenPheromone.put(p,res);
      }
    }

    // systematic pass - update
    for (int x = tlc.x; x <= brc.x; x++) {
      for (int y = tlc.y; y <= brc.y; y++) {
        Point p = new Point(x, y);
        DSCell newestAtp = groupMap.getMap().getNewestAt(p);

        if (newestAtp != null && nextGenPheromone.get(p) != null) {
          //newestAtp.setPheromone(nextGenPheromone.get(p));
          LinkedList<DSCell> cellsAtPoint = groupMap.getMap().getAllAt(p);

          if (cellsAtPoint == null || cellsAtPoint.isEmpty()) {
            continue;
          }

          for (DSCell c : cellsAtPoint) {
            c.setPheromone(nextGenPheromone.get(p));
          }

        }
      }
    }

    // statistics -> each group gets here once per step
    stat.knownCellsStats(agent);
    stat.revisitedStats(agent);
    stat.mapSizeStats(agent);

    // statistics -> once in step guaranted
    synchronized (this) {
      if (stat.completedStep == agent.getStep()) return;
      stat.completedStep = agent.getStep();
    }

    stat.numberOfUsedGroups(agent, agentStep);
  }

  protected DSCell getNewestCellAt(DSMap map, HashMap<Point, DSCell> emptyCells, Point atPoint) {
    // get cells from map if present otherwise fetch emptyCell
    // using newest as all cells should have synchronized pheromone levels
    DSCell cellsAtPoint = map.getMap().getNewestAt(atPoint);
    if (cellsAtPoint == null) {
      // beyond border > null
      cellsAtPoint = emptyCells.getOrDefault(atPoint, null);
    }
    return cellsAtPoint;
  }
}

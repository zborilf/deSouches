package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class DSCells {
  HashMap<Point, LinkedList<DSCell>> PHashCells;

  // for revisit time stat measuring
  HashMap<Point, Integer> revisitedNewestEvent = new HashMap<>();
  HashMap<Point, Integer> revisitedTime = new HashMap<>();

  public synchronized LinkedList<DSCell> getCells() {

    LinkedList<DSCell> cells = new LinkedList();
    LinkedList<DSCell> cellsAt;
    for (Point point : PHashCells.keySet()) {
      cellsAt = PHashCells.get(point);
      if (cellsAt != null) for (DSCell element : cellsAt) cells.add(element);
    }
    return (cells);
  }

  // top left corner
  public synchronized Point getTLC() {
    if (PHashCells.isEmpty()) return null;
    Point tlc = PHashCells.keySet().iterator().next();
    tlc = new Point(tlc.x, tlc.y);
    for (Point point : PHashCells.keySet()) {
      if (point.x <= tlc.x) tlc.x = point.x;
      if (point.y <= tlc.y) tlc.y = point.y;
    }
    return (tlc);
  }
  // bottom right corner
  public synchronized Point getBRC() {
    if (PHashCells.isEmpty()) return null;

    Point brc = PHashCells.keySet().iterator().next();
    brc = new Point(brc.x, brc.y);
    for (Point point : PHashCells.keySet()) {
      if (point.x >= brc.x) brc.x = point.x;
      if (point.y >= brc.y) brc.y = point.y;
    }

    return (brc);
  }

  protected synchronized void put(DSCell element) {
    Point pos = element.getPosition();

    if (PHashCells.containsKey(pos)) {
      LinkedList<DSCell> cells = PHashCells.get(pos);
      for (DSCell cell : cells)
        if (cell.getType() == element.getType()) { // not two of the same type at one place
          // try to update info
          if (element.getTimestamp() > cell.getTimestamp()) {
            cell.setTimestamp(element.getTimestamp());
            cell.setPheromone(element.getPheromone());
            cell.setFoundBy(element.getFoundBy());
          }
          return;
        }
      cells.add(element);
    } else {
      LinkedList<DSCell> cells = new LinkedList();
      cells.add(element);
      PHashCells.put(pos, cells);
    }

    int oldTime = revisitedNewestEvent.getOrDefault(pos, 1);
    int cellTime = getNewestAt(pos).getTimestamp();
    if (cellTime >= oldTime) {
      revisitedNewestEvent.put(pos, cellTime);
      if (cellTime > oldTime) revisitedTime.put(pos, cellTime - oldTime);
    }
  }

  protected synchronized void removeOlder(Point point, int step, boolean removeArea) {
    LinkedList<DSCell> oldList = PHashCells.get(point);
    LinkedList<DSCell> newList = new LinkedList();

    if (oldList != null) {
      for (DSCell element : oldList)
        if ((element.getTimestamp() > step)
            || (((element.getType() == DSCell.__DSGoalArea)
                || (element.getType() == DSCell.__DSRoleArea)))) { // && (!removeArea)))
          //                  if ((element.getType() == DSCell.__DSGoalArea) || (element.getType()
          // ==
          // DSCell.__DSRoleArea))
          //                      System.out.println("Zachranuji areu na "+element.getPosition());
          newList.add(element);
        }
    }
    //      System.out.println("Po clearu zbylo "+newList.size());
    PHashCells.put(point, newList);
  }

  public synchronized void removeCell(int x, int y, int type) {
    Point point = new Point(x, y);
    LinkedList<DSCell> oldList = getAllAt(point);
    LinkedList<DSCell> newList = new LinkedList<DSCell>();

    for (DSCell element : oldList) if (element.getType() != type) newList.add(element);
    PHashCells.put(point, newList);
  }

  public synchronized LinkedList<DSCell> getAllAt(Point point) {
    if (PHashCells.containsKey(point)) return ((LinkedList<DSCell>) PHashCells.get(point).clone());
    else return (null);
  }

  public synchronized DSCell getNewestAt(Point point) {
    // get newest of cells according to timestamp + synchronize pheromone
    // filter out absolute points for pheromone dispersion (zone + dispenser)

    LinkedList<DSCell> cellsAtPoint = this.getAllAt(point);

    if (cellsAtPoint == null || cellsAtPoint.isEmpty()) {
      return null;
    }

    DSCell newestCell =
        cellsAtPoint.stream().max(Comparator.comparingDouble(DSCell::getTimestamp)).get();

    return newestCell;
  }

  public synchronized DSCell getOneAt(Point point) {
    LinkedList<DSCell> cells = getAllAt(point);
    DSCell cell;
    if (cells != null)
      if (!cells.isEmpty()) {
        cell = cells.getFirst();
        for (DSCell cell2 : cells) {
          if (cell2.getType() > cell.getType()) cell = cell2;
        }
        return (cell);
      }
    return (null);
  }

  public int getRevisitAt(Point point) {
    return revisitedTime.getOrDefault(point, 0);
  }

  public synchronized DSCell getKeyType(Point point, int type) {
    LinkedList<DSCell> cellsAtPoint = getAllAt(point);
    if (cellsAtPoint == null) return null;

    return cellsAtPoint.stream().filter(c -> c.getType() == type).findFirst().orElse(null);
  }

  public synchronized LinkedList<DSCell> getAllType(
      int type) { // objekty daneho typu na vsech pozicich
    // Hash verze

    LinkedList<DSCell> cells = new LinkedList();
    LinkedList<DSCell> cellsAt = new LinkedList();
    for (Point point : PHashCells.keySet()) {
      cellsAt = PHashCells.get(point);
      for (DSCell element : cellsAt) if (element.getType() == type) cells.add(element);
    }
    return (cells);
  }

  public synchronized boolean containsKey(Point point) {
    return (PHashCells.containsKey(point));
  }

  public DSCells() {
    PHashCells = new HashMap<Point, LinkedList<DSCell>>();
  }
}

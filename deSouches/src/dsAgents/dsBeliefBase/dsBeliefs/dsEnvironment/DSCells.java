package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class DSCells {
  // LinkedList<DSCell> PCells;
  // new map hash point -> list of cells
  HashMap<Point, LinkedList<DSCell>> PHashCells;

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
  protected synchronized Point getTLC() {
    // Hash

    Point tlc = PHashCells.keySet().iterator().next();
    tlc = new Point(tlc.x, tlc.y);
    for (Point point : PHashCells.keySet()) {
      if (point.x <= tlc.x) tlc.x = point.x;
      if (point.y <= tlc.y) tlc.y = point.y;
    }

    /*
    if (PCells.isEmpty())`
            return null;
    Point tlc =PCells.getFirst().getPosition();
    for(DSCell cell:PCells) {
        if (cell.getX() <= tlc.x)
            tlc.x=cell.getX();
        if (cell.getY() <= tlc.y)
            tlc.y=cell.getY();
    }*/
    return (tlc);
  }
  // bottom right corner
  protected synchronized Point getBRC() {

    // Hash

    Point brc = PHashCells.keySet().iterator().next();
    brc = new Point(brc.x, brc.y);
    for (Point point : PHashCells.keySet()) {
      if (point.x >= brc.x) brc.x = point.x;
      if (point.y >= brc.y) brc.y = point.y;
    }

    return (brc);

    /*

    if (PCells.isEmpty())
        return null;
    Point brc = PCells.getFirst().getPosition();
    for (DSCell cell : PCells){
        if (cell.getX() >= brc.x)
            brc.x = cell.getX();
    if (cell.getY() >= brc.y)
        brc.y = cell.getY();
    }
    return(brc);
     */
  }

  protected synchronized void put(DSCell element) {

    // hash verze

    if (PHashCells.containsKey(element.getPosition())) {
      LinkedList<DSCell> cells = PHashCells.get(element.getPosition());
      for (DSCell cell : cells)
        if (cell.getType() == element.getType()) // not two of the same type at one place
        return;
      cells.add(element);
    } else {
      LinkedList<DSCell> cells = new LinkedList();
      cells.add(element);
      PHashCells.put(element.getPosition(), cells);
    }
  }

  protected synchronized void removeOlder(Point point, int step, boolean removeArea) {
    // new Hash

    LinkedList<DSCell> oldList = PHashCells.get(point);
    LinkedList<DSCell> newList = new LinkedList();

    DSCell tsCell =
        new DSCell(
            point.x, point.y, "clear", "",
            step); // every place once visited contains this with timestamp
    if (oldList != null) {
      for (DSCell element : oldList)
        if ((element.getTimestamp() > step)
            || (((element.getType() == DSCell.__DSGoal)
                || (element.getType() == DSCell.__DSRoleArea)))) { // && (!removeArea)))
          //                  if ((element.getType() == DSCell.__DSGoal) || (element.getType() ==
          // DSCell.__DSRoleArea))
          //                      System.out.println("Zachranuji areu na "+element.getPosition());
          newList.add(element);
        }
      newList.add(tsCell);
    } else {
      newList = new LinkedList();
      newList.add(tsCell);
    }
    //      System.out.println("Po clearu zbylo "+newList.size());
    PHashCells.put(point, newList);
  }

  public synchronized void removeCell(int x, int y, int type) {
    // new Hash

    Point point = new Point(x, y);
    LinkedList<DSCell> oldList = PHashCells.get(point);
    LinkedList<DSCell> newList = new LinkedList<DSCell>();

    for (DSCell element : oldList) if (element.getType() != type) newList.add(element);
    PHashCells.put(point, newList);
  }

  public synchronized LinkedList<DSCell> getAllAt(Point point) {

    // Hash version

    if (PHashCells.containsKey(point)) return ((LinkedList<DSCell>) PHashCells.get(point).clone());
    else return (null);

    /*
    LinkedList<DSCell> cells=new LinkedList<DSCell>();
    for(DSCell element:PCells)
        if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y))
            cells.add(element);
    return(cells);
     */
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

  public synchronized DSCell getKeyType(Point point, int type) {

    // Hash version

    if (!PHashCells.containsKey(point)) return (null);
    LinkedList<DSCell> cellsAtPos = PHashCells.get(point);
    for (DSCell element : cellsAtPos) if (element.getType() == type) return (element);
    return (null);

    /* old version
    for(DSCell element:PCells)
        if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y)&&(element.getType()==type))
            return(element);
    return(null);
    */
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
    // Hash version
    return (PHashCells.containsKey(point));

    /* Old version
    for(DSCell element:PCells)
        if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y))
            return(true);

        return(false);
     */
  }

  public DSCells() {
    // hash version
    PHashCells = new HashMap<Point, LinkedList<DSCell>>();
    // old version
    //   PCells=new LinkedList<DSCell>();
  }
}

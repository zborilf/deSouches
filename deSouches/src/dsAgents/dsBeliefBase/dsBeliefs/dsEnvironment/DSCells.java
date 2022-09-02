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

  public synchronized void newWidth(int width){
    HashMap<Point, LinkedList<DSCell>> nHashCells=new HashMap();
    for(Point p:PHashCells.keySet())
      if((p.x>=0)&&(p.x<width))
        nHashCells.put(p,PHashCells.get(p));
    PHashCells=nHashCells;
  }


  public synchronized void newHeight(int height){
    HashMap<Point, LinkedList<DSCell>> nHashCells=new HashMap();
    for(Point p:PHashCells.keySet())
      if((p.y>=0)&&(p.y<height))
        nHashCells.put(p,PHashCells.get(p));
      PHashCells=nHashCells;
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

  protected synchronized void removeOlder(Point point, int step) {
    LinkedList<DSCell> oldList = PHashCells.get(point);
    LinkedList<DSCell> newList = new LinkedList();

    if (oldList != null) {
      for (DSCell element : oldList)
        if ((element.getTimestamp() > step)
      //      || (((element.getType() == DSCell.__DSGoalArea)
                || (element.getType() == DSCell.__DSRoleArea)) {
          newList.add(element);
        }
    }
    PHashCells.put(point, newList);
  }

  public synchronized void removeCell(int x, int y, int type) {
    Point point = new Point(x, y);
    LinkedList<DSCell> oldList = getAllAt(point);
    LinkedList<DSCell> newList = new LinkedList<>();
    if (oldList == null) {
      return;
    }

    for (DSCell element : oldList) if (element.getType() != type) newList.add(element);
    PHashCells.put(point, newList);
  }

  public synchronized LinkedList<DSCell> getAllAt(Point position) {
    if (PHashCells.containsKey(position)) return ((LinkedList<DSCell>) PHashCells.get(position).clone());
    else return (null);
  }

  public boolean isObjectAt(int type, Point position){
    if (PHashCells.containsKey(position)){
      LinkedList<DSCell> cells=(LinkedList<DSCell>) PHashCells.get(position).clone();
      for(DSCell cell:cells)
        if(cell.getType()==type)
          return(true);
    }
    return(false);
  }

  public boolean isObjectAround(int type, Point position){
    if((isObjectAt(type,new Point(position.x+1, position.y)))||
            (isObjectAt(type,new Point(position.x-1, position.y)))||
            (isObjectAt(type,new Point(position.x, position.y+1)))||
            (isObjectAt(type,new Point(position.x, position.y-1))))
      return(true);
    return(false);
  }

  public boolean rightHandPriority(Point position){
    if(isObjectAt(DSCell.__DSAgent,new Point(position.x, position.y-1)))
      return(true);
    if(isObjectAt(DSCell.__DSEntity_Friend,new Point(position.x, position.y-1)))
      return(false);
    if(isObjectAt(DSCell.__DSAgent,new Point(position.x+1, position.y)))
      return(true);
    if(isObjectAt(DSCell.__DSEntity_Friend,new Point(position.x+1, position.y)))
      return(false);
    if(isObjectAt(DSCell.__DSAgent,new Point(position.x, position.y+1)))
      return(true);
    if(isObjectAt(DSCell.__DSEntity_Friend,new Point(position.x, position.y+1)))
      return(false);
    if(isObjectAt(DSCell.__DSAgent,new Point(position.x-1, position.y)))
      return(true);
    if(isObjectAt(DSCell.__DSEntity_Friend,new Point(position.x-1, position.y)))
      return(false);
    return(false);
  }

  public boolean isDestructibleAt(Point point){
    if((LinkedList<DSCell>)getAllAt(point)==null)
      return(false);
    LinkedList<DSCell> cells=(LinkedList<DSCell>)getAllAt(point).clone();
    if(cells!=null)
      for(DSCell c: cells){
        if((c.getType()==DSCell.__DSObstacle)||
                ((c.getType()>=DSCell.__DSBlock)&&(c.getType()<=DSCell.__DSBlock+20)))
         return(true);
      }
    return(false);
  }

  public synchronized boolean isEnemyAt(Point point){
    if((LinkedList<DSCell>)getAllAt(point)==null)
      return(false);
    LinkedList<DSCell> cells=(LinkedList<DSCell>)getAllAt(point).clone();
    if(cells!=null)
      for(DSCell c: cells){
        if(c.getType()==DSCell.__DSEntity_Enemy)
          return(true);
      }
    return(false);
  }


  public LinkedList<DSCell> getAttackables(){
    LinkedList<DSCell> cells=getCells();
    LinkedList<DSCell> attackables=new LinkedList<DSCell>();
    if(cells!=null)
      for(DSCell cell:cells){
        if((cell.getType()==DSCell.__DSEntity_Enemy)||
                (cell.getType()==DSCell.__DSObstacle)||
                ((cell.getType()>=DSCell.__DSBlock)&&(cell.getType()<=DSCell.__DSBlock+20)&&
                        !isObjectAround(DSCell.__DSEntity_Friend,cell.getPosition())))
        attackables.add(cell);
    }
    return(attackables);
  }

  public synchronized DSCell getNewestAt(Point point) {
    // get newest of cells according to timestamp + synchronize pheromone

    LinkedList<DSCell> cellsAtPoint = this.getAllAt(point);

    if (cellsAtPoint == null || cellsAtPoint.isEmpty()) {
      return null;}

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

    LinkedList<DSCell> cells = new LinkedList();
    for (Point point : PHashCells.keySet()) {
      LinkedList<DSCell> cellsAt = PHashCells.get(point);
      for (DSCell element : cellsAt) if (element.getType() == type) cells.add(element);
    }
    return (cells);
  }


  protected synchronized LinkedList<Point> getAllTypePositions(
          int type) { // objekty daneho typu na vsech pozicich

    LinkedList<Point> cellPos = new LinkedList();
    for (Point position : PHashCells.keySet()) {
      LinkedList<DSCell> cellsAt = PHashCells.get(position);
      for (DSCell element : cellsAt) if (element.getType() == type) cellPos.add(position);
    }
    return (cellPos);
  }


  public synchronized boolean containsKey(Point point) {
    return (PHashCells.containsKey(point));
  }

  public DSCells() {
    PHashCells = new HashMap<Point, LinkedList<DSCell>>();
  }
}

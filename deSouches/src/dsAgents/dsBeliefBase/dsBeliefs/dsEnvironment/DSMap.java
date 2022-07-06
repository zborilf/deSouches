/*

* Mapa je m��ka s neur�it�mi rozm�ry. Je implementov�na jako seznam pozorovan�ch pozic a jejich naposledy zji�t�n�ch stav�.
* Mapa je p�i�azena jednomu nebo v�ce agent�m. Na po��tku m� ka�d� agent svoji mapu a koordin�ty jsou po��t�ny od po��te�n� pozice
* ka�d�ho agenta. V p��pad� �e se agenti potkaj�, slou�� sv� mapy, koordin�ty jsou (*n�jak*) p�epo��t�ny a agenti tuto mapu pak sd�l�.
* Do mapy si agent zna�� pozorov�n� s �asov�m raz�tkem po��zen� pozorov�n�. Aktu�ln� jsou takov� stavy jednotliv�ch pozic v map�

*/

package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import dsAgents.DSAgent;
import dsAgents.dsPerceptionModule.DSPerceptor;
import java.awt.*;
import java.util.*;

public class DSMap {

  private static final String TAG = "DSMap";

  DSCells PMapCells;
  int PWidthMap = 0;
  int PHeightMap = 0;
  HashMap<DSAgent, Point> PAgentPosition;
  private boolean PMasterMap = false;

  DSAgent PAgent;

  static final int _maxDistance = 5000;

  int PY, PX, PXMin, PXMax, PYMin, PYMax;
  // private Border border = new Border();

  int centralizeXCoords(int x) {
    if (PWidthMap == 0) return (x);
    while (x < 0) x += PWidthMap;
    x = x % PWidthMap;
    return (x);
  }

  int centralizeYCoords(int y) {
    if (PHeightMap == 0) return (y);
    while (y < 0) y += PHeightMap;
    y = y % PHeightMap;
    return (y);
  }

  void updateXYMinMax(int x, int y) {
    if (x > PXMax) PXMax = x;
    if (x < PXMin) PXMin = x;
    if (y > PYMax) PYMax = y;
    if (y < PYMin) PYMin = y;
  }

  public void setMasterMap() {
    PMasterMap = true;
    PWidthMap = 70; // provizorka
    PHeightMap = 70; // provizorka
  }

  public boolean isMasterMap() {
    return (PMasterMap);
  }

  public DSCells getMap() {
    return (PMapCells);
  }

  public Point getOwnerAgentPos() { // Master
    return (new Point(PX, PY));
  }

  public Point getAgentPos(DSAgent agent) {
    return (PAgentPosition.get(agent));
  }

  public void setAgentPos(DSAgent agent, Point position) {
    PAgentPosition.put(agent, position);
  }

  public String getOwnerName() {
    return (PAgent.getAgentName());
  }

  public DSAgent getOwner() {
    return PAgent;
  }

  public static int distance(Point a, Point b) {
    if ((a == null) || (b == null)) return (_maxDistance);
    return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y)); // Manhattan
    // TODO:l prekroceni mapy
  }

  public static boolean closer(Point a, Point b, Point c) {
    return (distance(a, b) < distance(a, c));
  }

  /*
         MAKING ZONES
  */

  static LinkedList<Point> get8Neighbours(Point cell, LinkedList<Point> cells) {
    LinkedList<Point> neighbours = new LinkedList<Point>();
    int x = cell.x;
    int y = cell.y;
    for (Point cell2 : cells)
      if ((Math.abs(x - cell2.getX()) <= 1) && (Math.abs(y - cell2.getY()) <= 1))
        neighbours.add(cell2);
    return (neighbours);
  }

  public LinkedList<Point> getNeighboursExactDistance(Point cell, int r) {
    LinkedList<Point> neighbours = new LinkedList<Point>();
    for (int x = cell.x - r; x <= cell.x + r; x++) {
      for (int y = cell.y - r; y <= cell.y + r; y++) {
        // todo:l prekroceni mapy
        Point p = new Point(x, y);
        if (distance(p, cell) == r) neighbours.add(p);
      }
    }
    return (neighbours);
  }

  static LinkedList<Point> getZone(LinkedList<Point> cells) {
    /*
           gets contiguous are with the first cell of the list (and removes it form the list)
    */
    if (cells.isEmpty()) return (cells);
    Point cell;
    LinkedList<Point> open = new LinkedList<Point>();
    LinkedList<Point> closed = new LinkedList<Point>();
    open.add(cells.getFirst());
    while (!open.isEmpty()) {
      cell = open.getFirst();
      open.removeFirst();
      closed.add(cell);
      open.addAll(get8Neighbours(cell, cells));
      for (Point cell2 : open) cells.remove(cell2);
    }
    return (closed);
  }

  public static LinkedList<LinkedList<Point>> getPointsZones(LinkedList<Point> cells) {
    /*
        Divides cells into contiguous areas
    */
    LinkedList<LinkedList<Point>> zones = new LinkedList<LinkedList<Point>>();
    LinkedList<Point> zone = new LinkedList<Point>();
    while (!cells.isEmpty()) {
      zone = getZone(cells);
      for (Point cell2 : zone) cells.remove(cell2);
      zones.add(zone);
    }
    return (zones);
  }

  public synchronized void mergeMaps(DSMap map, Point displacement) {
    // 2022 version
    DSCell newCell;
    LinkedList<DSCell> mapClone = (LinkedList<DSCell>) map.getMap().getCells().clone();
    for (DSCell cell : mapClone) {
      newCell =
          new DSCell(
              centralizeXCoords(cell.getX() + displacement.x),
              centralizeYCoords(cell.getY() + displacement.y),
              cell.getType(),
              cell.getTimestamp(),
              cell.getFoundBy());
      PMapCells.put(newCell);
    }
    return;
  }

  public synchronized void shiftMap(Point displacement) {
    for (DSCell cell : PMapCells.getCells()) {
      cell.setX(centralizeXCoords(cell.getX() + displacement.x));
      cell.setY(centralizeYCoords(cell.getY() + displacement.y));
    }
    for (DSAgent agent : PAgentPosition.keySet()) {
      Point newPosition =
          new Point(
              centralizeXCoords(PAgentPosition.get(agent).x + displacement.x),
              centralizeYCoords(PAgentPosition.get(agent).y + displacement.y));
      PAgentPosition.put(agent, newPosition);
    }
  }

  public static synchronized boolean areasConflict(Point p1, DSBody b1, Point p2, DSBody b2) {
    for (DSCell cell1 : b1.getBodyList())
      for (DSCell cell2 : b2.getBodyList())
        if ((cell1.getX() + p1.x == cell2.getX() + p2.x)
            && (cell1.getX() + p1.y == cell2.getX() + p2.y)) return (true);
    return (false);
  }

  boolean isFriendAt(Point position) {
    HashMap<DSAgent, Point> agentPositions = (HashMap<DSAgent, Point>) (PAgentPosition.clone());
    Set<DSAgent> agents = agentPositions.keySet();
    for (DSAgent agent : agents) {
      Point agentPos = (Point) agent.getMapPosition().clone();
      if ((position.x == agentPos.x) && (position.y == agentPos.y)) return (true);
    }
    return (false);
  }

  boolean isAgentBody(Point position, DSBody body) {
    int Tx = centralizeXCoords(position.x - PX);
    int Ty = centralizeYCoords(position.y - PY);
    return (body.pointInBody(body, new Point(Tx, Ty)));
  }

  public boolean isObstacleAt(Point position, DSBody agentbody, DSBody body, int step) {
    for (DSCell bodyItem : body.getBodyList()) {
      DSCell node =
          PMapCells.getKeyType(
              new Point(position.x + bodyItem.getX(), position.y + bodyItem.getY()),
              DSCell.__DSObstacle);
      if (node != null) {
        if (isFriendAt(node.getPosition())) return (true);
        if (isAgentBody(node.getPosition(), agentbody))
          // prekazka je ve skutecnosti agent sam
          return (false);

        if ((node.getType() == DSCell.__DSObstacle)) return (true);
        // Pratele nejsou na mape, ale zname jejich presne aktualni pozice

        else // TODO to dole omezit casove souc_cas-timestamp (cca 5 kroku)
        if ((step - node.getTimestamp()) < 5)
          if ((node.getType() == DSCell.__DSEntity_Enemy)
              || (node.getType() == DSCell.__DSEntity_Friend)
              || ((node.getType() >= DSCell.__DSBlock) && (node.getType() < DSCell.__DSDispenser)))
            return (true);
      }
    }
    return (false);
  }

  boolean isOjectAt(Point position, int objectType) {

    return (!(PMapCells.getKeyType(position, objectType) == null));
  }

  public String oppositeDirection(String dir) {
    if (dir.contentEquals("n")) return ("s");
    if (dir.contentEquals("s")) return ("n");
    if (dir.contentEquals("w")) return ("e");
    if (dir.contentEquals("e")) return ("w");
    return ("");
  }

  public String getFreeNeighbour(Point place, DSAgent agent) {
    if (!isObstacleAt(
        new Point(place.x + 1, place.y), agent.getBody(), agent.getBody(), agent.getStep()))
      return ("e");
    else if (!isObstacleAt(
        new Point(place.x - 1, place.y), agent.getBody(), agent.getBody(), agent.getStep()))
      return ("w");
    else if (!isObstacleAt(
        new Point(place.x, place.y + 1), agent.getBody(), agent.getBody(), agent.getStep()))
      return ("s");
    else if (!isObstacleAt(
        new Point(place.x, place.y - 1), agent.getBody(), agent.getBody(), agent.getStep()))
      return ("n");
    return ("");
  }

  public Point getFreeNeighbourPosition(Point place, DSAgent agent) {
    Point direction = DSPerceptor.getPositionFromDirection(getFreeNeighbour(place, agent));
    if (direction == null) return (null);
    return (new Point(place.x + direction.x, place.y + direction.y));
  }

  public LinkedList<Point> allObjects(int type) {
    LinkedList<DSCell> objects = PMapCells.getAllType(type);
    LinkedList<Point> objectPositions = new LinkedList<Point>();
    if (objects == null) return (objectPositions);
    objects = (LinkedList<DSCell>) objects.clone();
    for (DSCell cell : objects) {
      objectPositions.add(cell.getPosition());
    }
    return (objectPositions);
  }

  public synchronized Point objectAroundCell(
      Point position, int objectType) { // TODO tohle zobecnit na body

    LinkedList<DSCell> cells = new LinkedList<DSCell>();

    if (PMapCells.getKeyType(
            new Point((int) position.getX() + 1, (int) position.getY()), objectType)
        != null) return (new Point(1, 0));
    if (PMapCells.getKeyType(
            new Point((int) position.getX() - 1, (int) position.getY()), objectType)
        != null) return (new Point(-1, 0));
    if (PMapCells.getKeyType(
            new Point((int) position.getX(), (int) position.getY() + 1), objectType)
        != null) return (new Point(0, 1));
    if (PMapCells.getKeyType(
            new Point((int) position.getX(), (int) position.getY() - 1), objectType)
        != null) return (new Point(0, -1));

    return (null);
  }

  class sortByDistance implements Comparator<Point> {
    Point referencePoint;

    public int compare(Point a, Point b) {
      if (distance(referencePoint, a) == distance(referencePoint, b)) return (0);
      if (closer(referencePoint, a, b)) return (-1);
      else return (1);
    }

    public sortByDistance(Point rp) {
      referencePoint = rp;
    }
  }

  LinkedList<Point> objectsSortedByDistance(int type, Point position) {
    LinkedList<Point> objects = allObjects(type);
    if (objects == null) return (null);
    objects.sort(new sortByDistance(position));
    return (objects);
  }

  public Point nearestFreeBlock(
      int blockType, Point position) { // nejlbizsi blok od pozice nesousedici s agentem F/E
    LinkedList<Point> blocksSorted =
        objectsSortedByDistance(DSCell.__DSBlock + blockType, position);
    if (blocksSorted == null) return (null);
    for (Point position2 : blocksSorted)
      if (objectAroundCell(position2, DSCell.__DSEntity_Enemy) == null)
        if (objectAroundCell(position2, DSCell.__DSEntity_Friend) == null) return (position2);
    return (null);
  }

  public Point nearestObject(int type, Point agentPosition) {

    LinkedList<DSCell> objects = PMapCells.getAllType(type);

    if (objects == null) return (null);
    if (objects.size() == 0) return (null);

    LinkedList<DSCell> objects2 = (LinkedList<DSCell>) objects.clone();

    Point nearest = objects.getFirst().getPosition();

    for (DSCell cell : objects2) {
      if (closer(agentPosition, cell.getPosition(), nearest)) nearest = cell.getPosition();
    }
    return (nearest);
  }

  public synchronized void addCell(DSCell cell) {
    PMapCells.put(cell);
  }

  public synchronized boolean updateCell(DSCell cell) {
    int x = cell.getX();
    int y = cell.getY();
    cell.setX(centralizeXCoords(cell.getX()));
    cell.setY(centralizeYCoords(cell.getY()));

    PMapCells.put(cell);
    return (true);
  }

  public void shiftMapBy(Point dif) {}

  String nmb(int i) {
    if (Math.abs(i) < 10) return (" 0" + Math.abs(i));
    else return (" " + Math.abs(i));
  }

  public synchronized DSCell[][] map2Array(Point tlc, Point brc) {
    int lx = tlc.x;
    int ty = tlc.y;
    int rx = brc.x;
    int by = brc.y;
    int width = rx - lx + 1;
    int height = by - ty + 1;

    DSCell[][] mapArray = new DSCell[width][height];

    for (DSCell cell : PMapCells.getCells()) {
      if (mapArray[cell.getX() - lx][cell.getY() - ty] == null)
        mapArray[cell.getX() - lx][cell.getY() - ty] = cell;
      if (mapArray[cell.getX() - lx][cell.getY() - ty].getType() < cell.getType())
        mapArray[cell.getX() - lx][cell.getY() - ty] = cell;
    }

    /*     System.out.println("role areas:");
    for(DSCell cell:PMapCells.getAllType(DSCell.__DSRoleArea))
        System.out.print(cell.getPosition()+" / ");
    System.out.println();*/

    return (mapArray);
  }

  /*

      synchronized public String stringMap(){
          DSCell node;
          DSCell[][] mapArray;
          String so="";

          for(DSAgent agent: PAgentPosition.keySet())
              so=so+agent.getEntityName()+", ";
          so=so+"\n";

          Point tlc= PMapCells.getTLC();     // top left corner
          Point brc= PMapCells.getBRC();     // bottom right corner

          int lx=tlc.x;
          int ty=tlc.y;
          int width=brc.x-lx+1;
          int height=brc.y-ty+1;

          for(int i=0;i<width;i++)
              so=so+nmb(i+lx);
          so=so+" \n";

          for(int j=0;j<height;j++) {
              for (int i = 0; i < width; i++) {
                  node = PMapCells.getOneAt(new Point(i,j));
                  if (node != null) {
                      if ((j == getOwnerAgentPos().y) && (i == getOwnerAgentPos().x)) {
                      } else {
                          so = so + DSCell.getTypeSign(node.getType());
                      }
                  } else {
                      so = so + " ..";
                  }
              }
              so = so + nmb(j+ty) + " \n";
          }

          return(so);
      }

  */

  public synchronized String stringMap() {
    DSCell node;
    DSCell[][] mapArray;
    String so = "";

    for (DSAgent agent : PAgentPosition.keySet()) so = so + agent.getEntityName() + ", ";
    so = so + "\n";

    Point tlc = PMapCells.getTLC(); // top left corner
    Point brc = PMapCells.getBRC(); // bottom right corner

    mapArray = map2Array(tlc, brc);

    int lx = tlc.x;
    int ty = tlc.y;
    int width = brc.x - lx + 1;
    int height = brc.y - ty + 1;

    for (int i = 0; i < width; i++) so = so + nmb(i + lx);
    so = so + " \n";

    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        node = mapArray[i][j];
        if (node != null) {
          if ((j == getOwnerAgentPos().y) && (i == getOwnerAgentPos().x)) {
          } else {
            so = so + DSCell.getTypeSign(node.getType());
          }
        } else {
          so = so + " ..";
        }
      }
      so = so + nmb(j + ty) + " \n";
    }

    return (so);
  }

  public synchronized String stringPheroMap() {
    DSCell node;
    DSCell[][] mapArray;
    final String MIN = "00";
    StringBuilder so = new StringBuilder();
    int curStep = 0;

    for (DSAgent agent : PAgentPosition.keySet()) {
      so.append(agent.getEntityName()).append(", ");
      curStep = Math.max(curStep, agent.getStep());
    }
    so.append("\n");

    Point tlc = PMapCells.getTLC(); // top left corner
    Point brc = PMapCells.getBRC(); // bottom right corner

    mapArray = map2Array(tlc, brc);

    int lx = tlc.x;
    int ty = tlc.y;
    int width = brc.x - lx + 1;
    int height = brc.y - ty + 1;

    for (int i = 0; i < width; i++) so.append(nmb(i + lx));
    so.append(" \n");

    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        so.append(" ");
        node = mapArray[i][j];
        if (node != null) {
          if (node.getType() == DSCell.__DSEntity_Friend) {
            so.append("**");
          } else {
            int phero = (int) node.getPheromone();
            String hex = Integer.toHexString(phero).toUpperCase();
            hex = hex.length() == 1 ? '0' + hex : hex;
            if (phero >= 255 || phero < 0) System.err.println(" ERROR: PHEROMONE OVERFLOW");
            so.append(hex);
          }
        } else {
          so.append(MIN);
        }
      }
      so.append("  ").append(nmb(j + ty)).append(" \n");
    }

    return (so.toString());
  }

  public void setWidth(int width) {
    PWidthMap = width;
  }

  public void setHeight(int height) {
    PWidthMap = height;
  }

  public boolean moveBy(DSAgent agent, int x, int y) {
    PX = centralizeXCoords(PAgentPosition.get(agent).x + x);
    PY = centralizeYCoords(PAgentPosition.get(agent).y + y);
    // PX=PX+x;
    // PY=PY+y;
    // updateXYMinMax(PX,PY);
    PAgentPosition.put(agent, new Point(PX, PY));
    return (true);
  }

  public void addAgent(DSAgent agent, Point displacement) {
    Point pos =
        new Point(
            (int) (displacement.x + agent.getMapPosition().getX()),
            (int) (displacement.y + agent.getMapPosition().getY()));
    PAgentPosition.put(agent, pos);
  }

  public void removeAgent(DSAgent agent) {
    PAgentPosition.remove(agent);
  }

  public void addNewAgent(DSAgent agent, Point position) {
    PAgentPosition.put(agent, position);
  }

  public DSMap(DSAgent agent) {
    PMapCells = new DSCells();
    PX = 0;
    PY = 0;
    PXMin = 0;
    PXMax = 0;
    PYMin = 0;
    PYMax = 0;
    PAgent = agent;
    PAgentPosition = new HashMap<DSAgent, Point>();
    PAgentPosition.put(agent, new Point(0, 0));
  }
}

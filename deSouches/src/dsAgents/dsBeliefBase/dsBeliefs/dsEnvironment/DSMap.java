/*

* Mapa je m��ka s neur�it�mi rozm�ry. Je implementov�na jako seznam pozorovan�ch pozic a jejich naposledy zji�t�n�ch stav�.
* Mapa je p�i�azena jednomu nebo v�ce agent�m. Na po��tku m� ka�d� agent svoji mapu a koordin�ty jsou po��t�ny od po��te�n� pozice
* ka�d�ho agenta. V p��pad� �e se agenti potkaj�, slou�� sv� mapy, koordin�ty jsou (*n�jak*) p�epo��t�ny a agenti tuto mapu pak sd�l�.
* Do mapy si agent zna�� pozorov�n� s �asov�m raz�tkem po��zen� pozorov�n�. Aktu�ln� jsou takov� stavy jednotliv�ch pozic v map�

*/

package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import dsAgents.DSAgent;
import dsAgents.DSConfig;

import java.awt.*;
import java.util.*;
import java.util.List;
;

public class DSMap {

  private static final String TAG = "DSMap";

  DSCells PMapCells;
  int PWidthMap = 0;
  int PHeightMap = 0;
  HashMap<DSAgent, Point> PAgentPosition;
  private boolean PMasterMap = false;
  HashMap<Point, Integer> PEnemiesAttacked;

  DSAgent PAgent;

  static final int _maxDistance = 5000;

  int PY, PX, PXMin, PXMax, PYMin, PYMax;
  // private Border border = new Border();


  public static String pointList(LinkedList<Point> points){
    String st="";
    for(Point p:points)
      st=st+"["+p.x+","+p.y+"] ";
    return(st);
  }

  public synchronized int centralizeXCoords(int x) {
    if (PWidthMap == 0) return (x);
    /*while (x < 0) x += PWidthMap;
    x = x % PWidthMap;*.
     */
    x=Math.floorMod(x,PWidthMap);
    return (x);

  }

  public synchronized int centralizeYCoords(int y) {
    if (PHeightMap == 0) return (y);
    y=Math.floorMod(y,PHeightMap);
    return (y);
  }


  public synchronized Point centralizeCoords(Point p){
    return(new Point(centralizeXCoords(p.x),centralizeYCoords(p.y)));
  }

  public void setMasterMap() {
    PMasterMap = true;
  }

  public boolean isMasterMap() {
    return (PMasterMap);
  }

  public DSCells getMapCells() {
    return (PMapCells);
  }

  public Point getOwnerAgentPos() { // Master
    return (new Point(PX, PY));
  }

  public Point getAgentPos(DSAgent agent) {
    return (centralizeCoords(PAgentPosition.get(agent)));
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

  public int distance(Point a, Point b) {
    if ((a == null) || (b == null)) return (_maxDistance);
    int dx,dy;
    if (PWidthMap==0)
        dx=Math.abs(a.x - b.x);
      else
        dx=(Math.min(Math.abs(a.x - b.x), PWidthMap- Math.abs(a.x - b.x)));  // on map or over the map border?
    if (PHeightMap==0)
        dy=Math.abs(a.y - b.y);
      else
        dy=(Math.min(Math.abs(a.y - b.y), PHeightMap- Math.abs(a.y - b.y)));
    return (dx+dy); // Manhattan
  }

  public Point shiftPosition(DSAgent agent, Point distance){
    // should reflect map size
    return(centralizeCoords(new Point(agent.getMapPosition().x+distance.x,agent.getMapPosition().y+distance.y)));
  }

  public boolean closer(Point a, Point b, Point c) {
    return (distance(a, b) < distance(a, c));
  }

  /*
         MAKING ZONES
  */

  // TODO, lepe normalizovat
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
        Point p = centralizeCoords(new Point(x, y));
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

  Point getGoalPositionHeuristic(LinkedList<Point> zone){
    for(Point z:zone){
      boolean good=true;
      for(int i=0; i<3; i++)
        for(int j=0; j<3; j++)
          if(isEnemyAt(new Point(z.x+i,z.y+j)))
            good=false;
          if(good)
            return(z);
    }
    return(null);
  }

  public synchronized LinkedList<Point> getPointsZones(LinkedList<Point> cells) {
    /*
        Divides cells into contiguous areas
    */
    LinkedList<Point> zones = new LinkedList<Point>();
    LinkedList<Point> zone;
    while (!cells.isEmpty()) {
      zone = getZone(cells);
      for (Point cell2 : zone) cells.remove(cell2);
      zones.add(getGoalPositionHeuristic(zone));     // TODO misto getFirst heuristicky ten bod v zone,ktery je nejlepsi pro odevzdani tasku
    }
    return (zones);
  }

  public synchronized void positionCleared(Point position, Integer step){
    // record of enemies attacked
    if(PMapCells.isEnemyAt(position))
            PEnemiesAttacked.put(position,step);
  }

  public LinkedList<Point> getRecentlyAttackedPositions(int fromStep){
    LinkedList<Point> positions=new LinkedList<Point>();
    for(Point position:PEnemiesAttacked.keySet())
      if(PEnemiesAttacked.get(position)>fromStep)
        positions.add(position);
      return(positions);
  }

  public synchronized void mergeMaps(DSMap map, Point displacement) {
    // 2022 version
    DSCell newCell;
    LinkedList<DSCell> mapClone = (LinkedList<DSCell>) map.getMapCells().getCells().clone();
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

  boolean isEnemyAt(Point position){
    LinkedList<DSCell> cells=PMapCells.getAllAt(position);
    if(cells==null)
      return(false);
    for(DSCell cell:cells)
      if(cell!=null)
      if(cell.getType()==DSCell.__DSEntity_Enemy)
        return(true);
      return(false);
  }

  boolean isAgentBody(Point position, DSBody body) {
    return (body.pointInBody(body, position));
  }


  public synchronized boolean isAgentBodyAt(Point position, DSAgent agent){
    Point relposition=new Point(position.x-agent.getMapPosition().x, position.y-agent.getMapPosition().y);
    return(agent.getBody().isCellAt(relposition));
  }


  public boolean isObstacleAt(Point position, DSBody agentbody, DSBody body, int step) {
    for (DSCell bodyItem : body.getBodyList()) {

      // for all positions where (possibly simulated) body is

      Point nodePosition = new Point(position.x + bodyItem.getX(), position.y + bodyItem.getY());
      if (!isAgentBody(nodePosition, agentbody)) {
        // the agent is not here, check possible unmovable elements
        LinkedList<DSCell> cells = PMapCells.getAllAt(nodePosition);
        if(cells!=null)
          for (DSCell cell : cells)
            if (cell.isUnmovable())
            return (true);
      }
    }
    return(false);
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




  public LinkedList<Point> getTypePositions(int type) {
    LinkedList<DSCell> objects = PMapCells.getAllType(type);
    LinkedList<Point> objectPositions = new LinkedList<Point>();
    if (objects == null) return (objectPositions);
    objects = (LinkedList<DSCell>) objects.clone();
    for (DSCell cell : objects) {
      objectPositions.add(centralizeCoords(cell.getPosition()));
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
    LinkedList<Point> objects = getTypePositions(type);
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

  public synchronized void removeOlder(Point position, int timestamp) {
    PMapCells.removeOlder(position, timestamp);
  }

  public synchronized void addCell(DSCell cell) {
    PMapCells.put(cell);
  }

  public int directionVertical(int x1, int x2){
    if(x1==x2)
      return(0);

    if(PWidthMap==0) {
      if (x1 > x2)
        return (-1);
      else
        return (1);
    }else
      if(((x1 > x2)&&(x1-x2)<(PWidthMap/2))||
              ((x2>x1)&&((x2-x1)>(PWidthMap/2))))
        return(-1);
      else
        return(1);
  }


  public int directionHorizontal(int y1, int y2){
    if(y1==y2)
      return(0);

    if(PHeightMap==0) {
      if (y1 > y2)
        return (-1);
      else
        return (1);
    }else
    if(((y1 > y2)&&(y1-y2<(PHeightMap/2)))||
            ((y2>y1)&&((y2-y1)>(PHeightMap/2))))
      return(-1);
    else
      return(1);
  }


  public synchronized boolean updateCell(DSCell cell) {
    int x = cell.getX();
    int y = cell.getY();
    cell.setX(centralizeXCoords(cell.getX()));
    cell.setY(centralizeYCoords(cell.getY()));

    PMapCells.put(cell);
    return (true);
  }


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

  String printCells(LinkedList<DSCell> cells){
    String s="";
    for(DSCell cell:cells)
      s=s+" ["+cell.getPosition().x+", "+cell.getPosition().y+"] ";
    return(s);
  }

  String point2String(Point p){
    return("["+p.x+","+p.y+"]");
  }

  public synchronized String stringMap() {
    DSCell node;
    DSCell[][] mapArray;
    Point masterPos = this.getOwner().getMapPosition();

    StringBuilder so = new StringBuilder();
    so.append("Width: "+PWidthMap+" Height: "+PHeightMap+"\n");

    for (DSAgent agent : PAgentPosition.keySet())
      so.append(agent.getEntityName()).append(point2String(agent.getMapPosition())).append(", ");

  so.append("\nGoals: ");
  so.append(PMapCells.getAllTypePositions(DSCell.__DSGoalArea));


  //  LinkedList<DSCell> cells;
    so=so.append("\n");
    for(int ii=0;ii<3;ii++) {
      String cs=printCells(PMapCells.getAllType(DSCell.__DSDispenser + ii));
      so = so.append("D"+ii+": "+cs+"\n");
    }
    so.append("\n");

    Point tlc = PMapCells.getTLC(); // top left corner
    Point brc = PMapCells.getBRC(); // bottom right corner
    if(PWidthMap!=0){
      // width is known
      tlc.x=0;
      brc.x=PWidthMap;
    }
    if(PHeightMap!=0){
      // width is known
      tlc.y=0;
      brc.y=PHeightMap;
    }

    mapArray = map2Array(tlc, brc);

    int lx = tlc.x;
    int ty = tlc.y;
    int width = brc.x - lx + 1;
    int height = brc.y - ty + 1;

    so.append("XXX");
    for (int i = 0; i < width; i++) so.append(nmb(i + lx));
    so.append(" \n");

    for (int j = 0; j < height; j++) {
      so.append(nmb(j + ty));
      for (int i = 0; i < width; i++) {
        node = mapArray[i][j];
        if (node != null) {
          if ((j == getOwnerAgentPos().y) && (i == getOwnerAgentPos().x)
              || node.getPosition().equals(masterPos)) {
            so.append(" **");
          } else {
            so.append(DSCell.getTypeSign(node.getType()));
          }
        } else {
          so.append(" ..");
        }
      }
      so.append(" \n");
    }

    return (so.toString());
  }


  public synchronized String stringPheroMap() {
    DSCell node;
    DSCell[][] mapArray;
    final String MIN = "00";
    StringBuilder so = new StringBuilder();
    int curStep = 0;
    Point masterPos = this.getOwner().getMapPosition();

    for (DSAgent agent : PAgentPosition.keySet()) {
      so.append(agent.getEntityName()).append(", ");
      curStep = Math.max(curStep, agent.getStep());
    }
    so.append("\n");

    Point tlc = PMapCells.getTLC(); // top left corner
    Point brc = PMapCells.getBRC(); // bottom right corner
    if(PWidthMap!=0){
      // width is known
      tlc.x=0;
      brc.x=PWidthMap;
    }
    if(PHeightMap!=0){
      // width is known
      tlc.y=0;
      brc.y=PHeightMap;
    }

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
          if (node.getType() == DSCell.__DSEntity_Friend || node.getPosition().equals(masterPos)) {
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

  public boolean sizeEstimated(){
    //return(isMasterMap());
     return((PWidthMap!=0) || (PHeightMap!=0));
  }

  public synchronized boolean setWidth(int width) {
    //return;
    if(width!=PWidthMap)
    if(width> DSConfig.___meaningfulSize) {
      PWidthMap = width;
      for(DSAgent agent:PAgentPosition.keySet())
        moveBy(agent,0,0);
      LinkedList<Point> points=new LinkedList<Point>();
      for(Point point:PMapCells.PHashCells.keySet())
        points.add(point);

      for(Point point:points)
        if((point.x<0)||(point.x>=width))
          PMapCells.PHashCells.remove(point);


     // for(DSCell cell:PMapCells.getCells()) {
     //   if((cell.getX()<0)||(cell.getX()>=width))
     //     PMapCells.PHashCells.remove(cell.getPosition());
        //cell.setX(Math.floorMod(cell.getX(), width));
        //PMapCells.put(cell);
        //PMapCells.newWidth(width);
      return(true);
      }
      return(false);
    }


  public synchronized boolean setHeight(int height) {
  //  return;
    if(height!=PHeightMap)
    if(height> DSConfig.___meaningfulSize) {
      PHeightMap = height;
      for(DSAgent agent:PAgentPosition.keySet())
        moveBy(agent,0,0);
      LinkedList<Point> points=new LinkedList<Point>();
      for(Point point:PMapCells.PHashCells.keySet())
        points.add(point);
      for(Point point:points)
        if((point.y<0)||(point.y>=height))
          PMapCells.PHashCells.remove(point);

/*      for (DSCell cell : PMapCells.getCells()) {
        if((cell.getY()<0)||(cell.getY()>=height))
          PMapCells.PHashCells.remove(cell.getPosition());*/
//        cell.setY(Math.floorMod(cell.getY(), height));
//        PMapCells.put(cell);
//        PMapCells.newHeight(height);
      return(true);
      }
    return(false);
    }



  synchronized public boolean moveBy(DSAgent agent, int x, int y) {

    try{
      agent.getOutput().write("Nekdo hybe agentem "+agent.getAgentName()+" na mape o "+"["+x+","+y+"]\n");
      agent.getOutput().flush();
    }catch (Exception e){};

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
    PEnemiesAttacked=new HashMap<Point, Integer>();
  }
}

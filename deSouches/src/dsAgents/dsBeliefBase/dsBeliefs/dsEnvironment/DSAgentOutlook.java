package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

/*
   2022, actual outlook of an agent (process add/delete lists)

*/

import dsAgents.DSAgent;
import java.awt.*;
import java.util.LinkedList;

public class DSAgentOutlook {
  DSCells POutlookCells;
  int PStep;

  public int getStep() {
    return (PStep);
  }

  public boolean isObstacleAt(Point p){
    return(POutlookCells.getKeyType(p,DSCell.__DSObstacle)!=null);
  }

  public void processAddThing(int x, int y, String type, String params, int step, DSAgent agent) {
    PStep = step;
    DSCell cell = new DSCell(x, y, type, params, step, agent);
    POutlookCells.put(cell);
  }

  public void processDeleteThing(int x, int y, String type, String params, int step) {
    PStep = step;
    POutlookCells.removeCell(x, y, DSCell.getThingTypeIndex(type, params));
  }

  public synchronized DSCells getCells() {
    return (POutlookCells);
  }

  // list of friend agents in actual outlook

  public LinkedList<Point> getFriendsSeen(int vision) {
    LinkedList<Point> flist = new LinkedList<Point>();
    for (int i = -vision; i <= vision; i++)
      for (int j = -vision; j <= vision; j++) {
        Point point = new Point(i, j);
        if (POutlookCells.getKeyType(point, DSCell.__DSEntity_Friend) != null) flist.add(point);
      }
    return (flist);
  }

  public boolean sees(int objectType){
    return(!(POutlookCells.getAllType(objectType).isEmpty()));
  }

  public boolean isObjectAt(int objectType, Point location){
    return(POutlookCells.isObjectAt(objectType,location));
  }

  public boolean isFree(Point position){
    if(!POutlookCells.containsKey(position))
      return(true);
    return(POutlookCells.getAllAt(position).isEmpty());
  }

  public Point seenNearest(int objectType){
    LinkedList<DSCell> cells=POutlookCells.getAllType(objectType);
    if(cells==null)
      return(null);
    if(cells.isEmpty())
      return(null);

    DSCell nearest=cells.getFirst();
    Point np=new Point(0,0);  // outlook, agent is always at 0,0
    for(DSCell cell:cells)
      if(DSMap.distance(cell.getPosition(),np)<DSMap.distance(nearest.getPosition(),np))
        nearest=cell;
    return(nearest.getPosition());
  }

  boolean blockInList(LinkedList<DSCell> cells,int typeFrom,int typeTo){
    if(cells==null)
        return(false);
    for(DSCell cell:cells)
      if((cell.getType()>= typeFrom)&&(cell.getType()<=typeTo))
            return(true);
      return(false);
  }

  public boolean seesBlockBy(Point position,int typeFrom, int typeTo){
      int x=position.x;
      int y=position.y;
      if(blockInList(POutlookCells.getAllAt(new Point(x-1,y)),typeFrom,typeTo))
        return(true);
      if(blockInList(POutlookCells.getAllAt(new Point(x+1,y)),typeFrom,typeTo))
        return(true);
      if(blockInList(POutlookCells.getAllAt(new Point(x,y-1)),typeFrom,typeTo))
        return(true);
      if(blockInList(POutlookCells.getAllAt(new Point(x, y+1)),typeFrom,typeTo))
        return(true);

        return(false);
  }

  public synchronized String stringOutlook(int vision, String agentname) {

    String so = "";

    for (int j = -vision; j <= vision; j++) {
      for (int i = -vision; i <= vision; i++) {
        if (Math.abs(i) + Math.abs(j) > vision) so = so + "   ";
        else {
          if ((j == 0) && (i == 0)) so = so + " AA";
          else {
            if (POutlookCells.containsKey(new Point(i, j))) {
              DSCell cell = POutlookCells.getOneAt(new Point(i, j));
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
    POutlookCells = new DSCells();
  }

  public String stringPheroOutLook(int vision) {
    StringBuilder so = new StringBuilder();

    for (int x = -vision; x <= vision; x++) {
      for (int y = -vision; y <= vision; y++) {
        if (Math.abs(x) + Math.abs(y) > vision) so.append("   ");
        else {
          DSCell regulC = POutlookCells.getNewestAt(new Point(x, y));

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

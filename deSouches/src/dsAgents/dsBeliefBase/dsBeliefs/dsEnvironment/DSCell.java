package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import dsAgents.DSAgent;
import dsAgents.DSConfig;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class  DSCell {

  public static int __DSClear = 0;
  public static int __DSObstacle = 1;
  public static int __DSTaskArea = 2;
  public static int __DSRoleArea = 3;
  public static int __DSMarker = 4;
  public static int __DSBorder = 5;
  public static int __DSGoalArea = 6;
  public static int __DSEntity_Friend = 1001;
  public static int __DSEntity_Enemy = 1002;
  public static int __DSAgent = 1003;


  public static int __DSBlock = 50;

  public static int __DSDispenser = 100;

  private double cellPheromone = 0;

  private final int MAX_PHERO = 100;

  static Map<String, Integer> _thingMap =
      new HashMap() {
        {
          put("clear", __DSClear);
          put("obstacle", __DSObstacle);
          put(DSConfig.___ourEntityNamePrefix, __DSEntity_Friend);
          put(DSConfig.___enemyEntityNamePrefix, __DSEntity_Enemy);
          put("markerclear", __DSMarker);
          put("taskboard", __DSTaskArea);
          put("goalZone", __DSGoalArea);
          put("roleZone", __DSRoleArea);
        }
      };

  static Map<Integer, String> _thingTypes =
      new HashMap() {
        {
          put(__DSClear, "   ");
          //put(__DSObstacle, " \u2589\u2589");
          put(__DSObstacle, " OO");
          put(__DSEntity_Friend, " '\u2589");
          put(__DSEntity_Enemy, " XX");
          //put(__DSEntity_Friend, " \u26C4");
          //put(__DSEntity_Enemy, " \u26D4");
          put(__DSMarker, " !!");
          put(__DSTaskArea, " TT");
          put(__DSGoalArea, " ++");
       //   put(__DSGoalArea, " \u2690\u2690");
          put(__DSRoleArea, " //");
       //   put(__DSRoleArea, " \u25BA\u25BA");
        }
      };

  public boolean isUnmovable(){
    if(((PType>=__DSBlock)&&(PType<=__DSBlock+20))||
            (PType==__DSObstacle)||
            (PType==__DSEntity_Friend)||
            (PType==__DSEntity_Enemy))
      return(true);
      else
        return(false);
  }

  protected static int getThingTypeIndex(String thing, String params) {

    if (thing.equals("dispenser")) {
      return (__DSDispenser + Integer.valueOf(params.substring(1)));
    }
    if (thing.equals("block")) {
      return (__DSBlock + Integer.valueOf(params.substring(1)));
    }

    if (_thingMap.containsKey(thing + params)) return (_thingMap.get(thing + params));
    if(thing.substring(0,6).contentEquals("entity"))
      return(__DSEntity_Enemy);
    return (-1);
  }

  protected static String getTypeSign(int type) {
    if (_thingTypes.containsKey(type)) return (_thingTypes.get(type));

    if ((type >= __DSBlock) && (type < __DSDispenser)) {
      return ("\u26BD" + String.valueOf(type - __DSBlock));
    }

    if ((type >= __DSDispenser)) {
      return ("\u26E8" + String.valueOf(type - __DSDispenser));
    }

    return (" ??");
  }

  private int PType, PX, PY, PTimeStamp;

  private DSAgent foundBy;

  public void setFoundBy(DSAgent foundBy) {
    this.foundBy = foundBy;
  }

  public DSAgent getFoundBy() {
    return foundBy;
  }

  public void setType(int type) {
    PType = type;
  }

  public int getType() {
    return (PType);
  }

  public int getX() {
    return (PX);
  }

  public int getY() {
    return (PY);
  }

  public Point getPosition() {
    return (new Point(PX, PY));
  }

  public void setTimestamp(int timestamp) {
    this.PTimeStamp = timestamp;
  }

  public int getTimestamp() {
    return (PTimeStamp);
  }

  public void setX(int x) {
    PX = x;
  }

  public void setY(int y) {
    PY = y;
  }


  private int startPheromoneByType(int type) {
    return MAX_PHERO; // add pheromone to exception for less likelihood of exploration eg. obstacle
  }

  public DSCell(int x, int y, int type, int timestamp) {
    PX = x;
    PY = y;
    PType = type;
    PTimeStamp = timestamp;
    cellPheromone = startPheromoneByType(PType);
  }

  public DSCell(int x, int y, String type, String params, int timestamp) {
    PX = x;
    PY = y;
    PType = getThingTypeIndex(type, params);
    PTimeStamp = timestamp;
    cellPheromone = startPheromoneByType(PType);
  }

  public DSCell(int x, int y, String type, String params, int timestamp, DSAgent foundBy) {

    PX = x;
    PY = y;
    PType = getThingTypeIndex(type, params);
    PTimeStamp = timestamp;
    this.foundBy = foundBy;
    cellPheromone = startPheromoneByType(PType);
  }

  public DSCell(int x, int y, int type, int timestamp, DSAgent foundBy) {
    PX = x;
    PY = y;
    PType = type;
    PTimeStamp = timestamp;
    this.foundBy = foundBy;
    cellPheromone = startPheromoneByType(PType);
  }

  public void setPheromone(double pheromone) {
    cellPheromone = pheromone;
  }

  public double getPheromone() {
    return cellPheromone;
  }
}

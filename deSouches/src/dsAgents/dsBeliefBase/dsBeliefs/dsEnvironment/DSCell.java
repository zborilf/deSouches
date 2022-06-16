package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import dsAgents.DSAgent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DSCell {

  public static int __DSClear = 0;
  public static int __DSObstacle = 1;
  public static int __DSEntity_Friend = 2; // ptani na agenta zmenit na entitu
  public static int __DSEntity_Enemy = 3;
  public static int __DSMarker = 4;
  public static int __DSBorder = 5;
  public static int __DSGoal = 6;
  public static int __DSAgent = 7;
  public static int __DSTaskArea = 8;
  public static int __DSRoleArea = 9;

  public static int __DSBlock = 50;

  public static int __DSDispenser = 100;

  private double cellPheromone = 0;
  private int AntStatisticsLastTimeVisited = 0;
  private int AntStatisticsStepsBetweenRevisits = 0;

  static Map<String, Integer> _thingMap =
      new HashMap<String, Integer>() {
        {
          put("clear", __DSClear); // toto je asi spatne, clear = marker : clear
          put("obstacle", __DSObstacle);
          put("entityA", __DSEntity_Friend);
          put("entityB", __DSEntity_Enemy);
          put("markerclear", __DSMarker);
          put("taskboard", __DSTaskArea);
          put("goalZone", __DSGoal);
          put("roleZone", __DSRoleArea);
        }
      };

  static Map<Integer, String> _thingTypes =
      new HashMap<Integer, String>() {
        {
          put(__DSClear, " ..");
          put(__DSObstacle, " ##");
          put(__DSEntity_Friend, " FF");
          put(__DSEntity_Enemy, " EE");
          put(__DSMarker, " MM");
          put(__DSTaskArea, " TT");
          put(__DSGoal, " ++");
          put(__DSRoleArea, " //");
        }
      };

  protected static int getThingTypeIndex(String thing, String params) {

    if (thing.equals("dispenser")) {
      return (__DSDispenser + Integer.valueOf(params.substring(1)));
    }
    if (thing.equals("block")) {
      return (__DSBlock + Integer.valueOf(params.substring(1)));
    }

    if (_thingMap.containsKey(thing + params)) return (_thingMap.get(thing + params));
    return (-1);
  }

  protected static String getTypeSign(int type) {
    if (_thingTypes.containsKey(type)) return (_thingTypes.get(type));

    if ((type >= __DSBlock) && (type < __DSDispenser)) {
      return (" B" + String.valueOf(type - __DSBlock) + "");
    }

    if ((type >= __DSDispenser)) {
      return (" D" + String.valueOf(type - __DSDispenser) + "");
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

  public int revisitedInSteps() {
    return AntStatisticsStepsBetweenRevisits;
  }

  public void setTimestamp(int timestamp) {
    this.PTimeStamp = timestamp;
    AntStatisticsStepsBetweenRevisits = timestamp - AntStatisticsLastTimeVisited;
    AntStatisticsLastTimeVisited = timestamp;
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

  public boolean positionMatch(int x, int y) {
    if ((x == PX) && (y == PY)) return (true);
    return (false);
  }

  public String cellToString() {
    String st = "Cell at [" + PX + "," + PY + "]" + "/" + PType + "/" + PTimeStamp;
    return (st);
  }

  public DSCell(int x, int y, int type, int timestamp) {
    // bude tam pozice rel. k agentovi, objekt, casove razitko
    PX = x;
    PY = y;
    PType = type;
    PTimeStamp = timestamp;
    AntStatisticsLastTimeVisited = timestamp;
  }

  // 2022, thing is from {obstacle, entity, dispenser, marker, block, taskboard}
  //              params for block {b1, ...} marker {clear} dispeneser {b1, ...}

  public DSCell(int x, int y, String type, String params, int timestamp) {
    // bude tam pozice rel. k agentovi, objekt, casove razitko
    PX = x;
    PY = y;
    PType = getThingTypeIndex(type, params);
    PTimeStamp = timestamp;
    AntStatisticsLastTimeVisited = timestamp;
  }

  // bude tam pozice rel. k agentovi, objekt, casove razitko
  public DSCell(int x, int y, String type, String params, int timestamp, DSAgent foundBy) {

    PX = x;
    PY = y;
    PType = getThingTypeIndex(type, params);
    PTimeStamp = timestamp;
    AntStatisticsLastTimeVisited = timestamp;
    this.foundBy = foundBy;
  }

  public DSCell(int x, int y, int type, int timestamp, DSAgent foundBy) {
    PX = x;
    PY = y;
    PType = type;
    PTimeStamp = timestamp;
    AntStatisticsLastTimeVisited = timestamp;
    this.foundBy = foundBy;
  }

  public void setPheromonePropagated(double pheromone) {
    cellPheromone = pheromone;
  }

  private double getTimePheromone(int curStep) {
    // current ~pheromone~ (information gain) by age
    final int TIME_EVAP_CONST = 5, MAX_VAL = 100;
    double p = Math.min((curStep - getTimestamp()) * TIME_EVAP_CONST, MAX_VAL);

    return p;
  }

  public double getFullPheromone() {
    return cellPheromone;
  }

  public double getVisiblePheromone(int curStep) {
    // used for path calculation
    // todo:l prumer ?
    // return Math.max(cellPheromone,getTimePheromone(curStep));
    return (cellPheromone + getTimePheromone(curStep)) / 2.0;
  }
}

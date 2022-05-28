package dsAgents.dsBeliefBase.dsBeliefs;

import java.util.HashMap;
import java.util.Map;

public class DSBeliefsIndexes {

  public static final int __simStart = 0;
  public static final int __name = 1;
  public static final int __team = 2;
  public static final int __teamSize = 3;
  public static final int __steps = 4;
  public static final int __role = 5;
  public static final int __actionID = 6;
  public static final int __timestamp = 7;
  public static final int __deadline = 8;
  public static final int __step = 9;
  public static final int __lastAction = 10;
  public static final int __lastActionParams = 11;
  public static final int __lastActionResult = 12;
  public static final int __score = 13;
  public static final int __thing = 14;
  public static final int __task = 15;
  public static final int __attached = 16;
  public static final int __energy = 17;
  public static final int __deactivated = 18;
  public static final int __roleZone = 19;
  public static final int __goalZone = 20;
  public static final int __violation = 21;
  public static final int __norm = 22;
  public static final int __surveyed = 23;
  public static final int __hit = 24;
  public static final int __ranking = 25;

  static Map<String, Integer> _beliefMap =
      new HashMap<String, Integer>() {
        {
          put("simStart", __simStart);
          put("name", __name);
          put("team", __team);
          put("teamSize", __teamSize);
          put("steps", __steps);
          put("role", __role);
          put("actionID", __actionID);
          put("timestamp", __timestamp);
          put("deadline", __deadline);
          put("step", __step);
          put("lastAction", __lastAction);
          put("lastActionParams", __lastActionParams);
          put("lastActionResult", __lastActionResult);
          put("score", __score);
          put("thing", __thing);
          put("task", __task);
          put("attached", __attached);
          put("energy", __energy);
          put("deactivated", __deactivated);
          put("roleZone", __roleZone);
          put("goalZone", __goalZone);
          put("violation", __violation);
          put("norm", __norm);
          put("surveyed", __surveyed);
          put("hit", __hit);
          put("ranking", __ranking);
        }
      };

  public static int getIndex(String beliefName) {
    if (_beliefMap.containsKey(beliefName)) return (_beliefMap.get(beliefName));
    return (-1);
  }
}

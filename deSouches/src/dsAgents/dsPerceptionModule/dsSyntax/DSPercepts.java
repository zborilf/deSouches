package dsAgents.dsPerceptionModule.dsSyntax;

import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import eis.iilang.Percept;
import java.util.HashMap;

public class DSPercepts {

  static HashMap<String, Integer> PBlockTypes = new HashMap<String, Integer>();
  static int PNextBlockType = 0;

  public static int blockTypeByName(String blockName) {
    if (PBlockTypes.containsKey(blockName)) return (PBlockTypes.get(blockName));
    PBlockTypes.put(blockName, PNextBlockType);

    PNextBlockType++;
    return (PNextBlockType - 1);
  }

  public static void initBlocks() {
    PBlockTypes.put("b0", 0);
    PBlockTypes.put("b1", 1);
    PBlockTypes.put("b2", 2);
    PBlockTypes.put("b3", 3);
    PNextBlockType = 4;
  }
}

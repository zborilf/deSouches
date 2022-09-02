package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSAStarItem;
import java.util.HashMap;
import java.util.Map;

public abstract class DSAction {

  static Map<String, Class> _actionMap =
      new HashMap<String, Class>() {
        {
          put("skip", DSSkip.class);
          put("move", DSMove.class);
          put("attach", DSAttach.class);
          put("detach", DSDetach.class);
          put("rotate", DSRotate.class);
          put("connect", DSConnect.class);
          put("disconnect", DSDisconnect.class);
          put("request", DSRequest.class);
          put("submit", DSSubmit.class);
          put("clear", DSClear.class);
          put("adopt", DSAdopt.class);
          put("survey", DSSurvey.class);
        }
      };

  public static Class getActionClass(String action) {
    return (_actionMap.get(action));
  }

  public abstract DSGGoal execute(DSAgent agent);

  public abstract boolean isExternal();

  public abstract String actionText();

  public void succeededEffect(DSAgent agent) {
    return;
  } // after suc. feedback

  public abstract DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody body, int step);
}

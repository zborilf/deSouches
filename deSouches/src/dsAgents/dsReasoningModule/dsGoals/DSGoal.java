package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSStatusIndexes;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStar;
import java.awt.*;
import java.util.HashMap;

public abstract class DSGoal {

  public static final int __DSGNoPlan = 0;
  public static final int __DSGExecutionSucceeded = 0;
  public static final int __DSGSubgoalNeeded = 1;
  public static final int __DSGFeedbackNeeded = 2;
  public static final int __DSGPlanningFailed = 3;
  public static final int __DSGExecutionFailed = 4;
  public static final int __DSGGoalAchieved = 5;
  public static final int __DSGMovePathFailed = 6;
  public static final int __DSGMoveBorderFailed = 7;

  private static final String TAG = "DSGoal";

  static final int __AStarSteps = 2500;

  HashMap<String, DSPlan>
      PPlans; // vytvorene prioritni plany pro cil (OR-plany, jeden uspesne vykonany -> cil splnen)
  DSPlan PRecentPlan; // naposledy vykonavany plan
  DSGoal PSubGoal;
  boolean PHasPlan;
  int PLastStatus = __DSGExecutionSucceeded;
  int PNOFails = 0;

  DSPlan astarGroup(String planName, int priority, DSAgent agent, Point destination, DSBody body) {
    DSMap map = agent.getMap();
    Point agentPosition = agent.getMapPosition();
    return (new DSAStar()
        .computePath(
            planName, priority, map, agentPosition, destination, body, __AStarSteps, agent));
  }

  public int goalStatus() {
    return (PLastStatus);
  }

  public boolean goalAchieved() {
    return (PLastStatus == __DSGGoalAchieved);
  }

  public void setPlansToSuccess() {
    PPlans.clear();
    DSPlan plan = new DSPlan("OKPlan", 3);
    PPlans.put("OKPlan", plan);
  }

  public abstract String getGoalDescription();

  public DSPlan highestPriorityPlan() { // vrati plan s nejvyssi prioritou, pokud zadny neni -> null
    DSPlan hpPlan = null;
    for (String planName : PPlans.keySet())
      if (hpPlan == null) hpPlan = PPlans.get(planName);
      else if (PPlans.get(planName).getPriority() > hpPlan.getPriority())
        hpPlan = PPlans.get(planName);
    return (hpPlan);
  }

  public abstract boolean revisePlans(DSAgent agent);

  // PPlan = new DSAStar().computePath(agent.getMap(), agent.getMap().getAgentPos(), PLocation,
  // agent.getBody(),300, agent);

  public DSGoal getSubGoal() {
    return (PSubGoal);
  }

  public DSPlan getRecentPlan() {
    return (PRecentPlan);
  }

  public int executionFeedback(int result, DSAgent agent) {
    if (PRecentPlan == null) return (__DSGNoPlan);

    if (result == DSStatusIndexes.__action_success) {
      PRecentPlan.externalActionSucceeded(agent);
      PLastStatus = __DSGExecutionSucceeded;
      return (PLastStatus);
    }

    if (result == DSStatusIndexes.__action_failed_random) {
      PRecentPlan.externalActionFailed(agent);
      PLastStatus = __DSGExecutionSucceeded;
      return (PLastStatus);
    }

    if (result == DSStatusIndexes.__action_failed_target) {
      PRecentPlan.externalActionFailed(agent);
      PLastStatus = __DSGExecutionSucceeded;
      ;
      PPlans.remove(PRecentPlan.getName());
      return (PLastStatus);
    }

    if (result == DSStatusIndexes.__action_failed_path) {
      PRecentPlan.externalActionFailed(agent);
      PLastStatus = __DSGMovePathFailed;
      PPlans.remove(PRecentPlan.getName());
      return (PLastStatus);
    }

    // a ted vsechny ostatni ...
    PRecentPlan.externalActionFailed(agent);
    PPlans.remove(PRecentPlan.getName());
    PLastStatus = __DSGExecutionFailed;

    // prozatim kazdy dalsi druh failu bude mit za nasledek pad planu (a pripadne preplanovani

    return (PLastStatus);
  }
  ;

  public int followGoal(DSAgent agent) {

    // HorseRider.inquire(TAG, "followGoal: "+agent.getAgentName()+" follow goal se statusem
    // "+PLastStatus);

    for (String planName :
        PPlans
            .keySet()) // nebyl jiz cin dosazen? TODO melo by byt uz po vykonani akce, resp. po
                       // feedbacku
    if (PPlans.get(planName).planSuceeded()) {
        System.out.println(
            agent.getEntityName()
                + ":"
                + this.getGoalDescription()
                + " achieved by "
                + PPlans.get(planName).plan2text());
        PLastStatus = __DSGGoalAchieved;
        return (PLastStatus);
      }

    if (PSubGoal
        != null) { // TODO ????? , existuje subgoal, ale ten se nevykonava ... aktualne nevyuzivane
      PLastStatus = __DSGPlanningFailed;
      return (PLastStatus);
    }

    revisePlans(agent); //

    DSPlan chosenPlan = highestPriorityPlan();

    if (chosenPlan == null) { // plan nebyl a ani se makePlanem nevytvoril -> gol spadl
      PLastStatus = __DSGPlanningFailed;
      return (PLastStatus);
    }

    if (!chosenPlan.executeOneStep(agent)) {
      PPlans.remove(chosenPlan); // plan byl neuspesny, bude odstranen
      PLastStatus = __DSGExecutionFailed;
    } else { // execution was OK, execution result may be 1, waiting feetback 2, demands subgoal
             // (zatim neni) 3, succeeded
      PRecentPlan = chosenPlan;
      PLastStatus = __DSGExecutionSucceeded;
      if (chosenPlan.waitingForFeedback()) {
        PLastStatus = __DSGFeedbackNeeded;
        return (PLastStatus);
      }
      if (chosenPlan.demandsSubgoal()) {
        PSubGoal = chosenPlan.getSubGoal();
        PLastStatus = __DSGSubgoalNeeded;
        return (PLastStatus);
      }
      if (chosenPlan.planSuceeded()) {
        PLastStatus = __DSGGoalAchieved;
        return (PLastStatus);
      }
    }
    return (PLastStatus);
  }

  public DSGoal() {
    PHasPlan = false;
    PPlans = new HashMap<String, DSPlan>();
    PRecentPlan = null;
    PSubGoal = null;
  }
}

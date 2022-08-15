package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSStatusIndexes;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSPAStar;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSOneStepGreedy;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/*
      How to set SubGoal
            setSubgoal(subGoal); // subGoal je DSGoal
            PPlans.put(p.getName(), p);    // p je DSPlan fail plan

     Pokud neni fail plan, pak cely cil skonci po subgoalu , nebo jeste pred nim
     TODO ... pokud revision nastavuje subGoal, mel by cil existovat minimalne do jednoho dalsiho revision tohoto cile

 */

public abstract class DSGGoal {

  public static final int __DSGNoPlan = 0;
  public static final int __DSGExecutionSucceeded = 0;
  public static final int __DSGSubgoalNeeded = 1;
  public static final int __DSGFeedbackNeeded = 2;
  public static final int __DSGPlanningFailed = 3;
  public static final int __DSGGoalFailed = 4;
  public static final int __DSGExecutionFailed = 5;
  public static final int __DSGGoalAchieved = 6;
  public static final int __DSGMovePathFailed = 7;
  public static final int __DSGMoveBorderFailed = 8;
  public static final int __DSGClearActionFailed = 9;

  static Map<Integer, String> _GoalStatus2String =
      new HashMap<>() {
        {
          put(__DSGExecutionSucceeded, "no plan / success");
          put(__DSGSubgoalNeeded, "subgoal needed");
          put(__DSGFeedbackNeeded, "feedback needed");
          put(__DSGPlanningFailed, "planning failed");
          put(__DSGGoalFailed, "egoal failed");
          put(__DSGExecutionFailed, "execution failed");
          put(__DSGGoalAchieved, "goal achieved");
          put(__DSGMovePathFailed, "move path failed");
          put(__DSGMoveBorderFailed, "move border failed");
          put(__DSGClearActionFailed, "clear failed");
        }
      };

  public static String getGoalStatusString(int status){
    if(_GoalStatus2String.containsKey(status))
      return(_GoalStatus2String.get(status));
      else
        return("");
  }

  public static String getGoalStatusType(int status) {
    return _GoalStatus2String.getOrDefault(status, "unknown");
  }

  private static final String TAG = "DSGoal";

  static final int __AStarSteps = 2500;

  HashMap<String, DSPlan>
      PPlans; // vytvorene prioritni plany pro cil (OR-plany, jeden uspesne vykonany -> cil splnen)
  DSPlan PRecentPlan; // naposledy vykonavany plan
  DSGGoal PSubGoal;
  boolean PHasPlan;
  int PLastStatus = __DSGExecutionSucceeded;
  int PNOFails = 0;

  /*
        Planning paths
          1, Astar (with limits? TODO)
          2, One step toward position, brutal force, with clear actions
   */

  /*  1  */

  DSPlan astarGroup(String planName, int priority, DSAgent agent, Point destination, DSBody body, boolean finalPlan) {
    DSMap map = agent.getMap();
    Point agentPosition = agent.getMapPosition();
    return (new DSPAStar()
        .computePath(
            planName, priority, map, agentPosition, destination, body, __AStarSteps, agent, finalPlan));
  }



  public int goalStatus() {
    return (PLastStatus);
  }

  public boolean goalAchieved() {
    return (PLastStatus == __DSGGoalAchieved);
  }

  public void subgoalAchieved(){
 //   getRecentPlan().subgoalAchieved();
    PSubGoal=null;
    PLastStatus=__DSGExecutionSucceeded;
  }

  public void setSubgoal(DSGGoal subgoal){
    PSubGoal=subgoal;
  }

  protected void setGoalToFail(){
    PLastStatus=__DSGGoalFailed;
  }

  public void setPlansToSuccess() {
    PPlans.clear();
    DSPlan plan = new DSPlan("OKPlan", 99);
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

  /*
        must be implemented
        -> as a result a plan is put into PPlans
   */

  public abstract boolean revisePlans(DSAgent agent);

  // PPlan = new DSAStar().computePath(agent.getMap(), agent.getMap().getAgentPos(), PLocation,
  // agent.getBody(),300, agent);

  public DSGGoal getSubGoal() {
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
      PLastStatus = __DSGExecutionSucceeded;   // random fail does not matter
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
    PLastStatus = __DSGGoalFailed;

    // prozatim kazdy dalsi druh failu bude mit za nasledek pad planu (a pripadne preplanovani

    return (PLastStatus);
  }
  ;

  protected synchronized void removeEmptyPlans(){
    for(String plan:PPlans.keySet())
      if(PPlans.get(plan).isEmpty())
        PPlans.remove(plan);
  }


  public int followGoal(DSAgent agent) {

    // HorseRider.inquire(TAG, "followGoal: "+agent.getAgentName()+" follow goal se statusem
    // "+PLastStatus);

    for (String planName :
        PPlans.keySet()) // nebyl jiz cin dosazen? TODO melo by byt uz po vykonani akce, resp. po
      // feedbacku
      if (PPlans.get(planName).planSuceeded()) {
        System.out.println(
            agent.getEntityName()
                + ":"
                + this.getGoalDescription()
                + " achieved by "
                + PPlans.get(planName).plan2text());
        PLastStatus = __DSGGoalAchieved;
        agent.printOutput("Cil dosazen, pry");
        return (PLastStatus);
      }

    if (PSubGoal
        != null) { // TODO ????? , existuje subgoal, ale ten se nevykonava ... aktualne nevyuzivane
      PLastStatus = __DSGPlanningFailed;
      return (PLastStatus);
    }

    revisePlans(agent); //

    DSPlan chosenPlan = highestPriorityPlan();



    if (PSubGoal!=null) { // plan nebyl a ani se makePlanem nevytvoril -> gol spadl
      PLastStatus = __DSGSubgoalNeeded;
      return (PLastStatus);
    }

    if (chosenPlan == null) { // plan nebyl a ani se makePlanem nevytvoril -> gol spadl
      PLastStatus = __DSGPlanningFailed;
      return (PLastStatus);
    }


    agent.printOutput("HPPlan after revision "+chosenPlan.plan2text());

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
      if (PSubGoal!=null) {
  //      PSubGoal = chosenPlan.getSubGoal();
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

  public DSGGoal() {
    PHasPlan = false;
    PPlans = new HashMap<String, DSPlan>();
    PRecentPlan = null;
    PSubGoal = null;
  }
}

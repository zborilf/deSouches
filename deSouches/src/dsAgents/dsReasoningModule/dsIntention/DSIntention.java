package dsAgents.dsReasoningModule.dsIntention;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.util.LinkedList;

public class DSIntention {

  // spravuje hierarchii cilu, pokud nejaky(!) plan je vykonavan a vyzaduje podcil, je puvodni cil
  // zablokovan vcetne vsech jeho planu (tohle ale nevypada pekne, to by asi melo byt blokovano
  // podcile ale stajne nejsou doposud vubec uvazovany, takze mozna je to vse zbytecne
  //
  // osetri pripojeni pozadovaneho subgoalu
  // vesmes jenom propaguje vykonani akce,
  // odstrani podcile, pokud jsou dosazeny
  // vraci Goal, pokud je potreba zpetne vazby

  private static final String TAG = "DSIntention";

  LinkedList<DSGGoal> PIntentionStack = new LinkedList<DSGGoal>();

  public static int __Intention_NoPlan = 0;
  public static int __Intention_InProcess = 1;
  public static int __Intention_Finished = 2;
  public static int __Intention_Failed = 3;

  public DSGGoal getTLG() {
    return (PIntentionStack.getLast());
  }

  public String description() {

    if (PIntentionStack.isEmpty()) return ("no intention");

    return (PIntentionStack.getFirst().getGoalDescription());
  }

  public int intentionState() {
    DSGGoal TLG = PIntentionStack.getLast();
    if (TLG.goalAchieved()) {
      return (__Intention_Finished);
    } else if ((
            TLG.goalStatus() == DSGGoal.__DSGPlanningFailed)
        || (TLG.goalStatus() == DSGGoal.__DSGExecutionFailed)
        || (TLG.goalStatus() == DSGGoal.__DSGMovePathFailed)
        || (TLG.goalStatus() == DSGGoal.__DSGGoalFailed))
      return (__Intention_Failed);

    return (__Intention_InProcess);
  }

  public boolean intentionExecutionFeedback(int actionResult, DSAgent agent) {
    // System.out.println("intentionExecutionFeedback: "+"Feeedback result in step
    // "+agent.getStep()+
    //             " for "+agent.getEntityName()+" is "+actionResult);
    if (PIntentionStack.getFirst().goalStatus() == DSGGoal.__DSGFeedbackNeeded) {
      PIntentionStack.getFirst().executionFeedback(actionResult, agent);
      return (true);
    }
    return (false);
  }

  public DSPlan getRecentPlan() {
    return (PIntentionStack.getFirst().getRecentPlan());
  }

  public DSGGoal executeIntention(DSAgent agent) {

    // needs a subgoal? insert subgoal and go on
    // execute goal
    // goal achieved -> if not TLG, remove
    // return goal (if feedback needed), else null

    DSGGoal actualGoal = PIntentionStack.getFirst();
    DSGGoal subGoal;

    agent.printOutput(PIntentionStack.toString());

    agent.printOutput("Nasleduji cil " + actualGoal.getGoalDescription());
    actualGoal.followGoal(agent);
    agent.printOutput("Cil byl nasledovan, status cile " + DSGGoal.getGoalStatusString(actualGoal.goalStatus()));


    if (actualGoal.goalStatus() == DSGGoal.__DSGSubgoalNeeded) {
      agent.printOutput("Subgoal!! Rozsiruji o " + actualGoal.getSubGoal().getGoalDescription());
      subGoal = actualGoal.getSubGoal();
      PIntentionStack.push(subGoal);
      actualGoal = subGoal;
    } else {
      // plan succesfully finished? Ok, get back one step
      if ((actualGoal.goalAchieved()) && (PIntentionStack.size() > 1)) { // dont pop TLG!
        PIntentionStack.pop();
        PIntentionStack.getFirst().subgoalAchieved();
      }

      // zde si nejsem jistej, asi obsolete
      if (actualGoal.goalStatus() == DSGGoal.__DSGFeedbackNeeded) return (actualGoal);
      }
     return (null);
  }

  public DSIntention(DSGGoal goal) {
    PIntentionStack.add(goal);
  }
}

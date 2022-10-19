package dsAgents.dsReasoningModule.dsIntention;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;

import java.util.LinkedList;

import static dsAgents.dsReasoningModule.dsIntention.DSIntention.__Intention_Finished;


public class DSIntentionPool {

  public static final int __No_Intention_Left = -1;
  public static final int __Nothing_Executed_But_Possible =0;
  public static final int __Recent_Intention_Persists = 1;
  public static final int __Recent_Intention_Finished = 2;
  public static final int __Recent_Intention_Failed = 3;



  private static final String TAG = "DSIntentionPool";
  LinkedList<DSIntention> PIntentions = new LinkedList<DSIntention>();
  DSIntention PLastIntentionExecuted;


  public void adoptIntention(DSIntention intention) {
    PIntentions.add(intention);
  }

  public DSIntention getIntention() {
    if (!PIntentions.isEmpty())
      return (PIntentions.getFirst()); // zde asi podle priority, nebo round-robin
    else return (null);
  }

  public DSGGoal getRecentIntentionTLG() {
    if (PLastIntentionExecuted == null)
      return (null);
    return (PLastIntentionExecuted.getTLG());
  }

  public boolean hasIntention(){
    return(!PIntentions.isEmpty());
  }

  public void removeIntention(DSIntention intention) {
    if (intention != null) if (PIntentions.contains(intention)) PIntentions.remove(intention);
  }

  public void clearPool() {
    PIntentions.clear();
  }

  public int checkActualIntentionPoolState(){
    /*
        The state is one of the following:
            public static int __No_Intention_Left = -1;
            public static int __Nothing_Executed_But_Possible =0
            public static int __Recent_Intention_Prevails = 1;
            public static int __Recent_Intention_Finished = 2;
            public static int __Recent_Intention_Failed = 3;
     */

    if (PLastIntentionExecuted == null)
      if (hasIntention())
        return (__Nothing_Executed_But_Possible);
      else
        return (__No_Intention_Left);

    if(PLastIntentionExecuted.intentionState() == __Intention_Finished){
        removeIntention(PLastIntentionExecuted);
        return(__Recent_Intention_Finished);

      } else
        if (PLastIntentionExecuted.intentionState() == DSIntention.__Intention_Failed) {
          removeIntention(PLastIntentionExecuted);
          return(__Recent_Intention_Failed);
      }
        return(__Recent_Intention_Persists);
    }


  public void processFeedback(int actionResult, DSAgent agent){
    if(PLastIntentionExecuted!=null)
          PLastIntentionExecuted.intentionExecutionFeedback(actionResult,agent);
  }

  public DSIntention executeOneIntention(
      DSAgent agent) { // vraci null -> intensna nesplnena, nebo TLG (top level goal) splnene intensny

    PLastIntentionExecuted =null;      // pesimistic

    DSIntention intention = getIntention();
    if (intention == null) {
      agent.printOutput("There is no intention");
      agent.executeSkip();
    } else {
      agent.printOutput("Executing intention");
      intention.executeIntention(agent);
      PLastIntentionExecuted = intention;
    }
    return (intention);
  }

  public DSIntentionPool() {}
}

package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsGUI.DSTaskGUI;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsMultiagent.dsTasks.DSTask;

public abstract class DSSBlockScenarios extends DSScenario {


  public boolean checkGoalArea(){
 //   return(PCommander.getMasterMap().getMapCells().isObjectAt(DSCell.__DSGoalArea,PTask.getGoalArea()));
    return(true);
  }

  public synchronized void goalCompleted(DSAgent agent, DSGGoal goal) {
    if(!checkGoalArea())
      PCommander.scenarioFailed(this, "GC: No Goal area at"+PTask.getGoalArea());
  }

  public synchronized void goalFailed(DSAgent agent, DSGGoal goal){
    if(!checkGoalArea())
      PCommander.scenarioFailed(this,"No Goal area at "+PTask.getGoalArea());
  }

  public DSSBlockScenarios(DeSouches commander, DSTask task) {
    super(commander, task);
    PGUI= DSTaskGUI.createGUI();
  }
}

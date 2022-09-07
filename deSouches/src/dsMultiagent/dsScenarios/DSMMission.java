package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsGUI.DSTaskGUI;
import dsMultiagent.dsTasks.DSTask;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class DSMMission {

  public static final int _disabledEvent = 0;
  public static final int _noBlockEvent = 1;
  public static final int _areaSpoted = 2;
  public static final int _newFighterEvent = 3;


  public static final int __mission_goes_well=1;
  public static final int __mission_timeout=2;
  public static final int __no_goal_area=3;


  int PDefaultPriority = 1;
  public static final int _idleLimit = 65;

  DeSouches PCommander;
  int PTaskTypeNumber;
  int PScenarioType;

  DSTask PTask;
  LinkedList<DSAgent> PAgentsAllocated;
  int PPriority;

  public LinkedList<DSAgent> getAgentsAllocated() {
    return (PAgentsAllocated);
  }

  protected DSTaskGUI PGUI=null;

  public void updateGUI(int step){};



  public String getName() {
    return ("No name scenario");
  }

  public int checkConsistency(){
    return(__mission_goes_well);
  }


  public String getAgentsAllocatedText() {
    String agents = "";
    for (Iterator i = PAgentsAllocated.iterator(); i.hasNext(); ) {
      agents = agents + ((DSAgent) i.next()).getEntityName() + ",";
    }
    return (agents);
  }

  public void scenarioFailed(String reason){
    PGUI.failed(reason);
  }


  public void scenarioSuceeded(){
    PGUI.suceeded();
  }

  public int getPriority() {
    return (PPriority);
  }

  public DSTask getTask() {
    return (PTask);
  }

  public abstract void goalCompleted(DSAgent agent, DSGGoal goal);

  public abstract void goalFailed(DSAgent agent, DSGGoal goal);

  public abstract boolean checkEvent(DSAgent agent, int eventType);

  public abstract boolean initMission(int step);

  public void calibrateScenario(DSMap map){

  }

  public int getDeadline() {
    return (PTask.getDeadline());
  }

  public boolean checkDeadlock() {
    DSAgent agent;
    for (Iterator i = PAgentsAllocated.iterator(); i.hasNext(); ) {
      agent = (DSAgent) i.next();
      if (agent.getIdleSteps() < _idleLimit) return (false);
    }
    return (true);
  }

  public DSMMission(DeSouches commander, DSTask task) {
    if(task==null)
      PTaskTypeNumber=-1;
    else
      PTaskTypeNumber = task.getTaskTypeNumber();
    PCommander = commander;
    PTask = task;
    PAgentsAllocated = new LinkedList<DSAgent>();
    if (task != null) PScenarioType = task.getTaskTypeNumber();
  }
}

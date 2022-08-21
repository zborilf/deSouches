package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsTaskGUI;
import dsMultiagent.dsTasks.DSTask;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class DSScenario {

  public static final int _disabledEvent = 0;
  public static final int _noBlockEvent = 1;
  public static final int _areaSpoted = 2;

  int PDefaultPriority = 1;
  public static final int _idleLimit = 65;

  DeSouches PCommander;
  int PTaskType;
  int PScenarioType;

  DSTask PTask;
  LinkedList<DSAgent> PAgentsAllocated;
  int PPriority;

  public LinkedList<DSAgent> getAgentsAllocated() {
    return (PAgentsAllocated);
  }

  protected dsTaskGUI PGUI=null;

  public void updateGUI(int step){};



  public String getName() {
    return ("No name scenario");
  }


  public String getAgentsAllocatedText() {
    String agents = "";
    for (Iterator i = PAgentsAllocated.iterator(); i.hasNext(); ) {
      agents = agents + ((DSAgent) i.next()).getEntityName() + ",";
    }
    return (agents);
  }

  public void scenarioFailed(){
    PGUI.failed();
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

  public abstract boolean initScenario(int step);

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

  public DSScenario(DeSouches commander, DSTask task) {
    if(task==null)
      PTaskType=-1;
    else
      PTaskType = task.getTaskTypeNumber();
    PCommander = commander;
    PTask = task;
    PAgentsAllocated = new LinkedList<DSAgent>();
    if (task != null) PScenarioType = task.getTaskTypeNumber();
  }
}

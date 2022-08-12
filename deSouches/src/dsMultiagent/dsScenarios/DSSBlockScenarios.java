package dsMultiagent.dsScenarios;

import dsAgents.DeSouches;
import dsAgents.dsTaskGUI;
import dsMultiagent.DSGroup;
import dsMultiagent.dsTasks.DSTask;

public abstract class DSSBlockScenarios extends DSScenario {

  @Override
  public void updateGUI() {
    if(PGUI!=null)
      PGUI.setDsgTaskText(PTask);
  }

  public DSSBlockScenarios(DeSouches commander, DSTask task) {
    super(commander, task);
  }
}

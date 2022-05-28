package dsMultiagent.dsScenarios;

import dsAgents.DeSouches;
import dsMultiagent.DSGroup;
import dsMultiagent.dsTasks.DSTask;

public abstract class DSSBlockScenarios extends DSScenario {

  public DSSBlockScenarios(DeSouches commander, DSGroup group, DSTask task, int taskType) {
    super(commander, group, task, taskType);
  }
}

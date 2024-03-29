package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.DSRoles;
import dsAgents.dsReasoningModule.dsGoals.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class DSDivideAndExplore extends DSMMission {

  static final int _RadiusMax = 30;
  static final int _RadiusIncrement = 30;

  static final int _areaWidth = 15;
  static final int _areaHeight = 15;

  int PRadius;
  DSAgent PAgent;
  HashMap<DSAgent, Rectangle> PAreas;

  void divideSpace(LinkedList<DSAgent> agents, boolean print) {
    int mx = 0;
    int my = 0;
    if (agents.isEmpty()) return;
    for (DSAgent agent : agents) {
      mx = mx + agent.getMapPosition().x;
      my = my + agent.getMapPosition().y;
      if (agents.getFirst().getGroup().isMasterGroup())
        if (print)
          System.out.println(
              "[" + agent.getMapPosition().x + "," + agent.getMapPosition().y + "] ; ");
    }
    if (agents.getFirst().getGroup().isMasterGroup())
      if (print) {
        mx = mx / agents.size();
        my = my / agents.size();
        System.out.println("teziste je v [" + mx + "," + my + "]");
      }
  }

  @Override
  public String getName() {
    return ("Divide and Explore Scenario");
  }

  @Override
  public void goalCompleted(DSAgent agent, DSGGoal goal) {
    // go on
    System.out.println(agent.getEntityName() + " DAE, Completed");

    divideSpace(PAgent.getGroup().getFreeAgents(2), false);

    if(!agent.getActualRole().contentEquals(DSRoles._roleDigger)) {
      DSGGoal newGoal = new DSGoalExplore(PRadius); // ... area!);
      agent.hearOrder(newGoal);
    }
    else
      PCommander.scenarioCompleted(this);
  }

  @Override
  public void goalFailed(DSAgent agent, DSGGoal goal) {


    divideSpace(PAgent.getGroup().getFreeAgents(2), false);

    DSGGoal newGoal = new DSGoalExplore(PRadius); // ... area!);
    agent.hearOrder(newGoal);
  }

  @Override
  public boolean checkEvent(DSAgent agent, int eventType) {
    return (false);
  }

  @Override
  public boolean initMission(int step) {
    //  1, allocates all 'free' agents from the PAgent's group
    //  2, assigns them a part of the environment for exploration
    //  3, sends them orders to explore the areas

    divideSpace(PAgent.getGroup().getFreeAgents(2), false);

    PAgent.hearOrder(new DSGoalExplore(PRadius)); // TODO tohle pryc
    return (true);
  }

  public DSDivideAndExplore(DSAgent agent, int radius) {
    super(agent.getCommander(),  null);
    PAreas = new HashMap<DSAgent, Rectangle>();
    PAgent = agent;
    PAgentsAllocated = new LinkedList<DSAgent>();
    PAgentsAllocated.add(agent);
    PRadius = radius;
    PPriority = 1;
    //TODO:l k cemu toto
  }
}

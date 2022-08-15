package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSHybridPathPlanner;

import java.awt.*;

public class DSGoToPosition extends DSGGoal {

  Point PDestination;
  DSBody PBody = null;
  int PTimeout;

  public String getGoalDescription() {
    return ("goToPosition");
  }

  public boolean revisePlans(DSAgent agent) {

    if (PPlans.containsKey("goToPosition")) return false;

    if (PDestination == null) {
      return (false);
    }


    if (PBody == null) PBody = agent.getBody();

    Point agentPos = agent.getMapPosition(); // asi zbytecne
    //    PPlan = new DSAStar().
    //    computePath(agent.getGroup().getGroupMap(), agent.getMap().getAgentPos(), PPosition,
    // PBody,100, agent);


    DSPlan plan = DSHybridPathPlanner.getOneStep("goToPosition",  agent.getMap(), 1, agent,
                                                PDestination, PBody, PTimeout-agent.getStep(), true);


    if (plan == null) return (false);

    PPlans.put(plan.getName(), plan);
    return (true);
  }

  public DSGoToPosition(Point position) {
    super();
    PDestination = position;
  }

  public DSGoToPosition(Point position, DSBody body, int timeout) {
    super();
    PTimeout = timeout;
    PBody = body;
    PDestination = position;
  }
}

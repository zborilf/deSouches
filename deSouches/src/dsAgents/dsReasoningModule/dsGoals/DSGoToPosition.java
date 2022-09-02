package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods.DSHybridPathPlanner;

import java.awt.*;

public class DSGoToPosition extends DSGGoal {

  Point PDestination;
  DSBody PBody = null;
  int PTimeout;
  String PName="goToPosition";

  public String getGoalDescription() {
    return (PName);
  }

  @Override
  public String getGoalParametersDescription(){
    return(" to "+PDestination+" body "+PBody.bodyToString());
  }

  public boolean revisePlans(DSAgent agent) {



    if ((PPlans.containsKey(PName))&&(!PPlans.get(PName).isEmpty()))
      return false;

    if (PDestination == null) {
      return (false);
    }


    if (PBody == null) PBody = agent.getBody();


    DSPlan plan = DSHybridPathPlanner.getOneStep("goToPosition",  agent.getMap(), 1, agent,
                                                PDestination, PBody, PTimeout-agent.getStep(), true);


    if (plan == null) {
      Point ap=agent.getMapPosition();
      Point op=agent.getMap().nearestObject(DSCell.__DSObstacle,agent.getMapPosition());
        if(Math.abs(op.x-ap.x)+Math.abs(op.y-ap.y)==1){
          plan=new DSPlan("goToPosition clear",2,false);
          plan.appendAction(new DSClear(new Point(op.x-ap.x,op.y-ap.y)));
          return(false);
        }
    }

    PPlans.put(PName, plan);
    return (true);
  }

  public void setName(String name){
    PName=name;
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

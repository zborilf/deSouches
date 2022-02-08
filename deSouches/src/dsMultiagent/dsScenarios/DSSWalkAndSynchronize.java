package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsGoals.*;

import java.util.LinkedList;


public class DSSWalkAndSynchronize extends DSScenario {
    final static int _RadiusMax=30;
    final static int _RadiusIncrement=30;



    int PRadius;
    DSAgent PAgent;

    @Override
    public void goalCompleted(DSAgent agent, DSGoal goal) {

        if(goal.getGoalName().contentEquals("clearGoal")
                ||goal.getGoalName().contentEquals("detachAllGoal")) {
            if(PRadius<_RadiusMax)
                PRadius=PRadius+_RadiusIncrement;
            DSGoal newGoal = new DSGoalRoam(PRadius);//DSGoalRoam();
            agent.hearOrder(newGoal);
        }
        if(goal.getGoalName().contentEquals("goRandomly")) {
            DSGoal newGoal = new DSGoalRoam(PRadius); //TODO tohle pryc
            agent.hearOrder(newGoal);
            PCommander.scenarioCompleted(this);
        }
    }

    @Override
    public void goalFailed(DSAgent agent, DSGoal goal) {
        if(goal.getGoalName().contentEquals("goRandomly")
                    ||goal.getGoalName().contentEquals("detachAllGoal")) {
            DSGoal newGoal = new DSGoalRoam(PRadius);//DSGoalRoam();
            agent.hearOrder(newGoal);
        }
        if(goal.getGoalName().contentEquals("clearGoal")) {
            DSGoal newGoal = new DSGoalRoam(PRadius);
            agent.hearOrder(newGoal);
        }
    }

    @Override
    public boolean checkEvent(DSAgent agent, int eventType){
        return(false);
    }

    @Override
    public boolean initScenario(int step) {
        PAgent.hearOrder(new DSGoalRoam(PRadius)); //TODO tohle pryc
        return(true);
    }


    public DSSWalkAndSynchronize(DSAgent agent, int radius){
        super(agent.getCommander(),agent.getGroup(),null,0);
        PAgent=agent;
        PAgentsAllocated=new LinkedList<DSAgent>();
        PAgentsAllocated.add(agent);
        PRadius=radius;
        PPriority=1;
    }

}

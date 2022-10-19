package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.DSRoles;
import dsAgents.dsGUI.DSFightersGUI;
import dsAgents.dsReasoningModule.dsGoals.DSGDetachAll;
import dsAgents.dsReasoningModule.dsGoals.DSGGoAndShoot;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoToPosition;
import dsMultiagent.dsTasks.DSTask;

import java.awt.*;

public class DSMFightersMission extends DSMMission{

    DSFightersGUI PGUI;
    int __distance=12;

    @Override
    public String getName() {
        return ("Fighter Mission");
    }

    @Override
    public int checkConsistency(){
        super.checkConsistency();
        if(PCommander.getMasterGroup()!=null)
        for(DSAgent agent:PCommander.getMasterGroup().getMembersList())
            if(agent.getActualRole().contentEquals(DSRoles._roleDigger))
                if(!agent.getScenarioName().contentEquals("Fighter Mission"))
                    this.checkEvent(agent,DSMMission._newFighterEvent);
        return(__mission_goes_well);
    }

    public DSMFightersMission(DeSouches commander, DSTask task) {
        super(commander, task);
        PGUI= DSFightersGUI.createGUI(PCommander);
        PGUI.addText(PCommander.getName());
    }

    int computeFDistance(Point position){
        int distance=0;
        for(DSAgent agent:PAgentsAllocated){
            distance=distance+PCommander.getMasterMap().distance(position,agent.getMapPosition());
        }
        return(distance);
    }

    void setGoal(DSAgent agent){
        Point destination;
        Point newDestination;

        int x = (int) Math.round(Math.random() * __distance-(__distance/2));
        int y = (int) Math.round(Math.random() * __distance-(__distance/2));
        destination=agent.getMap().shiftPosition(agent,new Point(x, y));

        if(PCommander.getMasterMap()!=null)
            for(int i=0;i<5;i++) {
                x = (int) Math.round(Math.random() * __distance - (__distance / 2));
                y = (int) Math.round(Math.random() * __distance - (__distance / 2));
                newDestination = agent.getMap().shiftPosition(agent,new Point(x, y));
                 if(computeFDistance(newDestination)>computeFDistance(destination))
                    destination=newDestination;
        }

        PGUI.addText(agent.getStep()+": New goal " + agent.getEntityName() + " total distance " + destination);
        DSGGoal newGoal = new DSGGoAndShoot(destination,agent.getStep()+30);
        agent.hearOrder(newGoal);
    }

    public void goalCompleted(DSAgent agent, DSGGoal goal){
        setGoal(agent);
    }


    public void goalFailed(DSAgent agent, DSGGoal goal){
        setGoal(agent);
    }

    public boolean checkEvent(DSAgent agent, int eventType){
        if(eventType==DSMMission._newFighterEvent) {
            PGUI.addText(agent.getStep() + ": New soldier " + agent.getEntityName());
            setGoal(agent);
            agent.setScenario(this);
            PAgentsAllocated.add(agent);
            return (true);
        }
        return(true);
    }

    public boolean initMission(int step){
        return(true);
    }


}

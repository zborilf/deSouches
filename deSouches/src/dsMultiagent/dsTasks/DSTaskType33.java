package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSConnect;
import dsAgents.dsExecutionModule.dsActions.DSDetach;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

public class DSTaskType33 extends DSTaskType {


    @Override
    DSPlan makePlanL1(){
        DSPlan plan=new DSPlan("Leutnant1, task18",2);
        DSConnect connect=new DSConnect(PMaster.getEI(),getConnectDirection(PLeutnant1),PLeutnant2.getEntityName());
        plan.appendAction(connect);
        connect=new DSConnect(PMaster.getEI(),getConnectDirection(PLeutnant1),PMaster.getEntityName());
        plan.appendAction(connect);
        DSDetach detach=new DSDetach(PMaster.getEI(),getConnectDirection(PLeutnant1));
        plan.appendAction(detach);
        return(plan);
    };

    @Override
    DSPlan makePlanL2(){
        DSPlan plan=new DSPlan("Leutnant2, task18",2);
        DSConnect connect=new DSConnect(PMaster.getEI(),getConnectDirection(PLeutnant2),PLeutnant3.getEntityName());
        plan.appendAction(connect);
        connect=new DSConnect(PMaster.getEI(),getConnectDirection(PLeutnant2),PLeutnant1.getEntityName());
        plan.appendAction(connect);
        DSDetach detach=new DSDetach(PMaster.getEI(),getConnectDirection(PLeutnant2));
        plan.appendAction(detach);
        return(plan);
    };

    @Override
    DSPlan makePlanL3(){
        DSPlan plan=new DSPlan("Leutnant3, task18",2);
        DSConnect connect=new DSConnect(PMaster.getEI(),getConnectDirection(PLeutnant3),PLeutnant2.getEntityName());
        plan.appendAction(connect);
        DSDetach detach=new DSDetach(PMaster.getEI(),getConnectDirection(PLeutnant3));
        plan.appendAction(detach);
        return(plan);
    };


    @Override
    public Point blockPosition(int agent) {
        if(agent==1)
            return(new Point(0,1));
        if(agent==2)
            return(new Point(0,2));
        if(agent==3)
            return(new Point(1,2));
        if(agent==4)
            return(new Point(1,1)   );
        return(null);
    }


    @Override
    public Point formationPosition(DSAgent agent, Point position) {
        if(agent==PMaster)
            return(new Point(position.x,position.y));
        if(agent==PLeutnant1)
            return(new Point(position.x,position.y+2));
        if(agent==PLeutnant2)
            return(new Point(position.x+1,position.y+2));
        if(agent==PLeutnant3)
            return(new Point(position.x+1,position.y));
        return(null);
    }

    @Override
    public DSBody getSoldierGoalBody() {
        return null;
    }

    @Override
    public DSBody getTaskBody() {
        return null;
    }


    @Override
    public DSBody agentFormationBody(DSAgent agent) {
        return null;
    }

    public DSTaskType33() {
        super();
        PTaskTypeNumber = 31;
        PTaskArea.addCell(new DSCell(0, 0, 0, 0));
        PTaskArea.addCell(new DSCell(0, 1, 0, 0));
        PTaskArea.addCell(new DSCell(0, 2, 0, 0));
        PTaskArea.addCell(new DSCell(0, 3, 0, 0));
        PTaskArea.addCell(new DSCell(1, 0, 0, 0));
        PTaskArea.addCell(new DSCell(1, 1, 0, 0));
        PTaskArea.addCell(new DSCell(1, 2, 0, 0));
        PTaskArea.addCell(new DSCell(1, 3, 0, 0));

    }


}

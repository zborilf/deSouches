package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSConnect;
import dsAgents.dsExecutionModule.dsActions.DSDetach;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

/*
     A      XXX
    OOO     XXX
    Mpos	L1	L2		Mpos	CL1 CL2	G
    2	    1	3		L1	    CM	D
				        L2	    CM	D

 */

public class DSTaskType5 extends DSTaskType {

    @Override
    protected DSPlan makePlanMaster(String taskName){
        DSPlan plan=new DSPlan("Master, connect and submit",2);
        DSConnect connect=new DSConnect(PMaster.getEI(),"s",PLeutnant1.getEntityName());
        plan.appendAction(connect);
        connect=new DSConnect(PMaster.getEI(),"s",PLeutnant2.getEntityName());
        plan.appendAction(connect);
        return(plan);
    }

    DSPlan makePlanL1(){
        DSPlan plan=new DSPlan("Leutnant1, task5",2);
        DSConnect connect=new DSConnect(PMaster.getEI(),"s",PMaster.getEntityName());
        plan.appendAction(connect);
        DSDetach detach=new DSDetach(PMaster.getEI(),"s");
        plan.appendAction(detach);
        return(plan);
    };


    DSPlan makePlanL2(){
        DSPlan plan=new DSPlan("Leutnant2, task5",2);
        DSConnect connect=new DSConnect(PMaster.getEI(),"s",PMaster.getEntityName());
        plan.appendAction(connect);
        DSDetach detach=new DSDetach(PMaster.getEI(),"s");
        plan.appendAction(detach);
        return(plan);
    };
/*
    @Override
    public String getConnectDirection(DSAgent agent){
        if(agent==PLeutnant1)
            return("s");
        if(agent==PLeutnant2)
            return("s");
        return("");
    }

    public DSBody getSoldierGoalBody() {
        return null;
    }

    @Override
    public DSBody getSoldierGoalBody(DSAgent agent) {
        return(DSBody.getDoubleBody(new Point(0,1)));
    }
*/
    @Override
    public DSBody getTaskBody() {
        return null;
    }


    @Override
    public Point formationPosition(DSAgent agent, Point position) {
        if(agent==PMaster)
            return(new Point(position.x,position.y));
        if(agent==PLeutnant1)
            return(new Point(position.x-1,position.y));
        if(agent==PLeutnant2)
            return(new Point(position.x+1,position.y));
        return(null);
    }

    @Override
    public Point blockPosition(int agent) {
        if(agent==1)
            return(new Point(0,1));
        if(agent==2)
            return(new Point(-1,1));
        if(agent==3)
            return(new Point(1,1));
        return(null);
    }

    @Override
    public Point blockPosition(DSAgent agent) {
        if(agent==PMaster)
            return(blockPosition(1));
        if(agent==PLeutnant1)
            return(blockPosition(2));
        if(agent==PLeutnant2)
            return(blockPosition(3));
        return(null);
    }



    public DSTaskType5() {
        super();
        PTaskTypeNumber = 5;
        PTaskArea.addCell(new DSCell(0, 1, 0, 0));
        PTaskArea.addCell(new DSCell(-1, 0, 0, 0));
        PTaskArea.addCell(new DSCell(-1, 1, 0, 0));
        PTaskArea.addCell(new DSCell(1, 0, 0, 0));
        PTaskArea.addCell(new DSCell(1, 1, 0, 0));
    }

}

package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.*;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

/*
    A     XXX
    O     XXX
    OO    XXX
          X


    Mpos	L1	    L2		Mpos	C	G
    0	    -1,2	1,1		L1	    S   S   CCW C  D
				            L2	    S   C   D

 */

public class DSTaskType12 extends DSTaskType{


    DSPlan makePlanL1(){
        DSPlan plan=new DSPlan("Leutnant1, task12",2);
    /*    DSMove move=new DSMove(PMaster.getEI(),"s");
        plan.appendAction(move);
        move=new DSMove(PMaster.getEI(),"s");
        plan.appendAction(move);
        DSRotateContraCW rotate=new DSRotateContraCW(PMaster.getEI());
        plan.appendAction(rotate);*/
        DSConnect connect=new DSConnect(PMaster.getEI(),"e",PLeutnant2.getEntityName());
        plan.appendAction(connect);
        connect=new DSConnect(PMaster.getEI(),"e",PMaster.getEntityName());
        plan.appendAction(connect);
        DSDetach detach=new DSDetach(PMaster.getEI(),"e");
        plan.appendAction(detach);
        return(plan);
    };


    DSPlan makePlanL2(){
        DSPlan plan=new DSPlan("Leutnant2, task12",2);
   /*     DSMove move=new DSMove(PMaster.getEI(),"s");
        plan.appendAction(move);*/
        DSConnect connect=new DSConnect(PMaster.getEI(),"s",PLeutnant1.getEntityName());
        plan.appendAction(connect);
        DSDetach detach=new DSDetach(PMaster.getEI(),"s");
        plan.appendAction(detach);
        return(plan);
    };

    /*

    @Override
    public DSBody getSoldierGoalBody(DSAgent agent) {
        if(agent==PLeutnant1)
            return(DSBody.getDoubleBody(new Point(1,0)));
        return(DSBody.getDoubleBody(new Point(0,1)));
    }


    @Override
    public String getConnectDirection(DSAgent agent){
        if(agent==PLeutnant1)
            return("e");
        if(agent==PLeutnant2)
            return("s");
        return("");
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
            return(new Point(position.x-1,position.y+2));
        if(agent==PLeutnant2)
            return(new Point(position.x+1,position.y+1));
        return(null);
    }

    @Override
    public Point blockPosition(int agent) {
        if(agent==1)
            return(new Point(0,1));
        if(agent==2)
            return(new Point(0,2));
        if(agent==3)
            return(new Point(1,+2));
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

    public DSTaskType12() {
        super();
        PTaskTypeNumber = 12;
        PTaskArea.addCell(new DSCell(-1, 0, 0, 0));
        PTaskArea.addCell(new DSCell(-1, 1, 0, 0));
        PTaskArea.addCell(new DSCell(-1, 2, 0, 0));
        PTaskArea.addCell(new DSCell(-1, 3, 0, 0));
        PTaskArea.addCell(new DSCell(0, 0, 0, 0));
        PTaskArea.addCell(new DSCell(0, 1, 0, 0));
        PTaskArea.addCell(new DSCell(0, 2, 0, 0));
        PTaskArea.addCell(new DSCell(1, 0, 0, 0));
        PTaskArea.addCell(new DSCell(1, 1, 0, 0));
        PTaskArea.addCell(new DSCell(1, 2, 0, 0));
    }


}

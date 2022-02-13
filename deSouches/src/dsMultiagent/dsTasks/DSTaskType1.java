package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;

import java.awt.*;

public class DSTaskType1 extends DSTaskType {

    DSBody PFormationBody;

/*
    @Override
    public DSBody getSoldierGoalBody(DSAgent agent) {
        if(agent==PMaster)
            return(DSBody.getDoubleBody(new Point(0,1)));
        if(agent==PLeutnant1)
            return(DSBody.getDoubleBody(new Point(0,-1)));
        return(null);
    }
*/

    @Override
    public DSBody getTaskBody() {
        return null;
    }


    public Point formationPosition(DSAgent agent, Point position) {
        if(agent==PMaster)
            return(new Point(position.x,position.y));
        if(agent==PLeutnant1)
            return(new Point(position.x,position.y+3));return null;
    }




    @Override
    public Point blockPosition(int agent) {
        if(agent==1)
            return(new Point(0,1));
        if(agent==2)
            return(new Point(0,2));
        return(null);
    }


    @Override
    public Point blockPosition(DSAgent agent) {
        if(agent==PMaster)
            return(blockPosition(1));
        if(agent==PLeutnant1);
        return(blockPosition(2));
    }


    public DSTaskType1(){
        super();
        PTaskTypeNumber=1;
        PTaskArea.addCell(new DSCell(0,1,0,0));
        PTaskArea.addCell(new DSCell(0,2,0,0));
        PTaskArea.addCell(new DSCell(0,3,0,0));

    }

}

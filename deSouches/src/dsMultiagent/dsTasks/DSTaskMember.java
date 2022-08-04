package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;

import java.awt.*;

public class DSTaskMember {

    DSAgent PAgent;
    Point PDispenserPosition;
    Point PGoalPosition;

    public DSAgent getAgent(){
        return(PAgent);
    }

    String point2String(Point point){
        return("["+point.x+","+point.y+"]");
    }

    int costEstimation(){
        return(DSMap.distance(PAgent.getMapPosition(), PDispenserPosition)+
                DSMap.distance(PDispenserPosition, PGoalPosition));
    }

    public Point getDispenserPosition(){
        return(PDispenserPosition);
    }

    public Point getGoalPosition(){
        return(PGoalPosition);
    }

    public String taskMember2String(){
        return(":- "+PAgent.getEntityName()+" -> "+point2String(PDispenserPosition)+" -> "+point2String(PGoalPosition));
    }

    public DSTaskMember(DSAgent agent, Point dispenserPosition, Point goalPosition){
        PAgent=agent;
        PDispenserPosition=dispenserPosition;
        PGoalPosition=goalPosition;
    }
}

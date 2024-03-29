package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;

import java.awt.*;

public class DSTaskMember {

    DSAgent PAgent;
    Point PDispenserPosition;
    Point PGoalPosition;
    int PPrice;

    public DSAgent getAgent(){
        return(PAgent);
    }

    public int getPrice(){
        return(PPrice);
    }

    String point2String(Point point){
        return("["+point.x+","+point.y+"]");
    }

    int costEstimation(){
        int cost=PAgent.getMap().distance(PDispenserPosition, PGoalPosition);
            cost=cost+PAgent.getMap().distance(PAgent.getMapPosition(), PDispenserPosition);
        return(cost);
    }

    public Point getDispenserPosition(){
        return(PDispenserPosition);
    }

    public Point getGoalPosition(){
        return(PGoalPosition);
    }

    public String taskMember2String(){
        return(":- "+PAgent.getEntityName()+":" + point2String(PAgent.getMapPosition())+" -> "
                                        +point2String(PDispenserPosition)+" -> "+point2String(PGoalPosition));
    }

    public DSTaskMember(DSAgent agent, Point dispenserPosition, Point goalPosition, int price){
        PPrice = price;
        PAgent=agent;
        PDispenserPosition=dispenserPosition;
        PGoalPosition=goalPosition;
    }
}

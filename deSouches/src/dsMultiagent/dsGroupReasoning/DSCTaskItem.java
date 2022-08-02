package dsMultiagent.dsGroupReasoning;

import java.awt.*;

public class DSCTaskItem {
    private int PTaskID;
    private int PPrice;
    private Point PAgentPosition;
    //   priate DSAgent PAgent
    private Point PDispenser;
    private Point PGoal;

    public int getPPrice() {
        return PPrice;
    }

    public int getPTaskID() {
        return PTaskID;
    }

    public Point getPAgentPosition() {
        return PAgentPosition;
    }

    public Point getPGoal() {
        return PGoal;
    }

    public Point getPDispenser() {
        return PDispenser;
    }

    public void printTask(){
        System.out.println(PTaskID+": "+PAgentPosition.toString()+" -> "+PDispenser.toString()+" -> "+PGoal.toString()+
                " (proice: "+PPrice+")");
    }

    public DSCTaskItem(int taskID, Point agentPosition, Point dispenser, Point goal, int price){
        PTaskID=taskID;
        PPrice=price;
        PAgentPosition=agentPosition;
        PGoal=goal;
        PDispenser=dispenser;
    }


}


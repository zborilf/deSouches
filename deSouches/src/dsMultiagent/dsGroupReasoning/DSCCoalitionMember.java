package dsMultiagent.dsGroupReasoning;

import dsAgents.DSAgent;

import java.awt.*;

public class DSCCoalitionMember {
    private int PTaskID;
    private int PPrice;
    private Point PAgentPosition;
    private DSAgent PAgent;
    private Point PDispenser;
    private Point PGoal;

    public int getPrice() {
        return PPrice;
    }


    public int getTaskID() {
        return PTaskID;
    }

    public DSAgent getAgent() { return PAgent; }

    public Point getAgentPosition() {
        return PAgentPosition;
    }

    public Point getGoal() {
        return PGoal;
    }

    public Point getPDispenser() {
        return PDispenser;
    }

    public void printTask(){
        System.out.println(PTaskID+ ": ["+PAgent.getAgentName()+"] :: "+PAgentPosition.toString()+" -> "+PDispenser.toString()+" -> "+PGoal.toString()+
                " (proice: "+PPrice+")\n");
    }

    String stringPos(Point p){
        return("["+p.x+","+p.y+"]");
    }

    public String task2String(){
        return(PTaskID+ ": ["+PAgent.getAgentName()+"] :: "+stringPos(PAgentPosition)+" -> "+
                stringPos(PDispenser)+" -> "+stringPos(PGoal)+
                " (proice: "+PPrice+")\n");
    }

    public DSCCoalitionMember(int taskID, DSAgent agent, Point agentPosition, Point dispenser, Point goal, int price){
        PTaskID=taskID;
        PPrice=price;
        PAgent=agent;
        PAgentPosition=agentPosition;
        PGoal=goal;
        PDispenser=dispenser;
    }


}


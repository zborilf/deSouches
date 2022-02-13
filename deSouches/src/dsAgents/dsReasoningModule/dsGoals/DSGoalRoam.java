package dsAgents.dsReasoningModule.dsGoals;


import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;
import java.util.Random;

public class DSGoalRoam extends DSGoal {
    private static final String TAG = "DSGoalRoam";
    // dojde na nejbližší nepřekážkový okraj své známé mapy

    int PDistance;
    Random PRandom=new Random();


    public String getGoalName(){
        return("Roam");
    }

    public boolean revisePlans(DSAgent agent){

        if(PPlans.containsKey("roam"))
            return false; // plan exists,  no revision

        int x=1,y=1;
        int dx,dy;
        int gx, gy;
        long quadrant;
        double directionQuad=Math.random();

        dx=(int)(PDistance*directionQuad);
        dy=(int)(PDistance*(1-directionQuad));
        quadrant=Math.round(PRandom.nextInt(4));
        if(quadrant>1)
            y=-1;
        if(quadrant%2==1)
            x=-1;

        gx= x*dx + agent.getPosition().x;
        gy= y*dy + agent.getPosition().y;


   //     PPlan= new DSAStar().computePath(agent.getMap(),agent.getMap().getAgentPos() ,new Point(dx,dy),agent.getBody(),300, agent);
        DSPlan plan = astarGroup("roam",1,agent, new Point(gx,gy), agent.getBody());


        if (plan == null){
            return (false);
        }
//        PPlan.appendAction(new DSClear(agent.getEI()));
        PPlans.put("roam",plan);
        return(true);
    }

    public DSGoalRoam(int distance){
        PDistance=4;//distance;
    }
}

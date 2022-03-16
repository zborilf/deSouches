package dsAgents.dsReasoningModule.dsGoals;


import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSAdopt;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;
import java.util.Random;

public class DSGoalRoam extends DSGoal {
    private static final String TAG = "DSGoalRoam";
    // dojde na nejbližší nepřekážkový okraj své známé mapy

    int PDistance;
    Random PRandom=new Random();


    public String getGoalName(){
        return("dsRoam");
    }

    public boolean revisePlans(DSAgent agent){

        if(agent.standsAtRoleZone()){
            if(agent.getActualRole().compareTo("digger")!=0) {
                // standing at role zone and is not 'digger', make high priority plan to change it
                DSPlan plan = new DSPlan("set role digger", 3);
                DSAdopt adoptAction = new DSAdopt(agent.getEI(), "digger");
                plan.appendAction(adoptAction);
                PPlans.put(plan.getName(), plan);
                return (true);
            }
        }

        if((PPlans.containsKey("roam"))&&(!PPlans.containsKey("clear"))) {
            Point direction;
            direction=agent.getMap().objectAroundCell(agent.getMapPosition(), DSCell.__DSObstacle);
            if(direction==null)
                return false; // plan exists,  no revision
            DSPlan plan=new DSPlan("clear",2);
            DSClear clearAction=new DSClear(agent.getEI(),direction);
            plan.appendAction(clearAction);
            PPlans.put(plan.getName(),plan);
            return(true);
        }

        if(PPlans.containsKey("roam"))
            return(false);

        // vypocte nahodne cilovy bod na mape ve vzdalenosti PDistance

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

        gx= x*dx + agent.getMapPosition().x;
        gy= y*dy + agent.getMapPosition().y;


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
        PDistance=  distance;
    }
}

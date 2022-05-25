package dsAgents.dsReasoningModule.dsGoals;

/*
        2022 - should be initiated with assigned area on the map
                it should
                        1, go randomly (with prob __random_walk higher priority than 2 and 3)
                        2, go to nearest known obstacle if known
                        3, go to role area if are not digger and role area is known
                        4, clear obstacles, if in touch
                        5, switch to digger if it is at role zone and is not digger
               Terminates when
                        Area is fully explored (by this agent during this behaviour)
                        Agent joins another group (should catch event 'group attached' or something ... )
 */


import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.DSAdopt;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSStraightPath;

import java.awt.*;
import java.util.Random;

public class DSGoalExplore extends DSGoal {
    private static final String TAG = "DSGoalRoam";
    // dojde na nejbližší nepřekážkový okraj své známé mapy

    final static double __random_walk=0.25;
    int PDistance;
    Random PRandom=new Random();


    public String getGoalDescription(){
        return("Goal explore / "+highestPriorityPlan().getName());
    }

    public boolean revisePlans(DSAgent agent) {


        if (!PPlans.containsKey("clear")) {
            Point direction=null;
            //    if(agent.getActualRole().compareTo("digger") == 0)
            //        direction = agent.getMap().objectAroundCell(agent.getMapPosition(), DSCell.__DSEntity_Enemy);
            Point obstacleAt;
            if (direction == null) {
                obstacleAt = agent.getMap().nearestObject(DSCell.__DSObstacle, agent.getMapPosition());
                if((obstacleAt!=null)&&(Math.random()<0.6))
                {
                    int range=1;
                    if(agent.getActualRole().contentEquals("digger"))
                        range=agent.getVisionRange();
                    if (DSMap.distance(agent.getMapPosition(), obstacleAt) <= range) {
                        DSPlan plan = new DSPlan("clear", 4);
                        direction=new Point(obstacleAt.x-agent.getMapPosition().x,
                                obstacleAt.y-agent.getMapPosition().y);

                        DSClear clearAction = new DSClear(agent.getEI(), direction);
                        plan.appendAction(clearAction);
                        PPlans.put(plan.getName(), plan);
                    }
                }
            }
        }



        if (agent.getActualRole().compareTo("default") == 0) {

            if (agent.isAtRoleZone()) {
                // standing at role zone and is not 'digger', make high priority plan to change it

                String role=agent.getCommander().roleNeeded(agent);


                DSPlan plan = new DSPlan("set role "+role, 3);
                DSAdopt adoptAction = new DSAdopt(agent.getEI(), role);
                plan.appendAction(adoptAction);
                PPlans.put(plan.getName(), plan);
                return (true);
            }

            if (!PPlans.containsKey("goToRoleZone")&&(Math.random()<0.5)) {
                DSPlan plan = null;
                Point dp = (agent.getMap().nearestObject(DSCell.__DSRoleArea, agent.getMapPosition()));
                if (dp != null) {
                    plan = new DSStraightPath().computeStraightPath(agent, "reachRoleZone",
                            (Point) agent.getMapPosition().clone(),
                            new Point(dp.x, dp.y), 2);
                }
                if (plan != null)
                    PPlans.put("goToRoleZone", plan);

            }
        }


        if (PPlans.containsKey("roam"))
            return (false);

        // vypocte nahodne cilovy bod na mape ve vzdalenosti PDistance

        int x = 1, y = 1;
        int dx, dy;
        int gx, gy;

        /*
        {
            long quadrant;
            double directionQuad = Math.random();

            dx = (int) (PDistance * directionQuad);
            dy = (int) (PDistance * (1 - directionQuad));
            quadrant = Math.round(PRandom.nextInt(4));
            if (quadrant > 1)
                y = -1;
            if (quadrant % 2 == 1)
                x = -1;

            gx = x * dx + agent.getMapPosition().x;
            gy = y * dy + agent.getMapPosition().y;
        }*/
        Point destination = null;
        String destinationS="";

        if (agent.getActualRole().compareTo("digger") != 0){
            destination = agent.getMap().nearestObject(DSCell.__DSRoleArea, agent.getMapPosition());
            destinationS = "to role area";
        }

        if(destination==null) {
            if(Math.random()>0.4)
                destination = agent.getMap().nearestObject(DSCell.__DSObstacle, agent.getMapPosition());
            destinationS = "to obstacle";
        }

        if((destination==null)||(Math.random()<__random_walk))
            {
                long quadrant;
                double directionQuad = Math.random();

                dx = (int) (PDistance * directionQuad);
                dy = (int) (PDistance * (1 - directionQuad));
                quadrant = Math.round(PRandom.nextInt(4));
                if (quadrant > 1)
                    y = -1;
                if (quadrant % 2 == 1)
                    x = -1;

                destination=new Point( x * dx + agent.getMapPosition().x, y * dy + agent.getMapPosition().y);
                destinationS = "to random position";
            }

        gx=destination.x;gy=destination.y;

   //     PPlan= new DSAStar().computePath(agent.getMap(),agent.getMap().getAgentPos() ,new Point(dx,dy),agent.getBody(),300, agent);
     //     DSPlan plan = astarGroup("roam",1,agent, new Point(gx,gy), agent.getBody());

        DSPlan plan = new DSStraightPath().computeStraightPath(agent,"roam",
                        (Point)agent.getMapPosition().clone(),
                            new Point(gx,gy), 1);


        if (plan == null){
            return (false);
        }
//        PPlan.appendAction(new DSClear(agent.getEI()));
        PPlans.put("roam",plan);
        return(true);
    }

    public DSGoalExplore(int distance){
        PDistance=  distance;
    }
}

package dsAgents.dsReasoningModule.dsPlans.dsPlanningMethods;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.DSAction;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsExecutionModule.dsActions.DSMove;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

public class DSOneStepGreedy {


    private static boolean mayShootAt(DSAgent agent, Point direction){
        Point mapPosition=new Point(agent.getMapPosition().x+direction.x,
                agent.getMapPosition().y+direction.y);

        return(!agent.getGroup().standsMemberAt(mapPosition) &&
                (agent.getOutlook().getCells().isDestructibleAt(direction)));

    }

    private static DSPlan tryShoot(DSAgent agent){

        DSPlan planC=new DSPlan("Greedy one step with block clear", 1, false);
        Point agentPosition=agent.getMapPosition();

        //     if (agent.getOutlook().isObstacleAt(new Point(1, 0)))
        if(mayShootAt(agent, new Point(1,0)))
        {
            DSClear clear = new DSClear(new Point(1, 0));
            planC.appendAction(clear);
            return(planC);
        }
//        if (agent.getOutlook().isObstacleAt(new Point(-1, 0)))
        if(mayShootAt(agent, new Point(-1,0)))
        {
            DSClear clear = new DSClear(new Point(-1, 0));
            planC.appendAction(clear);
            return(planC);
        }
//        if (agent.getOutlook().isObstacleAt(new Point(0, 1)))
        if(mayShootAt(agent, new Point(0,1)))
        {
            DSClear clear = new DSClear(new Point(0, 1));
            planC.appendAction(clear);
            return(planC);
        }
//        if (agent.getOutlook().isObstacleAt(new Point(0, -1)))

        if(mayShootAt(agent, new Point(0,-1)))
        {
            DSClear clear = new DSClear(new Point(0, -1));
            planC.appendAction(clear);
            return(planC);
        }
        return(null);
    }


    public synchronized static DSPlan agentWithBlockOSG(DSAgent agent, int dx, int dy, boolean finalPlan) {
        // block should be at '1' in the body list
        Point body = agent.getBody().getBodyList().get(1).getPosition();
        DSAction action;
        Point destination;
        Point agentPosition;
        DSBody finalBody;
        DSPlan plan = null;

        agentPosition = agent.getMapPosition();
        finalBody = new DSBody();

        agent.printOutput("HP Greedy with block");


        if (dx != 0) {
            destination = new Point(agentPosition.x + dx, agentPosition.y);
            finalBody.addCell(new DSCell(-dx, 0, DSCell.__DSBlock, agent.getStep()));

            plan = new DSPAStar()
                    .computePath("Greedy one step with block", 1, agent.getMap(),
                            agent.getMapPosition(), destination, finalBody, agent.getStep() + 20, agent, finalPlan);

            if (plan == null)
                agent.printOutput("Greedy one step with block dx failed, from" + agent.getMapPosition().toString() +
                        " to " + destination.toString());
        }

        if (dy != 0) {
            destination = new Point(agentPosition.x, agentPosition.y + dy);
            finalBody.addCell(new DSCell(0, -dy, DSCell.__DSBlock, agent.getStep()));

            DSPlan plan2 = new DSPAStar()
                    .computePath("Greedy one step with block", 1, agent.getMap(),
                            agent.getMapPosition(), destination, finalBody, agent.getStep() + 20, agent, finalPlan);

            /*
            if (plan != null) {
                agent.printOutput("plan ve smeru y o " +dy+ " je "+ plan.plan2text());
                return (plan);
            }*/

            if (plan2 == null)
                agent.printOutput("Greedy one step with block dy failed, from" + agent.getMapPosition().toString() +
                        " to " + destination.toString());

            if ((plan2 != null) && (plan != null)) {
                if (Math.random() < 0.5)
                    return (plan2);
                else
                    return (plan);
            }

        if (plan != null)
            return (plan);

        if (plan2 != null)
            return (plan2);
        }


        return(tryShoot(agent));
    }

    public synchronized static DSPlan oneStep(String planName, int priority, DSAgent agent,
                                                             Point destination, boolean finalPlan){




        /*
            VERZE JEN PRO JEDEN KROK
                1, Jsem na zadane pozici, vratim OK Plan0
                2, Zvolim smer k cili
                3, Najdu jeden nebo dva smery, kam jit (Manhattan)
                4a, Pokud nese blok, zavola docasne spec proceduru vyse
                4b, Pokud je prazdne v nekterem smeru, zvolim nejaky z prazdnych
                            Udelam akci move v tomto kroku a vratim plan s touto akci(nedokoncenou, false)
                5, Pokud neni, vyberu nejaky a udelam clear akci, vratim jako plan
         */

        Point agentPosition=agent.getMapPosition();

            /*
                    1
             */
            if(destination==agentPosition)
                // empty plan will succeed
                return(new DSPlan(planName, priority, finalPlan));

            /*
                    2
             */

            int dx;
            int dy;

            DSMap map=agent.getMap();

            dx=map.directionHorizontal(agentPosition.x,destination.x);
            dy=map.directionVertical(agentPosition.y, destination.y);

            agent.printOutput("agent position "+agentPosition+", destination "+destination+" dx:"+dx+" dy"+dy);

        /*
                6a
         */

        if(agent.getBody().getBodyList().size()>1){
            return(agentWithBlockOSG(agent, dx, dy, finalPlan));
        }

        /*
                Find free direction, 6 a 7
        */

        agent.printOutput("HP Greedy no block "+"apos" +agentPosition+ " destination="+destination+"["+dx+","+dy);


        DSPlan plan = new DSPlan("goForBlock",1,finalPlan);

            if (dx != 0)
                if //(!agent.getOutlook().isObstacleAt(new Point(dx, 0))){
                   (agent.getOutlook().isFree(new Point(dx, 0))){
                    DSMove move = new DSMove(DSPerceptor.getDirectionFromPosition(new Point(dx, 0)));
                    plan.appendAction(move);
                    return(plan);
                }


            if(dy!=0)
                if //(!agent.getOutlook().isObstacleAt(new Point(0, dy)))
                    (agent.getOutlook().isFree(new Point(0, dy))){
                    DSMove move = new DSMove(DSPerceptor.getDirectionFromPosition(new Point(0, dy)));
                    plan.appendAction(move);
                    return(plan);
                }
/*                else
                if (dx != 0)
                {
                    DSClear clear = new DSClear(new Point(dx, 0));
                    plan.appendAction(clear);
                    return(plan);
                }
        if (dy != 0)
        {
            DSClear clear = new DSClear(new Point(0, dy));
            plan.appendAction(clear);
            return(plan);
        }*/



           return(tryShoot(agent));

        }

    }


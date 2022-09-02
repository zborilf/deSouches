package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSAgentOutlook;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.*;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;
import java.util.LinkedList;

import static dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell.*;

public class DSGGetBlock2022 extends DSGGoal {

    private static final String TAG = "DSGoToDispenser";
    int PBlockType;
    Point PDispenserLocation;
    int PTimeout;
    int PStage=1;


    // napad, kdyz nevidi depod, sestavi plan jit k nejblizsimu 'neprekazkovemu' okraji sve mapy
    // pokud depot vidi, snazi se k nemu dojit, sestavi cestu k nemu, pokud by nahodou spadl,
    // sestavuje cestu znovu

    public String getGoalDescription() {
        return ("get block 2022");
    }

  /*
         Plan "Get Block from a Dispenser" (defaultne, pokud nevidí volný blok)
  */

    DSPlan makePlanGetFromDispenser(DSAgent agent) {

        /*
            VERZE JEN PRO JEDEN KROK
                1, Jsem vedle dispenseru, konec
                2, vidim dispenser na oci, adoptuji pozici (pripadne zmenim)
                3, Jsem na zadane pozici, koncim neuspechem
                4, Zvolim smer k dispenseru
                5, Najdu jeden nebo dva smery, kam jit (Manhattan)
                6, Pokud je prazdne v nekterem smeru, zvolim nejaky z prazdnych
                            Udelam akci move v tomto kroku a vratim plan s touto akci(nedokoncenou, false)
                7, Pokud neni, vyberu nejaky a udelam clear akci, vratim jako plan

         */

            /*
                    1
             */

        if (agent.getMap().objectAroundCell(agent.getMapPosition(), __DSDispenser + PBlockType) != null) {
            // EmptyPlan
            return (new DSPlan("dispenserReached", 3)); // TODO, mel by vyzadat a vzit blok
        }

            /*
                    2
             */

            /*
            agent.getOutlook().getDispenserSeen(__DSDispenser+PBlockType);
        ... atd
            */

            /*

            /*
                    3
             */

        int dx=0;
        int dy=0;

        Point ap = agent.getMapPosition();

        if (ap.x > PDispenserLocation.x)
            dx=-1;
                else
                    if (ap.x < PDispenserLocation.x)
                        dx=1;


        if (ap.y > PDispenserLocation.y)
            dy=-1;
                else
                    if (ap.y < PDispenserLocation.y)
                        dy=1;

                    DSMap map = agent.getMap();

        DSAgentOutlook ol=agent.getOutlook();

        /*
                Find free direction, 6 a 7
        */


        DSPlan plan = new DSPlan("goForBlock",1,false);

        if (dx != 0)
            if (!ol.isObstacleAt(new Point(dx, 0))){
                DSMove move = new DSMove(DSPerceptor.getDirectionFromPosition(new Point(dx, 0)));
                plan.appendAction(move);
                return(plan);
        }


        if(dy!=0)
            if (!ol.isObstacleAt(new Point(0, dy)))
                {
                    DSMove move = new DSMove(DSPerceptor.getDirectionFromPosition(new Point(0, dy)));
                    plan.appendAction(move);
                    return(plan);
                }
            else
                {
                    DSClear clear = new DSClear(new Point(0, dy));
                    plan.appendAction(clear);
                    return(plan);
                }



        //   mp neni [0,0] -> move there
        //   op neni [0,0] -> clear there

        return (null);
    }

  /*
         Plan "Go For a Block"
  */

    @Override
    public boolean revisePlans(DSAgent agent) { // true -> plans modified

        if (agent.getBody().blockAttached(PBlockType) != null) {
            DSPlan plan = new DSPlan("blockAttached", 3, true);
            agent.printOutput("Get block ... block attached");
            PPlans.put(plan.getName(), plan);
        }


        Point nearestBlock = agent.getOutlook().seenNearest(__DSBlock + PBlockType);
        if (nearestBlock != null)
            if(!agent.isAttacchedAt(nearestBlock)){
             if (!PPlans.containsKey("attachBlockPlan")) {
                    if (agent.getMap().distance(nearestBlock, new Point(0, 0)) == 1) {
               //         if (!agent.getOutlook().getCells().isObjectAround(__DSEntity_Friend, nearestBlock)) {
                        if(agent.getOutlook().getCells().rightHandPriority(nearestBlock)){

                            DSPlan p = new DSPlan("attachBlockPlan", 2);
                            DSAttach newAttach = new DSAttach(DSPerceptor.getDirectionFromPosition(nearestBlock), PBlockType);
                            p.appendAction(newAttach);
                            agent.printOutput("Get block ... block beside");
                            PPlans.put(p.getName(), p);
                            return (true);
                        }
                    }
/*                    else {                    // sees block in some distance (agentpos+nearestdistance), go for it!
                        int bx = nearestBlock.x + agent.getMapPosition().x;
                        int by = nearestBlock.y + agent.getMapPosition().y;
                        Point blockPosition = new Point(bx, by);
                        DSGGoal subGoal = new DSGApproachTypes(blockPosition, PTimeout); // ... area!);
                        setSubgoal(subGoal);
                        agent.printOutput("Get block ... block nearby -> approach");
                        return (true);
                    }*/
            }
   //         return (false);
        }



        Point nearest=agent.getOutlook().seenNearest(__DSDispenser + PBlockType);
        if(nearest!=null)
            if(agent.getMap().distance(nearest, new Point(0,0))==1){
                if(Math.random()<0.5)       // try to skip sometime
                if(!PPlans.containsKey("attachPlan")) {
                    DSPlan p = new DSPlan("attachPlan", 2);
                    Point dd = agent.getOutlook().seenNearest(__DSDispenser + PBlockType);
                    DSRequest newRequest = new DSRequest(DSPerceptor.getDirectionFromPosition(dd));
                    p.appendAction(newRequest);
                    DSAttach newAttach = new DSAttach(DSPerceptor.getDirectionFromPosition(dd), PBlockType);
                    p.appendAction(newAttach);
                    PPlans.put(p.getName(), p);
                    agent.printOutput("Get block ... dispenser beside beside");

                    return (true);
                }
                else
                {
                    DSPlan p = new DSPlan("attachPlanSkip", 2,false);
                    PPlans.put(p.getName(),p);
                    return(true);
                }
                return(false);
        }

        if((agent.getOutlook().isObjectAt(__DSDispenser + PBlockType, new Point(0,0)))){
            agent.printOutput("Get block ... stadnding on dispenser");
            DSPlan plan=new DSPlan("step beside",1,false);
            DSAction action;
            if(agent.getOutlook().isFree(new Point(0,-1)))
                action=new DSMove("n");
            else
            if(agent.getOutlook().isFree(new Point(0,1)))
                action=new DSMove("s");
            else
            if(agent.getOutlook().isFree(new Point(1,0)))
                action=new DSMove("e");
            else
            if(agent.getOutlook().isFree(new Point(-1,0)))
                action=new DSMove("w");
            else
            if(agent.getOutlook().isObstacleAt(new Point(0,-1)))
                action=new DSClear(new Point(0,-1));
            else
            if(agent.getOutlook().isObstacleAt(new Point(0,1)))
                action=new DSClear(new Point(0,1));
            else
            if(agent.getOutlook().isObstacleAt(new Point(1,0)))
                action=new DSClear(new Point(1,0));
            else
            if(agent.getOutlook().isObstacleAt(new Point(-1,0)))
                action=new DSClear(new Point(-1,0));
            else
                action=new DSSkip();
            plan.appendAction(action);
            PPlans.put(plan.getName(),plan);
            return(true);
        }

        if(PStage==1) {
            //           LinkedList<Integer> l = new LinkedList<>();
            //  l.add(DSCell.__DSDispenser + PBlockType);
            DSGGoal subGoal = new DSGApproachTypes(PDispenserLocation, PTimeout); // ... area!);
            setSubgoal(subGoal);
            agent.printOutput("Get block ... go for block, approaching");


            //       DSPlan p = new DSPlan("okplan", 1,false);
            //      PPlans.put(p.getName(), p);

            PStage=2;
            return(true);
        }

        if(PStage==2) {
            DSGGoal subGoal = new DSGGetBeside(__DSDispenser+PBlockType,PTimeout);
            agent.printOutput("Get block ... get beside by plan");
            setSubgoal(subGoal);
            PStage = 3;
            return(true);
        }



        return(false);

    }

    public DSGGetBlock2022(int blockType) {
        super();
        PBlockType = blockType;
        PDispenserLocation = null;
    }

    public DSGGetBlock2022(int dispenserType, Point dispenserLocation, int timeout) {
        super();
        PTimeout = timeout;
        PBlockType = dispenserType;
        PDispenserLocation = dispenserLocation;
    }
}

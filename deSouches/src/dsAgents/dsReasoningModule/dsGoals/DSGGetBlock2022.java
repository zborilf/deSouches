package dsAgents.dsReasoningModule.dsGoals;

import deSouches.utils.HorseRider;
import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSAgentOutlook;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.DSAttach;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsExecutionModule.dsActions.DSMove;
import dsAgents.dsExecutionModule.dsActions.DSRequest;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

import static dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell.__DSDispenser;

public class DSGGetBlock2022 extends DSGoal{

    private static final String TAG = "DSGoToDispenser";
    int PBlockType;
    Point PDispenserLocation;

    // napad, kdyz nevidi depod, sestavi plan jit k nejblizsimu 'neprekazkovemu' okraji sve mapy
    // pokud depot vidi, snazi se k nemu dojit, sestavi cestu k nemu, pokud by nahodou spadl,
    // sestavuje cestu znovu

    public String getGoalDescription() {
        return ("goToDispenser");
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

        DSPlan newPlan;

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

    DSPlan makePlanGoForBlock(DSAgent agent) {

        Point blockAt = agent.nearestFreeBlock(PBlockType);

        if (blockAt == null) return (null);

        // Make plan
        String neighbPos = agent.getMap().getFreeNeighbour(blockAt, agent);
        String neighbPosOp = agent.getMap().oppositeDirection(neighbPos);
        Point out = DSPerceptor.getPositionFromDirection(neighbPos);
        Point position = new Point(blockAt.x + out.x, blockAt.y + out.y);
        DSPlan newPlan = astarGroup("goForBlock", 2, agent, position, agent.getBody());
        if (newPlan != null) {
            newPlan.setPriority(1000 - newPlan.getLength());
            DSAttach newAttach = new DSAttach(neighbPosOp, PBlockType);
            newPlan.appendAction(newAttach);

            return (newPlan);
        }
        return (null);
    }

    DSPlan makePlanGrabBlock(DSAgent agent) {

        return (null);
    /*
            String direction=agent.getMap().getFreeNeighbourObject(DSCell.__DSBlock+PBlockType, agent);
            if(direction=="")
                return(null);
            // Make plan
            DSPlan newPlan=new DSPlan("grabBlock",3);
            DSAttach newAttach = new DSAttach( direction, PBlockType);
                newPlan.appendAction(newAttach);
                return (newPlan);
    */
    }

  /*
         Plan revision
  */

    @Override
    public boolean revisePlans(DSAgent agent) { // true -> plans modified

        // removes empty (non-final) plans
        for(String plan:PPlans.keySet())
            if(PPlans.get(plan).isEmpty())
                PPlans.remove(plan);

        boolean modified = false;

        if (agent.getBody().blockAttached(PBlockType) != null) {
            setPlansToSuccess();
            return (true);
        }

        if (!PPlans.containsKey("goForBlock")) {
            DSPlan gtdPlan = makePlanGetFromDispenser(agent);
            if (gtdPlan != null) {
                modified = true;
                PPlans.put("goForBlock", gtdPlan);
            }
        }

        /*
        if (!PPlans.containsKey("goForBlock")) {
            DSPlan gbPlan = makePlanGoForBlock(agent);
            if (gbPlan != null) {
                modified = true;
                PPlans.put("goForBlock", gbPlan);
            }
        }

        if (!PPlans.containsKey("grabBlock")) {
            DSPlan gbPlan = makePlanGrabBlock(agent);
            if (gbPlan != null) {
                modified = true;
                PPlans.put("grabBlock", gbPlan);
            }
        }
        */
        return (modified);
    }

    public DSGGetBlock2022(int blockType) {
        super();
        PBlockType = blockType;
        PDispenserLocation = null;
    }

    public DSGGetBlock2022(int dispenserType, Point dispenserLocation) {
        super();
        PBlockType = dispenserType;
        PDispenserLocation = dispenserLocation;
    }
}

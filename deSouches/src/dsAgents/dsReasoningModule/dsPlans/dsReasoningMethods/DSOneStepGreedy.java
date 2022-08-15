package dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSAgentOutlook;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.DSClear;
import dsAgents.dsExecutionModule.dsActions.DSMove;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

import static dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell.__DSDispenser;

public class DSOneStepGreedy {
    public synchronized static DSPlan oneStep(String planName, int priority, DSAgentOutlook outlook,
                                                             Point destination, boolean finalPlan){


        /*
            VERZE JEN PRO JEDEN KROK
                1, Jsem na zadane pozici, vratim OK Plan0
                2, Zvolim smer k cili
                3, Najdu jeden nebo dva smery, kam jit (Manhattan)
                4, Pokud je prazdne v nekterem smeru, zvolim nejaky z prazdnych
                            Udelam akci move v tomto kroku a vratim plan s touto akci(nedokoncenou, false)
                5, Pokud neni, vyberu nejaky a udelam clear akci, vratim jako plan
         */

            Point agentPosition=new Point(0,0);

            /*
                    1
             */
            if(destination==agentPosition)
                // empty plan will succeed
                return(new DSPlan(planName, priority, finalPlan));

            /*
                    2
             */

            int dx=0;
            int dy=0;


            if (agentPosition.x > destination.x)
                dx=-1;
            else
            if (agentPosition.x < destination.x)
                dx=1;


            if (agentPosition.y > destination.y)
                dy=-1;
            else
            if (agentPosition.y < destination.y)
                dy=1;


        /*
                Find free direction, 6 a 7
        */


            DSPlan plan = new DSPlan("goForBlock",1,finalPlan);

            if (dx != 0)
                if (!outlook.isObstacleAt(new Point(dx, 0))){
                    DSMove move = new DSMove(DSPerceptor.getDirectionFromPosition(new Point(dx, 0)));
                    plan.appendAction(move);
                    return(plan);
                }


            if(dy!=0)
                if (!outlook.isObstacleAt(new Point(0, dy)))
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



            // should not got there (agent na cilove pozici je osetren vyse)

        return(new DSPlan(planName, priority,finalPlan));

        }

    }

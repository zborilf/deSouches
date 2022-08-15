package dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;

public class DSHybridPathPlanner {

    public static synchronized DSPlan getOneStep(String planName, DSMap map, int priority, DSAgent agent, Point destination,
                                            DSBody finalBody, int timeout, boolean finalPlan) {
        DSPlan plan=new DSPAStar()
                .computePath(
                        planName, priority, map, map.getAgentPos(agent), destination, finalBody, timeout, agent, finalPlan);
        if(plan!=null)
            if(plan.getLength()<=1) {
                agent.printOutput("HP A* One action");
                return (plan);
            }
            else
            {
                agent.printOutput("HP A* More actions");

                DSPlan plan2=new DSPlan(planName, priority, finalPlan);
                /*
                plan2.appendAction(plan.getAction());
                return(plan2);
                 */
                // zkusim to vratit komplet, pak je ale zbytecne i to prvni if
                return(plan);
            }

            // one step is always false

        plan=DSOneStepGreedy.oneStep(planName,priority,agent.getOutlook(),destination, false);
            if(plan!=null) {
                agent.printOutput("HP Greedy");
                return (plan);
            }

            // Astar and greedy failed, need to do some alternative
        return(null);   // at this momen it just return null -> 'fail plan'
    }

}

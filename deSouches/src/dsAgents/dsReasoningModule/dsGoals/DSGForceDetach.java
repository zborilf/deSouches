package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsExecutionModule.dsActions.DSDetach;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.util.LinkedList;


public class DSGForceDetach extends DSGGoal{

    int PStage=0;

    @Override
    public String getGoalDescription() {
        return ("forceDetachGoal"+PStage);
    }

    @Override
    public boolean revisePlans(DSAgent agent) {
        if(agent.getStep()<10) {
            setPlansToSuccess();
            return(true);
        }
        //LinkedList<String> directions = agent.getBody().getAllDirectionsAttached();
        DSPlan detachAllPlan = new DSPlan("forceDdetach", PStage,false);
        DSDetach detach;
        switch(PStage){
            case 0: {
                detach = new DSDetach("s");
                detachAllPlan.insertAction(detach);
                PPlans.put("forceDetachS", detachAllPlan);
                PStage = 1;
                return (true);
            }
            case 1: {
                    detach = new DSDetach("n");
                    detachAllPlan.insertAction(detach);
                    PPlans.put("forceDetachN", detachAllPlan);
                    PStage = 2;
                    return (true);
                }
            case 2: {
                detach = new DSDetach("e");
                detachAllPlan.insertAction(detach);
                PPlans.put("forceDetachE", detachAllPlan);
                PStage = 3;
                return (true);
            }
            case 3: {
                detach = new DSDetach("w");
                detachAllPlan.insertAction(detach);
                PPlans.put("forceDetachW", detachAllPlan);
                PStage = 4;
                return (true);
            }
            case 4: {
                PStage = 5;
                setSubgoal(new DSGDetachAll()); // for sure
            }
            case 5:
                setPlansToSuccess();
        }
        return true;
    }
}

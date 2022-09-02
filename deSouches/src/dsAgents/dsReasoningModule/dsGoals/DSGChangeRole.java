package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

public class DSGChangeRole extends DSGGoal{

    static final int __timeprovided=100;

    public String getGoalDescription(){
        return("Goal change role");
    }

    @Override
    public boolean revisePlans(DSAgent agent) {
        if(!agent.getActualRole().contentEquals("default"))
            PPlans.put("role changed", new DSPlan("role changed",3));


        agent.getMap().nearestObject(DSCell.__DSRoleArea, agent.getMapPosition());
        DSGGoal subGoal = new DSGApproachTypes(
                agent.getMap().nearestObject(DSCell.__DSRoleArea, agent.getMapPosition()),
                agent.getStep()+__timeprovided);
        setSubgoal(subGoal);
        return(true);
    }
}

package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import dsMultiagent.dsTasks.DSTaskType;

public class DSGConnectGoal extends DSGoal{

    private DSPlan PPlan;
    private DSTaskType PTaskType;
    private String PTaskName;
    private int PNOLeutnant;

    @Override
    public String getGoalName() {
            return("customGoal");
    }

    @Override
    public boolean revisePlans(DSAgent agent) {
        if (PPlans.containsKey("customGoal"))
            return false;
        DSPlan plan = PTaskType.dancePlan(agent,PTaskName);
        if(plan!=null)
        {
            PPlans.put("customGoal", plan);
            return (true);
        }
        return(false);
    }

    public DSGConnectGoal(DSTaskType taskType, int nOLeutnant, String taskName){
        PNOLeutnant=nOLeutnant;
        PTaskType=taskType;
        PTaskName=taskName;
    }

}

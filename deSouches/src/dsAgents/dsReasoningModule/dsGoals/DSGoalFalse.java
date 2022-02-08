package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;

public class DSGoalFalse extends DSGoal {
    @Override
    public String getGoalName() {
        return("false");
    }

    @Override
    public int goalStatus() { return(DSGoal.__DSGExecutionFailed);}

    @Override
    public boolean revisePlans(DSAgent agent) {
        return false;
    }
}

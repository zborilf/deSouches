package dsAgents.dsReasoningModule.dsGoals;

import dsAgents.DSAgent;

public class DSGChangeRole extends DSGoal {

    String PRole;

    public boolean goalAchieved(){
        return(PLastStatus==__DSGGoalAchieved);
    }


    @Override
    public String getGoalDescription() {
        return null;
    }

    @Override
    public boolean revisePlans(DSAgent agent) {
        if(agent.getActualRole().compareTo(PRole)==0){
            PLastStatus=__DSGGoalAchieved;
        }
//        if(if(PPlans.containsKey("changeRole"))
            return false;
    }

    public DSGChangeRole(String role){
        PRole=  role;
    }
}

package dsAgents.dsReasoningModule.dsPlans;

import java.util.LinkedList;

import deSouches.utils.HorseRider;
import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsExecutionModule.dsActions.DSAction;


/*
%	Plan is a sequence of actions
%	Goal when created makes a plan / instance of this and fills it up with actions, may use also A* algorithm and others ...
*/

public class DSPlan {


    private static final String TAG = "DSPlan";
    LinkedList<DSAction> PLinearPlan;
    int PPriority;      // priorita planu
    DSAction PLastExternal=null;
    DSGoal PSubGoal=null;
    String PPlanName;
    boolean PWaitingForFeedback=false;
    boolean PPlanSuceeded=false;

    public String plan2text(){
        String st="";
        for(DSAction action:PLinearPlan) {
            st = st.concat(action.actionText() + " ; ");
        }
        return(st);
    }

    public void printPlan(DSAgent agent) {
        HorseRider.inquire(TAG, "printPlan: "+"PLAN pro agenta "+agent.getEntityName()+">> "+plan2text());
    }



    public int getPriority(){
        return(PPriority);
    }

    public String getName(){
        return(PPlanName);
    }

    public boolean planSuceeded(){
        return(PPlanSuceeded);
    }

    public DSAction getAction() {
        return(PLinearPlan.getFirst());
    }

    public boolean waitingForFeedback(){
        return(PWaitingForFeedback);
    }

    public boolean demandsSubgoal(){
        return(PSubGoal!=null);
    }

    public DSGoal getSubGoal() {return(PSubGoal);}




    public boolean appendAction(DSAction action) {
        PLinearPlan.add(action);
        return(true);
    }

    public boolean insertAction(DSAction action) {
        PLinearPlan.add(0,action);
        return(true);
    }

    public int getLength(){
        return(PLinearPlan.size());
    }

    public void setPriority(int priority){
        PPriority=priority;
    }

    public DSAction popAction(){
        DSAction action=PLinearPlan.pop();
        return(action);
    }

    public void effectAndDeleteAction(DSAgent agent)
    {
        if(!PLinearPlan.isEmpty()) {
            DSAction action = popAction();
            action.succeededEffect(agent);
        }
        if(PLinearPlan.isEmpty())
            PPlanSuceeded=true;
    }

    public boolean externalActionSucceeded(DSAgent agent){ // muze se rozsirit o popis akce a parametry, pro kontrolu

        if(PWaitingForFeedback) {
            effectAndDeleteAction(agent);
            PWaitingForFeedback = false;
            return(true);
        }
        else
            return(false);
    }

    public boolean externalActionFailed(DSAgent agent){
        if(PWaitingForFeedback)
            PWaitingForFeedback = false;
        return(true);
    }

    public boolean executeOneStep(DSAgent agent){  // akce odpalena

        if(!PLinearPlan.isEmpty()) {

            if(waitingForFeedback() || demandsSubgoal()) {
     //        HorseRider.warn(TAG, "executeOneStep: "+agent.getEntityName()+" step "+ agent.getStep()+
     //                   "exe of "+PLinearPlan.getFirst() + "failed, "+waitingForFeedback() + demandsSubgoal() + PLinearPlan.isEmpty());
                return (false);
            }

            DSAction action=PLinearPlan.getFirst();
            DSGoal subgoal=action.execute(agent);

        // subgoal ...  co udelat, aby tato akce mohla byt odstranena
        //          ... true, vse ok, muze byt odstranena
        //          ... false, nejde akci vykonat, za zadncyh okolnosti
        //          ... subgoal, tento subgoal musi byt splnen, pak je splnena i akce

            if(subgoal.getGoalName().contentEquals("false")){
            // TODO to, ze zde je jen return zpusobuje, ze se fail akce opakuji znovu a znovu
            //  plan by mel byt             PLinearPlan.clear(); ale jak odlisit od uspechu, kde je
                return(false);
            }
            if(!subgoal.getGoalName().contentEquals("true")) {
                // TODO, tak kde se uspesna akce popne z planu?
                PSubGoal = subgoal;
                return(true);
            }
            if(action.isExternal()) {
                PLastExternal = action;
                PWaitingForFeedback = true;
                return(true);
            }
        }
        effectAndDeleteAction(agent);
        return(true);
    }



    public DSPlan(String planName, int priority) {
        PPlanName=planName;
        PPriority=priority;
        PLinearPlan = new LinkedList<DSAction>();
        PSubGoal=null;
    }

}


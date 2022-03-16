package dsAgents.dsReasoningModule.dsIntention;

import java.util.LinkedList;

import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

public class DSIntention {

        // spravuje hierarchii cilu, pokud nejaky(!) plan je vykonavan a vyzaduje podcil, je puvodni cil
	// zablokovan vcetne vsech jeho planu (tohle ale nevypada pekne, to by asi melo byt blokovano
	// podcile ale stajne nejsou doposud vubec uvazovany, takze mozna je to vse zbytecne
	// 
        // osetri pripojeni pozadovaneho subgoalu
        // vesmes jenom propaguje vykonani akce, 
	// odstrani podcile, pokud jsou dosazeny
        // vraci Goal, pokud je potreba zpetne vazby




    private static final String TAG = "DSIntention";
    LinkedList<DSGoal> PIntentionStack=new LinkedList<DSGoal>();
    public static int __Intention_NoPlan=0;
    public static int __Intention_InProcess=1;
    public static int __Intention_Finished=2;
    public static int __Intention_Failed=3;


    public DSGoal getTLG(){
        return(PIntentionStack.getLast());
    }

    public String description(){

        if(PIntentionStack.isEmpty())
            return("no intention");

        return(PIntentionStack.getFirst().getGoalName());
    }

    public int intentionState() {
        DSGoal TLG=PIntentionStack.getLast();
        if (TLG.goalAchieved()) {
            return (__Intention_Finished);
        }else
            if((
                    TLG.goalStatus()==DSGoal.__DSGPlanningFailed)||
                    (TLG.goalStatus()==DSGoal.__DSGExecutionFailed)||
                    (TLG.goalStatus()==DSGoal.__DSGMovePathFailed))
                        return(__Intention_Failed);

        return (__Intention_InProcess);
    }


    public boolean intentionExecutionFeedback(int actionResult, DSAgent agent){
       // System.out.println("intentionExecutionFeedback: "+"Feeedback result in step "+agent.getStep()+
       //             " for "+agent.getEntityName()+" is "+actionResult);
        if(PIntentionStack.getFirst().goalStatus()==DSGoal.__DSGFeedbackNeeded) {
            PIntentionStack.getFirst().executionFeedback(actionResult, agent);
            return(true);
        }
            return(false);
    }

    public DSPlan getRecentPlan(){
        return(PIntentionStack.getFirst().getRecentPlan());
    }

    public DSGoal executeIntention(DSAgent agent) {


        // needs a subgoal? insert subgoal and go on
        // execute goal
        // goal achieved -> if not TLG, remove
        // return goal (if feedback needed), else null

        DSGoal actualGoal=PIntentionStack.getFirst();
        DSGoal subGoal=null;


        if(actualGoal.goalStatus()==DSGoal.__DSGSubgoalNeeded)
        {
            subGoal = actualGoal.getSubGoal();
            PIntentionStack.push(subGoal);
            actualGoal = subGoal;
        }

        actualGoal.followGoal(agent);

        // plan succesfully finished? Ok, get back one step
        if((actualGoal.goalAchieved())&&
                (PIntentionStack.size()>1)) // dont pop TLP!
            PIntentionStack.pop();

        // zde si nejsem jistej, asi obsolete
        if(actualGoal.goalStatus()==DSGoal.__DSGFeedbackNeeded)
            return(actualGoal);
        else
            return(null);
    }


    public DSIntention(DSGoal goal) {
        PIntentionStack.add(goal);
    }
}

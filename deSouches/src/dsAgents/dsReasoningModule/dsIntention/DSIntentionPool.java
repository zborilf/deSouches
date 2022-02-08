package dsAgents.dsReasoningModule.dsIntention;

import deSouches.utils.HorseRider;
import dsAgents.DSAgent;

import java.util.LinkedList;

public class DSIntentionPool {
    private static final String TAG = "DSIntentionPool";
    LinkedList<DSIntention> PIntentions = new LinkedList<DSIntention>();
    DSIntention PLastIntention;

    public String description(){
        String st="";
        for(DSIntention intent:PIntentions)
            st=st+intent.description()+" / ";
        return(st);
    }

    public void adoptIntention(DSIntention intention){
        PIntentions.add(intention);
    }

    public DSIntention getIntention(){
        if(!PIntentions.isEmpty())
            return(PIntentions.getFirst()); // zde asi podle priority, nebo round-robin
        else
            return(null);
    }

    public void removeIntention(DSIntention intention){
        if(intention!=null)
            if(PIntentions.contains(intention))
                   PIntentions.remove(intention);
    }

    public void clearPool(){
        PIntentions.clear();
    }

    public DSIntention executeOneIntention(DSAgent agent){   // vraci null -> intensna nesplnena, nebo TLG (top level goal) splnene intensny
        DSIntention intention=getIntention();
        if(intention==null) {
            HorseRider.inform(TAG, agent.getEntityName()+" executeOneIntention: "+"Zadnou intensnu nemame");
            return (null);
        }
        else
        if(intention.intentionState()==DSIntention.__Intention_Finished){
            PIntentions.remove(intention);
            return(intention);
        }
        else {
            intention.executeIntention(agent);
            PLastIntention=intention;
        }
        return(intention);
    }

    public DSIntentionPool(){
    }

}

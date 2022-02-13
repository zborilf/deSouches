package dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods;

import java.awt.Point;

import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsExecutionModule.dsActions.DSAction;

public class DSAStarItem {

    Point PPosition;
    DSAStarItem PPrevious;
    DSAction PAction;
    DSBody PBody;
    int PHeuristic;
    int PGCost;

    public Point getPosition(){ return(PPosition);}

    public DSBody getBody() {
        return(PBody);
    }


    public DSAction getAction() {
        return(PAction);
    }

    public DSAStarItem getPrevious() {
        return(PPrevious);
    }

    public int getCost() {
        return(PGCost);
    }

    public int getGCost() {
        return(PGCost);
    }

    public void setHeuristic(int heuristic){ PHeuristic=heuristic;}

    public int getHeuristic(){return(PHeuristic);}

    public void setGCost(int gcost){ PGCost=gcost;}


    public DSAStarItem(Point position, DSAStarItem previous, DSAction action, DSBody body, int gCost, int heuristic){
        PBody=body;
        PPosition=position;
        PAction=action;
        PPrevious=previous;
        PGCost=gCost;
        PHeuristic=heuristic;
    }
}

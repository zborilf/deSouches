package dsAgents.dsExecutionModule.dsActions;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoalFalse;
import dsAgents.dsReasoningModule.dsGoals.DSGoalTrue;
import dsAgents.dsReasoningModule.dsPlans.dsReasoningMethods.DSAStarItem;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Identifier;

import java.awt.*;


public class DSMove extends dsAgents.dsExecutionModule.dsActions.DSAction {
    private final String PDirection;
    int PDx=0;
    int PDy=0;

    @Override
    public DSGoal execute(DSAgent agent) {
        Action a = new Action("move", new Identifier(PDirection));
        try {
            agent.getEI().performAction(agent.getJADEAgentName(), a);
        } catch (ActException e) {
            return(new DSGoalFalse());
        }
        return(new DSGoalTrue());
    }

    @Override
    public DSAStarItem simulate(DSMap map, DSAStarItem item, DSBody agentBody, int step) {
        // TODO Auto-generated method stub
        Point point=item.getPosition();
        DSBody body=item.getBody();
        Point newPosition=new Point(point.x+PDx,point.y+PDy);
        if(map.isObstacleAt(newPosition, agentBody, body, step))
            return(null);
        return(new DSAStarItem(newPosition,item,this,body,0,0));
    }

    public void succeededEffect(DSAgent agent){

        DSMap map=agent.getMap();
        map.moveBy(PDx,PDy);
        agent.moveBy(PDx,PDy);
    }

    @Override
    public boolean isExternal(){return(true);}

    @Override
    public String actionText() {
        return("Move ["+PDx+","+PDy+"]");

    }


    public DSMove(EnvironmentInterfaceStandard ei, String direction){
        super(ei);
        PDirection=direction;
        Point pos= DSPerceptor.getPositionFromDirection(PDirection);
        PDx=pos.x;PDy=pos.y;
    /*    PDx=0; PDy=0;
        if(PDirection.contentEquals("n"))
            PDy=-1;
        else
        if(PDirection.contentEquals("s"))
            PDy=1;
        else
        if(PDirection.contentEquals("w"))
            PDx=-1;
        else
        if(PDirection.contentEquals("e"))
            PDx=1;*/
    }

    public String getPlannedDirection(){
        return PDirection;
    }

}

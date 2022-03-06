package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsReasoningModule.dsGoals.DSClearGoal;
import dsAgents.dsReasoningModule.dsGoals.DSGoToPosition;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;

import java.awt.*;


public class DSSSeekAndDestroy extends DSScenario {

    final static int _Seek=1;
    final static int _Destroy=2;

    int PState=_Seek;
    DSAgent PAgent;
    String PDirection;
    Point PObstacleAt, PAttackPossition;

    @Override
    public void goalCompleted(DSAgent agent, DSGoal goal) {
        if (PState == _Destroy){
            initScenario(PAgent.getStep());
        return;
    }
        //PCommander.scenarioCompleted(this);
        if(PState==_Seek) {
            PState = _Destroy;
            Point direction=PAgent.getMap().objectAroundCell(PAgent.getPosition(),DSCell.__DSObstacle);
            if(direction==null) {
                initScenario(PAgent.getStep());
                return;
            }
            Point targetPosition=new Point(PAgent.getPosition().x+direction.x,PAgent.getPosition().y+direction.y);
            if(PAgent.getMap().objectAroundCell(targetPosition,DSCell.__DSEntity_Friend)!=null)
                initScenario(PAgent.getStep());
            else{
                Point relDirection=
                        new Point(direction.x-PAgent.getPosition().x,direction.y-PAgent.getPosition().y);
                System.out.println(PAgent.getEntityName() + " Odpalim ve smeru " + relDirection);
                PAgent.hearOrder(new DSClearGoal(relDirection));
             }
            //}
        }
    }

    @Override
    public void goalFailed(DSAgent agent, DSGoal goal) {
            initScenario(agent.getStep());
    }

    @Override
    public boolean checkEvent(DSAgent agent, int eventType){
        return(true);
    }

    @Override
    public boolean initScenario(int step) {
        PState=_Seek;
        System.out.println(PAgent.getEntityName()+"Seeeek and destroy");
        Point guard=PAgent.getGroup().getUGuardedGoalArea();
        Point PObstacleAt=PAgent.getMap().nearestObject(DSCell.__DSObstacle,PAgent.getGroup().getUGuardedGoalArea());
        Point PAttackPosition=PAgent.getMap().getFreeNeighbourPosition(PObstacleAt,PAgent);
        if(PAttackPosition!=null){
            System.out.println(PAgent.getEntityName()+" znicim blizky "+guard+" blok na "+
                    PObstacleAt+" z"+PAttackPosition+", master="+ PAgent.getGroup().getMaster());
            DSGoToPosition sADGoal = new DSGoToPosition(PAttackPosition, PAgent.getBody());
            PAgent.hearOrder(sADGoal);
            return true;
        }
        return(false);
    }

    public DSSSeekAndDestroy(DSAgent agent){
        super(agent.getCommander(),agent.getGroup(), null,0);
        PAgent=agent;
    }

}

package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsExecutionModule.dsActions.DSConnect;
import dsAgents.dsExecutionModule.dsActions.DSSubmit;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import java.awt.*;
import java.util.LinkedList;

public abstract class DSTaskType {

    DSBody PTaskArea;
    DSBody PTaskShape;
    int PTaskTypeNumber;
    String PTaskName;
    protected DSAgent PMaster;
    protected DSAgent PLeutnant1;
    protected DSAgent PLeutnant2;
    protected DSAgent PLeutnant3;
    protected Point PMasterGoalPosition;
    protected Point PLeutnant1GoalPosition;
    protected Point PLeutnant2GoalPosition;
    protected Point PLeutnant3GoalPosition;
    protected Point PMasterDispenserPosition;
    protected Point PLeutnant1DispenserPosition;
    protected Point PLeutnant2DispenserPosition;
    protected Point PLeutnant3DispenserPosition;
    protected int PMasterBlockType;
    protected int PLeutnant1BlockType;
    protected int PLeutnant2BlockType;
    protected int PLeutnant3BlockType;

    DSPlan makePlanL1(){
        return null;
    }

    DSPlan makePlanL2(){
        return null;
    }

    DSPlan makePlanL3(){
        return null;
    }

    // vzdy nakonec konektne jen s L1

    protected DSPlan makePlanSubmit(String taskName){
        DSPlan plan=new DSPlan("Master, submit",2);
        DSSubmit submit=new DSSubmit(PMaster.getEI(),taskName);
        plan.appendAction(submit);
        return(plan);
    }

    protected DSPlan makePlanMaster(String taskName){
        DSPlan plan=new DSPlan("Master, connect",2);
        DSConnect connect=new DSConnect(PMaster.getEI(),"s",PLeutnant1.getEntityName());
        plan.appendAction(connect);
        return(plan);
    };

    public abstract Point blockPosition(int agent);

    public String getConnectDirection(DSAgent agent){
        Point agentPos=blockPosition(agent);
        Point blockPos=formationPosition(agent,new Point(0,0));
        Point blockDir=new Point(-blockPos.x+agentPos.x,-blockPos.y+agentPos.y);
        return(DSPerceptor.getDirectionFromPosition(blockDir));
    }

    public DSBody getSoldierGoalBody(DSAgent agent){
        Point agentPos=blockPosition(agent);
        Point blockPos=formationPosition(agent,new Point(0,0));
        Point blockDir=new Point(-blockPos.x+agentPos.x,-blockPos.y+agentPos.y);
        return(DSBody.getDoubleBody(blockDir));
    };


    public Point blockPosition(DSAgent agent) {
        if(agent==PMaster)
            return(blockPosition(1));
        if(agent==PLeutnant1)
            return(blockPosition(2));
        if(agent==PLeutnant2)
            return(blockPosition(3));
        if(agent==PLeutnant3)
            return(blockPosition(4));
        return(null);
    }


    public int getBlockType(int agent, DSBody body){
        Point position=this.blockPosition(agent);
        int type=body.getCellType(position);
        return(type - DSCell.__DSBlock);
    }

    public int getBlockType(DSAgent agent, DSBody body){
        return(body.getCellType(this.blockPosition(agent))- DSCell.__DSBlock);
    }

    public LinkedList<Integer> getTypesNeeded(DSBody taskBody){
        LinkedList<Integer> typesNeeded=new LinkedList<Integer>();
        int type;
        for(int i=1;i<taskBody.getBodyList().size();i++) {
            type = getBlockType(i, taskBody);
            typesNeeded.add(type);
        }
        return(typesNeeded);
    }



    public DSBody getSoldierGoalBody(){
        return(null);
    };



    public abstract DSBody getTaskBody(); // tvar tasku


    public DSBody getTaskArea(){
        return(PTaskArea);
    }; // plocha nutna pro sestaveni tasku

    public int getTaskType(){
        return(PTaskTypeNumber);
    }

    public DSPlan dancePlan(DSAgent agent,String taskName){
        if(agent==PMaster)
            return(makePlanMaster(taskName));
        if(agent==PLeutnant1)
            return(makePlanL1());
        if(agent==PLeutnant2)
            return(makePlanL2());
        if(agent==PLeutnant3)
            return(makePlanL3());
        return(null);
    } // po serazeni - jak zatancit


    public abstract Point formationPosition(DSAgent agent, Point position);

    public DSBody agentFormationBody(DSAgent agent){
        return(null);
    }

    public void setMaster(DSAgent agent){
        PMaster=agent;
    }

    public void setLeutnant1(DSAgent agent){
        PLeutnant1=agent;
    }

    public void setLeutnant2(DSAgent agent){
        PLeutnant2=agent;
    }

    public void setLeutnant3(DSAgent agent){
        PLeutnant3=agent;
    }


    public DSTaskType(){
        PTaskShape=new DSBody();
        PTaskArea=new DSBody();
    }

}

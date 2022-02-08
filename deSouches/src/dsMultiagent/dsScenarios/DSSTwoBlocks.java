package dsMultiagent.dsScenarios;

import deSouches.utils.HorseRider;
import dsAgents.*;
import dsAgents.DeSouches;
import dsAgents.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsMultiagent.DSGroup;
import dsMultiagent.dsTasks.DSTask;

import java.awt.*;
import java.util.LinkedList;

public class DSSTwoBlocks extends DSSBlockScenarios {
    private static final String TAG = "DSTwoBlocks";
    // dostane Task a k tomu Skupinu
    //


    int PStateM, PStateL1;
    DSAgent PMaster;
    DSAgent PLeutnant1;
    Point PMasterGoalPos;
    Point PMasterDispenserPos;
    DSBody PMasterGoalBody;
    Point PSlaveGoalPos;
    Point PSlaveDispenserPos;
    DSBody PSlaveGoalBody;

    int PType1;
    int PType2;


    @Override
    public void goalCompleted(DSAgent agent, DSGoal goal) {
        HorseRider.inform(TAG, "goalCompleted: " + "SCEN: Task te chvali, agente " + agent.getEntityName() + " za " + goal.getGoalName());
        if (agent == PMaster) {
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType1));
            if (goal.getGoalName().contentEquals("detachAllGoal")) {
                PStateM=1;
                agent.hearOrder(new DSGetBlock(PType1, PMasterDispenserPos));
            }

            if (goal.getGoalName().contentEquals("goToDispenser")) {
                PStateM = 2;
                agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody));
            }
            if (goal.getGoalName().contentEquals("goToPosition")){
                PStateM = 3;
            }
            if (goal.getGoalName().contentEquals("connectAndDetach")) {
                agent.hearOrder(new DSAttachAndSubmit(PTask.getName(), "s", PType1));
                PStateM=4;
            }
            if (goal.getGoalName().contentEquals("attachAndSubmit")) {
                PStateM=5;
                PCommander.scenarioCompleted(this);
            }
        }
        if (agent == PLeutnant1) {
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType1));
            if (goal.getGoalName().contentEquals("detachAllGoal")) {
                PStateL1 =1;
                agent.hearOrder(new DSGetBlock(PType2, PSlaveDispenserPos));
            }

            if (goal.getGoalName().contentEquals("goToDispenser")){
                PStateL1 = 2;
            agent.hearOrder(new DSGoToPosition(PSlaveGoalPos, PSlaveGoalBody));
            }
            if (goal.getGoalName().contentEquals("goToPosition"))
                PStateL1 =3;
            if (goal.getGoalName().contentEquals("connectAndDetach")) {
                agent.hearOrder(new DSGoalRoam(10));
                PStateL1 =4;
            }

        }
        if ((PStateM == 3)&&(PStateL1 ==3)) {
            // zde ma byt dance pro vsechny cleny, vysledkem je vhodny plan
            PMaster.hearOrder(new DSGConnectAndDetach("s", PLeutnant1.getEntityName()));
            if (PTaskType == 1)
                PLeutnant1.hearOrder(new DSGConnectAndDetach("n", PMaster.getEntityName()));
            if (PTaskType == 2)
                PLeutnant1.hearOrder(new DSGConnectAndDetach("s", PMaster.getEntityName()));
            if (PTaskType == 3)
                PLeutnant1.hearOrder(new DSGConnectAndDetach("s", PMaster.getEntityName()));

        }
    }

    @Override
    public void goalFailed(DSAgent agent, DSGoal goal) {
        HorseRider.warn(TAG, "goalFailed: " + "SCEN: Task to je smula agente " + agent.getEntityName() + " za " + goal.getGoalName());
        if (agent == PMaster) {
            if (goal.getGoalName().contentEquals("goToDispenser")) {
                agent.hearOrder(new DSGoalRoam(4));
                PStateM=1;
            }
            if (goal.getGoalName( ).contentEquals("goToPosition")) {
                PStateM = 1;
                agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody));            }
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType1));
        }
        if (agent == PLeutnant1) {
            if (goal.getGoalName().contentEquals("goToDispenser")) {
                PStateL1 =1;
                agent.hearOrder(new DSGoalRoam(4));
            }
            if (goal.getGoalName().contentEquals("goToPosition")) {
                PStateL1 = 2;
                agent.hearOrder(new DSGoToPosition(PSlaveGoalPos, PSlaveGoalBody));
            }
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType2));
        }
    }


    
    public boolean checkEvent(DSAgent agent, int eventType){

        switch(eventType) {
            case DSScenario._disabledEvent:
                if (PMaster == agent) {
                    agent.hearOrder(new DSGetBlock(PType1));
                    PStateM = 1;
                }
                if (PLeutnant1 == agent) {
                    agent.hearOrder(new DSGetBlock(PType2));
                    PStateL1 = 1;
                }
                return(true);

            case DSScenario._noBlockEvent:
                if (PMaster == agent) {
                    PMaster.getBody().resetBody();
                    if (PStateM == 2) {
                        PStateM = 1;
                        agent.hearOrder(new DSGetBlock(PType1, PMasterDispenserPos));
                    }
                }
                if (PLeutnant1 == agent) {
                    PLeutnant1.getBody().resetBody();
                    if (PStateL1 == 2) {
                        PStateL1 = 1;
                        agent.hearOrder(new DSGetBlock(PType2, PSlaveDispenserPos));
                    }
                }
                return(true);
        }
        return(false);
    }
    

    boolean allocateAgents(int step){

        Point positionG;

        positionG=PGroup.getGoalArea(PTask,step);// PGroup.allObjects(DSCell.__DSGoal).getFirst();
        if(positionG==null)
            return(false);

        PMasterGoalPos = positionG;

        DSOptimizer optim=new DSOptimizer(PGroup.getFreeAgents(PPriority),positionG ,PTask, PGroup.getMap());
        LinkedList<DSODispenserGoalMission> missions=optim.getOptimalAgents();

        PMaster = missions.get(0).getAgent();
        PTask.getTaskType().setMaster(PMaster);
        PLeutnant1 = missions.get(1).getAgent();
        PTask.getTaskType().setLeutnant1(PLeutnant1);

        PAgentsAllocated.add(PMaster);
        PAgentsAllocated.add(PLeutnant1);
        PMasterDispenserPos = missions.get(0).getDispenserPosition();
        PSlaveDispenserPos = missions.get(1).getDispenserPosition();
        PType1=missions.get(0).getDispenserType();
        PType2=missions.get(1).getDispenserType();
        PSlaveGoalPos = PTask.getTaskType().formationPosition(PLeutnant1,PMasterGoalPos);


        return(true);
    }


    @Override
    public boolean initScenario(int step) {

        if(!allocateAgents(step))
            return(false);

        // posunout leutnanty na spravnou goalpozici
        // nastavit goalbody
        PMasterGoalBody=PTask.getTaskType().getSoldierGoalBody(PMaster);
        PSlaveGoalBody=PTask.getTaskType().getSoldierGoalBody(PLeutnant1);

        // task types
        PMaster.hearOrder(new DSGDetachAll());
        PLeutnant1.hearOrder(new DSGDetachAll());

        // setscenario agentum
        PMaster.setScenario(this);
        PLeutnant1.setScenario(this);

        // nastaveni stavu automatu pro tento scenar
        PStateM=1;
        PStateL1 =1;


        System.out.println("initScenario: " + "\n!@!  TASK:" + PTask.getName() + " type "+PTask.getTaskTypeNumber()+" / "+
                ", Group master"+PMaster.getGroup().getMaster()+"\n@@Master " + PMaster.getEntityName() +
                "  pujde na " + PMasterDispenserPos + " pro " + PType1 + " a do golu " + PMasterGoalPos +
                " body is "+PMasterGoalBody.bodyToString()+
                ",\n@@ Leutnant1 " + PLeutnant1.getEntityName() + "  pujde na " + PSlaveDispenserPos + " pro " + PType2 +
                " a do golu " + PSlaveGoalPos+" body is "+PSlaveGoalBody.bodyToString());


        return(true);
    }


/*
    @Override
    public boolean initScenario(int step) { //(DSAgent master, Point positionM, Point positionGM, int type1, DSAgent slave, Point positionS,
        //Point positionGS, int type2){

        // Two blocks -> Three pos. configurations
        if(PTask.getTypesNeeded().size()!=2)
            return(false);

        if(!allocateAgents(step))
            return(false);

        if (PScenarioType == 1) {
            PSlaveGoalPos.y = PSlaveGoalPos.y + 3;
        }
        if (PScenarioType == 2) {
            PSlaveGoalPos.x = PSlaveGoalPos.x + 1;
        }
        if (PScenarioType == 3) {
            PSlaveGoalPos.x = PSlaveGoalPos.x - 1;
        }

        System.out.println("initScenario: " + "\n!+!  TASK:" + PTask.getName() + " type "+PTask.getTaskTypeNumber()+" / "+
                ", Group master"+PMaster.getGroup().getMaster()+"Master " + PMaster.getEntityName() +
                "  pujde na " + PMasterDispenserPos + " pro " + PType1 + " a do golu " + PMasterGoalPos +
                "a Slave " + PSlave.getEntityName() + "  pujde na " + PSlaveDispenserPos + " pro " + PType2 + " a do golu " + PSlaveGoalPos);
        PMaster.hearOrder(new DSGDetachAll());
        PSlave.hearOrder(new DSGDetachAll());
        PMasterGoalBody = new DSBody();
        PMasterGoalBody.addCell(new DSCell(0, 1, PType1, 0));
        PSlaveGoalBody = new DSBody();
        if (PScenarioType == 1)
            PSlaveGoalBody.addCell(new DSCell(0, -1, PType2, 0));
        if (PScenarioType == 2)
            PSlaveGoalBody.addCell(new DSCell(0, 1, PType2, 0));
        if (PScenarioType == 3)
            PSlaveGoalBody.addCell(new DSCell(0, 1, PType2, 0));
        PMaster.setScenario(this);
        PSlave.setScenario(this);
        PStateM=1; PStateS=1;
        return(true);
    }
*/



    public DSSTwoBlocks(DeSouches commander, DSGroup group, DSTask task, int taskType){
        super(commander, group, task, taskType);
        PPriority=2;
    }

}

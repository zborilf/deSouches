package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsMultiagent.DSGroup;
import dsMultiagent.dsTasks.DSTask;
import dsMultiagent.dsTasks.DSTaskType;

import java.awt.*;
import java.util.LinkedList;

public class DSSFourBlocks extends DSSBlockScenarios{
    private static final String TAG = "DSThreeBlocks";


    private static final int _RoleMaster=0;
    private static final int _RoleLeutnant1=1;
    private static final int _RoleLeutnant2=2;

    private int PStateM, PStateL1, PStateL2, PStateL3;
    private DSAgent PMaster;
    private DSAgent PLeutnant1, PLeutnant2, PLeutnant3;
    private Point PMasterGoalPos;
    private DSBody PMasterGoalBody;
    private Point PLeutnant1GoalPos;
    private DSBody PLeutnant1GoalBody;
    private Point PLeutnant2GoalPos;
    private DSBody PLeutnant2GoalBody;
    private DSTaskType PTaskType;
    private Point PMasterDispenserPos;
    private Point PLeutnant1DispenserPos;
    private Point PLeutnant2DispenserPos;
    private Point PLeutnant3GoalPos;
    private DSBody PLeutnant3GoalBody;
    private Point PLeutnant3DispenserPos;

    private int PType1, PType2, PType3, PType4;


    @Override
    public void goalCompleted(DSAgent agent, DSGoal goal) {

        System.out.println("goalCompleted: " + "SCEN: Task te chvali, agente " + agent.getEntityName() + " za " + goal.getGoalName());

        if (agent == PMaster) {
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType1));
            if (goal.getGoalName().contentEquals("detachAllGoal")) {
                PStateL1=1;
                agent.hearOrder(new DSGetBlock(PType1, PMasterDispenserPos));
            }
            if (goal.getGoalName().contentEquals("goToDispenser")) {
                PStateM = 2;
                agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody));
            }
            if (goal.getGoalName().contentEquals("goToPosition")){
                PStateM = 3;
                agent.hearOrder(new DSGConnectGoal(PTaskType,2,PTask.getName()));
            }
            if (goal.getGoalName().contentEquals("customGoal")) {
                PStateM=4;
                agent.hearOrder(new DSGSubmitGoal(PTask.getName()));
            }
            if (goal.getGoalName().contentEquals("submitGoal"))
                PCommander.scenarioCompleted(this);
        }


        if (agent == PLeutnant1) {
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType2));

            if (goal.getGoalName().contentEquals("detachAllGoal")) {
                PStateL1=1;
                agent.hearOrder(new DSGetBlock(PType2, PLeutnant1DispenserPos));
            }
            if (goal.getGoalName().contentEquals("goToDispenser")){
                PStateL1 = 2;
                agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody));
            }
            if (goal.getGoalName().contentEquals("goToPosition")) {
                PStateL1 = 3;
                agent.hearOrder(new DSGConnectGoal(PTaskType,2,PTask.getName()));
            }
            if (goal.getGoalName().contentEquals("customGoal")) {
                PStateL1=4;
            }

        }


        if (agent == PLeutnant2) {
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType3));
            if (goal.getGoalName().contentEquals("detachAllGoal")) {
                PStateL1 = 1;
                agent.hearOrder(new DSGetBlock(PType3, PLeutnant2DispenserPos));
            }
            if (goal.getGoalName().contentEquals("goToDispenser")) {
                PStateL2 = 2;
                agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody));
            }
            if (goal.getGoalName().contentEquals("goToPosition")) {
                PStateL2 = 3;
                agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
            }
            if (goal.getGoalName().contentEquals("customGoal")) {
                PStateL2 = 4;
            }
        }

            if (agent == PLeutnant3) {
                if (goal.getGoalName().contentEquals("goRandomly"))
                    agent.hearOrder(new DSGetBlock(PType4));
                if (goal.getGoalName().contentEquals("detachAllGoal")) {
                    PStateL3 = 1;
                    agent.hearOrder(new DSGetBlock(PType4, PLeutnant3DispenserPos));
                }
                if (goal.getGoalName().contentEquals("goToDispenser")) {
                    PStateL3 = 2;
                    agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody));
                }
                if (goal.getGoalName().contentEquals("goToPosition")) {
                    PStateL3 = 3;
                    agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
                }
                if (goal.getGoalName().contentEquals("customGoal")) {
                    PStateL3 = 4;
                }
            }
    }


    @Override
    public void goalFailed(DSAgent agent, DSGoal goal) {

        if (agent == PMaster) {

            if (goal.getGoalName().contentEquals("goToDispenser")) {
                agent.hearOrder(new DSGoalRoam(4));
                PStateM=2;
            }
            if (goal.getGoalName().contentEquals("goToPosition")) {
                PStateM = 3;
                agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody));
            }
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType1));
        }

        if (agent == PLeutnant1) {

            if (goal.getGoalName().contentEquals("goToDispenser")) {
                PStateL1=2;
                agent.hearOrder(new DSGoalRoam(4));
            }
            if (goal.getGoalName().contentEquals("goToPosition")) {
                PStateL1 = 3;
                agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody));
            }
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType2));
        }


        if (agent == PLeutnant2) {

            if (goal.getGoalName().contentEquals("goToDispenser")) {
                PStateL2=2;
                agent.hearOrder(new DSGoalRoam(4));
            }
            if (goal.getGoalName().contentEquals("goToPosition")) {
                PStateL2 = 3;
                agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody));
            }
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType3));
        }

        if (agent == PLeutnant3) {

            if (goal.getGoalName().contentEquals("goToDispenser")) {
                PStateL3=2;
                agent.hearOrder(new DSGoalRoam(4));
            }
            if (goal.getGoalName().contentEquals("goToPosition")) {
                PStateL3 = 3;
                agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody));
            }
            if (goal.getGoalName().contentEquals("goRandomly"))
                agent.hearOrder(new DSGetBlock(PType4));
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

                if (PLeutnant2 == agent) {
                    agent.hearOrder(new DSGetBlock(PType3));
                    PStateL2 = 1;
                }

                if (PLeutnant3 == agent) {
                    agent.hearOrder(new DSGetBlock(PType4));
                    PStateL2 = 1;
                }
                return (true);

            case DSScenario._noBlockEvent:
                if (PMaster == agent){
                    PMaster.getBody().resetBody();
                    if (PStateM == 2) {
                        PStateM = 1;
                        agent.hearOrder(new DSGetBlock(PType1, PMasterDispenserPos));
                    }
                }

                if (PLeutnant1 == agent) {
                    PLeutnant1.getBody().resetBody();
                    if(PStateL1==2) {
                        PStateL1 = 1;
                        agent.hearOrder(new DSGetBlock(PType2, PLeutnant1DispenserPos));
                    }
                }

                if (PLeutnant2 == agent) {
                    PLeutnant2.getBody().resetBody();
                    if(PStateL2==2) {
                        PStateL2 = 1;
                        agent.hearOrder(new DSGetBlock(PType2, PLeutnant2DispenserPos));
                    }
                }

                if (PLeutnant3 == agent) {
                    PLeutnant3.getBody().resetBody();
                    if(PStateL3==2) {
                        PStateL3 = 1;
                        agent.hearOrder(new DSGetBlock(PType3, PLeutnant3DispenserPos));
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
        PTaskType.setMaster(PMaster);
        PLeutnant1 = missions.get(1).getAgent();
        PTaskType.setLeutnant1(PLeutnant1);
        PLeutnant2 = missions.get(2).getAgent();
        PTaskType.setLeutnant2(PLeutnant2);
        PLeutnant3 = missions.get(3).getAgent();
        PTaskType.setLeutnant3(PLeutnant3);
        PAgentsAllocated.add(PMaster);
        PAgentsAllocated.add(PLeutnant1);
        PAgentsAllocated.add(PLeutnant2);
        PAgentsAllocated.add(PLeutnant3);
        PMasterDispenserPos = missions.get(0).getDispenserPosition();
        PLeutnant1DispenserPos = missions.get(1).getDispenserPosition();
        PLeutnant2DispenserPos = missions.get(2).getDispenserPosition();
        PLeutnant3DispenserPos = missions.get(3).getDispenserPosition();
        PType1=missions.get(0).getDispenserType();
        PType2=missions.get(1).getDispenserType();
        PType3=missions.get(2).getDispenserType();
        PType4=missions.get(3).getDispenserType();
        PLeutnant1GoalPos = PTaskType.formationPosition(PLeutnant1,PMasterGoalPos);
        PLeutnant2GoalPos = PTaskType.formationPosition(PLeutnant2,PMasterGoalPos);
        PLeutnant3GoalPos = PTaskType.formationPosition(PLeutnant3,PMasterGoalPos);


        return(true);
    }


    @Override
    public boolean initScenario(int step) {


        if(!allocateAgents(step))
            return(false);
        // posunout leutnanty na spravnou goalpozici

        // nastavit goalbody
        PMasterGoalBody=PTaskType.getSoldierGoalBody(PMaster);
        PLeutnant1GoalBody=PTaskType.getSoldierGoalBody(PLeutnant1);
        PLeutnant2GoalBody=PTaskType.getSoldierGoalBody(PLeutnant2);
        PLeutnant3GoalBody=PTaskType.getSoldierGoalBody(PLeutnant3);
        // task types
        // hearorder agentum
        System.out.println("TT:"+PTaskType.getTaskType()+"/"+PType1+"/"+PType2+"/"+PType3+"/"+PType4+"/");

        PMaster.hearOrder(new DSGDetachAll());
        PLeutnant1.hearOrder(new DSGDetachAll());
        PLeutnant2.hearOrder(new DSGDetachAll());
        PLeutnant3.hearOrder(new DSGDetachAll()
        );

        /*
        PMaster.hearOrder(new DSGetBlock(PType1, PMasterDispenserPos));
        PLeutnant1.hearOrder(new DSGetBlock(PType2, PLeutnant1DispenserPos));
        PLeutnant2.hearOrder(new DSGetBlock(PType3, PLeutnant2DispenserPos));
        PLeutnant3.hearOrder(new DSGetBlock(PType4, PLeutnant3DispenserPos));
*/

        // setscenario agentum
        PMaster.setScenario(this);
        PLeutnant1.setScenario(this);
        PLeutnant2.setScenario(this);
        PLeutnant3.setScenario(this);
        // nastaveni stavu automatu pro tento scenar
        PStateM=1;
        PStateL1=1;
        PStateL2=1;
        PStateL3=1;

        System.out.println("initScenario: " + "\n!@!  TASK:" + PTask.getName() + " type "+PTask.getTaskTypeNumber()+" / "+
                ", Group master"+PMaster.getGroup().getMaster().getEntityName()+"\n@@Master " + PMaster.getEntityName() +
                "  pujde na " + PMasterDispenserPos + " pro " + PType1 + " a do golu " + PMasterGoalPos +
                " body is "+PMaster.getBody().bodyToString()+
                ",\n@@ Leutnant1 " + PLeutnant1.getEntityName() + "  pujde na " + PLeutnant1DispenserPos + " pro " + PType2 +
                " a do golu " + PLeutnant1GoalPos+" body is "+PLeutnant1.getBody().bodyToString()+
                ",\n@@ Leutnant2 " + PLeutnant2.getEntityName() + "  pujde na " + PLeutnant2DispenserPos + " pro " + PType3 +
                " a do golu " + PLeutnant2GoalPos+" body is "+PLeutnant2.getBody().bodyToString()+
                ",\n@@ Leutnant3 " + PLeutnant3.getEntityName() + "  pujde na " + PLeutnant3DispenserPos + " pro " + PType4 +
                " a do golu " + PLeutnant3GoalPos+" body is "+PLeutnant3.getBody().bodyToString()
        );


        return(true);
    }

    public DSSFourBlocks(DeSouches commander, DSGroup group, DSTask task, int taskType){
        super(commander, group, task, taskType);
        PPriority=2;
        PTaskType=task.getTaskType();
    }

}



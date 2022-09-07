package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsMultiagent.dsTasks.DSTask;

public class DSSFourBlocks extends DSSBlockMission {
  private static final String TAG = "DSThreeBlocks";


  public String getName(){
    return("Four Block mission");
  }

  public void updateGUI(int step) {
    PGUI.setDsgTaskText(PTask, step, PMasterGoalPos, PLeutnant1GoalPos, PLeutnant2GoalPos, PLeutnant3GoalPos);
  }

  @Override
  public synchronized void goalCompleted(DSAgent agent, DSGGoal goal) {


   agent.getCommander().printOutput(
        "goalCompleted: "
            + "SCEN: Task te chvali, agente "
            + agent.getEntityName()
            + " za "
            + goal.getGoalDescription());

    if (agent == PMaster) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1,PMasterDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed detachAll -> getBlock");
        PStateM = 1;
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed getBlock -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goToPosition -> (connect?)");
        PStateM = 3;
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed evasiveMenoeuvre -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed connect -> submit");
        PStateM = 5;
        agent.hearOrder(new DSGSubmitGoal(PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("submitGoal")) PCommander.scenarioCompleted(this);
    }

    if (agent == PLeutnant1) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed completed goRandomly -> fetBlock");
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed detachAll -> getBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed getBlock -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goToPosition -> (connect?)");
        PStateL1 = 3;
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed evasiveMenoeuvre -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed connect -> DONE");
        PStateL1 = 5;
      }
    }

    if (agent == PLeutnant2) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed detachAll -> getBlock");
        PStateL2 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed getBlock -> goToPosition");
        PStateL2 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goToPosition -> (connect?)");
        PStateL2 = 3;
        }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed evasive manoeuvre -> goToPosition");
        PStateL2 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed  submit -> DONE");
        PStateL2 = 5;
      }
    }

    if (agent == PLeutnant3) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed detachAll -> getBlock");
        PStateL3 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed getBlock -> goToPosition");
        PStateL3 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goToPosition -> (connect?)");
        PStateL3 = 3;
         }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" evasiveManoeuvre -> goToPosition");
        PStateL3 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed connect -> DONE");
        PStateL3 = 5;
      }
    }
    if((PStateM==3)&&(PStateL1==3)&&(PStateL2==3)&&(PStateL3==3)){
      PMaster.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant1.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant2.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant3.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PGUI.addText2Terminal(agent.getStep()+":"+PMaster.getEntityName()+"  -> connect");
      PGUI.addText2Terminal(agent.getStep()+":"+PLeutnant1.getEntityName()+"  -> connect");
      PGUI.addText2Terminal(agent.getStep()+":"+PLeutnant2.getEntityName()+"  -> connect");
      PGUI.addText2Terminal(agent.getStep()+":"+PLeutnant3.getEntityName()+"  -> connect");
      PStateM=4; PStateL1=4; PStateL2=4; PStateL3=4;
    }
    else
    if((PStateM==3)||(PStateL1==3)||(PStateL2==3)||(PStateL3==3)){
      PGUI.addText2Terminal(agent.getStep()+":"+PMaster.getEntityName()+","+PLeutnant1.getEntityName()+","+
              PLeutnant2.getEntityName()+","+PLeutnant3.getEntityName()+"  NO TIME TO CONNECT "+PStateM+PStateL1+
              PStateL2+PStateL3+")");
    }
    super.goalCompleted(agent,goal);
  }

  @Override
  public synchronized void goalFailed(DSAgent agent, DSGGoal goal) {

    super.goalFailed(agent,goal);

    agent.getCommander().printOutput(
            "goalFailed: "
                    + "SCEN: Task to je smula agente "
                    + agent.getEntityName()
                    + " kvuli "
                    + goal.getGoalDescription());


    if (agent == PMaster) {

      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" failed detachAll -> detachAll");
         agent.hearOrder(new DSGDetachAll());
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed getBlock -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
        PStateM = 1;
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed goToPosition -> randomRoam");
        PStateM = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }

      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed randomRoam -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));

      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed connect -> connect");
        agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("submitGoal")) {
        PGUI.addText2Terminal(agent.getStep() + ":" + agent.getEntityName() + " failed submit -> FAILED ");
        this.scenarioFailed("Submit failed");
      }

    }

    if (agent == PLeutnant1) {

      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" failed detachAll -> detachAll");
        agent.hearOrder(new DSGDetachAll());
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed getBlock -> getBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));

      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed goToPosition -> randomRoam");
        PStateL1 = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed randomRoam -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType2,PLeutnant1DispenserPos, getTask().getDeadline()));
    }
    if (goal.getGoalDescription().contentEquals("customGoal")) {
      PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed connect -> connect");
      agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
    }

    if (agent == PLeutnant2) {
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" failed detachAll -> detachAll");
        agent.hearOrder(new DSGDetachAll());
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed getBlock -> getBlock");
        PStateL2 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed goToPosition -> randomRoam");
        PStateL2 = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }

      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed randomRoam -> goToPosition");
        PStateL2 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed connect -> connect");
        agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType3,PLeutnant2DispenserPos, getTask().getDeadline()));

    }

    if (agent == PLeutnant3) {

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed getBlock -> getBlock");
        PStateL3 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed goToPosition -> randomRoam");
        PStateL3 = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed randomRoam -> goToPosition");
        PStateL3 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getEntityName() + " failed connect -> connect");
        agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
    }
  }


  boolean allocateAgents() {

    PMasterGoalPos = PTask.getSubtaskRoutes(0).getGoalPosition();

    PMaster = PTask.getSubtaskRoutes(0).getAgent();
    PTask.getTaskType().setMaster(PMaster);
    PLeutnant1 = PTask.getSubtaskRoutes(1).getAgent();
    PTask.getTaskType().setLeutnant1(PLeutnant1);
    PLeutnant2 = PTask.getSubtaskRoutes(2).getAgent();
    PTask.getTaskType().setLeutnant2(PLeutnant2);
    PLeutnant3 = PTask.getSubtaskRoutes(3).getAgent();
    PTask.getTaskType().setLeutnant3(PLeutnant3);

    PAgentsAllocated.add(PMaster);
    PAgentsAllocated.add(PLeutnant1);
    PAgentsAllocated.add(PLeutnant2);
    PAgentsAllocated.add(PLeutnant3);

    PMasterDispenserPos = PTask.getSubtaskRoutes(0).getDispenserPosition();
    PLeutnant1DispenserPos = PTask.getSubtaskRoutes(1).getDispenserPosition();
    PLeutnant2DispenserPos = PTask.getSubtaskRoutes(2).getDispenserPosition();
    PLeutnant3DispenserPos = PTask.getSubtaskRoutes(3).getDispenserPosition();

    PType1 = PTask.getTypesNeeded().get(0);
    PType2 = PTask.getTypesNeeded().get(1);
    PType3 = PTask.getTypesNeeded().get(2);
    PType4 = PTask.getTypesNeeded().get(3);

    PLeutnant1GoalPos = PTaskType.formationPosition(PLeutnant1, PMasterGoalPos);
    PLeutnant2GoalPos = PTaskType.formationPosition(PLeutnant2, PMasterGoalPos);
    PLeutnant3GoalPos = PTaskType.formationPosition(PLeutnant3, PMasterGoalPos);

    return (true);
  }

  @Override
  public boolean initMission(int step) {

    if (!allocateAgents()) return (false);

    super.initMission(step);

    // posunout leutnanty na spravnou goalpozici

    // nastavit goalbody
    PMasterGoalBody = PTaskType.getSoldierGoalBody(PMaster);
    PLeutnant1GoalBody = PTaskType.getSoldierGoalBody(PLeutnant1);
    PLeutnant2GoalBody = PTaskType.getSoldierGoalBody(PLeutnant2);
    PLeutnant3GoalBody = PTaskType.getSoldierGoalBody(PLeutnant3);
    // task types
    // hearorder agentum
    PCommander.printOutput(
        "TT:"
            + PTaskType.getTaskType()
            + "/"
            + PType1 + PMaster.getEntityName()
            + "/"
            + PType2  + PLeutnant1.getEntityName()
            + "/"
            + PType3 + PLeutnant2.getEntityName()
            + "/"
            + PType4 + PLeutnant3.getEntityName()
            + "/");

    PMaster.hearOrder(new DSGDetachAll());
    PLeutnant1.hearOrder(new DSGDetachAll());
    PLeutnant2.hearOrder(new DSGDetachAll());
    PLeutnant3.hearOrder(new DSGDetachAll());


    // setscenario agentum
    PMaster.setScenario(this);
    PLeutnant1.setScenario(this);
    PLeutnant2.setScenario(this);
    PLeutnant3.setScenario(this);
    // nastaveni stavu automatu pro tento scenar
    PStateM = 1;
    PStateL1 = 1;
    PStateL2 = 1;
    PStateL3 = 1;

    System.out.println(
        "initScenario: "
            + "\n!@!  TASK:"
            + PTask.getName()
            + " type "
            + PTask.getTaskTypeNumber()
            + " / "
            + ", Group master"
            + PMaster.getGroup().getMaster().getEntityName()
            + "\n@@Master "
            + PMaster.getEntityName()
            + "  pujde na "
            + PMasterDispenserPos
            + " pro "
            + PType1
            + " a do golu "
            + PMasterGoalPos
            + " body is "
            + PMaster.getBody().bodyToString()
            + ",\n@@ Leutnant1 "
            + PLeutnant1.getEntityName()
            + "  pujde na "
            + PLeutnant1DispenserPos
            + " pro "
            + PType2
            + " a do golu "
            + PLeutnant1GoalPos
            + " body is "
            + PLeutnant1.getBody().bodyToString()
            + ",\n@@ Leutnant2 "
            + PLeutnant2.getEntityName()
            + "  pujde na "
            + PLeutnant2DispenserPos
            + " pro "
            + PType3
            + " a do golu "
            + PLeutnant2GoalPos
            + " body is "
            + PLeutnant2.getBody().bodyToString()
            + ",\n@@ Leutnant3 "
            + PLeutnant3.getEntityName()
            + "  pujde na "
            + PLeutnant3DispenserPos
            + " pro "
            + PType4
            + " a do golu "
            + PLeutnant3GoalPos
            + " body is "
            + PLeutnant3.getBody().bodyToString());

        updateGUI(step);

        return (true);
  }

  public DSSFourBlocks(DeSouches commander, DSTask task) {
    super(commander, task);
    PPriority = 2;
    PTaskType = task.getTaskType();
  }
}

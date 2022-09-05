package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsMultiagent.dsTasks.DSTask;

public class DSSThreeBlocks extends DSSBlockMission {
  private static final String TAG = "DSThreeBlocks";

  /*
  private static final int _RoleMaster = 0;
  private static final int _RoleLeutnant1 = 1;
  private static final int _RoleLeutnant2 = 2;


  private int PStateM, PStateL1, PStateL2;
  private DSAgent PMaster;
  private DSAgent PLeutnant1, PLeutnant2;
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

  private int PType1, PType2, PType3;
*/

  @Override
  public String getName(){
    return("Three Block scenario");
  }

  public void updateGUI(int step) {
    PGUI.setDsgTaskText(PTask, step, PMasterGoalPos, PLeutnant1GoalPos, PLeutnant2GoalPos, null);
  }

  @Override
  public synchronized  void goalCompleted(DSAgent agent, DSGGoal goal) {

    agent.getCommander().printOutput(
        "goalCompleted: "
            + "SCEN: Task te chvali, agente "
            + agent.getEntityName()
            + " za "
            + goal.getGoalDescription());

    if (agent == PMaster) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goRandomly -> GetBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1,PMasterDispenserPos, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed detachAll -> GetBlock");
        PStateL1 = 1;
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
      //  agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed connect-> submit");
        PStateM = 4;
        agent.hearOrder(new DSGSubmitGoal(PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("submitGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed  submit -> DONE");
        PCommander.scenarioCompleted(this);
      }
    }

    if (agent == PLeutnant1) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goRandomly -> getBlock");
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
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToPosition -> (connect?)");
        PStateL1 = 3;
     //   agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed connect -> DONE");
        PStateL1 = 4;
      }
    }

    if (agent == PLeutnant2) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " completed goRandomly -> GetBlock");
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed detachAll -> GetBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed getBock -> goToPosition");
        PStateL2 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goToPosition -> (connect?)");
        PStateL2 = 3;
     //   agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed connect -> DONE");
        PStateL2 = 4;
      }
    }
    if((PStateM==3)&&(PStateL1==3)&&(PStateL2==3)){
      PMaster.printOutput("budem tancovat M\n");
      PLeutnant1.printOutput("budem tancovat L1\n");
      PLeutnant2.printOutput("budem tancovat L2\n");
      PMaster.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant1.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant2.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PGUI.addText2Terminal(agent.getStep()+":"+PMaster.getEntityName()+"  -> connect");
      PGUI.addText2Terminal(agent.getStep()+":"+PLeutnant1.getEntityName()+"  -> connect");
      PGUI.addText2Terminal(agent.getStep()+":"+PLeutnant2.getEntityName()+"  -> connect");
      PStateM=4; PStateL1=4; PStateL2=4;
    }
    else
    if((PStateM==3)||(PStateL1==3)||(PStateL2==3)){
      PGUI.addText2Terminal(agent.getStep()+":"+PMaster.getEntityName()+","+PLeutnant1.getEntityName()+","+
              PLeutnant2.getEntityName()+"  NO TIME TO CONNECT ("+PStateM+PStateL1+PStateL2+")");

    }
      super.goalCompleted(agent,goal);

  }

  @Override
  public synchronized  void goalFailed(DSAgent agent, DSGGoal goal) {

    super.goalFailed(agent,goal);
    agent.getCommander().printOutput(
            "goalFailed: "
                    + "SCEN: Task to je smula agente "
                    + agent.getEntityName()
                    + " kvuli "
                    + goal.getGoalDescription());


    if (agent == PMaster) {

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed getBlock -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
        PStateM = 2;
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
        agent.hearOrder(new DSGGetBlock2022(PType1,PMasterDispenserPos, getTask().getDeadline()));

      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed connect -> connect");
        agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }

      if (goal.getGoalDescription().contentEquals("submitGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getStep() + ":" + agent.getEntityName() + " failed submit -> FAILED ");
        this.scenarioFailed("Submit failed");
      }

      }   // Master

    if (agent == PLeutnant1) {

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed getBlock -> getBlock");
        PStateL1 = 2;
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

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed getBlock -> getBlock");
        PStateL2 = 2;
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
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType3,PLeutnant2DispenserPos, getTask().getDeadline()));
    }
    if (goal.getGoalDescription().contentEquals("customGoal")) {
      PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed connect -> connect");
      agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
    }
  }

  /*
  public boolean checkEvent(DSAgent agent, int eventType) {
    agent.printOutput("Checking event "+eventType);
    switch (eventType) {
      case DSMMission._disabledEvent:
        if (PMaster == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType1,PMasterDispenserPos, getTask().getDeadline()));
          PStateM = 1;
        }
        if (PLeutnant1 == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType2,PLeutnant1DispenserPos, getTask().getDeadline()));
          PStateL1 = 1;
        }
        if (PLeutnant2 == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType3,PLeutnant2DispenserPos, getTask().getDeadline()));
          PStateL2 = 1;
        }
        return (true);
      case DSMMission._noBlockEvent:
        if (PMaster == agent) {
          PMaster.getBody().resetBody();
          if (PStateM == 2) {
            PStateM = 1;
            agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
          }
        }
        if (PLeutnant1 == agent) {
          PLeutnant1.getBody().resetBody();
          if (PStateL1 == 2) {
            PStateL1 = 1;
            agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
          }
        }
        if (PLeutnant2 == agent) {
          PLeutnant2.getBody().resetBody();
          if (PStateL2 == 2) {
            PStateL2 = 1;
            agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant2DispenserPos, getTask().getDeadline()));
          }
        }

        return (true);
    }
    return (false);
  }
*/

/*
  public void calibrateScenario(DSMap map){
    PMasterDispenserPos=map.centralizeCoords(PMasterDispenserPos);
    PMasterGoalPos=map.centralizeCoords(PMasterGoalPos);
    PLeutnant1DispenserPos=map.centralizeCoords(PLeutnant1DispenserPos);
    PLeutnant1GoalPos=map.centralizeCoords(PLeutnant1GoalPos);
    PLeutnant2DispenserPos=map.centralizeCoords(PLeutnant2DispenserPos);
    PLeutnant2GoalPos=map.centralizeCoords(PLeutnant2GoalPos);
  }
*/

  boolean allocateAgents() {

    PMasterGoalPos = PTask.getSubtaskRoutes(0).getGoalPosition();

    PMaster = PTask.getSubtaskRoutes(0).getAgent();
    PTask.getTaskType().setMaster(PMaster);
    PLeutnant1 = PTask.getSubtaskRoutes(1).getAgent();
    PTask.getTaskType().setLeutnant1(PLeutnant1);
    PLeutnant2 = PTask.getSubtaskRoutes(2).getAgent();
    PTask.getTaskType().setLeutnant2(PLeutnant2);

    PAgentsAllocated.add(PMaster);
    PAgentsAllocated.add(PLeutnant1);
    PAgentsAllocated.add(PLeutnant2);

    PMasterDispenserPos = PTask.getSubtaskRoutes(0).getDispenserPosition();
    PLeutnant1DispenserPos = PTask.getSubtaskRoutes(1).getDispenserPosition();
    PLeutnant2DispenserPos = PTask.getSubtaskRoutes(2).getDispenserPosition();

    PType1 = PTask.getTypesNeeded().get(0);
    PType2 = PTask.getTypesNeeded().get(1);
    PType3 = PTask.getTypesNeeded().get(2);

    PLeutnant1GoalPos = PTaskType.formationPosition(PLeutnant1, PMasterGoalPos);
    PLeutnant2GoalPos = PTaskType.formationPosition(PLeutnant2, PMasterGoalPos);

    return (true);
  }

  @Override
  public boolean initMission(int step) {


    if (!allocateAgents()) return (false);
    // posunout leutnanty na spravnou goalpozici

    super.initMission(step);


    // nastavit goalbody
    PMasterGoalBody = PTaskType.getSoldierGoalBody(PMaster);
    PLeutnant1GoalBody = PTaskType.getSoldierGoalBody(PLeutnant1);
    PLeutnant2GoalBody = PTaskType.getSoldierGoalBody(PLeutnant2);
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
                    + "/");
    /*
           PMaster.hearOrder(new DSGetBlock(PType1, PMasterDispenserPos));
           PLeutnant1.hearOrder(new DSGetBlock(PType2, PLeutnant1DispenserPos));
           PLeutnant2.hearOrder(new DSGetBlock(PType3, PLeutnant2DispenserPos));

    */
    PMaster.hearOrder(new DSGDetachAll());
    PLeutnant1.hearOrder(new DSGDetachAll());
    PLeutnant2.hearOrder(new DSGDetachAll());
    // setscenario agentum
    PMaster.setScenario(this);
    PLeutnant1.setScenario(this);
    PLeutnant2.setScenario(this);
    // nastaveni stavu automatu pro tento scenar
    PStateM = 1;
    PStateL1 = 1;
    PStateL2 = 1;

    System.out.println(
        "initScenario: "
            + "\n!@!  TASK:"
            + PTask.getName()
            + " type "
            + PTask.getTaskTypeNumber()
            + " / "
            + ", Group master"
            + PMaster.getGroup().getMaster().getEntityName()
            + "\n@@ Master "
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
            + PLeutnant2.getBody().bodyToString());

    updateGUI(step);

    return (true);
  }

  public DSSThreeBlocks(DeSouches commander, DSTask task) {
    super(commander, task);
    PPriority = 2;
    PTaskType = task.getTaskType();
  }
}

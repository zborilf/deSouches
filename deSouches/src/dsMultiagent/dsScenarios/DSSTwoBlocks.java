package dsMultiagent.dsScenarios;

import dsAgents.*;
import dsAgents.DeSouches;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsMultiagent.dsTasks.DSTask;

public class DSSTwoBlocks extends DSSBlockMission {
  private static final String TAG = "DSTwoBlocks";
  // dostane Task a k tomu Skupinu
  //

  /*
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
   */

  @Override
  public String getName(){
    return("Two Block scenario");
  }

  public void updateGUI(int step) {
    PGUI.setDsgTaskText(PTask, step, PMasterGoalPos, PLeutnant1GoalPos, null, null);
  }

  @Override
  public synchronized void goalCompleted(DSAgent agent, DSGGoal goal) {

    super.goalCompleted(agent,goal);
    agent.getCommander().printOutput(
        "goalCompleted: "
            + "SCEN: Task te chvali, agente "
            + agent.getEntityName()
            + " za "
            + goal.getGoalDescription());

    if (agent == PMaster) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" detachAll -> getBlock");
        PStateM = 1;
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" getBlock -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" goToPosition  -> (connect?)");
        PStateM = 3;
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("connectAndDetach")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" connect -> submit");
        agent.hearOrder(new DSAttachAndSubmit(PTask.getName(), "s", PType1));
        PStateM = 5;
      }
      if (goal.getGoalDescription().contentEquals("attachAndSubmit")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed  submit -> DONE");
        PStateM = 6;
        PCommander.scenarioCompleted(this);
      }
    }
    if (agent == PLeutnant1) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" completed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" detachAll -> getBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" getBlock -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")){
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" goToPosition  -> (connect?)");
        PStateL1 = 3;
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("connectAndDetach")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed connect -> DONE");
        agent.hearOrder(new DSGoalExplore(10));
        PStateL1 = 5;
      }
    }
    if ((PStateM == 3) && (PStateL1 == 3)) {
      PGUI.addText2Terminal(agent.getEntityName()+" -> ConnectAndDetach");
      // zde ma byt dance pro vsechny cleny, vysledkem je vhodny plan

      PMaster.hearOrder(new DSGConnectAndDetach("s", PLeutnant1.getEntityName()));
      if (PTaskTypeNumber == 1)
        PLeutnant1.hearOrder(new DSGConnectAndDetach("n", PMaster.getEntityName()));
      if (PTaskTypeNumber == 2)
        PLeutnant1.hearOrder(new DSGConnectAndDetach("s", PMaster.getEntityName()));
      if (PTaskTypeNumber == 3)
        PLeutnant1.hearOrder(new DSGConnectAndDetach("s", PMaster.getEntityName()));
      PGUI.addText2Terminal(agent.getStep()+":"+PMaster.getEntityName()+"  -> connect");
      PGUI.addText2Terminal(agent.getStep()+":"+PLeutnant1.getEntityName()+"  -> connect");
      PStateM=4; PStateL1=4;
    }
    else
    if ((PStateM == 3) || (PStateL1 == 3)) {
      PGUI.addText2Terminal(agent.getStep()+":"+PMaster.getEntityName()+","+PLeutnant1.getEntityName()+","+
              "  NO TIME TO CONNECT ("+PStateM+PStateL1+")");
      }
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
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " failed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("attachAndSubmit")) {
        PGUI.addText2Terminal(agent.getStep() + ":" + agent.getEntityName() + " failed submit -> FAILED ");
        this.scenarioFailed("Submit failed");
      }
      } // Master

    if (agent == PLeutnant1) {
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
      if (goal.getGoalDescription().contentEquals("goRandomly")){
        PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " FAILED M: goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
      }
    }   // Leutnant 1

    if ((PStateM == 4) && (PStateL1 == 4)) {
      PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName()+" -> ConnectAndDetach");
      // zde ma byt dance pro vsechny cleny, vysledkem je vhodny plan
      if(agent == PMaster)
          PMaster.hearOrder(new DSGConnectAndDetach("s", PLeutnant1.getEntityName()));
      if(agent == PLeutnant1) {
        if (PTaskTypeNumber == 1)
          PLeutnant1.hearOrder(new DSGConnectAndDetach("n", PMaster.getEntityName()));
        if (PTaskTypeNumber == 2)
          PLeutnant1.hearOrder(new DSGConnectAndDetach("s", PMaster.getEntityName()));
        if (PTaskTypeNumber== 3)
          PLeutnant1.hearOrder(new DSGConnectAndDetach("s", PMaster.getEntityName()));
      }
    }
  }

  /*
  public boolean checkEvent(DSAgent agent, int eventType) {
    agent.printOutput("Checking event "+eventType);
    switch (eventType) {
      case DSMMission._disabledEvent:
        if (PMaster == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
          PStateM = 1;
        }
        if (PLeutnant1 == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
          PStateL1 = 1;
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
        return (true);
    }
    return (false);
  }
*/

  /*
  public void calibrateScenario(DSMap map){
    PMasterDispenserPos=map.centralizeCoords(PMasterDispenserPos);
    PMasterGoalPos=map.centralizeCoords(PMasterGoalPos);
    PSlaveDispenserPos=map.centralizeCoords(PSlaveDispenserPos);
    PSlaveGoalPos=map.centralizeCoords(PSlaveGoalPos);
  }
*/

  boolean allocateAgents() {


    PMasterGoalPos = PTask.getSubtaskRoutes(0).getGoalPosition();


    PMaster = PTask.getSubtaskRoutes(0).getAgent();
    PTask.getTaskType().setMaster(PMaster);
    PLeutnant1 = PTask.getSubtaskRoutes(1).getAgent();
    PTask.getTaskType().setLeutnant1(PLeutnant1);

    PAgentsAllocated.add(PMaster);
    PAgentsAllocated.add(PLeutnant1);

    PMasterDispenserPos = PTask.getSubtaskRoutes(0).getDispenserPosition();
    PLeutnant1DispenserPos = PTask.getSubtaskRoutes(1).getDispenserPosition();
    PType1 = PTask.getTypesNeeded().get(0);
    PType2 = PTask.getTypesNeeded().get(1);

    PLeutnant1GoalPos = PTask.getTaskType().formationPosition(PLeutnant1, PMasterGoalPos);

    return (true);
  }



  @Override
  public boolean initMission(int step) {


    if (!allocateAgents()) return (false);


    super.initMission(step);

    // posunout leutnanty na spravnou goalpozici
    // nastavit goalbody
    PMasterGoalBody = PTask.getTaskType().getSoldierGoalBody(PMaster);
    PLeutnant1GoalBody = PTask.getTaskType().getSoldierGoalBody(PLeutnant1);

    // task types
    PMaster.hearOrder(new DSGDetachAll());
    PLeutnant1.hearOrder(new DSGDetachAll());

    // setscenario agentum
    PMaster.setScenario(this);
    PLeutnant1.setScenario(this);

    // nastaveni stavu automatu pro tento scenar
    PStateM = 1;
    PStateL1 = 1;

    PCommander.printOutput(
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
            + PMasterGoalBody.bodyToString()
            + ",\n@@ Leutnant1 "
            + PLeutnant1.getEntityName()
            + "  pujde na "
            + PLeutnant1DispenserPos
            + " pro "
            + PType2
            + " a do golu "
            + PLeutnant1GoalPos
            + " body is "
            + PLeutnant1GoalBody.bodyToString());

            updateGUI(step);


    return (true);
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



  public DSSTwoBlocks(DeSouches commander, DSTask task) {
    super(commander, task);
    PPriority = 2;
  }


}

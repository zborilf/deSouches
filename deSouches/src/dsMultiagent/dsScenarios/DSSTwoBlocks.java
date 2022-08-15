package dsMultiagent.dsScenarios;

import deSouches.utils.HorseRider;
import dsAgents.*;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsMultiagent.dsTasks.DSTask;
import java.awt.*;

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
  public String getName(){
    return("Two Block scenario");
  }

  @Override
  public void goalCompleted(DSAgent agent, DSGGoal goal) {
    agent.getCommander().printOutput(
        "goalCompleted: "
            + "SCEN: Task te chvali, agente "
            + agent.getEntityName()
            + " za "
            + goal.getGoalDescription());

    if (agent == PMaster) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getEntityName()+" detachAll -> getBlock");
        PStateM = 1;
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName()+" goToDispenesr -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getEntityName()+" goToPosition!!");
        PStateM = 3;
      }
      if (goal.getGoalDescription().contentEquals("connectAndDetach")) {
        PGUI.addText2Terminal(agent.getEntityName()+" connectAndDetach -> attachAndSubmit");
        agent.hearOrder(new DSAttachAndSubmit(PTask.getName(), "s", PType1));
        PStateM = 4;
      }
      if (goal.getGoalDescription().contentEquals("attachAndSubmit")) {
        PGUI.addText2Terminal(agent.getEntityName()+" attachAndSubmit!! COMPLETED");
        PStateM = 5;
        PCommander.scenarioCompleted(this);
      }
    }
    if (agent == PLeutnant1) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType2, PSlaveDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getEntityName()+" detachAll -> getBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType2, PSlaveDispenserPos, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName()+" goToDispenesr -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PSlaveGoalPos, PSlaveGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")){
        PGUI.addText2Terminal(agent.getEntityName()+" goToPosition!!");
        PStateL1 = 3;
      }
      if (goal.getGoalDescription().contentEquals("connectAndDetach")) {
        PGUI.addText2Terminal(agent.getEntityName()+" connectAndDetach (I am done, L1)");
        agent.hearOrder(new DSGoalExplore(10));
        PStateL1 = 4;
      }
    }
    if ((PStateM == 3) && (PStateL1 == 3)) {
      PGUI.addText2Terminal(agent.getEntityName()+" -> ConnectAndDetach");
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
  public void goalFailed(DSAgent agent, DSGGoal goal) {
    agent.getCommander().printOutput(
        "goalFailed: "
            + "SCEN: Task to je smula agente "
            + agent.getEntityName()
            + " za "
            + goal.getGoalDescription());

    if (agent == PMaster) {
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName()+" FAILED M: goToDispenser -> goalExplore");
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
        PStateM = 1;
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getEntityName()+" FAILED M: goToPosition -> goToPosition");
        PStateM = 1;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getEntityName() + " FAILED M: goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }
    }
    if (agent == PLeutnant1) {
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName() + " FAILED L1: goToDispenser -> goalExplore");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType2, PSlaveDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getEntityName() + " FAILED L1: goToPosition -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PSlaveGoalPos, PSlaveGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly")){
        PGUI.addText2Terminal(agent.getEntityName() + " FAILED M: goRandomly -> getBlock");
        agent.hearOrder(new DSGGetBlock2022(PType2, PSlaveDispenserPos, getTask().getDeadline()));
      }
    }
  }

  public boolean checkEvent(DSAgent agent, int eventType) {
    agent.printOutput("Checking event "+eventType);
    switch (eventType) {
      case DSScenario._disabledEvent:
        if (PMaster == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
          PStateM = 1;
        }
        if (PLeutnant1 == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType2, PSlaveDispenserPos, getTask().getDeadline()));
          PStateL1 = 1;
        }
        return (true);

      case DSScenario._noBlockEvent:
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
            agent.hearOrder(new DSGGetBlock2022(PType2, PSlaveDispenserPos, getTask().getDeadline()));
          }
        }
        return (true);
    }
    return (false);
  }

  boolean allocateAgents() {


    PMasterGoalPos = PTask.getSubtaskRoutes(0).getGoalPosition();


    PMaster = PTask.getSubtaskRoutes(0).getAgent();
    PTask.getTaskType().setMaster(PMaster);
    PLeutnant1 = PTask.getSubtaskRoutes(1).getAgent();
    PTask.getTaskType().setLeutnant1(PLeutnant1);

    PAgentsAllocated.add(PMaster);
    PAgentsAllocated.add(PLeutnant1);

    PMasterDispenserPos = PTask.getSubtaskRoutes(0).getDispenserPosition();
    PSlaveDispenserPos = PTask.getSubtaskRoutes(1).getDispenserPosition();
    PType1 = PTask.getTypesNeeded().get(0);
    PType2 = PTask.getTypesNeeded().get(1);

    PSlaveGoalPos = PTask.getTaskType().formationPosition(PLeutnant1, PMasterGoalPos);

    return (true);
  }



  @Override
  public boolean initScenario(int step) {


    if (!allocateAgents()) return (false);

    // posunout leutnanty na spravnou goalpozici
    // nastavit goalbody
    PMasterGoalBody = PTask.getTaskType().getSoldierGoalBody(PMaster);
    PSlaveGoalBody = PTask.getTaskType().getSoldierGoalBody(PLeutnant1);

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
            + PSlaveDispenserPos
            + " pro "
            + PType2
            + " a do golu "
            + PSlaveGoalPos
            + " body is "
            + PSlaveGoalBody.bodyToString());

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
    PGUI=dsTaskGUI.createGUI();
    PGUI.setDsgTaskText(task);
  }


}

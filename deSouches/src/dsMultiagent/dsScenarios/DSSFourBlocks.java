package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsAgents.dsTaskGUI;
import dsMultiagent.dsTasks.DSTask;
import dsMultiagent.dsTasks.DSTaskType;
import java.awt.*;

public class DSSFourBlocks extends DSSBlockScenarios {
  private static final String TAG = "DSThreeBlocks";

  private static final int _RoleMaster = 0;
  private static final int _RoleLeutnant1 = 1;
  private static final int _RoleLeutnant2 = 2;

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

  public String getName(){
    return("Four Block scenario");
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
        PGUI.addText2Terminal(agent.getEntityName()+" completed goRandomly -> GetBlock");
        agent.hearOrder(new DSGGetBlock2022(PType1,PMasterDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed detachAll -> GetBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToDispenser -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToPosition -> ConnectGoal");
        PStateM = 3;
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PStateM = 4;
        agent.hearOrder(new DSGSubmitGoal(PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("submitGoal")) PCommander.scenarioCompleted(this);
    }

    if (agent == PLeutnant1) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goRandomly -> GetBlock");
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
      }

      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed detachAll -> GetBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToDispenser -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToPosition -> ConnectGoal");
        PStateL1 = 3;
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PStateL1 = 4;
      }
    }

    if (agent == PLeutnant2) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goRandomly -> GetBlock");
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed detachAll -> GetBlock");
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToDispenser -> goToPosition");
        PStateL2 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToPosition -> ConnectGoal");
        PStateL2 = 3;
        }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateL2 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PStateL2 = 4;
      }
    }

    if (agent == PLeutnant3) {
      if (goal.getGoalDescription().contentEquals("goRandomly")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goRandomly -> GetBlock");
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("detachAllGoal")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed detachAll -> GetBlock");
        PStateL3 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToDispenser -> goToPosition");
        PStateL3 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PGUI.addText2Terminal(agent.getEntityName()+" completed goToPosition -> ConnectGoal");
        PStateL3 = 3;
         }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName()+" evasive manoeuvre -> goToPosition");
        PStateL3 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        PStateL3 = 4;
      }
    }
    if((PStateM==3)&&(PStateL1==3)&&(PStateL2==3)){
      PMaster.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant1.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant2.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      PLeutnant3.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
    }
  }

  @Override
  public synchronized void goalFailed(DSAgent agent, DSGGoal goal) {

    agent.getCommander().printOutput(
            "goalFailed: "
                    + "SCEN: Task to je smula agente "
                    + agent.getEntityName()
                    + " kvuli "
                    + goal.getGoalDescription());


    if (agent == PMaster) {

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));

        //agent.hearOrder(new DSGoalExplore(4));
        PStateM = 1;
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PStateM = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }

      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName() + " evasive manoeuvre -> goToPosition");
        PStateM = 2;
        agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
    }

    if (agent == PLeutnant1) {

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PStateL1 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));

        //        agent.hearOrder(new DSGoalExplore(4));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PStateL1 = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName() + " evasive manoeuvre -> goToPosition");
        PStateL1 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant1GoalPos, PLeutnant1GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType2,PLeutnant1DispenserPos, getTask().getDeadline()));
    }
    if (goal.getGoalDescription().contentEquals("customGoal")) {
      agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
    }

    if (agent == PLeutnant2) {

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PStateL2 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
        //        agent.hearOrder(new DSGoalExplore(4));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PStateL2 = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }

      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName() + " evasive manoeuvre -> goToPosition");
        PStateL2 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant2GoalPos, PLeutnant2GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType3,PLeutnant2DispenserPos, getTask().getDeadline()));

    }

    if (agent == PLeutnant3) {

      if (goal.getGoalDescription().contentEquals("get block 2022")) {
        PStateL3 = 1;
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
        //        agent.hearOrder(new DSGoalExplore(4));
      }
      if (goal.getGoalDescription().contentEquals("goToPosition")) {
        PStateL3 = 2;
        agent.hearOrder(new DSEvasiveManoeuvre());
      }
      if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
        PGUI.addText2Terminal(agent.getEntityName() + " evasive manoeuvre -> goToPosition");
        PStateL3 = 2;
        agent.hearOrder(new DSGoToPosition(PLeutnant3GoalPos, PLeutnant3GoalBody, getTask().getDeadline()));
      }
      if (goal.getGoalDescription().contentEquals("customGoal")) {
        agent.hearOrder(new DSGConnectGoal(PTaskType, 2, PTask.getName()));
      }
      if (goal.getGoalDescription().contentEquals("goRandomly"))
        agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
    }
  }

  public boolean checkEvent(DSAgent agent, int eventType) {
    agent.printOutput("Checking event "+eventType);
    switch (eventType) {
      case DSScenario._disabledEvent:
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

        if (PLeutnant3 == agent) {
          agent.hearOrder(new DSGGetBlock2022(PType4,PLeutnant3DispenserPos, getTask().getDeadline()));
          PStateL2 = 1;
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
            agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
          }
        }

        if (PLeutnant2 == agent) {
          PLeutnant2.getBody().resetBody();
          if (PStateL2 == 2) {
            PStateL2 = 1;
            agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
          }
        }

        if (PLeutnant3 == agent) {
          PLeutnant3.getBody().resetBody();
          if (PStateL3 == 2) {
            PStateL3 = 1;
            agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
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
    PType3 = PTask.getTypesNeeded().get(3);

    PLeutnant1GoalPos = PTaskType.formationPosition(PLeutnant1, PMasterGoalPos);
    PLeutnant2GoalPos = PTaskType.formationPosition(PLeutnant2, PMasterGoalPos);
    PLeutnant3GoalPos = PTaskType.formationPosition(PLeutnant3, PMasterGoalPos);

    return (true);
  }

  @Override
  public boolean initScenario(int step) {

    if (!allocateAgents()) return (false);
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
    PGUI=dsTaskGUI.createGUI();
   // PGUI.setDsgTaskText(task);
  }
}

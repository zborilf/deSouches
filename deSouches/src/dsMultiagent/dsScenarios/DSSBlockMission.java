package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsGUI.DSTaskGUI;
import dsAgents.dsReasoningModule.dsGoals.DSGGetBlock2022;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsMultiagent.dsTasks.DSTask;
import dsMultiagent.dsTasks.DSTaskType;

import javax.swing.*;
import java.awt.*;

public abstract class DSSBlockMission extends DSMMission {



  protected int PStateM, PStateL1, PStateL2, PStateL3;
  protected DSAgent PMaster;
  protected DSAgent PLeutnant1, PLeutnant2, PLeutnant3;
  protected Point PMasterGoalPos;
  protected DSBody PMasterGoalBody;
  protected Point PLeutnant1GoalPos;
  protected DSBody PLeutnant1GoalBody;
  protected Point PLeutnant2GoalPos;
  protected DSBody PLeutnant2GoalBody;
  protected DSTaskType PTaskType;
  protected Point PMasterDispenserPos;
  protected Point PLeutnant1DispenserPos;
  protected Point PLeutnant2DispenserPos;
  protected Point PLeutnant3GoalPos;
  protected DSBody PLeutnant3GoalBody;
  protected Point PLeutnant3DispenserPos;
  protected int PType1, PType2, PType3, PType4;



  public int checkConsistency() {
    int sd = getDeadline();
    if (getTask() != null) {
      sd = sd - getTask().goalDistanceMax(); // minimal estimated time of completion
    }

    if (sd < PMaster.getStep()) {
      PCommander.printOutput("@@! Scenario failed due to timeout " + getTask().getName());
      return (__mission_timeout);
    }

    DSMap map=PMaster.getMap();
    if (!map.getMapCells().isObjectAt(DSCell.__DSGoalArea,PMasterGoalPos))
      return(__no_goal_area);

    return (__mission_goes_well);
  }


  public boolean initMission(int step){
    /*DSGTaskPoolFrame gui = PCommander.getGGUI();
    gui.addText("Spoustim misi "+PTask.getNickName());
    gui.addText(PMaster.getEntityName()+" disp "+PMasterDispenserPos+" goal "+PMasterGoalPos);
    if(PLeutnant1!=null)
      gui.addText(PLeutnant1.getEntityName()+" disp "+PLeutnant1DispenserPos+" goal "+PLeutnant1GoalPos);
    if(PLeutnant2!=null)
      gui.addText(PLeutnant2.getEntityName()+" disp "+PLeutnant2DispenserPos+" goal "+PLeutnant2GoalPos);
    if(PLeutnant3!=null)
      gui.addText(PLeutnant3.getEntityName()+" disp "+PLeutnant3DispenserPos+" goal "+PLeutnant3GoalPos);

*/
    return(true);
  }

  public boolean checkGoalArea(){
 //   return(PCommander.getMasterMap().getMapCells().isObjectAt(DSCell.__DSGoalArea,PTask.getGoalArea()));
    return(true);
  }

  public synchronized void goalCompleted(DSAgent agent, DSGGoal goal) {
    if(!checkGoalArea())
      PCommander.scenarioFailed(this, "GC: No Goal area at"+PTask.getGoalArea());
  }

  public synchronized void goalFailed(DSAgent agent, DSGGoal goal){
    if(!checkGoalArea())
      PCommander.scenarioFailed(this,"No Goal area at "+PTask.getGoalArea());
  }

  public void calibrateScenario(DSMap map){
    PMasterDispenserPos=map.centralizeCoords(PMasterDispenserPos);
    PMasterGoalPos=map.centralizeCoords(PMasterGoalPos);
    PLeutnant1DispenserPos=map.centralizeCoords(PLeutnant1DispenserPos);
    PLeutnant1GoalPos=map.centralizeCoords(PLeutnant1GoalPos);
    PLeutnant2DispenserPos=map.centralizeCoords(PLeutnant2DispenserPos);
    PLeutnant2GoalPos=map.centralizeCoords(PLeutnant2GoalPos);
    PLeutnant3DispenserPos=map.centralizeCoords(PLeutnant3DispenserPos);
    PLeutnant3GoalPos=map.centralizeCoords(PLeutnant3GoalPos);
  }


  public boolean checkEvent(DSAgent agent, int eventType) {
    agent.printOutput("Checking event "+eventType);
    switch (eventType) {
      case DSMMission._disabledEvent:
        if (PMaster == agent) {
          PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - disabled -> getBlock");
          agent.hearOrder(new DSGGetBlock2022(PType1,PMasterDispenserPos, getTask().getDeadline()));
          PStateM = 1;
        }

        if (PLeutnant1 == agent) {
          PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - disabled -> getBlock");
          agent.hearOrder(new DSGGetBlock2022(PType2,PLeutnant1DispenserPos, getTask().getDeadline()));
          PStateL1 = 1;
        }

        if (PLeutnant2 == agent) {
          PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - disabled -> getBlock");
          agent.hearOrder(new DSGGetBlock2022(PType3,PLeutnant2DispenserPos, getTask().getDeadline()));
          PStateL2 = 1;
        }

        if (PLeutnant3 == agent) {
          PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - disabled -> getBlock");
          agent.hearOrder(new DSGGetBlock2022(PType4,PLeutnant3DispenserPos, getTask().getDeadline()));
          PStateL2 = 1;
        }
        return (true);

      case DSMMission._noBlockEvent:
        if (PMaster == agent) {
          PMaster.getBody().resetBody();
          if (PStateM == 2) {
            PStateM = 1;
            PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - no block -> getBlock");
            agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
          }
        }

        if (PLeutnant1 == agent) {
          PLeutnant1.getBody().resetBody();
          if (PStateL1 == 2) {
            PStateL1 = 1;
            PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - no block -> getBlock");
            agent.hearOrder(new DSGGetBlock2022(PType2, PLeutnant1DispenserPos, getTask().getDeadline()));
          }
        }

        if (PLeutnant2 == agent) {
          PLeutnant2.getBody().resetBody();
          if (PStateL2 == 2) {
            PStateL2 = 1;
            PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - no block -> getBlock");
            agent.hearOrder(new DSGGetBlock2022(PType3, PLeutnant2DispenserPos, getTask().getDeadline()));
          }
        }

        if (PLeutnant3 == agent) {
          PLeutnant3.getBody().resetBody();
          if (PStateL3 == 2) {
            PStateL3 = 1;
            PGUI.addText2Terminal(agent.getStep()+":"+agent.getEntityName() + " EVENT - no block -> getBlock");
            agent.hearOrder(new DSGGetBlock2022(PType4, PLeutnant3DispenserPos, getTask().getDeadline()));
          }
        }
        return (true);
    }
    return (false);
  }



  public DSSBlockMission(DeSouches commander, DSTask task) {
    super(commander, task);
    PGUI= DSTaskGUI.createTaskFrame();
    PCommander.getTasksFrame().addTaskFrame(PGUI.getFrame());
  }
}

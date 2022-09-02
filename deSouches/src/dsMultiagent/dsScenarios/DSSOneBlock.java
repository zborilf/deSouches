package dsMultiagent.dsScenarios;

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsGoals.*;
import dsMultiagent.dsTasks.DSTask;

import java.awt.*;

public class DSSOneBlock extends DSSBlockScenarios{


    int PStateM;
    DSAgent PMaster;
    Point PMasterGoalPos;
    Point PMasterDispenserPos;
    DSBody PMasterGoalBody;

    int PType1;

    @Override
    public String getName() {
        return ("One block scenario");
    }

    public void updateGUI(int step) {
        PGUI.setDsgTaskText(PTask, step, PMasterGoalPos, null, null, null);
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

            if (goal.getGoalDescription().contentEquals("evasiveManoeuvre")) {
                PGUI.addText2Terminal(agent.getEntityName()+" evasive manoeuvre -> goToPosition");
                PStateM = 2;
                agent.hearOrder(new DSGoToPosition(PMasterGoalPos, PMasterGoalBody, getTask().getDeadline()));
            }


            if (goal.getGoalDescription().contentEquals("goToPosition")) {
                PGUI.addText2Terminal(agent.getEntityName()+" goToPosition!!");
                agent.hearOrder(new DSAttachAndSubmit(PTask.getName(), "s", PType1));
            }
            if (goal.getGoalDescription().contentEquals("attachAndSubmit")) {
                PGUI.addText2Terminal(agent.getEntityName()+" attachAndSubmit!! COMPLETED");
                PCommander.scenarioCompleted(this);
            }
        }
        super.goalCompleted(agent,goal);

    }


    @Override
    public void goalFailed(DSAgent agent, DSGGoal goal) {

        super.goalFailed(agent,goal);
        agent.getCommander().printOutput(
                "goalFailed: "
                        + "SCEN: Task to je smula agente "
                        + agent.getEntityName()
                        + " kvuli "
                        + goal.getGoalDescription());

        if (agent == PMaster) {
            if (goal.getGoalDescription().contentEquals("get block 2022")) {
                PGUI.addText2Terminal(agent.getEntityName()+" FAILED M: goToDispenser -> repeat");
                agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
                PStateM = 1;
            }
            if (goal.getGoalDescription().contentEquals("goToPosition")) {
                PGUI.addText2Terminal(agent.getEntityName()+" FAILED M: goToPosition -> goToPosition");
                PStateM = 1;
                agent.hearOrder(new DSEvasiveManoeuvre());
            }
            if (goal.getGoalDescription().contentEquals("goRandomly")) {
                PGUI.addText2Terminal(agent.getEntityName() + " FAILED M: goRandomly -> getBlock");
                agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
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
                return (true);

            case DSScenario._noBlockEvent:
                if (PMaster == agent) {
                    PMaster.getBody().resetBody();
                    if (PStateM == 2) {
                        PStateM = 1;
                        agent.hearOrder(new DSGGetBlock2022(PType1, PMasterDispenserPos, getTask().getDeadline()));
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

        PAgentsAllocated.add(PMaster);

        PMasterDispenserPos = PTask.getSubtaskRoutes(0).getDispenserPosition();
        PType1 = PTask.getTypesNeeded().get(0);


        return (true);
    }

    @Override
    public boolean initScenario(int step) {


        if (!allocateAgents()) return (false);

        // posunout leutnanty na spravnou goalpozici
        // nastavit goalbody
        PMasterGoalBody = PTask.getTaskType().getSoldierGoalBody(PMaster);

        // task types
        PMaster.hearOrder(new DSGDetachAll());

        // setscenario agentum
        PMaster.setScenario(this);

        // nastaveni stavu automatu pro tento scenar
        PStateM = 1;


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
                        + PMasterGoalBody.bodyToString());

        updateGUI(step);

        return (true);
    }


    public DSSOneBlock(DeSouches commander, DSTask task) {
        super(commander, task);
        PPriority = 2;
    }

}

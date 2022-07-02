package dsAgents;

import antExploreUtils.AntMapUpdateSingleton;
import deSouches.utils.HorseRider;
import dsAgents.dsBeliefBase.DSBeliefBase;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsIntention.DSIntention;
import dsAgents.dsReasoningModule.dsIntention.DSIntentionPool;
import dsMultiagent.DSGroup;
import dsMultiagent.DSSynchronize;
import dsMultiagent.dsScenarios.DSScenario;
import eis.EnvironmentInterfaceStandard;
import eis.PerceptUpdate;
import eis.exceptions.AgentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.Percept;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DSAgent extends Agent {
  private static final String TAG = "DSAgent";

  private final int SLEEP_BETWEEN_STEPS = 50;

  private EnvironmentInterfaceStandard PEI;

  boolean PLeutnant;
  private DSBeliefBase PBeliefBase;
  private String PName;
  private String PEntity;
  private int PNumber;
  private int PIdleSteps;
  private Point PLastPosition;

  private int PScenarioPriority = 0;
  private DSIntentionPool PIntentionPool;
  private HashMap<DSAgent, Point> PSynchronized;

  public ArrayList<Point> roamList = new ArrayList<>();

  AntMapUpdateSingleton antmap = AntMapUpdateSingleton.getInstance();
  public int triedAdopt_deleteLater = 0;

  public String getAgentName() {
    return (PBeliefBase.getName());
  }

  public String getJADEAgentName() {
    return (PBeliefBase.getJADEName());
  }

  public int getStep() {
    return (PBeliefBase.getStep());
  }

  public DSMap getMap() {
    return (PBeliefBase.getMap());
  }

  public Point getMapPosition() {
    return (PBeliefBase.getAgentPosition());
  }

  public void holdsBlock(int type) {
    PBeliefBase.setHoldsBolockType(type);
  }

  public void holdsNothing(int type) {
    PBeliefBase.setHoldsBolockType(-1);
  }

  public int getVisionRange() {
    return (PBeliefBase.getVision());
  }

  public int getSpeed() {
    return (PBeliefBase.getSpeed());
  }

  public EnvironmentInterfaceStandard getEI() {
    return (PEI);
  }

  public void setScenario(DSScenario scenario) {
    PBeliefBase.setScenario(scenario);
    PScenarioPriority = scenario.getPriority();
    HorseRider.inform(
        TAG, "setScenario: priority for " + PBeliefBase.getName() + " is " + PScenarioPriority);
  }

  public boolean followsScenario() {
    return (PBeliefBase.getScenario() != null);
  }

  private int getScenarioPriority() {
    return (PScenarioPriority);
  }

  protected DSScenario getScenario() {
    return (PBeliefBase.getScenario());
  }

  protected void removeScenario() {
    PBeliefBase.setScenario(null);
    PScenarioPriority = 0;
    PBeliefBase.getCommander().needJob(this);
  }

  public int getIdleSteps() {
    return (PIdleSteps);
  }

  public boolean isBusy(int priority) {
    return (getScenarioPriority() >= priority);
  }

  public DeSouches getCommander() {
    return (PBeliefBase.getCommander());
  }

  public DSGroup getGroup() {
    return (PBeliefBase.getGroup());
  }

  public void setGroup(DSGroup group) {
    PBeliefBase.setGroup(group);
    PBeliefBase.setMap(group.getMap());
  }

  public void setBody(DSBody body) {
    PBeliefBase.setBody(body);
  } // TODO body zvlastni belief

  public DSBody getBody() {
    return (PBeliefBase.getBody());
  } // TODO body zvlastni belief

  public String getEntityName() {
    return (PBeliefBase.getName());
  }

  public int getNumber() {
    return (PNumber);
  }

  /*
             MAS INTERFACE
  */

  public boolean hearOrder(DSGoal goal) {
    // vytvori  intention a goal je jeji top level goal, rozsiri ni intention pool
    PIntentionPool.clearPool(); // TODO provizorne, pri vice intensnach odstranit
    DSIntention intention = new DSIntention(goal);
    PIntentionPool.adoptIntention(intention);
    return (true);
  }

  public boolean informCompleted(DSGoal goal) {
    if (PBeliefBase.getScenario() != null) {
      PBeliefBase.getScenario().goalCompleted(this, goal);
      return (true);
    } else return (false);
  }

  public boolean informFailed(DSGoal goal) {
    if (PBeliefBase.getScenario() != null) {
      PBeliefBase.getScenario().goalFailed(this, goal);
      return (true);
    } else return (false);
  }

  public void setActualRole(String role) {
    PBeliefBase.setRole(role);
  }

  public String getActualRole() {
    return (PBeliefBase.getAcualRole());
  }

  public Point getNearestGoal() {
    return (PBeliefBase.nearestGoal());
  }

  public Point getNearestDispenser(int dispenserType) {
    return (PBeliefBase.nearestDispenser(dispenserType));
  }

  public Point nearestFreeBlock(int blockType) {
    return (PBeliefBase.nearestFreeBlock(blockType));
  }

  public void setGUIFocus() {
    PBeliefBase.setGUIFocus(true);
  }

  public void removeGUIFocus() {
    PBeliefBase.setGUIFocus(false);
  }

  /*
   *                CONTROL LOOP
   */

  public void propagateFeedback(
      DSPerceptor perceptor, Collection<Percept> newPercepts, DSIntention recentIntentionExecuted) {

    int actionResult = PBeliefBase.getLastActionResult();

    if (recentIntentionExecuted != null) {
      recentIntentionExecuted.intentionExecutionFeedback(actionResult, (DSAgent) this);
    }
  }

  public class controlLoop extends CyclicBehaviour {

    DSAgent agent;

    DSIntention recentIntentionExecuted = null;

    public void action() {
      Map<String, PerceptUpdate> perceptsCol = null;
      PerceptUpdate percepts = null;
      DSPerceptor perceptor = new DSPerceptor();

      try {
        perceptsCol = PEI.getPercepts(PName, PEntity);
      } catch (PerceiveException e) {
        e.printStackTrace();
      }
      percepts = perceptsCol.get(PEntity);

      if (percepts.isEmpty()) {
        /*
        new percepts are gained only with new simulation step. inserting sleep to reduce CPU load, remove in case program
        doest not have time to process percepts
         */
        doWait(SLEEP_BETWEEN_STEPS);
        return;
      }

      // percepts not empty ↓↓↓

      // SENSING

      DSPerceptor.processPercepts(PBeliefBase, percepts);

      Collection<Percept> newPercepts = percepts.getAddList(); // TODO ??

      //              DSPerceptor.processPercepts(PBeliefBase,percepts);

      // FEEDBACK, result of the last action performed

      propagateFeedback(perceptor, newPercepts, recentIntentionExecuted);

      // MAP UPDATE

      Point myPos = PBeliefBase.getAgentPosition();

      //         PBeliefBase.getMap().clearArea(PBeliefBase.getVision(), myPos,
      // PBeliefBase.getStep());

      // barriera pro vycisteni mapy v dohledu
      // while(!PCommander.barrier(PStep,1,(DSAgent)this.getAgent())){}
      perceptor.actualizeMap(
          PBeliefBase.getMap(),
          PBeliefBase.getOutlook(),
          PBeliefBase.getMap().getAgentPos((DSAgent) (this.getAgent())),
          PBeliefBase.getVision(),
          PBeliefBase.getTeamName(),
          PBeliefBase.getStep(),
          (DSAgent) this.getAgent());

      // UPDATE PHEROMONES for exploration purposes
      antmap.updateMap(this.agent);

      if (PBeliefBase.getGUIFocus()) {

        PBeliefBase.getGUI()
            .setXY(PBeliefBase.getAgentPosition().x, PBeliefBase.getAgentPosition().y);
        PBeliefBase.getGUI()
            .setTextMap(
                "MAP:"
                    + PBeliefBase.getMap().getOwnerName()
                    + "\n"
                    + PBeliefBase.getMap().isMasterMap()
                    + "\n"
                    + PBeliefBase.getAgentPosition()
                    + "\n"
                    + PBeliefBase.getMap().stringMap());
        PBeliefBase.getGUI()
            .setPheromoneTextMap(
                "PHEROMONE MAP:"
                    + PBeliefBase.getMap().getOwnerName()
                    + "\n"
                    + PBeliefBase.getMap().isMasterMap()
                    + "\n"
                    + PBeliefBase.getAgentPosition()
                    + "\n"
                    + PBeliefBase.getMap().stringPheroMap());
      }

      // sensing phase is over, salut commander / for synchronization and commander level
      // reconsiderations

      PBeliefBase.getCommander().salut(PBeliefBase.getStep(), 0, (DSAgent) this.getAgent());

      perceptor.getBodyFromPercepts(percepts.getAddList());

      // tady nechapu, o co jde
      if (!perceptor.seesBlocksInBody(percepts.getAddList()))
        if (PBeliefBase.getScenario() != null)
          PBeliefBase.getScenario()
              .checkEvent((DSAgent) (this.getAgent()), DSScenario._noBlockEvent);

      //  == DEADLOCK ==

      // not moving period ++, deadlock detection
      if ((myPos.x == PLastPosition.x) && (myPos.y == PLastPosition.y)) PIdleSteps++;
      else {
        PIdleSteps = 0;
        PLastPosition = (Point) myPos.clone();
      }

      // pokud se nehybou dlouho, zlikvidujem scenar a nahlasime generalovi
      if (getScenario() != null)
        if (getScenario().checkDeadlock()) {
          // vsichni clenove tymu se nehybou dele, nez je stanoveny deadlock limit
          getCommander().scenarioFailed(PBeliefBase.getScenario());
          //    System.out.println(getEntityName()+" DEADLOCK pro " +
          //                  "task "+getScenario().getTask().getName()+
          //                  " group members "
          //                  getScenario().getAgentsAllocatedText());
        }

      // agent disabled? Inform and dont execute
      if (perceptor.disabled(newPercepts)) {
        PBeliefBase.getCommander().agentDisabled((DSAgent) this.getAgent());
        return;
      }

      // EXECUTION
      // vyber zameru
      // vykonani zameru
      // report uspesneho/neuspesneho ukonceni nasledovani zameru

      // report spatrenych pratel, skrz mapu -> vede na merge group
      PBeliefBase.getSynchronizer()
          .addObservation(
              (DSAgent) this.getAgent(),
              PBeliefBase.getStep(),
              PBeliefBase.getOutlook().getFriendsSeen(PBeliefBase.getVision()),
              PBeliefBase.getTeamSize());

      if (recentIntentionExecuted != null) {
        if (recentIntentionExecuted.intentionState() == DSIntention.__Intention_Finished) {
          PIntentionPool.removeIntention(recentIntentionExecuted);
          informCompleted(recentIntentionExecuted.getTLG());
        } else if (recentIntentionExecuted.intentionState() == DSIntention.__Intention_Failed) {
          informFailed(recentIntentionExecuted.getTLG());
          PIntentionPool.removeIntention(recentIntentionExecuted);
        }
      }

      if ((PIntentionPool.getIntention() == null) && (getScenario() == null))
        PBeliefBase.getCommander().needJob((DSAgent) this.getAgent());

      // EXECUTING INTENTION
      recentIntentionExecuted = PIntentionPool.executeOneIntention((DSAgent) this.getAgent());
      // PRINT recentIntention on GUI

      if (PBeliefBase.getGUIFocus()) {
        PBeliefBase.getGUI().noticeLastGoal(recentIntentionExecuted.getTLG().getGoalDescription());
        PBeliefBase.getGUI().writePlan(recentIntentionExecuted.getRecentPlan());
      }
    } // END action()

    public controlLoop(DSAgent agent) {
      super();
      this.agent = agent;
    }
  }

  public void setup() {

    try {
      PEI.registerAgent(PName);
    } catch (AgentException ex) {
    }

    try {
      PEI.associateEntity(PName, PEntity);
    } catch (RelationException ere) {
      HorseRider.yell(TAG, "setup: " + " ####### asociace se nezdarila", ere);
    }
    PIdleSteps = 0;
    PLastPosition = new Point(0, 0);
  }

  protected void takeDown() {
    HorseRider.warn(TAG, "takeDown: " + "Agent " + getAID().getName() + " umiiiraaaa.");
  }

  public DSAgent(
      String name,
      DSGroup group,
      String entity,
      EnvironmentInterfaceStandard ei,
      DeSouches commander,
      int number,
      boolean leutnant,
      DSSynchronize synchronizer,
      dsGUI gui,
      boolean guiFocus) {
    super();

    PLeutnant = leutnant; // in 2020 there will be little bit more roles
    PNumber = number; // but unique number remains
    PEI = ei; // handle to ei (environment to connect the server)
    PName = name; // tohle ale prijde s prvnim vemem (a nastavi tak BB/GUI)
    PEntity = entity;
    PIntentionPool = new DSIntentionPool(); // set of intentions
    PSynchronized = new HashMap<DSAgent, Point>(); // ???

    PBeliefBase = new DSBeliefBase(this, synchronizer); // new BB for the agent

    PBeliefBase.setGUI(gui);
    PBeliefBase.setGUIFocus(guiFocus);

    PBeliefBase.setJADEName(name); // jade name
    PBeliefBase.setIsLeutnant(leutnant); // TODO should be general role in 2020
    PBeliefBase.setCommander(commander); // commander of this agent (deSouches)
    PBeliefBase.setBody(
        new DSBody()); // shape of the agene (one element for agent, later more complex with boxes
    // attached)
    // TODO - recet connection -> agent should check its shape, could carry some boxes

    if (group == null) {
      group = new DSGroup(this);
      group.getMap().addNewAgent(this, new Point(0, 0));
      //    commander.groupCreated(group);
    }
    PBeliefBase.setGroup(group);
    PBeliefBase.setMap(group.getMap());

    controlLoop loop = new controlLoop(this);
    addBehaviour(loop);
  }
}

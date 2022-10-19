package dsAgents;

import antExploreUtils.AntMapUpdateSingleton;
import deSouches.utils.HorseRider;
import dsAgents.dsBeliefBase.DSBeliefBase;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSAgentOutlook;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsExecutionModule.dsActions.DSSkip;
import dsAgents.dsGUI.DSAgentGUI;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsGoals.DSGGoal;
import dsAgents.dsReasoningModule.dsIntention.DSIntention;
import dsAgents.dsReasoningModule.dsIntention.DSIntentionPool;
import dsMultiagent.DSGroup;
import dsMultiagent.dsScenarios.DSMMission;
import eis.EnvironmentInterfaceStandard;
import eis.PerceptUpdate;
import eis.exceptions.AgentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.Percept;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell.*;
import static dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell.__DSDispenser;
// import static dsAgents.dsReasoningModule.dsIntention.DSIntention.__Intention_Finished;

public class DSAgent extends Agent {
  private static final String TAG = "DSAgent";

  private final int SLEEP_BETWEEN_STEPS = 10;

  private EnvironmentInterfaceStandard PEI;

  boolean PLeutnant;
  private DSBeliefBase PBeliefBase;
  private String PName;
  private String PEntity;
  private int PNumber;
  private int PIdleSteps;
  private Point PLastPosition;
  private FileWriter POutput;

  private int PScenarioPriority = 0;
  private DSIntentionPool PIntentionPool;
  private HashMap<DSAgent, Point> PSynchronized;

  public ArrayList<Point> roamList = new ArrayList<>();

  AntMapUpdateSingleton antmap = AntMapUpdateSingleton.getInstance();

/*
                  IMPLEMENTATION
 */

  //    I/O services

  public void printOutput(String s){
    try{
      POutput.write(s+"\n");
      POutput.flush();
    }catch(Exception e){};
  }

  /*
    Belief base interface
  */


  public String getAgentName() {
    return (PBeliefBase.getName());
  }

  public int getVision() { return(PBeliefBase.getVision()); }

  public DSAgentOutlook getOutlook() { return(PBeliefBase.getOutlook()); }

  public String getJADEAgentName() {
    return (PBeliefBase.getJADEName());
  }

  public int getStep() {
    return (PBeliefBase.getStep());
  }

  public DSMap getMap() {
    return (PBeliefBase.getGroup().getMap());
  }

  public String getLastGoal() {
    return (PBeliefBase.getLastGoal());
  }

  public Point getMapPosition() {
    return (PBeliefBase.getAgentPosition());
  }

  public void holdsBlock(int type) {
    PBeliefBase.setHoldsBolockType(type);
  }

  public boolean isHoldingBlock(){
    return(getOutlook().seesBlockBy(new Point(0,0),0,10));
  }

  public void holdsNothing(int type) {
    PBeliefBase.setHoldsBolockType(-1);
  }

  public boolean isAttacchedAt(Point position){
    return(PBeliefBase.isAttached(position));
  }

  public int getVisionRange() {
    return (PBeliefBase.getVision());
  }

  public int getSpeed() {
    return (PBeliefBase.getSpeed());
  }

  public FileWriter getOutput(){
    return(POutput);
  }

  public EnvironmentInterfaceStandard getEI() {
    return (PEI);
  }


  public void setScenario(DSMMission scenario) {
    PBeliefBase.setMission(scenario);
    PScenarioPriority = scenario.getPriority();
    HorseRider.inform(
        TAG, "setScenario: priority for " + PBeliefBase.getName() + " is " + PScenarioPriority);
  }

  public boolean followsScenario() {
    return (PBeliefBase.getMission() != null);
  }

  private int getScenarioPriority() {
    return (PScenarioPriority);
  }

  protected DSMMission getMission() {
    return (PBeliefBase.getMission());
  }

  public String getScenarioName() {
    if(getMission()==null)
      return("no Scenario");
    return(getMission().getName());
  }

  /*
        Orders and queries from deSouches
   */

  protected void removeScenario() {
    printOutput("REMOVING SCENARIO");
    PBeliefBase.setMission(null);
   // PIntentionPool.clearPool();       // TODO ... experiment
    PScenarioPriority = 0;
  }

    public boolean isBusyTask() {
      if(PBeliefBase.getMission()==null)
        return(false);
      return(PBeliefBase.getMission().getTask()!=null);
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


  public DSCell getNotRecentlyAttacked(LinkedList<DSCell> cells){
    LinkedList<Point> attacked=
            PBeliefBase.getMap().getRecentlyAttackedPositions(PBeliefBase.getStep()-DSConfig.___attackInterval);
    for(DSCell cell:cells) {
      DSMap map= PBeliefBase.getMap();;
      Point p2=map.centralizeCoords(map.shiftPosition(this,cell.getPosition()));
      if (!attacked.contains(p2))
        return (cell);
    }
      return(null);
  }

  /*
             MAS INTERFACE
  */

  public void clearPerformed(Point target){
    DSMap map=PBeliefBase.getMap();
    Point targetAbs=map.centralizeCoords(map.shiftPosition(this, target));
    map.positionCleared(targetAbs,PBeliefBase.getStep());
  }

  public boolean hearOrder(DSGGoal goal) {
    // vytvori  intention a goal je jeji top level goal, rozsiri ni intention pool
    PBeliefBase.setJobDemanded(false);      // if a demand is to deSouches is pending
    PIntentionPool.clearPool(); // TODO provizorne, pri vice intensnach odstranit
    printOutput("NEW COMMAND! "+goal.getGoalDescription());
    DSIntention intention = new DSIntention(goal);
    PIntentionPool.adoptIntention(intention);
    return (true);
  }

  public boolean informCompleted(DSGGoal goal) {
    if (PBeliefBase.getMission() != null) {
      PBeliefBase.getMission().goalCompleted(this, goal);
      return (true);
    } else return (false);
  }

  public boolean informFailed(DSGGoal goal) {
    if (PBeliefBase.getMission() != null) {
      printOutput("Failed goal "+goal.getGoalDescription());
      PBeliefBase.getMission().goalFailed(this, goal);
      return (true);
    } else return (false);
  }

  public String getActualRole() {
    return (PBeliefBase.getAcualRole());
  }


  public Point getNearestGoal() {
    return (PBeliefBase.nearestGoal());
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

  public void executeSkip(){
      new DSSkip().execute(this);
  }

  /*
   *
   *                CONTROL LOOP
   *
   */


  public void propagateFeedback(DSIntention recentIntentionExecuted) {

    int actionResult = PBeliefBase.getLastActionResult();


    try{
      POutput.write("LAR: "+  PBeliefBase.getLastActionResultString()+"\n");
    }catch(Exception e){};

      PIntentionPool.processFeedback(actionResult, this);
  }


  /*
            AGENT CONTROL LOOP
   */


  /*
        LOOP
          % sensing
            S1, GET SENSOR DATA - PERCEPTS
            S2, PROCESS PERCEPTS (one by one)
            S3, PROPAGATE FEEDBACK (nejde to dat do process percepts?)
            S4, UPDATE MAP (Using Outlook created in S2
            S5, UPDATE PHERMONE MAP
             6, PRINT . AGENT GUI
            C7, SALUT COMANDER
            C8, RAISE 'NOTHING CARYING' EVENT ... PROB TO HANDLE CLEAR ATTACK
            C9, CHECK DEADLOCK, AGENT IS NOT MOVING, CANCEL SCENARIO (move to C7)?
            C10, CHECK AGENT DISABLED -> INFORM COMANDER
            C11, SYNCHRONIZER, REPORT OBSERVATION, POSSIBLY MERGE MAPS

            E12, IF INTENTION FAILED -> INFORM
            E13, IF INTENTION COMPLETED -> INFORM
            E14, IF NO SCENARIO and NO INTENTION -> ASK FOR JOB
            E15, EXECUTE, IF THERE IS SOMETHING TO DO


  */

  public class controlLoop extends CyclicBehaviour {

    DSAgent agent;

    DSIntention recentIntentionExecuted = null;



    void drawMaps2GUI(){

      if (PBeliefBase.getGUIFocus()) {

        PBeliefBase.getGUI()
                .setXY(PBeliefBase.getAgentPosition().x, PBeliefBase.getAgentPosition().y);
        DSMap map=PBeliefBase.getMap();
        int ga=map.getMapCells().getAllType(__DSGoalArea).size();
        int za=map.getMapCells().getAllType(__DSRoleArea).size();
        int d0=map.getMapCells().getAllType(__DSDispenser+0).size();
        int d1=map.getMapCells().getAllType(__DSDispenser+1).size();
        int d2=map.getMapCells().getAllType(__DSDispenser+2).size();


        PBeliefBase.getGUI()
                .setTextMap(
                        "MAP:"
                                + map.getOwnerName()
                                + "\n"
                                + map.isMasterMap()
                                + "\n"
                                + PBeliefBase.getAgentPosition()
                                + "\n"
                                + ga + "/" + za + "/"+ d0 + "/"+ d1 + "/"+ d2 + "/ \n"
                                + map.stringMap());


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
    }

    void printOutputAfterCycle(){
      printOutput("----------------------------------------------------------");
      printOutput("Step: " + PBeliefBase.getStep() + " , pos " + PBeliefBase.getAgentPosition().toString());
      printOutput("Agent body "+getBody().bodyToString());
      printOutput("Atteched "+PBeliefBase.getAttched().toString());
      if(PBeliefBase.getMission()!=null)
        printOutput("Scenario: "+PBeliefBase.getMission().getName());
      else
        printOutput("Scenario: none");
      printOutput("Goal: "+PBeliefBase.getLastGoal());
      if(recentIntentionExecuted!=null)
        if(recentIntentionExecuted.getTLG()!=null)
          printOutput(" desc: "+ recentIntentionExecuted.getTLG().getGoalParametersDescription());
        else
          printOutput("\n");
      printOutput("Control Loop Finished");
    }

/*
          +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
          ==========================   CONTROL LOOOP   ================================
 */


    public void action() {

      Map<String, PerceptUpdate> perceptsCol = null;



      /*
                  SENSING
       */

      try {
        perceptsCol = PEI.getPercepts(PName, PEntity);
      } catch (PerceiveException e) {
        e.printStackTrace();
      }
      PerceptUpdate percepts = perceptsCol.get(PEntity);

      if (percepts.isEmpty()) {
        /*
        new percepts are gained only with new simulation step. inserting sleep to reduce CPU load, remove in case program
        doest not have time to process percepts
         */
        doWait(SLEEP_BETWEEN_STEPS);
        return;
      }

      // percepts not empty ↓↓↓

      DSPerceptor.processPercepts(PBeliefBase, percepts);

      Collection<Percept> newPercepts = percepts.getAddList(); // TODO ??

      // System.out.println(newPercepts);

      //              DSPerceptor.processPercepts(PBeliefBase,percepts);

      // FEEDBACK, result of the last action performed

      DSPerceptor perceptor = new DSPerceptor();

      // Must be before Map Update ... reflects the results of 'Move' actions ... actualizes the agent position

      propagateFeedback(recentIntentionExecuted);

      /*
                  MAP UPDATE
       */

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

      drawMaps2GUI();



      // sensing phase is over, salut commander / for synchronization and commander level
      // reconsiderations

      if(!PBeliefBase.getCommander().salut(PBeliefBase.getStep(), PBeliefBase.getScore(), (DSAgent) this.getAgent()))

        // waits when it is not the last salut in the round
      //       this.getAgent().doWait();

      /*
              C8
       */

      // v případě, že agent nic nenese, ověřit, jetli je to v pořádku
      // pro jistotu, nevím, na základě čeho by agent mohl seznat, že mu byl odsteřal blok, ověřit

      if (!perceptor.seesBlocksInBody(agent.getOutlook()))
        if (PBeliefBase.getMission() != null)
          PBeliefBase.getMission()
              .checkEvent((DSAgent) (this.getAgent()), DSMMission._noBlockEvent);

      //  == DEADLOCK ==

      // not moving period ++, deadlock detection


       /*
            C9
       */


      Point myPos = PBeliefBase.getAgentPosition();


      if ((myPos.x == PLastPosition.x) && (myPos.y == PLastPosition.y)) PIdleSteps++;
      else {
        PIdleSteps = 0;
        PLastPosition = (Point) myPos.clone();
      }

      // agent disabled? Inform and don§t execute
      // tady to nema co delat, melo by se osetrovat pri zpracovani perceptu

      if (perceptor.disabled(newPercepts)) {
        PBeliefBase.getCommander().agentDisabled((DSAgent) this.getAgent());
        return;
      }

      /*
              C11
       */


      // EXECUTION
      // vyber zameru
      // vykonani zameru
      // report uspesneho/neuspesneho ukonceni nasledovani zameru

      // MUSI BYT PREDTIM ZPRACOVANY VSTUPY VSECH AGENTU, KVULI SYNCHRONIZACI!!!!


      /*
            Uprava, o tom, ktera intention byla naposled odpalena a jak to dopadlo se ma starat
            Intention pool!
       */

    switch (PIntentionPool.checkActualIntentionPoolState()) {
      case DSIntentionPool.__No_Intention_Left:
        if (!followsScenario())
          // there is no intention and no scenario is active (should send some command later)
          // inform deSouches and skip
          if(!PBeliefBase.getJobDemanded()) {
            PBeliefBase.setJobDemanded(true);
            PBeliefBase.getCommander().needJob((DSAgent) this.getAgent());
          }
        break;

      case DSIntentionPool.__Recent_Intention_Failed:
        informFailed(PIntentionPool.getRecentIntentionTLG());
        break;

      case DSIntentionPool.__Recent_Intention_Finished:
        informCompleted(PIntentionPool.getRecentIntentionTLG());
        break;

      case DSIntentionPool.__Recent_Intention_Persists:
      case DSIntentionPool.__Nothing_Executed_But_Possible:

    }

      // EXECUTING INTENTION

      PIntentionPool.executeOneIntention((DSAgent) this.getAgent());

      // some outro ... remember last goal (could be taken from the pool, but who cares)

      DSGGoal g=PIntentionPool.getRecentIntentionTLG();
      if(g==null)
        PBeliefBase.setLastGoal("No goal");
      else{
        PBeliefBase.setLastGoal(g.getGoalDescription());
        if (PBeliefBase.getGUIFocus()) {
          PBeliefBase.getGUI().noticeLastGoal(g.getGoalDescription());
          PBeliefBase.getGUI().writePlan(g.getRecentPlan());
        }
      }


      // PRINT recentIntention on GUI
      printOutputAfterCycle();

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
      DSAgentGUI gui,
      boolean guiFocus) {
    super();

    PLeutnant = leutnant; // in 2020 there will be little bit more roles
    PNumber = number; // but unique number remains
    PEI = ei; // handle to ei (environment to connect the server)
    PName = name; // tohle ale prijde s prvnim vemem (a nastavi tak BB/GUI)
    PEntity = entity;
    PIntentionPool = new DSIntentionPool(); // set of intentions
    PSynchronized = new HashMap<DSAgent, Point>(); // ???

    PBeliefBase = new DSBeliefBase(this); //, synchronizer); // new BB for the agent

    PBeliefBase.setGUI(gui);
    PBeliefBase.setGUIFocus(guiFocus);

    PBeliefBase.setJADEName(name); // jade name
    PBeliefBase.setIsLeutnant(leutnant); // TODO should be general role in 2020
    PBeliefBase.setCommander(commander); // commander of this agent (deSouches)
    PBeliefBase.setBody(
        new DSBody()); // shape of the agene (one element for agent, later more complex with boxes

    try {
      POutput=new FileWriter("ag_"+entity+".txt");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }


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

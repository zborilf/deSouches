package dsAgents;

import deSouches.utils.HorseRider;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsGUI.DSAgentGUI;
import dsAgents.dsGUI.DSGTaskPoolFrame;
import dsAgents.dsGUI.DSGeneralGUI;
import dsAgents.dsGUI.DSTaskGUI;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
import dsAgents.dsReasoningModule.dsGoals.DSEvasiveManoeuvre;
import dsAgents.dsReasoningModule.dsGoals.DSGChangeRole;
import dsAgents.dsReasoningModule.dsGoals.DSGDetachAll;
import dsAgents.dsReasoningModule.dsGoals.DSGForceDetach;
import dsMultiagent.DSGroup;
import dsMultiagent.DSSynchronize;
import dsMultiagent.dsGroupOptions.dsGroupOption;
import dsMultiagent.dsGroupOptions.dsGroupOptionsPool;
import dsMultiagent.dsGroupOptions.dsGroupTaskOption;
import dsMultiagent.dsGroupReasoning.DSCCoalition;
import dsMultiagent.dsGroupReasoning.DSCCoalitionMaker;
import dsMultiagent.dsGroupReasoning.DSCCoalitionMember;
import dsMultiagent.dsScenarios.*;
import dsMultiagent.dsTasks.DSTask;
import dsMultiagent.dsTasks.DSTaskMember;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ManagementException;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import massim.eismassim.EnvironmentInterface;

public class DeSouches extends Agent {

  private static final String TAG = "DeSouches";
  private static final double _timeNeeded = 1.3;      // estim. delay
  private static final int _busyAgentsLimit = 8;



  private int PTeamSize;
  private DSAgentGUI PGUI;
  private DSGeneralGUI PGGUI;
  private DSGTaskPoolFrame PGTP;
  private DSAgent PGUIFocus;

  static EnvironmentInterfaceStandard PEI;
  private int PPopulationSize = 0;
  private DSSynchronize PSynchronizer;
  private dsMultiagent.dsGroupOptions.dsGroupOptionsPool PGroupOptions;
  private LinkedList<String> PActiveTasks;
  LinkedList<DSTaskGUI> PTaskGUIs;
  int PScore=0;

  private HashMap<String, DSAgent> PRegisteredAgents;

  private FileWriter POutput, PMapsOutput;


  private HashMap<Integer, LinkedList<DSAgent>> PBarriers;
  private HashMap<Integer, Boolean> PStepsDone;


  private LinkedList<DSMMission> PActiveMissions;

  public DSGTaskPoolFrame getTasksFrame(){
    return(PGTP);
  }


  public DSMap getMasterMap(){
    return(PSynchronizer.getMasterGroup().getMap());
  }

  public void printOutput(String s){
    try{
      POutput.write(s+"\n");
      POutput.flush();
    }catch(Exception e){};
  }

  /*
     deSouches BB is included in this module
  */

  private int PLastStep = 0;

  /*
      DIVISIONS MANAGEMENT
  */


  public void setTeamSize(int teamSize){
    PTeamSize=teamSize;
  }

  /*
     Group reasoning: revise options /f relevant strategies are fixed for the tasks
                                         applicable (?), teams reconsiderations
  */

  public String roleNeeded(DSAgent agent) {
    if(agent.getActualRole().contentEquals("worker") ||
            agent.getActualRole().contentEquals("digger"))
      return("");

    String role = "digger";
    LinkedList<DSAgent> workers = agent.getGroup().getMembersByRole("worker",false);
    if (workers != null)
      if (workers.size() >= DSConfig.___max_workers) return (role);
    if (Math.random() > DSConfig.___diggerRatio) role = "worker";
    else
      role = "digger";
    return (role);
  }

  /*
         Reasoning about tasks -> coalition search


  void printPointLists(LinkedList<LinkedList<Point>> points) {
    String st;
    for (LinkedList<Point> points2 : points) {
      st = "";
      for (Point point : points2) st = st + "[" + point.x + "," + point.y + "] ";
      PGGUI.addTask(st);
    }
  }
*/

  synchronized ArrayList<DSCCoalition> proposeCoalitions4task(DSTask task) {

    /*
        toto je bastl, opravit po ladeni, vyhodit
     */


    ArrayList<DSCCoalition> coalitions = null;
    LinkedList<Integer> types = task.getTypesNeeded();

    boolean possible = true;
    if (PSynchronizer.getMasterGroup() == null) {
   //   PGGUI.addText("No Mastergroup");
      return (coalitions);
    }

    LinkedList<DSAgent> workers = new LinkedList<DSAgent>();
    LinkedList<LinkedList<Point>> dispensersForTypes = new LinkedList<LinkedList<Point>>();
    LinkedList<Point> dispensersForType = new LinkedList<Point>();
    LinkedList<Point> goalslLocations = new LinkedList<Point>();
    LinkedList<Point> goalZones = new LinkedList<Point>();

    DSMap map = PSynchronizer.getMasterGroup().getMap();
  //  String st;
    if (types == null) possible = false;
    else {
   //   PGGUI.addText("Task nickname "+task.getNickName()+"\n");
      // list of list of dispensers locations for every needed type

      for (int type : types) {
   //     st = "DType "+type + ": ";
        dispensersForType = map.getTypePositions(DSCell.__DSDispenser + type);
        if (dispensersForType.isEmpty()) {
          possible = false;
   //       st = st + "nejsou";
        } else {
          dispensersForTypes.add(dispensersForType);
    //      for (Point position : dispensersForType) st = st + " / " + position.toString();
        }
    //    PGGUI.addText(st);
      }

    //  st = "Goal:";

      goalslLocations = map.getTypePositions(DSCell.__DSGoalArea);


      LinkedList<Point> goalZones2 =  PSynchronizer.getMasterGroup().getMap().getPointsZones(goalslLocations);

      /*
      Filters goal locations to those not in distance to 4 from any allocated goal
       */
      for(Point gA:goalZones2) {
        boolean possibleGA = true;
        for (Point allocatedGA : getTasksGoalAreas())
          if (PSynchronizer.getMasterGroup().getMap().distance(gA, allocatedGA) < DSConfig.___safeGoalAreasDistance) {
            possibleGA = false;
            break;
          }
        if(possibleGA)
          goalZones.add(gA);
      }


      if (goalZones == null) {
      //  st = st + "nejsou";
        possible = false;
      } // else for (Point position : goalZones) st = st + " / " + position.toString();
      // PGGUI.addText(st);


      // st = "Agents: ";
      for (DSAgent agent : PSynchronizer.getMasterGroup().getMembers()) {
      //  st = st + " " + agent.getMapPosition();
      }
//      PGGUI.addText(st);

  //    st = "Diggers: ";
      LinkedList<DSAgent> diggers = PSynchronizer.getMasterGroup().getMembersByRole("digger",false);
    //  if (diggers != null) for (DSAgent digger : diggers) st = st + digger.getEntityName() + " / ";
    //  PGGUI.addText(st);

  //    st = "Not busy workers: ";
      workers = PSynchronizer.getMasterGroup().getMembersByRole("worker",true);
      if (workers == null) possible = false;
   //   else for (DSAgent worker : workers) st = st + worker.getEntityName() + " / ";
   //   PGGUI.addText(st);
    }

    if (possible)
      if (workers.size() < dispensersForTypes.size())
        // not enaugh workers
        possible = false;


    if (possible) {
      /*PGGUI.addText("" +
              "" +
              "TASK POSSIBLE!");*/

      // computes coalitions
      // takse the cheapest ( get(0) )
      // prints it on General GUI
      // and finally modify 'subtasks' in the 'task' properly

      int timeout=(int)Math.round(((task.getDeadline()-PLastStep)-DSConfig.___taskTimeoutProlongAdd) /
              DSConfig.___taskTimeoutProlongMulti);

//      printOutput("Possible goal zones "+goalZones.toString());

      coalitions =
              new DSCCoalitionMaker().proposeTaskCoallitions(workers, dispensersForTypes, goalZones,
                      timeout);




    }
      return (coalitions);
    }

  /*
        Execute selected and prepared task
   */

  boolean executeTask(DSTask task){

    int taskType = task.getTaskTypeNumber();

    printOutput("---------------");
    printOutput(PLastStep+": Task : "+task.getName()+" type "+task.getTaskType().getTaskType()+
            " deadline "+task.getDeadline());
    for(int i=0;i<4;i++)
      if(task.getSubtaskRoutes(i)!=null)
        printOutput(">> "+task.getTypesNeeded().get(i)+"::"+task.getSubtaskRoutes(i).taskMember2String()+
                                                              " sup. price "+task.getSubtaskRoutes(i).getPrice());

      Point g=task.getSubtaskRoutes(0).getGoalPosition();
      printOutput("Checkuju pozici golu "+g);
      printOutput("Vysledek je "+getMasterMap().getMapCells().isObjectAt(DSCell.__DSGoalArea,g));

      printOutput("---------------");



    if (taskType <= 3){
      printOutput("---------------");
      DSSTwoBlocks twoBlocks = new DSSTwoBlocks(this, task);
      twoBlocks.initMission(PLastStep);
      PActiveMissions.add(twoBlocks);
      PActiveTasks.add(task.getNickName());
      return (true);
    }

    if ((taskType>3) && (taskType <= 13)) {
      printOutput("Budou trojicky");
      DSSThreeBlocks threeBlocks = new DSSThreeBlocks(this, task);
      threeBlocks.initMission(PLastStep);
      PActiveTasks.add(task.getNickName());
      PActiveMissions.add(threeBlocks);
      return (true);
    }


    if (taskType <= 42) {
      printOutput("Budou ctvericky");
      DSSFourBlocks fourBlocks= new DSSFourBlocks(this, task);
      fourBlocks.initMission(PLastStep);
      PActiveTasks.add(task.getNickName());
      PActiveMissions.add(fourBlocks);
      return (true);
    }


    if (taskType == 43){
      printOutput(("Jednickovej task"));
      DSSOneBlock oneBlock= new DSSOneBlock(this, task);
      oneBlock.initMission(PLastStep);
      PActiveTasks.add(task.getNickName());
      PActiveMissions.add(oneBlock);
      return(true);
    }



    return (false);
  }


  void printScenarios(){

    for(DSMMission scenario: PActiveMissions){
      printOutput("Scenario "+scenario.getName()+" / "+scenario.toString());
      if(scenario.getTask()!=null)
        printOutput("for task "+scenario.getTask().getName());
      printOutput(" -- "+scenario.getAgentsAllocatedText());
    }
  }

  void printInformations(){
    try {
      PMapsOutput.write("Step: "+PLastStep+"\n");
      PMapsOutput.write(PSynchronizer.getMasterGroup().getMap().stringMap());
      PMapsOutput.write("\n\n");
      PMapsOutput.flush();
    } catch (Exception e) {
    }
    printOutput(" --------------------------------- ");
    printOutput("Step "+PLastStep);
    printOutput("Active tasks: " +(PActiveTasks.toString()));
    printOutput("Active scenarios ");
    printScenarios();
    printOutput("Goal positions allocated: " +getTasksGoalAreasSt());
    printOutput("Score: "+PScore);

  }

  public synchronized void cloneTask(DSTask task, int step) {

    String name;
    int deadline;
    int reward;
    DSBody body;
    DSCell cell;

    name = task.getName();
    deadline = task.getDeadline();
    reward = task.getReward();


    body = new DSBody();
    for (DSCell cell2: task.getTaskBody().getBodyList()) {

      int x = cell2.getX();
      int y = cell2.getY();
      int type = cell2.getType();
      cell = new DSCell(x, y, type, 0);
      if (Math.abs(x) + Math.abs(y) == 1)
        body.insertFirstCell(cell); // must be at the first position
      else
        if(cell.getType()!=DSCell.__DSAgent)
          body.addCell(cell);
    }

   taskProposed(
            new DSTask(name, deadline, reward, body, step)); // nebo primo do GroupOptionsPool? Necht je to pres deSouches
  }

  void groupReasoning() {

    printInformations();



    LinkedList<DSMMission> missions = (LinkedList<DSMMission>) PActiveMissions.clone();

    for (DSMMission mission : missions){
      int missionState=mission.checkConsistency();
      if(missionState==DSMMission.__mission_timeout) {
        printOutput("@@! Scenario failed due to timeout " + mission.getTask().getName());
        scenarioFailed(mission," TIMEOUT");
      }
      if (missionState == DSMMission.__no_goal_area) {
        printOutput("@@! Scenario failed due to invalid goal destination " + mission.getTask().getName());
        scenarioFailed(mission," NO GOAL AREA");
      }
/*      int sd=mission.getDeadline();
      if(mission.getTask()!=null) {
        sd = sd - mission.getTask().goalDistanceMax(); // minimal estimated time of completion
      }

      if (sd < PLastStep) {
        printOutput("@@! Scenario failed due to timeout " + mission.getTask().getName());
        scenarioFailed(mission," TIMEOUT");
      }*/
    }

    for (dsGroupOption option : PGroupOptions.getOptions())

      /*
            Task Options
       */

      if ((option instanceof dsGroupTaskOption)){ //&&(!PMakamNaUloze)) {   // zatim jen jedna uloha je mozna

        DSTask task = ((dsGroupTaskOption) option).getTask();

        if (task.getDeadline() < PLastStep) PGroupOptions.removeOption(task.getName());
        //       if(task.getTypesNeeded()!=null) {
        //           PGGUI.addTask("Je treba resit " + task.getName() +
        //                   " deadline " + task.getDeadline() + " pozadavky " +
        // task.getTypesNeeded().toString());
        ArrayList<DSCCoalition> coalitions= proposeCoalitions4task(task);
        printOutput("group option "+task.getName());
        if(coalitions!=null)
          if(coalitions.size()>0){

            // clone it
/*
            DSTask task2 = cloneTask(((dsGroupTaskOption) option).getTask(),task.getName()+"_"+PLastStep);
            taskProposed(task2);
 */
            DSCCoalition coalition=coalitions.get(0);
            DSTaskMember tmember;
            for (DSCCoalitionMember cmember : coalition.getCoalitionMembers()) {
              tmember = new DSTaskMember(cmember.getAgent(), cmember.getPDispenser(), cmember.getGoal(), cmember.getPrice());
              task.setSubtaskRoute(cmember.getTaskID() - 1, tmember);
            }
           // String taskString = task.task2String(PLastStep);

            // take a copy of proposed ... possibly multiple tasks of the same kinf


            //PGGUI.addText(taskString);

            //   if ((task.getDeadline() - PLastStep ) > (task.subtaskCostEstimation() + _timeNeeded)) {

            executeTask(task); // task is possible (agents, resources, time)

            PGroupOptions.removeOption(task.getName());

            cloneTask(task,PLastStep);   // new option for the same task, could be followed more times

            break; // one task per cycle

        } // end non-empty coalition



      } // end na udalost typu 'novy task'

      /*
            tasksCoalitions contains possible coalitions for actual tasks
       */

      /*
            END sumarizace TASK OPTIONS
       */

  }

  public synchronized boolean salut(int step, int score, DSAgent agent) { // barrier

  //  System.out.println("Step "+step+" - "+agent.getEntityName()+" salutes ");

    if (PSynchronizer
            .addObservation(
                    POutput,
                    agent,
                    agent.getStep(),
                    agent.getOutlook().getFriendsSeen(agent.getVision()),
                    PTeamSize))
    {
      // steps done
      PScore=score;

      PStepsDone.put(agent.getStep(),true);

   //   PGGUI.clearTasks();
      PLastStep = step;
    //  PGGUI.addText("Step:" + PLastStep);

      if(PSynchronizer.getMasterGroup()==null)
        if (PSynchronizer.masterGroupCandidate(agent.getGroup()))
          printOutput("> ! > " + agent.getStep() + " MASTERGRUPA " + agent.getEntityName() + "!!!\n");

      if(PSynchronizer.getMasterGroup()!=null)
        if(PSynchronizer.estimateSize()){
          for(DSMMission scenario: PActiveMissions)
            scenario.calibrateScenario(getMasterMap());
        }

        // print task states
      for(DSMMission scenario: PActiveMissions)
        if(scenario.getTask()!=null)
          scenario.updateGUI(PLastStep);

      groupReasoning();

      return(true);
    }
    return(false);
  }



  /*
         INTERFACE
  */

  /*  public synchronized void friendsReport(DSAgent agent, LinkedList<Point> list, int step){
          PSynchronize.addObservation(agent,step,list);
      }
  */

  void removeScenario(DSMMission scenario){
    LinkedList<DSMMission> ns=new LinkedList();
    for(DSMMission s: PActiveMissions)
      if(!(s==scenario))  //.getTask().getName().contentEquals(scenario.getTask().getName()))
        ns.add(s);
      PActiveMissions =ns;
  }

  void removeActiveTask(String task) {
    LinkedList<String> nt=new LinkedList();
    for(String s:PActiveTasks)
      if(!s.contentEquals(task))
        nt.add(s);
    PActiveTasks=nt;

  }

  LinkedList<Point> getTasksGoalAreas(){
    LinkedList<Point> gas=new LinkedList();
    for(DSMMission s: PActiveMissions)
      if(s.getTask()!=null)
        gas.add(s.getTask().getGoalArea());
      return(gas);
  }

  String getTasksGoalAreasSt(){
    String st="";
    for(DSMMission s: PActiveMissions)
      if(s.getTask()!=null)
        st=st+s.getTask().getName()+" / "+s.getTask().getGoalArea();
    return(st);
  }

    public synchronized void scenarioCompleted(DSMMission scenario) {
    if (scenario.getTask() != null) {
      printOutput("scenario completed: " + scenario.getTask().getName());
      scenario.scenarioSuceeded();

      //PSynchronizer.getMasterGroup().releaseGoalArea(scenario.getTask());
    }
    boolean newJob=false;
      if(scenario.getTask()!=null){
        newJob=true;
      }

    for (DSAgent agent : scenario.getAgentsAllocated()) {
      agent.removeScenario();
      if(newJob)
        agent.hearOrder(new DSEvasiveManoeuvre());
    }
      PActiveMissions.remove(scenario);
  }

  public synchronized void scenarioFailed(DSMMission scenario, String reason) {
    if (scenario == null) return;
      scenario.scenarioFailed(reason);
    if (scenario.getTask() != null) {
      printOutput(PLastStep+":scenario failed: " + scenario.getTask().getName()+" reason: "+reason);
      for (DSAgent agent : scenario.getAgentsAllocated()) {
        printOutput("Removing scenario for "+agent.getEntityName());
        agent.removeScenario();
        agent.hearOrder(new DSGDetachAll());
      }
      printOutput("Removing scenario "+scenario.toString());
      printOutput("Before removeScenario");
      printScenarios();
      removeScenario(scenario);
      printOutput("After removeScenario");
      printScenarios();

    }
    if (scenario.getTask() != null)
      removeActiveTask(scenario.getTask().getNickName());
    PActiveMissions.remove(scenario);
  }

  /*
         when a group is extended, then
                     1 scenarios carried out by its members is forgotten
  */

  public void groupExtendedBy(DSGroup extendedGroup, DSGroup by) {
    for (DSAgent agent : by.getMembers()) agent.removeScenario();
  }

  public void agentDisabled(DSAgent agent) {
    // TODO koupil to vojak, failne asi jenom twoblocks resp viceblocks
    agent.setBody(new DSBody());
    HorseRider.inform(TAG, "agentDisabled: DISABLED " + agent.getEntityName());
    if (agent.followsScenario()) agent.getScenario().checkEvent(agent, DSMMission._disabledEvent);
  }

  public synchronized void needJob(DSAgent agent) {
    if((PSynchronizer.getMasterGroup()!=null)&&
        (agent.getActualRole().contentEquals("default"))){
        // an agent with default role in the stage 2 (with mastermap) -> try to change role
      agent.hearOrder(new DSGChangeRole());
    }
    else {
    printOutput(agent.getEntityName()+" needs job");
    DSDivideAndExplore scenario = new DSDivideAndExplore(agent, 5); // 10 je max delka prochazky
    agent.setScenario(scenario);
    scenario.initMission(agent.getStep());
     }
  }



  public synchronized boolean taskExpired(String name) {
    PGroupOptions.removeOption(name);
    return (true);
  }

  public synchronized boolean taskProposed(DSTask task) {


    dsGroupTaskOption taskOption = new dsGroupTaskOption(task);

    PGroupOptions.addOption(taskOption);

  //  if (PGroupOptions.addOption(taskOption))
  //    PGGUI.addText(task.getName() + "\\" + task.getTaskBody().bodyToString());

    return (false);
  }

  /*
   *       INIT
   */

  public class createArmy extends OneShotBehaviour {

    public void action() {
      String agentName;
      DSAgent agent;
      boolean leutnant = true;
      Collection<String> entities;
      entities = PEI.getEntities();

      /*
      PGGUI = DSGeneralGUI.createGUI((DeSouches) this.getAgent());
*/
      PGTP = new DSGTaskPoolFrame();

      PGUI = DSAgentGUI.createGUI(1, (DeSouches) this.getAgent());

      int agentNo = 0;
      HorseRider.inform(TAG, "action: " + "Je treba vytvorit agenty pro ... ");
      boolean guiFocus = true;

      for (String entity : entities) {
        agentName = "Agent" + agentNo++;
        try {
          agent =
              new DSAgent(
                  agentName,
                  null,
                  entity,
                  PEI,
                  (DeSouches) this.getAgent(),
                  agentNo,
                  leutnant,
                  PGUI,
                  guiFocus);

          if (guiFocus) PGUIFocus = agent;
          guiFocus = false;
          leutnant = false;
          this.getAgent().getContainerController().acceptNewAgent(agentName, agent);
          agent.setup();

          this.getAgent().getContainerController().getAgent(agentName).start();
          //  needJob(agent)

          agent.hearOrder(new DSGForceDetach());

        } catch (Exception pe) {}
      }
    }
  }

  public void registerAgent(String agentName, DSAgent agent) {
    if (PRegisteredAgents.containsKey(agentName)) return;
    PRegisteredAgents.put(agentName, agent);
    PGUI.registerAgent(agentName);
  }

  public void changeGUIFocus(String agentName) {
    if (PRegisteredAgents.containsKey(agentName)) {
      PGUIFocus.removeGUIFocus();
      PGUIFocus = PRegisteredAgents.get(agentName);
      PGUIFocus.setGUIFocus();
      PGUI.setAgentName(agentName);
      PGUI.setScore(PScore);
      if(PGUIFocus.getScenario()!=null)
         PGUI.setScenario(PGUIFocus.getScenario().getName());
    }
  }

  public DeSouches() {
    super();

    DSPercepts.initBlocks();
    PRegisteredAgents = new HashMap<String, DSAgent>();
    PSynchronizer = new DSSynchronize();
    PActiveTasks = new LinkedList<String>();
    PTaskGUIs = new LinkedList<DSTaskGUI>();
    PActiveMissions = new LinkedList<DSMMission>();
    PBarriers = new HashMap<Integer, LinkedList<DSAgent>>();
    PGroupOptions = new dsGroupOptionsPool();
    PStepsDone=new HashMap();

    try {
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      Calendar cal = Calendar.getInstance();
      String fn="deSouches"+dateFormat.format(cal.getTime())+".dso";
      POutput=new FileWriter(fn);
      PMapsOutput=new FileWriter("deSMaps.txt");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    try {

      PEI = new EnvironmentInterface("eismassimconfig.json");
      PEI.start();
    } catch (ManagementException e) {
      HorseRider.warn(TAG, "DeSouches: Something failed!", e);
    }
    ;

    createArmy rl = new createArmy(); // this);
    addBehaviour(rl);
  }
}

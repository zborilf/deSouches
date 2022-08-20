package dsAgents;

import deSouches.utils.HorseRider;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
import dsAgents.dsReasoningModule.dsGoals.DSEvasiveManoeuvre;
import dsAgents.dsReasoningModule.dsGoals.DSGDetachAll;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import massim.eismassim.EnvironmentInterface;

public class DeSouches extends Agent {

  private static final String TAG = "DeSouches";
  private static final double _timeNeeded = 1.3;      // estim. delay
  private static final int _busyAgentsLimit = 8;
  private static final int __max_workers = 15;


  private int PTeamSize;
  private dsGUI PGUI;
  private dsGeneralGUI PGGUI;
  private DSAgent PGUIFocus;

  static EnvironmentInterfaceStandard PEI;
  private int PPopulationSize = 0;
  private DSSynchronize PSynchronizer;
  private dsMultiagent.dsGroupOptions.dsGroupOptionsPool PGroupOptions;
  private LinkedList<String> PActiveTasks;
  LinkedList<dsTaskGUI> PTaskGUIs;

  private HashMap<String, DSAgent> PRegisteredAgents;

  private FileWriter POutput, PMapsOutput;


  private HashMap<Integer, LinkedList<DSAgent>> PBarriers;
  private HashMap<Integer, Boolean> PStepsDone;


  private LinkedList<DSScenario> PScenariosActive;



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

  public void groupCreated(DSGroup group) {
    //      PGroupPool.addGroup(group);
  }

  public FileWriter getOutput(){
    return(POutput);
  }

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
      if (workers.size() >= __max_workers) return (role);
    if (Math.random() < 0.9) role = "worker";
    else
      role = "digger";
    return (role);
  }

  /*
         Reasoning about tasks -> coalition search
  */

  void printPointLists(LinkedList<LinkedList<Point>> points) {
    String st;
    for (LinkedList<Point> points2 : points) {
      st = "";
      for (Point point : points2) st = st + "[" + point.x + "," + point.y + "] ";
      PGGUI.addTask(st);
    }
  }

  ArrayList<DSCCoalition> proposeCoalitions4task(DSTask task) {

    /*
        toto je bastl, opravit po ladeni, vyhodit
     */


    ArrayList<DSCCoalition> coalitions = null;
    LinkedList<Integer> types = task.getTypesNeeded();

    boolean possible = true;
    if (PSynchronizer.getMasterGroup() == null) {
      PGGUI.addTask("No Mastergroup");
      return (coalitions);
    }

    LinkedList<DSAgent> workers = new LinkedList<DSAgent>();
    LinkedList<LinkedList<Point>> dispensersForTypes = new LinkedList<LinkedList<Point>>();
    LinkedList<Point> dispensersForType = new LinkedList<Point>();
    LinkedList<Point> goalslLocations = new LinkedList<Point>();
    LinkedList<Point> goalZones = new LinkedList<Point>();

    DSMap map = PSynchronizer.getMasterGroup().getMap();
    String st;
    if (types == null) possible = false;
    else {
      // list of list of dispensers locations for every needed type

      for (int type : types) {
        st = type + ": ";
        dispensersForType = map.allObjects(DSCell.__DSDispenser + type);
        if (dispensersForType.isEmpty()) {
          possible = false;
          st = st + "nejsou";
        } else {
          dispensersForTypes.add(dispensersForType);
          for (Point position : dispensersForType) st = st + " / " + position.toString();
        }
        PGGUI.addTask(st);
      }

      st = "Goal:";



      goalslLocations = map.allObjects(DSCell.__DSGoalArea);

      LinkedList<Point> goalZones2 =  PSynchronizer.getMasterGroup().getMap().getPointsZones(goalslLocations);


      /*
      Filters goal locations to those not in distance to 4 from any allocated goal
       */
      for(Point gA:goalZones2) {
        boolean possibleGA = true;
        for (Point allocatedGA : getTasksGoalAreas())
          if (DSMap.distance(gA, allocatedGA) < 6) {
            possibleGA = false;
            break;
          }
        if(possibleGA)
          goalZones.add(gA);
      }



      if (goalZones == null) {
        st = st + "nejsou";
        possible = false;
      } else for (Point position : goalZones) st = st + " / " + position.toString();
      PGGUI.addTask(st);


      st = "Agents: ";
      for (DSAgent agent : PSynchronizer.getMasterGroup().getMembers()) {
        st = st + " " + agent.getMapPosition();
      }
      PGGUI.addTask(st);

      st = "Diggers: ";
      LinkedList<DSAgent> diggers = PSynchronizer.getMasterGroup().getMembersByRole("digger",false);
      if (diggers != null) for (DSAgent digger : diggers) st = st + digger.getEntityName() + " / ";
      PGGUI.addTask(st);

      st = "Not busy workers: ";
      workers = PSynchronizer.getMasterGroup().getMembersByRole("worker",true);
      if (workers == null) possible = false;
      else for (DSAgent worker : workers) st = st + worker.getEntityName() + " / ";
      PGGUI.addTask(st);
    }

    if (possible)
      if (workers.size() < dispensersForTypes.size())
        // not enaugh workers
        possible = false;


    if (possible) {
      PGGUI.addTask("" +
              "" +
              "TASK POSSIBLE!");
      // computes coalitions
      // takse the cheapest ( get(0) )
      // prints it on General GUI
      // and finally modify 'subtasks' in the 'task' properly

      int timeout=(int)Math.round((task.getDeadline()-PLastStep)/_timeNeeded);

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

/*
    if(!PActiveTasks.isEmpty()) {
      return (false);
    }
*/


    printOutput("---------------");
    printOutput(PLastStep+": Task : "+task.getName()+" type "+task.getTaskType().getTaskType()+
            " deadline "+task.getDeadline());
    for(int i=0;i<4;i++)
      if(task.getSubtaskRoutes(i)!=null)
        printOutput(">> "+task.getTypesNeeded().get(i)+"::"+task.getSubtaskRoutes(i).taskMember2String()+
                                                              " sup. price "+task.getSubtaskRoutes(i).getPrice());
    printOutput("---------------");

    if (!PActiveTasks.contains(task.getName()))     // useless here

      PActiveTasks.add(task.getName());


    if (taskType <= 3){
      printOutput("---------------");
      DSSTwoBlocks twoBlocks = new DSSTwoBlocks(this, task);
      twoBlocks.initScenario(PLastStep);
      PScenariosActive.add(twoBlocks);
      return (true);
    }

    if ((taskType>3) && (taskType <= 13)) {
      printOutput("Budou trojicky");
      DSSThreeBlocks threeBlocks = new DSSThreeBlocks(this, task);
      threeBlocks.initScenario(PLastStep);
      PScenariosActive.add(threeBlocks);
      return (true);
    }

    if (taskType <= 42) {
      printOutput("Budou ctvericky");
      DSSFourBlocks fourBlocks= new DSSFourBlocks(this, task);
      fourBlocks.initScenario(PLastStep);
      PScenariosActive.add(fourBlocks);
      return (true);
    }
/*
    if (taskType == 43){
      printOutput(("Jednickovej task"));
      DSSOneBlock oneBlock= new DSSOneBlock(this, task);
      oneBlock.initScenario(PLastStep);
      PScenariosActive.add(oneBlock);
      return(true);
    }
*/

    return (false);
  }



  DSTask cloneTask(DSTask task, String taskName){

    DSTask taskC;
    String name=taskName;
    int deadline=task.getDeadline();
    DSBody body=new DSBody();
    int reward=task.getReward();

    DSCell cell;
    for (DSCell c:task.getTaskBody().getBodyList()) {
      if(c.getType()!=DSCell.__DSAgent) {
        int x = c.getX();
        int y = c.getY();
        cell = new DSCell(x, y, c.getType(), 0);
        if (Math.abs(x) + Math.abs(y) == 1)
          body.insertFirstCell(cell); // must be at the first position
        else body.addCell(cell);
      }
    }

    taskC=new DSTask(name, deadline, reward, body, PLastStep);
    return(taskC);
  }


  void groupReasoning() {



    try {
      PMapsOutput.write(PSynchronizer.getMasterGroup().getMap().stringMap());
      PMapsOutput.write("\n\n");
      PMapsOutput.flush();
    } catch (Exception e) {
    }
    printOutput("Step "+PLastStep+"\n");
    printOutput("Active tasks: " +(PActiveTasks.toString()));
    printOutput("Goal positions allocated: " +getTasksGoalAreasSt());


    LinkedList<DSScenario> scenarios = (LinkedList<DSScenario>) PScenariosActive.clone();
    for (DSScenario scenario : scenarios){
      int sd=scenario.getDeadline();
      if (sd < PLastStep) {
        printOutput("@@! Scenario failed due to timeout " + scenario.getTask().getName());
        scenarioFailed(scenario);
      }}

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
            String taskString = task.task2String(PLastStep);

            // take a copy of proposed ... possibly multiple tasks of the same kinf


            PGGUI.addTask(taskString);

            //   if ((task.getDeadline() - PLastStep ) > (task.subtaskCostEstimation() + _timeNeeded)) {

            executeTask(task); // task is possible (agents, resources, time)

            PGroupOptions.removeOption(task.getName());  // ... NO! The task can be repeated!!

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

  /*
   *       BARRIER   - of agents. True, if all the agents are in the list
   */

  public boolean stepsDone(int step){
    if(PStepsDone.keySet().contains(step))
      return(true);
      else
        return(false);
  }

  public synchronized boolean salut(int step, int phase, DSAgent agent) { // barrier

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
      PStepsDone.put(agent.getStep(),true);

      PGGUI.clearTasks();
      PLastStep = step;
      PGGUI.addTask("Step:" + PLastStep);


      if (PSynchronizer.masterGroupCandidate(agent.getGroup()))
          printOutput("> ! > " + agent.getStep() + " MASTERGRUPA " + agent.getEntityName() + "!!!\n");


        // print task states
      for(DSScenario scenario:PScenariosActive)
        if(scenario.getTask()!=null)
          scenario.updateGUI();


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

  void removeScenario(DSScenario scenario){
    LinkedList<DSScenario> ns=new LinkedList();
    for(DSScenario s:PScenariosActive)
      if(!s.getTask().getName().contentEquals(scenario.getTask().getName()))
        ns.add(s);
      PScenariosActive=ns;
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
    for(DSScenario s:PScenariosActive)
      if(s.getTask()!=null)
        gas.add(s.getTask().getGoalArea());
      return(gas);
  }

  String getTasksGoalAreasSt(){
    String st="";
    for(DSScenario s:PScenariosActive)
      if(s.getTask()!=null)
        st=st+s.getTask().getName()+" / "+s.getTask().getGoalArea();
    return(st);
  }

    public synchronized void scenarioCompleted(DSScenario scenario) {
    if (scenario.getTask() != null) {
      printOutput("scenario completed: " + scenario.getTask().getName());
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

  }

  public synchronized void scenarioFailed(DSScenario scenario) {
    if (scenario == null) return;
    if (scenario.getTask() != null) {
      printOutput(PLastStep+":scenario failed: " + scenario.getTask().getName());
      for (DSAgent agent : scenario.getAgentsAllocated()) {
        agent.removeScenario();
        agent.hearOrder(new DSGDetachAll());
      }      removeScenario(scenario);
    }

    if (scenario.getTask() != null)
      removeActiveTask(scenario.getTask().getName());
  }

  /*
         when a group is extended, then
                     1 scenarios carried out by its members is forgotten
  */

  public void groupExtendedBy(DSGroup extendedGroup, DSGroup by) {

  /*  String st="";
    if (extendedGroup.isMasterGroup()) st="MasterGroupExtended: ";
    else st="2groupExtended: ";


    st=st+extendedGroup.printGroup();
    st=st+(" by group ");
    st=st+by.printGroup();
    st=st+" step " + extendedGroup.getMembers().getFirst().getStep();

    try {
      POutput.write(st + "\n");
      POutput.flush();
    }catch(Exception e){};
    */


    for (DSAgent agent : by.getMembers()) agent.removeScenario();
    //        PGroupPool.printGroups();
  }

  public void agentDisabled(DSAgent agent) {
    // TODO koupil to vojak, failne asi jenom twoblocks resp viceblocks
    agent.setBody(new DSBody());
    HorseRider.inform(TAG, "agentDisabled: DISABLED " + agent.getEntityName());
    if (agent.followsScenario()) agent.getScenario().checkEvent(agent, DSScenario._disabledEvent);
  }

  public synchronized void needJob(DSAgent agent) {
    /*  if(PGroupPool.getGroups().size()==1){
        // all synchronized
        DSSSeekAndDestroy scenario=new DSSSeekAndDestroy(agent);
        agent.setScenario(scenario);
        scenario.initScenario(agent.getStep());
    }
    else {*/
    printOutput(agent.getEntityName()+" needs job");
    DSDivideAndExplore scenario = new DSDivideAndExplore(agent, 5); // 10 je max delka prochazky
    agent.setScenario(scenario);
    scenario.initScenario(agent.getStep());
    // }
  }


  int getBusyAgents() {

    if (PSynchronizer.getMasterGroup() == null) return (0);
    return (PSynchronizer.getMasterGroup().getBusyAgents(2).size());
  }

  public synchronized boolean taskExpired(String name) {
    PGroupOptions.removeOption(name);
    PGGUI.addTask("Removed: " + name);

    return (true);
  }

  public synchronized boolean taskProposed(DSTask task) {


    dsGroupTaskOption taskOption = new dsGroupTaskOption(task);

    if (PGroupOptions.addOption(taskOption))
      PGGUI.addTask(task.getName() + "\\" + task.getTaskBody().bodyToString());

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

      PGGUI = dsGeneralGUI.createGUI((DeSouches) this.getAgent());

      PGUI = dsGUI.createGUI(1, (DeSouches) this.getAgent());

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

        } catch (Exception pe) {
          HorseRider.yell(TAG, "action: " + "Tak tohle se nepovedlo " + agentName, pe);
        }
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
    PTaskGUIs = new LinkedList<dsTaskGUI>();
    PScenariosActive = new LinkedList<DSScenario>();
    PBarriers = new HashMap<Integer, LinkedList<DSAgent>>();
    PGroupOptions = new dsGroupOptionsPool();
    PStepsDone=new HashMap();

    try {
      POutput=new FileWriter("deSouches.txt");
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

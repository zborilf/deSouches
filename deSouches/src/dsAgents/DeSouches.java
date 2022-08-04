package dsAgents;

import deSouches.utils.HorseRider;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import massim.eismassim.EnvironmentInterface;

public class DeSouches extends Agent {

  private static final String TAG = "DeSouches";
  private static final int _timeNeeded = 40;
  private static final int _busyAgentsLimit = 8;
  private static final int __max_workers = 8;

  private boolean PMakamNaUloze=false;

  private int PTeamSize;
  private dsGUI PGUI;
  private dsGeneralGUI PGGUI;
  private DSAgent PGUIFocus;

  static EnvironmentInterfaceStandard PEI;
  private int PPopulationSize = 0;
  private DSSynchronize PSynchronizer;
  private dsMultiagent.dsGroupOptions.dsGroupOptionsPool PGroupOptions;
  private LinkedList<String> PActiveTasks;
  private HashMap<String, DSAgent> PRegisteredAgents;

  private HashMap<Integer, LinkedList<DSAgent>> PBarriers;
  private HashMap<Integer, Boolean> PStepsDone;


  private LinkedList<DSScenario> PScenariosActive;

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

  public void setTeamSize(int teamSize){
    PTeamSize=teamSize;
  }

  public void groupRemoved(DSGroup group) {

    //      PGroupPool.removeGroup(group);
  }

  /*
     Group reasoning: revise options / relevant strategies are fixed for the tasks
                                         applicable (?), teams reconsiderations
  */

  public String roleNeeded(DSAgent agent) {
    String role = "digger";
    LinkedList<DSAgent> workers = agent.getGroup().getMembersByRole("worker");
    if (workers != null) if (workers.size() >= __max_workers) return (role);
    if (Math.random() < 1) role = "worker";
    // if (Math.random() < 0) role = "explorer";
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

  DSCCoalition checkTask(DSTask task) {

    /*
    toto je bastl, opravit po ladeni, vyhodit
     */

    DSCCoalition coalition=null;
    LinkedList<Integer> types=task.getTypesNeeded();

    boolean possible = true;
    if (PSynchronizer.getMasterGroup() == null) {
      PGGUI.addTask("No Mastergroup");
      return(coalition);
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
      if (goalslLocations == null) {
        st = st + "nejsou";
        possible = false;
      } else for (Point position : goalslLocations) st = st + " / " + position.toString();
      PGGUI.addTask(st);

      goalZones = DSMap.getPointsZones(goalslLocations);

      st = "Agents: ";
      for (DSAgent agent : PSynchronizer.getMasterGroup().getMembers()) {
        st = st + " " + agent.getMapPosition();
      }
      PGGUI.addTask(st);

      st = "Diggers: ";
      LinkedList<DSAgent> diggers = PSynchronizer.getMasterGroup().getMembersByRole("digger");
      if (diggers != null) for (DSAgent digger : diggers) st = st + digger.getEntityName() + " / ";
      PGGUI.addTask(st);

      st = "Workers: ";
      workers = PSynchronizer.getMasterGroup().getMembersByRole("worker");
      if (workers == null) possible = false;
      else for (DSAgent worker : workers) st = st + worker.getEntityName() + " / ";
      PGGUI.addTask(st);
    }

    if (possible)
      if (workers.size() < dispensersForTypes.size())
        // not enaugh workers
        possible = false;


    if (possible) {
      PGGUI.addTask("TASK POSSIBLE!");
      // computes coalitions
      // takse the cheapest ( get(0) )
      // prints it on General GUI
      // and finally modify 'subtasks' in the 'task' properly

      coalition=
              new DSCCoalitionMaker().proposeTaskCoallitions(workers, dispensersForTypes, goalZones).get(0);

    }

    return(coalition);
  }

  /*
        Execute selected and prepared task
   */

  boolean executeTask(DSTask task){

    int taskType = task.getTaskTypeNumber();


    if (!PActiveTasks.contains(task.getName()))

      PActiveTasks.add(task.getName());


    if (taskType <= 3){
      DSSTwoBlocks twoBlocks = new DSSTwoBlocks(this, task);
      twoBlocks.initScenario(PLastStep);
      PScenariosActive.add(twoBlocks);
      return (true);
    }

    if (taskType <= 18) {
      DSSThreeBlocks threeBlocks = new DSSThreeBlocks(this, task);
      threeBlocks.initScenario(PLastStep);
      PScenariosActive.add(threeBlocks);
      return (true);
    }

    if (taskType <= 42) {
      DSSFourBlocks fourBlocks= new DSSFourBlocks(this, task);
      fourBlocks.initScenario(PLastStep);
      PScenariosActive.add(fourBlocks);
      return (true);
    }

    return (false);
  }



  void groupReasoning() {
    for (dsGroupOption option : PGroupOptions.getOptions())
      if (option instanceof dsGroupTaskOption) {
        DSTask task = ((dsGroupTaskOption) option).getTask();
        if (task.getDeadline() < PLastStep) PGroupOptions.removeOption(task.getName());
        //       if(task.getTypesNeeded()!=null) {
        //           PGGUI.addTask("Je treba resit " + task.getName() +
        //                   " deadline " + task.getDeadline() + " pozadavky " +
        // task.getTypesNeeded().toString());
        DSCCoalition coalition=checkTask(task);
        if(coalition!=null) {

          DSTaskMember tmember;
          for (DSCCoalitionMember cmember : coalition.getCoalitionMembers()) {
            tmember = new DSTaskMember(cmember.getAgent(), cmember.getPDispenser(), cmember.getGoal());
            task.setSubtaskRoute(cmember.getTaskID() - 1, tmember);
          }
          String taskString = task.task2String(PLastStep);

          PGGUI.addTask(taskString);

          if ((task.getDeadline() - PLastStep ) > (task.subtaskCostEstimation() + _timeNeeded)) {
            if(!PMakamNaUloze)
            executeTask(task); // task is possible (agents, resources, time)
            PMakamNaUloze=true;
          }

        } // end non-empty coalition



      } // end na udalost typu 'novy task'
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

  public synchronized void salut(int step, int phase, DSAgent agent) { // barrier

    if (PSynchronizer
            .addObservation(
                    agent,
                    agent.getStep(),
                    agent.getOutlook().getFriendsSeen(agent.getVision()),
                    PTeamSize))
    {
      // steps done
      PStepsDone.put(agent.getStep(),true);
      System.out.println("Step "+agent.getStep()+" done");
      //  }

      // if (step > PLastStep) {

      PGGUI.clearTasks();
      PLastStep = step;
      checkDeadlines(step); // TODO ... reconsider options, but only once per step
      PGGUI.addTask("Step:" + PLastStep);
      groupReasoning();
    }
  }

  /*
         INTERFACE
  */

  /*  public synchronized void friendsReport(DSAgent agent, LinkedList<Point> list, int step){
          PSynchronize.addObservation(agent,step,list);
      }
  */

  public synchronized void scenarioCompleted(DSScenario scenario) {
    if (scenario.getTask() != null) {
      System.out.println("scenario completed: " + scenario.getTask().getName());
      PSynchronizer.getMasterGroup().releaseGoalArea(scenario.getTask());
    }
    PScenariosActive.remove(scenario);
    ;
    for (DSAgent agent : scenario.getAgentsAllocated()) {
      agent.removeScenario();
    }
  }

  public synchronized void scenarioFailed(DSScenario scenario) {
    if (scenario == null) return;
    if (scenario.getTask() != null) {
      HorseRider.inform(TAG, "scenario failed: " + scenario.getTask().getName());
      PScenariosActive.remove(scenario);
    }
    for (DSAgent agent : scenario.getAgentsAllocated()) {
      agent.removeScenario();
    }
    if (scenario.getTask() != null) PActiveTasks.remove(scenario.getTask());
  }

  /*
         when a group is extended, then
                     1 scenarios carried out by its members is forgotten
  */

  public void groupExtendedBy(DSGroup extendedGroup, DSGroup by) {

    if (extendedGroup.isMasterGroup()) System.out.println("MasterGroupExtended: ");
    else System.out.println("groupExtended: ");

    extendedGroup.printGroup();
    System.out.println(" by group ");
    by.printGroup();
    System.out.println(" step " + extendedGroup.getMembers().getFirst().getStep());

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
    DSDivideAndExplore scenario = new DSDivideAndExplore(agent, 5); // 10 je max delka prochazky
    agent.setScenario(scenario);
    scenario.initScenario(agent.getStep());
    // }
  }

  public void checkDeadlines(int step) {

    // predelat ... dsGroupOptins 2022

    LinkedList<DSScenario> scenarios = (LinkedList<DSScenario>) PScenariosActive.clone();
    for (DSScenario scenario : scenarios)
      if (scenario.getDeadline() < step) {
        System.out.println("@@! Scenario failed due to timeout " + scenario.getTask().getName());
        scenarioFailed(scenario);
      }
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

  public synchronized boolean taskProposed(DSTask task, int step) {

    /*
    DSGroup chosenGroup;
    DSSTwoBlocks twoBlocks;
    DSSThreeBlocks threeBlocks;
    DSSFourBlocks fourBlocks;
    */

    dsGroupTaskOption taskOption = new dsGroupTaskOption(task);

    if (PGroupOptions.addOption(taskOption))
      PGGUI.addTask(task.getName() + "\\" + task.getTaskBody().bodyToString());

    /*
    PGGUI.addTask(task.getName() + "\\" + task.getTaskBody().bodyToString());
    int taskType = task.getTaskTypeNumber();


    if (taskType <= 42) {

        chosenGroup = null;

        if (!PActiveTasks.contains(task.getName()))

            // jen pro dva, typ 1 linearni, typ 2 na bok, linearni
            PSynchronizer.getMasterGroup();
        if (PSynchronizer.getMasterGroup() != null) {
            if (PSynchronizer.getMasterGroup().isCapable(task, 2)) {
                chosenGroup = PSynchronizer.getMasterGroup();
            } else
                chosenGroup = null;

        }


        if (chosenGroup != null) {

            if (task.getDeadline() - chosenGroup.getFreeAgents(2).getFirst().getStep() >= _timeNeeded) {

                PActiveTasks.add(task.getName());


                if (task.getTypesNeeded().size() == 2)
                    if ((taskType <= 3) && (getBusyAgents() + 2 <= _busyAgentsLimit)) {
                        twoBlocks = new DSSTwoBlocks(this, chosenGroup, task, taskType);
                        twoBlocks.initScenario(step);
                        PScenariosActive.add(twoBlocks);
                        return (true);
                    }


                if (task.getTypesNeeded().size() == 3)
                    if ((taskType <= 18) && (getBusyAgents() + 3 <= _busyAgentsLimit)) {
                        threeBlocks = new DSSThreeBlocks(this, chosenGroup, task, taskType);
                        threeBlocks.initScenario(step);
                        PScenariosActive.add(threeBlocks);
                        return (true);
                    }


                if (task.getTypesNeeded().size() == 4)
                    if ((taskType <= 42) && (getBusyAgents() + 4 <= _busyAgentsLimit)) {
                        fourBlocks = new DSSFourBlocks(this, chosenGroup, task, taskType);
                        fourBlocks.initScenario(step);
                        PScenariosActive.add(fourBlocks);
                        return (true);
                    }
            }
        }
    }
    */

    return (false);
  }

  /*
      public synchronized boolean tasksProposed(LinkedList<DSTask> tasks, int step){


      if(tasks.size()==0)
          return(false);
      int taskType;
      LinkedList<DSGroup> capableGroups;
      DSGroup chosenGroup;
      DSSTwoBlocks twoBlocks;
      DSSThreeBlocks threeBlocks;
      DSSFourBlocks fourBlocks;
      PGGUI.clearTasks();

      for(DSTask task:tasks) {

          PGGUI.addTask(task.toString());
          taskType = task.getTaskTypeNumber();


          if (taskType <= 42) { // LADENI, bereme jen Task4

              chosenGroup = null;

              if (!PActiveTasks.contains(task.getName()))

                  // jen pro dva, typ 1 linearni, typ 2 na bok, linearni
                  PSynchronizer.getMasterGroup();
              if (PSynchronizer.getMasterGroup() != null) {
                  if (PSynchronizer.getMasterGroup().isCapable(task, 2)) {
                      chosenGroup = PSynchronizer.getMasterGroup();
                  } else
                      chosenGroup = null;

              }


              if (chosenGroup != null) {

                  if(task.getDeadline()-chosenGroup.getFreeAgents(2).getFirst().getStep()>=_timeNeeded){

                      PActiveTasks.add(task.getName());


                      if (task.getTypesNeeded().size() == 2)
                          if ((taskType <= 3)&&(getBusyAgents()+2<=_busyAgentsLimit)) {
                              twoBlocks = new DSSTwoBlocks(this, chosenGroup, task, taskType);
                              twoBlocks.initScenario(step);
                              PScenariosActive.add(twoBlocks);
                              return (true);
                          }


                      if (task.getTypesNeeded().size() == 3)
                          if ((taskType <= 18)&&(getBusyAgents()+3<=_busyAgentsLimit)) {
                              threeBlocks = new DSSThreeBlocks(this, chosenGroup, task, taskType);
                              threeBlocks.initScenario(step);
                              PScenariosActive.add(threeBlocks);
                              return (true);
                          }


                      if (task.getTypesNeeded().size() == 4)
                          if ((taskType <=42)&&(getBusyAgents()+4<=_busyAgentsLimit)) {
                              fourBlocks = new DSSFourBlocks(this, chosenGroup, task, taskType);
                              fourBlocks.initScenario(step);
                              PScenariosActive.add(fourBlocks);
                              return (true);
                          }
                  }
              }
          }
      }
      return(false);
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

      PGGUI = dsGeneralGUI.createGUI(0, (DeSouches) this.getAgent());
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
    }
  }

  public DeSouches() {
    super();

    DSPercepts.initBlocks();
    PRegisteredAgents = new HashMap<String, DSAgent>();
    PSynchronizer = new DSSynchronize();
    PActiveTasks = new LinkedList<String>();
    PScenariosActive = new LinkedList<DSScenario>();
    PBarriers = new HashMap<Integer, LinkedList<DSAgent>>();
    PGroupOptions = new dsGroupOptionsPool();
    PStepsDone=new HashMap();

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

package dsAgents;

import deSouches.utils.HorseRider;
import dsAgents.dsBeliefs.dsEnvironment.DSBody;
import dsMultiagent.dsScenarios.*;
import dsMultiagent.DSGroup;
import dsMultiagent.DSGroupPool;
import dsMultiagent.DSSynchronize;
import dsMultiagent.dsTasks.DSTask;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ManagementException;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import massim.eismassim.EnvironmentInterface;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class DeSouches extends Agent{

    private static final String TAG = "DeSouches";
    private static final int _timeNeeded=80;
    private static final int _busyAgentsLimit=8;

    static EnvironmentInterfaceStandard PEI;
    private int PPopulationSize=0;
    private DSSynchronize PSynchronize;
    private DSGroupPool PGroupPool;
    private LinkedList<String> PActiveTasks;
    private HashMap<Integer,LinkedList<DSAgent>> PBarriers;
    private DSGroup PMasterGroup;

    private LinkedList<DSScenario> PScenariosActive;


    /*
        DIVISIONS MANAGEMENT
    */

    public void groupCreated(DSGroup group){
        PGroupPool.addGroup(group);
    }

    public void groupRemoved(DSGroup group){
        PGroupPool.removeGroup(group);
    }

    public void groupExtendedBy(DSGroup extendedGroup, DSGroup by){
        if(by==PMasterGroup)
            PMasterGroup=extendedGroup;
        HorseRider.inquire(TAG, "groupExtended: "+"ACTUAL GROUPS");
        PGroupPool.printGroups();
    }

    /*
    *       BARRIER
     */

    public synchronized boolean barrier(int step, int phase, DSAgent agent){
        int id=step*10+phase;
        LinkedList<DSAgent> agentList;
        agentList=PBarriers.get(id);
        if(agentList==null)
            agentList = new LinkedList<DSAgent>();
        if(!agentList.contains(agent))
            agentList.add(agent);
        PBarriers.put(id,agentList);
        System.out.println("::"+agent.getEntityName()+"--"+step+"/"+phase+"/"+agentList.size()+" population "+
                PPopulationSize);
        return(agentList.size()==PPopulationSize);
    }

    /*
            INTERFACE
     */

    public synchronized void friendsReport(DSAgent agent, LinkedList<Point> list, int step){
        PSynchronize.addObservation(agent,step,list);
    }


    public synchronized void scenarioCompleted(DSScenario scenario){
        if(scenario.getTask()!=null) {
            System.out.println("scenario completed: " + scenario.getTask().getName());
            PMasterGroup.releaseGoalArea(scenario.getTask());
            PMasterGroup.getMap().printMap("Scenario completed");
        }
            PScenariosActive.remove(scenario);;
            for(DSAgent agent:scenario.getAgentsAllocated()) {
            agent.removeScenario();
        }
    }

    public synchronized void scenarioFailed(DSScenario scenario) {
        if (scenario == null)
            return;
        if (scenario.getTask() != null){
            HorseRider.inform(TAG, "scenario failed: " + scenario.getTask().getName());
            PScenariosActive.remove(scenario);
        }
        for(DSAgent agent:scenario.getAgentsAllocated()) {
            agent.removeScenario();
        }
        if(scenario.getTask()!=null)
            PActiveTasks.remove(scenario.getTask());
    }

    public void agentDisabled(DSAgent agent){
        //TODO koupil to vojak, failne asi jenom twoblocks resp viceblocks
        agent.setBody(new DSBody());
        HorseRider.inform(TAG, "agentDisabled: DISABLED "+agent.getEntityName());
        agent.getScenario().checkEvent(agent, DSScenario._disabledEvent);
    }


    public synchronized void needJob(DSAgent agent){
        if(PGroupPool.getGroups().size()==1){
            // all synchronized
            DSSSeekAndDestroy scenario=new DSSSeekAndDestroy(agent);
            agent.setScenario(scenario);
            scenario.initScenario(agent.getStep());
        }
        else {
            DSSWalkAndSynchronize scenario = new DSSWalkAndSynchronize(agent, 8);
            agent.setScenario(scenario);
            scenario.initScenario(agent.getStep());
        }
    }

    public void checkDeadlines(int step){
        LinkedList<DSScenario> scenarios=(LinkedList<DSScenario>)PScenariosActive.clone();
        for(DSScenario scenario:scenarios)
            if(scenario.getDeadline()<step) {
                System.out.println("@@! Scenario failed due to timeout "+scenario.getTask().getName());
                scenarioFailed(scenario);
            }
    }

    int getBusyAgents(){

        if(PMasterGroup==null)
            return(0);
        return(PMasterGroup.getBusyAgents(2).size());
    }

    public synchronized boolean tasksProposed(LinkedList<DSTask> tasks, int step){

        // pouze overi, zdali pro nektery task je mastergrupa, nebo nejaka grupa, pokud jeste mastergrupa nebyla
        // ustavena vhodna, schopna. Pokud je, necha vytvorit patricny scenar

        System.out.println("NoGroups = "+PGroupPool.getGroups().size());
        System.out.println("NoGroupsActive = "+PScenariosActive.size());
        System.out.println("Busy agents = "+getBusyAgents());
        if(PMasterGroup!=null)
            System.out.println("MasterGroup is "+PMasterGroup.getMaster());


     //   if(PScenariosActive.size()>0)
     //       return(false);


        if(tasks.size()==0)
            return(false);
        int taskType;
        LinkedList<DSGroup> capableGroups;
        DSGroup chosenGroup;
        DSSTwoBlocks twoBlocks;
        DSSThreeBlocks threeBlocks;
        DSSFourBlocks fourBlocks;

        for(DSTask task:tasks) {


            taskType = task.getTaskTypeNumber();


            if (taskType <= 42) { // LADENI, bereme jen Task4

                chosenGroup = null;

                if (!PActiveTasks.contains(task.getName()))

                    // jen pro dva, typ 1 linearni, typ 2 na bok, linearni
                    if (PMasterGroup != null) {
                        if (PMasterGroup.isCapable(task, 2)) {
                            chosenGroup = PMasterGroup;
                        } else
                            chosenGroup = null;
                        HorseRider.inform(TAG, "taskActive: MasterGroup je grupa " + PMasterGroup.getMaster() +
                                " / " + PMasterGroup.getMembers().size());
                    } else {
                        capableGroups = PGroupPool.getCapableGroups(task, 2);
                        if (capableGroups.size() > 0) {
                            chosenGroup = capableGroups.getFirst(); // TODO neco chytrejsiho
                            PMasterGroup = chosenGroup;
                            PMasterGroup.setMasterGroup();
                        }
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
            boolean leutnant=true;
            Collection<String> entities;
            entities = PEI.getEntities();
            PPopulationSize=entities.size();
            PSynchronize=new DSSynchronize(PPopulationSize);

            int agentNo = 0;
            HorseRider.inform(TAG, "action: "+"Je treba vytvorit agenty pro ... ");

            for (String entity : entities) {
                agentName = "Agent" + agentNo++;
                try {
                    agent = new DSAgent(agentName, null, entity, PEI, (DeSouches)this.getAgent(),agentNo, leutnant);
                    leutnant=false;
                    this.getAgent().getContainerController().acceptNewAgent(agentName, agent);
                    agent.setup();

                    this.getAgent().getContainerController().getAgent(agentName).start();
                  //  needJob(agent)

                } catch (Exception pe) {
                    HorseRider.yell(TAG, "action: "+"Tak tohle se nepovedlo " + agentName, pe);
                }
            }
        }
    }

    public DeSouches() {
        super();

        DSPercepts.initBlocks();
        PGroupPool=new DSGroupPool();
        PActiveTasks=new LinkedList<String>();
        PMasterGroup=null;
        PScenariosActive=new LinkedList<DSScenario>();
        PBarriers=new HashMap<Integer,LinkedList<DSAgent>>();
        try {
            PEI = new EnvironmentInterface("C:\\Users\\zbori\\_%_%_EDEN\\GIT_REPOSITORY\\MAPC\\eismassimconfig.json");
            PEI.start();
        } catch (ManagementException e) {
            HorseRider.warn(TAG, "DeSouches: Something failed!", e);
        };

        createArmy rl=new createArmy();//this);
        addBehaviour(rl);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new DeSouches();
    }
}

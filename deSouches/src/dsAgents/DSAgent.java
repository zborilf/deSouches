package dsAgents;


import deSouches.utils.HorseRider;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsAgents.dsPerceptionModule.DSPerceptor;
import dsAgents.dsReasoningModule.dsBeliefBase.DSBeliefBase;
import dsAgents.dsReasoningModule.dsGoals.DSGoal;
import dsAgents.dsReasoningModule.dsIntention.DSIntention;
import dsAgents.dsReasoningModule.dsIntention.DSIntentionPool;
import dsMultiagent.DSGroup;
import dsMultiagent.dsScenarios.DSScenario;
import eis.EnvironmentInterfaceStandard;
import eis.PerceptUpdate;
import eis.exceptions.AgentException;
import eis.exceptions.RelationException;
import eis.iilang.Percept;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static deSouches.utils.HorseRider.makeLogFor;

public class DSAgent extends Agent {
    private static final String TAG = "DSAgent";

    private EnvironmentInterfaceStandard PEI;
    boolean PLeutnant;

    private DSBeliefBase PBeliefBase;
    private String PName;
    private String PEntity;
    private int PNumber;
    private int PIdleSteps;
    private Point PLastPosition;

    private int PScenarioPriority=0;
    private DSIntentionPool PIntentionPool;
    private HashMap<DSAgent, Point> PSynchronized;



    public String getAgentName(){
        return(PBeliefBase.getName());
    }

    public String getJADEAgentName(){
        return(PBeliefBase.getJADEName());
    }

    public int getStep(){
        return(PBeliefBase.getStep());
    }

    public Point getPosition(){
        return(PBeliefBase.getPosition());
    }

    public void holdsBlock(int type){
        PBeliefBase.setHoldsBolockType(type);
    }

    public void holdsNothing(int type){
        PBeliefBase.setHoldsBolockType(-1);
    }

    public EnvironmentInterfaceStandard getEI() {
        return (PEI);
    }

    public void setScenario(DSScenario scenario) {
        PBeliefBase.setScenario(scenario);
        PScenarioPriority = scenario.getPriority();
        HorseRider.inform(TAG, "setScenario: priority for "+PBeliefBase.getName()+" is "+PScenarioPriority);
    }

    private int getScenarioPriority(){
       return(PScenarioPriority);
    }

    protected DSScenario getScenario(){
        return(PBeliefBase.getScenario());
    }

    protected void removeScenario(){
        PBeliefBase.setScenario(null);
        PScenarioPriority=0;
        PBeliefBase.getCommander().needJob(this);
    }

    public int getIdleSteps(){
        return(PIdleSteps);
    }

    public boolean isBusy(int priority) {
        return(getScenarioPriority() >= priority);
    }

    public DeSouches getCommander(){
        return(PBeliefBase.getCommander());
    }

    public String getGroupMasterName(){
        return(PBeliefBase.getGroup().getMaster());
    }

    public DSGroup getGroup(){
        return(PBeliefBase.getGroup());
    }

    public void setGroup(DSGroup group){
        PBeliefBase.setGroup(group);
        HorseRider.inform(TAG, "setGroup: "+"Nastavil jsem atentovi "+PBeliefBase.getName()+
                " skupinu mastra "+group.getMaster());
    }

    public DSMap getMap(){
        return(PBeliefBase.getMap());
    }

    public void setBody(DSBody body) {PBeliefBase.setBody(body);} // TODO body zvlastni belief

    public DSBody getBody() {return(PBeliefBase.getBody());} // TODO body zvlastni belief


    public String getEntityName(){
        return(PBeliefBase.getName());
    }

    public int getNumber(){
        return(PNumber);
    }


    /*
                MAS INTERFACE
     */


    public boolean hearOrder(DSGoal goal){
        // vytvori  intention a goal je jeji top level goal, rozsiri ni intention pool
        PIntentionPool.clearPool(); // TODO provizorne, pri vice intensnach odstranit
        DSIntention intention=new DSIntention(goal);
        PIntentionPool.adoptIntention(intention);
        return(true);
    }


    public boolean informCompleted(DSGoal goal){
        if(PBeliefBase.getScenario()!=null) {
            PBeliefBase.getScenario().goalCompleted(this, goal);
            return (true);
        }
        else
            return(false);
    }


    public boolean informFailed(DSGoal goal){
        if(PBeliefBase.getScenario()!=null) {
            PBeliefBase.getScenario().goalFailed(this, goal);
            return (true);
        }
        else
            return(false);
    }

    public void moveBy(int Dx, int Dy){
        PBeliefBase.moveBy(Dx,Dy);
    }

    public Point getNearestObject(int objectType){
        return(PBeliefBase.nearestObject(this,objectType));
    }

    public Point getNearestGoal(){
        return(PBeliefBase.nearestGoal());
    }

    public Point getNearestDispenser(int dispenserType){
        return(PBeliefBase.nearestDispenser(dispenserType));
    }

    public Point nearestFreeBlock(int blockType){
        return(PBeliefBase.nearestFreeBlock(blockType));
    }

    /*
    *                CONTROL LOOP
    */



    public void propagateFeedback(DSPerceptor perceptor, Collection<Percept> newPercepts,
                                    DSIntention recentIntentionExecuted){

        int actionResult= PBeliefBase.getLastActionResult();

        if (recentIntentionExecuted != null){
                System.out.println("\nResult je "+actionResult);
                recentIntentionExecuted.intentionExecutionFeedback(actionResult, (DSAgent) this);
        }
    }


    public class controlLoop extends CyclicBehaviour {

        DSIntention recentIntentionExecuted=null;


        public void action() {
            Map<String, PerceptUpdate> perceptsCol=null;
            PerceptUpdate percepts=null;
            DSPerceptor perceptor = new DSPerceptor();

            try {
                perceptsCol=PEI.getPercepts(PName,PEntity);
                percepts = perceptsCol.get(PEntity);
                if (!(percepts.isEmpty())) {
                    /*          System.out.println("Agent "+PBeliefBase.getName()+" is in group "+
                            PBeliefBase.getGroup().getMaster()+" at "+PBeliefBase.getPosition()+
                            " body "+PBeliefBase.getBody().bodyToString());*/

                    // SENSING


                    DSPerceptor.processPercepts(PBeliefBase, percepts);



                    Collection<Percept> newPercepts=percepts.getAddList();  // TODO ??

                    System.out.println(percepts);


                    DSPerceptor.processPercepts(PBeliefBase,percepts);


                    // FEEDBACK

                    propagateFeedback(perceptor, newPercepts, recentIntentionExecuted);

                    // MAP UPDATE
                    Point myPos = PBeliefBase.getPosition();

                    PBeliefBase.getMap().clearArea(PBeliefBase.getVision(), myPos, PBeliefBase.getStep());

                    // barriera pro vycisteni mapy v dohledu
                    // while(!PCommander.barrier(PStep,1,(DSAgent)this.getAgent())){}
                    perceptor.actualizeMap(getMap(), percepts.getAddList(), getMap().getAgentPos((DSAgent) (this.getAgent())),
                            PBeliefBase.getTeamName(), PBeliefBase.getStep());


             //       getMap().printMap(((DSAgent) this.getAgent()).getGroupMasterName());
             //       getMap().printCells();

                    // REPORT AKTUALNICH TASKU LEUTNANTEM DESOUCHEMU
                    if (PBeliefBase.isLeutnant()) {
                        HorseRider.inform(TAG, "++++++++++++++++++++ step " + PBeliefBase.getStep() + " +++++++++++++++++++++");
                        PBeliefBase.getCommander().checkDeadlines(PBeliefBase.getStep());
                        PBeliefBase.getCommander().tasksProposed(perceptor.getTasksFromPercepts(percepts.getAddList()),PBeliefBase.getStep());
                        PBeliefBase.getMap().printMap("map "+getStep());
                    }

                    perceptor.getBodyFromPercepts(percepts.getAddList());

                    // tady nechapu, o co jde
                    if(!perceptor.seesBlocksInBody(percepts.getAddList()))
                        if(PBeliefBase.getScenario()!=null)
                            PBeliefBase.getScenario().checkEvent((DSAgent) (this.getAgent()),DSScenario._noBlockEvent);

                        // not moving period ++
                    if((myPos.x==PLastPosition.x)&&(myPos.y==PLastPosition.y))
                        PIdleSteps++;
                    else{
                        PIdleSteps=0;
                        PLastPosition=(Point)myPos.clone();
                    }

                        // pokud se nehybou dlouho, zlikvidujem scenar a nahlasime generalovi
                    if(getScenario()!=null)
                    if(getScenario().checkDeadlock()){
                                // vsichni clenove tymu se nehybou dele, nez je stanoveny deadlock limit
                            getCommander().scenarioFailed(PBeliefBase.getScenario());
                            System.out.println(getEntityName()+" DEADLOCK pro " +
                                  //  "task "+getScenario().getTask().getName()+
                                    " group members "+
                                        getScenario().getAgentsAllocatedText());
                            }

                    // agent disabled? Inform and dont execute
                    if (perceptor.disabled(newPercepts))
                        PBeliefBase.getCommander().agentDisabled((DSAgent) this.getAgent());


                    else
                    {
                        // EXECUTION
                        // vyber zameru
                        // vykonani zameru
                        // report uspesneho/neuspesneho ukonceni nasledovani zameru

                        if(PBeliefBase.getScenario()==null)
                            System.out.println("Scenario: No scenario");
                        else
                            System.out.println("Scenario: "+PBeliefBase.getScenario().getName());

                        if(PIntentionPool.getIntention()==null)
                            System.out.println("Intention: No intention");
                        else
                            System.out.println("Intention: "+PIntentionPool.getIntention().description());

                        PBeliefBase.getCommander().friendsReport(
                                (DSAgent) this.getAgent(), perceptor.getFriendsSeen(), PBeliefBase.getStep());
                        if((PIntentionPool.getIntention()==null)&&(getScenario()==null))
                            PBeliefBase.getCommander().needJob((DSAgent) this.getAgent());
                        recentIntentionExecuted = PIntentionPool.executeOneIntention((DSAgent) this.getAgent());
                        if (recentIntentionExecuted != null) {
                            if (recentIntentionExecuted.intentionState() == DSIntention.__Intention_Finished) {
                                PIntentionPool.removeIntention(recentIntentionExecuted);
                                informCompleted(recentIntentionExecuted.getTLG());
                            } else if (recentIntentionExecuted.intentionState() == DSIntention.__Intention_Failed) {
                                informFailed(recentIntentionExecuted.getTLG());
                                PIntentionPool.removeIntention(recentIntentionExecuted);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }       // END action()


        public controlLoop(Agent agent) {
            super();
        }
    }



    public void setup(){

        try{
            PEI.registerAgent(PName);
        } catch (AgentException ex){}

        try {
            PEI.associateEntity(PName, PEntity);
        } catch (RelationException ere) {
            HorseRider.yell(TAG, "setup: "+" ####### asociace se nezdarila", ere);
        }
        PIdleSteps=0;
        PLastPosition=new Point(0,0);
    }


    protected void takeDown() {
        HorseRider.warn(TAG, "takeDown: "+"Agent "+getAID().getName()+" umiiiraaaa.");
    }


    public DSAgent(String name, DSGroup group, String entity, EnvironmentInterfaceStandard ei, DeSouches commander,
                   int number, boolean leutnant){
        super();

            PLeutnant=leutnant;					// in 2020 there will be little bit more roles
            PNumber=number;                   			// but unique number remains
            PEI=ei;                             		// handle to ei (environment to connect the server)
            PName=name;
            PEntity=entity;
            PIntentionPool=new DSIntentionPool();               // set of intentions
            PSynchronized=new HashMap<DSAgent, Point>();        // ???

            PBeliefBase=new DSBeliefBase(this);                 // new BB for the agent

            PBeliefBase.setJADEName(name);                                           // jade name
            PBeliefBase.setIsLeutnant(leutnant);                                     // TODO should be general role in 2020
            PBeliefBase.setCommander(commander);                                     // commander of this agent (deSouches)
            PBeliefBase.setBody(new DSBody());                                       // shape of the agene (one element for agent, later more complex with boxes attached)
										     // TODO - recet connection -> agent should check its shape, could carry some boxes

            if(group==null){
                group = new DSGroup(this);
                group.getMap().addNewAgent(this, new Point(0,0));
                commander.groupCreated(group);
            }
            PBeliefBase.setGroup(group);
            PBeliefBase.setMap(group.getMap());

        controlLoop loop=new controlLoop(this);
        addBehaviour(loop);
    }
}

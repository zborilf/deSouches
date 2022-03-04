package dsAgents.dsReasoningModule.dsBeliefBase;

/*
 Vše, co deSouches ví, vjemy => pozice na mapě, objekty na mapách, úkoly, krok, energie, skore
                    sociální => generál, příslušnost ke skupině, master skupiny, který scénář vykonává
 */

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.DSBeliefsIndexes;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.DSRole;
import dsAgents.dsPerceptionModule.DSStatusIndexes;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.DSGroup;
import dsMultiagent.dsScenarios.DSScenario;
import eis.PerceptUpdate;
import eis.iilang.Parameter;
import eis.iilang.Percept;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class DSBeliefBase {



    private DSMap PMap;
    private DSAgent PAgent;
    private DSGroup PGroup=null;
    private int PStepsTotal;
    private int PTeamSize;
    private int PScore=0;
    private int PStep=0;
    LinkedList<DSRole> PRoles=null;
    private int PVision=0;     // obsolete in 2022
    private boolean PIsLeutnant=false;  // obsolete in 2022
    private int PEnergy=0;
    private DeSouches PCommander;
    private String PName="unknown";
    private String PJADEName="unknown";
    private String PTeamName="unknown";
    private DSScenario PScenario;       // active scenario
    private DSBody PBody=null;
    private boolean inicialized=false;
    private int PHoldsBlockType;

    private int PLastActionResult=DSStatusIndexes.__action_unknown_action;
    private String PLastAction="unknown";
    private String PLastActionParams="success";


    // 0 : __simStart
    // INIT

    public void inicialized(){
        inicialized=true;
    }

    public boolean needsInit(){
        return(!inicialized);
    }


    // 1 : __name

    public void setName(Collection<Parameter> parameters){
        String name=parameters.iterator().next().toString();
        PName = name;
    }

    public String getName(){
        return(PName);
    }

    // 2 : __team

    public void setTeamName(Collection<Parameter> parameters){
        String teamName=parameters.iterator().next().toString();
        PTeamName=teamName;
    }

    public String getTeamName(){
        return(PTeamName);
    }

    // 3 : __teamSize

    public void setTeamSize(Collection<Parameter> parameters) {
        int teamSize=Integer.valueOf(parameters.iterator().next().toString());
        PTeamSize=teamSize;
    }

    protected int getTeamSize() {return(PTeamSize);}

    // 4 : __steps

    public void setStepsTotal(Collection<Parameter> parameters) {
        int stepsTotal=Integer.valueOf(parameters.iterator().next().toString());
        PStepsTotal=stepsTotal;
    }

    protected int getPStepsTotal() {return(PStepsTotal);}

    // 5 : __role

    // TODO


    // 6 : __actionID

    // TODO


    // 7 : __timestamp

    // TODO


    // 8 : __deadLine

    // TODO


    // 9 : __step

    public void setStep(Collection<Parameter> parameters) {
        int step=Integer.valueOf(parameters.iterator().next().toString());
        PStep=step;
    }

    public int getStep(){
        return(PStep);
    }


    // 10 : __lastAction

    // TODO



    // 11 : __lastActionParams

    // TODO


    // 12 : __lastActionResult

    public void setLastActionResult(Collection<Parameter> parameters){
        String actionResult=parameters.iterator().next().toString();
        PLastActionResult=DSStatusIndexes.getIndex(actionResult);
        System.out.println("LAR LAR LAR "+PLastActionResult);
    }

    public int getLastActionResult(){
        return(PLastActionResult);
    }


    // 13 : __score

    public void setScore(Collection<Parameter> parameters){
        int score=Integer.valueOf(parameters.iterator().next().toString());
        PScore=score;
    }

    public int getScore(){
        return(PScore);
    }


    // 14 : __thing

    // TODO


    // 15 : __task

    // TODO


    // 16 : __attached

    // TODO


    // 17 : __energy

    public void setEnergy(Collection<Parameter> parameters){
        String energy=parameters.iterator().next().toString();
        PEnergy=Integer.parseInt(energy);
    }

    public int getEnergy(){
        return(PEnergy);
    }


    // 18 : __deactivated

    // TODO


    // 19 : __roleZone

    // TODO


    // 20 : __goalZone

    // TODO


    // 21 : __violation

    // TODO


    // 22 : __norm

    // TODO


    // 23 : __surveyed

    // TODO


    // 24 : __hit

    // TODO


    // 25 : __ranking

    // TODO


/*

        Internal beliefs

 */


    // JMENO

    public void setJADEName(String name){
        PJADEName=name;
    }

    public String getJADEName(){
        return(PJADEName);
    }





    // POZICE

    public Point getPosition() {      // vráti pozici na (skupinové) mapě pro agenta
        return(PMap.getAgentPos(PAgent));
    }

    public void moveBy(int PDx, int PDy) {
        PMap.moveBy(PAgent,PDx,PDy);
    }


    // LastActionResult




    // IS LEUTNANT?

    public void setIsLeutnant(boolean isLeutnant){
        PIsLeutnant=isLeutnant;
    }

    public boolean isLeutnant(){
        return(PIsLeutnant);
    }

    // GENERAL

    public void setCommander(DeSouches commander){
        PCommander=commander;
    }

    public DeSouches getCommander(){
        return(PCommander);
    }

    // ROLES

    public void setRoles(LinkedList<DSRole> roles){
        Percept p;
        PRoles = roles;
    };

    // DOHLED - zastarale 2022, vytahuje se z role

    public void setVision(int range){
        PVision=range;
    }

    public int getVision(){
        return(PVision);
    }

    // TELO AGENTA

    public void setBody(DSBody body){
        PBody=body;
    }

    public DSBody getBody(){
        return(PBody);
    }

    // MAPA

    public DSMap getMap(){
        return(PMap);
    }

    public void setMap(DSMap map){
        PMap=map;
    }

    // SOCIAL

    public void setGroup(DSGroup group){
        PGroup=group;
        setMap(PGroup.getMap());
    }

    public DSGroup getGroup(){
        return(PGroup);
    }

    public int getGroupSize(){
        return(PGroup.getMembers().size());
    }

    // AKTUALNI SCENAR

    public void setScenario(DSScenario scenario){
        PScenario=scenario;
    }

    public DSScenario getScenario(){
        return(PScenario);
    }


    public void setHoldsBolockType(int blockType){
        PHoldsBlockType=blockType;
    }

    public int getHoldsBolockType(){
        return PHoldsBlockType;
    }



    /*
                FUNKCNI PREDSTAVY (vypoctove)
     */


    boolean isNeighbour(Point point1, Point point2)
    // jsou tyto body sousede v ctyrokoli?
    {
        if(
                (
                        (
                                Math.abs(point1.x-point2.x)==1) || (Math.abs(point1.y-point2.y)==1)
                )
                        &&
                        (
                                (point1.x-point2.x==0) || (point1.y-point2.y==0)
                        )
        )
            return(true);

        return(false);
    }



    public boolean friendAroundCell(Point position, DSAgent agent){
        // je pritel ze skupiny na zaklade jeho skutecne pozice v ctyrokoli?
        // melo by stacit skontrolovat grupu, protoze blok shani jen mastergrupa, takze jej muze take jen ta drzet

        LinkedList<DSAgent> members=(LinkedList<DSAgent>)PAgent.getGroup().getMembers().clone();
        for(DSAgent friend:members){
            if(isNeighbour(position,friend.getPosition()))
                return(true);
        }
        return(true);
    }


    public Point nearestObject(DSAgent agent, int type) {
        return(getMap().nearestObject(type,agent.getPosition()));
    }

    public Point nearestDispenser(int type){
        return(nearestObject(PAgent, DSCell.__DSDispenser+type));
        // return(PMap.nearestObject(DSCell.__DSDispenser+type,PMap.getAgentPos())); // for this agent 0,0
    }

    public Point nearestFreeBlock(int type){
        Point blockAt=getMap().nearestFreeBlock(type, PAgent.getPosition());
        if(blockAt!=null)
                return(blockAt);
            return(null);
    }

    public Point nearestGoal(){
        return(nearestObject(PAgent,DSCell.__DSGoal));
        //        return(PMap.nearestObject(DSCell.__DSGoal,new Point(0,0)));
    }





    public DSBeliefBase(DSAgent agent){
        PAgent=agent;
    }

}

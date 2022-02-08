package dsAgents;

/*
 Vše, co deSouches ví, vjemy => pozice na mapě, objekty na mapách, úkoly, krok, energie, skore
                    sociální => generál, příslušnost ke skupině, master skupiny, který scénář vykonává
 */

import dsAgents.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.DSGroup;
import dsMultiagent.dsScenarios.DSScenario;

import java.awt.*;
import java.util.LinkedList;

public class DSBeliefBase {
    private DSMap PMap;
    private DSAgent PAgent;
    private DSGroup PGroup=null;
    private int PScore=0;
    private int PStep=0;
    private int PVision=0;
    private boolean PIsLeutnant=false;
    private int PEnergy=0;
    private DeSouches PCommander;
    private String PName="unknown";
    private String PJADEName="unknown";
    private String PTeamName="unknown";
    private DSScenario PScenario;
    private DSBody PBody=null;
    private boolean inicialized=false;
    private int PHoldsBlockType;

    // INIT

    protected void inicialized(){
        inicialized=true;
    }

    protected boolean needsInit(){
        return(!inicialized);
    }

    // JMENO

    protected void setName(String name){
        PName=name;
    }

    protected String getName(){
        return(PName);
    }

    protected void setJADEName(String name){
        PJADEName=name;
    }

    protected String getJADEName(){
        return(PJADEName);
    }

    protected void setTeamName(String teamName){
        PTeamName=teamName;
    }

    protected String getTeamName(){
        return(PTeamName);
    }

    // POZICE

    protected Point getPosition() {      // vráti pozici na (skupinové) mapě pro agenta
        return(PMap.getAgentPos(PAgent));
    }

    protected void moveBy(int PDx, int PDy) {
        PMap.moveBy(PAgent,PDx,PDy);
    }

    // KROK

    protected void setStep(int step){
        PStep=step;
    }

    protected int getStep(){
        return(PStep);
    }

    // ENERGIE

    protected void setEnergy(int energy){
        PEnergy=energy;
    }

    protected int getEnergy(){
        return(PEnergy);
    }

    // SKORE

    protected void setScore(int score){
        PScore=score;
    }

    protected int getScore(){
        return(PScore);
    }

    // IS LEUTNANT?

    protected void setIsLeutnant(boolean isLeutnant){
        PIsLeutnant=isLeutnant;
    }

    protected boolean isLeutnant(){
        return(PIsLeutnant);
    }

    // GENERAL

    public void setCommander(DeSouches commander){
        PCommander=commander;
    }

    public DeSouches getCommander(){
        return(PCommander);
    }

    // DOHLED

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

    // FUNKCNI PREDSTAVY (vypoctove)


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


    protected Point nearestObject(DSAgent agent, int type) {
        return(getMap().nearestObject(type,agent.getPosition()));
    }

    protected Point nearestDispenser(int type){
        return(nearestObject(PAgent, DSCell.__DSDispenser+type));
        // return(PMap.nearestObject(DSCell.__DSDispenser+type,PMap.getAgentPos())); // for this agent 0,0
    }

    protected Point nearestFreeBlock(int type){
        Point blockAt=getMap().nearestFreeBlock(type, PAgent.getPosition());
        if(blockAt!=null)
                return(blockAt);
            return(null);
    }

    protected Point nearestGoal(){
        return(nearestObject(PAgent,DSCell.__DSGoal));
        //        return(PMap.nearestObject(DSCell.__DSGoal,new Point(0,0)));
    }




    public DSBeliefBase(DSAgent agent){
        PAgent=agent;
    }

}

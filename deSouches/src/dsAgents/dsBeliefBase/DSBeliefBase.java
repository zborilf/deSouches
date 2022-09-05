package dsAgents.dsBeliefBase;

/*
Vše, co deSouches ví, vjemy => pozice na mapě, objekty na mapách, úkoly, krok, energie, skore
                   sociální => generál, příslušnost ke skupině, master skupiny, který scénář vykonává
*/

import dsAgents.DSAgent;
import dsAgents.DeSouches;
import dsAgents.dsBeliefBase.dsBeliefs.DSRole;
import dsAgents.dsBeliefBase.dsBeliefs.DSRoles;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.*;
import dsAgents.dsGUI.DSAgentGUI;
import dsAgents.dsPerceptionModule.DSStatusIndexes;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
import dsMultiagent.DSGroup;
import dsMultiagent.dsScenarios.DSMMission;
import dsMultiagent.dsTasks.DSTask;
import eis.iilang.Function;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class DSBeliefBase {

  private DSAgentGUI PGUI;

  private DSMap PMap;
  private boolean PGUIFocus;
  private DSAgentOutlook POutlook;
  private DSAgentOutlook PDeleteOutlook;

  private DSAgent PAgent;
  private DSGroup PGroup = null;
  private int PStepsTotal;
  private int PTeamSize;
  private int PScore = 0;
  private int PStep = 0;
  private DSRoles PRoles;
  private DSRole PActualRole = null;

  private boolean PIsLeutnant = false; // obsolete in 2022
  private int PEnergy = 0;
  private DeSouches PCommander;
  private String PName = "unknown";
  private String PJADEName = "unknown";
  private String PTeamName = "unknown";
  private DSMMission PMission; // active mission
  private DSBody PBody = null;
  private int PHoldsBlockType;
  private boolean inicialized = false;


  private String PLastGoal;
  private int PLastActionResult = DSStatusIndexes.__action_unknown_action;
  private String PLastActionResultString = "";
  private boolean PJobDemamnded=false;
  private String PLastAction = "unknown";
  private String PLastActionParams = "success";
  private LinkedList<Point> PAttached = new LinkedList<Point>();


  public void setGUI(DSAgentGUI gui) {
    PGUI = gui;
  }

  public DSAgentGUI getGUI() {
    return (PGUI);
  }

  public void setGUIFocus(boolean focus) {
    PGUIFocus = focus;
  }

  public boolean getGUIFocus() {
    return (PGUIFocus);
  }

  public void setJobDemanded(boolean value){
    PJobDemamnded=value;
  }

  public boolean getJobDemanded(){
    return(PJobDemamnded);
  }

  public void setLastGoal(String goal){
    PLastGoal=goal;
  }

  public String getLastGoal(){
    return(PLastGoal);
  }

  public LinkedList<Point> getAttched(){
    return(PAttached);
  }

  public boolean setRole(String role) {
    if (PRoles.getRole(role) != null) {
      PActualRole = PRoles.getRole(role);
      return (true);
    }
    return (false);
  }

  public String getAcualRole() {
    if (PActualRole == null) return "Role Not Available yet";
    return (PActualRole.getRoleName());
  }

  // 0 : __simStart

  // 1 : __name

  public void setName(Collection<Parameter> parameters) {
    String name = parameters.iterator().next().toString();
    PName = name;
    if (PGUIFocus) PGUI.setAgentName(PName);
    PCommander.registerAgent(PName, PAgent);
  }

  public String getName() {
    return (PName);
  }

  // 2 : __team

  public void setTeamName(Collection<Parameter> parameters) {
    String teamName = parameters.iterator().next().toString();

    PTeamName = teamName;
  }

  public String getTeamName() {
    return (PTeamName);
  }

  // 3 : __teamSize

  public void setTeamSize(Collection<Parameter> parameters) {
    int teamSize = Integer.parseInt(parameters.iterator().next().toString());
    PCommander.setTeamSize(teamSize);
    PTeamSize = teamSize;
  }

  public int getTeamSize() {
    return (PTeamSize);
  }

  // 4 : __steps

  public void setStepsTotal(Collection<Parameter> parameters) {
    int stepsTotal = Integer.valueOf(parameters.iterator().next().toString());
    PStepsTotal = stepsTotal;
  }

  protected int getPStepsTotal() {
    return (PStepsTotal);
  }

  // 5 : __role

  public void processRole(Collection<Parameter> parameters) {
    if (parameters.size() == 1)
      PActualRole = PRoles.getRole(parameters.iterator().next().toString());
    else {
      DSRole role = new DSRole(parameters);
      PRoles.addRole(role);
    }
  }

  // 6 : __actionID

  // TODO

  // 7 : __timestamp

  // TODO

  // 8 : __deadLine

  // TODO

  // 9 : __step

  public void setStep(Collection<Parameter> parameters) {
    int step = Integer.valueOf(parameters.iterator().next().toString());
    PStep = step;
    if (PGUIFocus) PGUI.setStep(step);
  }

  public int getStep() {
    return (PStep);
  }

  // 10 : __lastAction

  public void setLastAction(Collection<Parameter> parameters) {
    String action = parameters.iterator().next().toString();
    PLastAction = action;
    if (PGUIFocus) PGUI.setLastAction(action);
  }

  // 11 : __lastActionParams

  public void setLastActionParams(Collection<Parameter> parameters) {
    String action = parameters.iterator().next().toString();
    PLastAction = action;
    if (PGUIFocus) PGUI.setLastActionParams(action);
  }

  // 12 : __lastActionResult

  public void setLastActionResult(Collection<Parameter> parameters) {
    String actionResult = parameters.iterator().next().toString();
    PLastActionResult = DSStatusIndexes.getIndex(actionResult);
    PLastActionResultString = actionResult;

    if (PGUIFocus) PGUI.setLastActionResult(actionResult);
  }

  public int getLastActionResult() {
    return (PLastActionResult);
  }

  public String getLastActionResultString() { return(PLastActionResultString); }

  // 13 : __score

  public void setScore(Collection<Parameter> parameters) {
    int score = Integer.valueOf(parameters.iterator().next().toString());
    PScore = score;
    PGUI.setScore(score);
  }

  public int getScore() {
    return (PScore);
  }

  // 14 : __thing

  public void addThingToOutlook(Collection<Parameter> parameters) {
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    String type = i.next().toString();
    String params = i.next().toString();
    POutlook.processAddThing(x, y, type, params, PStep, PAgent);
  }

  public void deleteThingFromOutlook(Collection<Parameter> parameters) {
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    String type = i.next().toString();
    String params = i.next().toString();
    POutlook.processDeleteThing(x, y, type, params, PStep);
    PDeleteOutlook.processAddThing(x, y, type, params, PStep, PAgent);
  }

  // 15 : __task

  public void removeTask(Collection<Parameter> parameters) {
    PCommander.taskExpired(parameters.iterator().next().toString());
  }



  public void setTask(Collection<Parameter> parameters) {

    String name;
    int deadline;
    int reward;
    DSBody body;
    DSCell cell;
    ParameterList shapeParameters;

    Iterator<Parameter> piterator = parameters.iterator();

    name = piterator.next().toString();
    deadline = Integer.parseInt(piterator.next().toString());
    reward = Integer.parseInt(piterator.next().toString());

    shapeParameters = (ParameterList) piterator.next();

    body = new DSBody();
    for (Parameter parameter : shapeParameters) {
      int x = Integer.parseInt(((Function) parameter).getParameters().get(0).toString());
      int y = Integer.parseInt(((Function) parameter).getParameters().get(1).toString());
      String typeS = ((Function) parameter).getParameters().get(2).toString();
      int type = DSPercepts.blockTypeByName(typeS);
      cell = new DSCell(x, y, type + DSCell.__DSBlock, 0);
      if (Math.abs(x) + Math.abs(y) == 1)
        body.insertFirstCell(cell); // must be at the first position
      else body.addCell(cell);
    }
    PCommander.taskProposed(
        new DSTask(name, deadline, reward, body, PStep)); // nebo primo do GroupOptionsPool? Necht je to pres deSouches
  }




  // 16 : __attached

  public void addAttached(Collection<Parameter> parameters){
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    Point point=new Point(x,y);
    PAttached.add(point);
  }


  public void removeAttached(Collection<Parameter> parameters){
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    Point point=new Point(x,y);
    LinkedList<Point> newList=new LinkedList();
    for(Point point2:PAttached)
      if (!(point2.x==point.x)||!(point2.y==point.y)) {
        newList.add(point2);
      }
    PAttached=newList;
  }

  public boolean isAttached(Point position){
    return(PAttached.contains(position));
  }

  // 17 : __energy

  public void setEnergy(Collection<Parameter> parameters) {
    String energy = parameters.iterator().next().toString();
    PEnergy = Integer.parseInt(energy);
    if (PGUIFocus) PGUI.setEnergy(energy);
  }

  public int getEnergy() {
    return (PEnergy);
  }

  // 18 : __deactivated

  // TODO

  // 19 : __roleZone

  public void addRoleZoneToOutlook(Collection<Parameter> parameters) {
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    POutlook.processAddThing(x, y, "roleZone", "", PStep, PAgent);
  }

  public void leavesRoleZone(Collection<Parameter> parameters) {
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    POutlook.processDeleteThing(x, y, "roleZone", "", PStep);
    PDeleteOutlook.processAddThing(x, y, "roleZone", "", PStep, PAgent);
  }

  // 20 : __goalZone

  public void addGoleZoneToOutlook(Collection<Parameter> parameters) {
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    POutlook.processAddThing(x, y, "goalZone", "", PStep, PAgent);
  }

  public void leavesGoalZone(Collection<Parameter> parameters) {
    // TODO melo by byt zbytecne
    Iterator i = parameters.iterator();
    int x = Integer.parseInt(i.next().toString());
    int y = Integer.parseInt(i.next().toString());
    POutlook.processDeleteThing(x, y, "goalZone", "", PStep);
    PDeleteOutlook.processAddThing(x, y, "goalZone", "", PStep, PAgent);
  }

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

  public void setJADEName(String name) {
    PJADEName = name;
  }

  public String getJADEName() {
    return (PJADEName);
  }

  // POZICE

  public Point getAgentPosition() { // vráti pozici na (skupinové) mapě pro agenta
    return (PMap.getAgentPos(PAgent));
  }

  // IS LEUTNANT? ... 2022 - role

  public void setIsLeutnant(boolean isLeutnant) {
    PIsLeutnant = isLeutnant;
  }

  public boolean isLeutnant() {
    return (PIsLeutnant);
  }

  // GENERAL

  public void setCommander(DeSouches commander) {
    PCommander = commander;
  }

  public DeSouches getCommander() {
    return (PCommander);
  }

  // DOHLED - zastarale 2022, vytahuje se z role

  public int getVision() {
    if (PActualRole == null) return (0);
    return (PActualRole.getVision());
  }

  public int getSpeed() {
    if (PActualRole == null) return (0);
    int attached = this.getBody().getAllDirectionsAttached().size();
    return PActualRole.getSpeed(attached);
  }

  // AGENT BODY

  public void setBody(DSBody body) {
    PBody = body;
  }

  public DSBody getBody() {
    return (PBody);
  }

  // MAP

  public DSMap getMap() {
    return (PMap);
  }

  public void setMap(DSMap map) {
    PMap = map;
  }

  // OUTLOOK

  public DSAgentOutlook getOutlook() {
    return (POutlook);
  }

  public DSAgentOutlook getDeleteOutlook() {
    return (PDeleteOutlook);
  }

  public void clearDeleteOutlook() {
    PDeleteOutlook = new DSAgentOutlook(PAgent);
  }

  // SOCIAL

  public void setGroup(DSGroup group) {
    PGroup = group;
    setMap(PGroup.getMap());
  }

  public DSGroup getGroup() {
    return (PGroup);
  }


  // MISSIONS

  public void setMission(DSMMission mission) {
    PMission = mission;
    if (PMission != null)
      PAgent.printOutput("---- SCENARIO SET TO "+ PMission.getName());
    else
      PAgent.printOutput("---- SCENARIO SET TO null");
    if (PGUIFocus) {
      if(mission!=null)
        PGUI.setScenario(mission.getName());
    }
  }

  public DSMMission getMission() {
    return (PMission);
  }

  public void setHoldsBolockType(int blockType) {
    PHoldsBlockType = blockType;
  }








  public Point nearestObject(DSAgent agent, int type) {
    return (getMap().nearestObject(type, agent.getMapPosition()));
  }


  public Point nearestFreeBlock(int type) {
    Point blockAt = getMap().nearestFreeBlock(type, PAgent.getMapPosition());
    if (blockAt != null) return (blockAt);
    return (null);
  }

  public Point nearestGoal() {
    return (nearestObject(PAgent, DSCell.__DSGoalArea));
  }

  public DSBeliefBase(DSAgent agent) {
    PAgent = agent;
    PRoles = new DSRoles();
    POutlook = new DSAgentOutlook(agent);
    PDeleteOutlook = new DSAgentOutlook(agent);
    PHoldsBlockType = -1;
  }
}

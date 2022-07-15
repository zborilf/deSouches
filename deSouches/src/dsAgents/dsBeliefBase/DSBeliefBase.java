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
import dsAgents.dsGUI;
import dsAgents.dsPerceptionModule.DSStatusIndexes;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
import dsMultiagent.DSGroup;
import dsMultiagent.DSSynchronize;
import dsMultiagent.dsScenarios.DSScenario;
import dsMultiagent.dsTasks.DSTask;
import eis.iilang.Function;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class DSBeliefBase {

  private dsGUI PGUI;

  private DSMap PMap;
  private int PWidthMap;
  private int PHeightMap;
  private boolean PGUIFocus;
  private DSAgentOutlook POutlook;

  private DSAgent PAgent;
  private DSGroup PGroup = null;
  private int PStepsTotal;
  private int PTeamSize;
  private int PScore = 0;
  private int PStep = 0;
  private DSRoles PRoles;
  private DSRole PActualRole = null;

  DSSynchronize PSynchronizer;

  private boolean PIsLeutnant = false; // obsolete in 2022
  private int PEnergy = 0;
  private DeSouches PCommander;
  private String PName = "unknown";
  private String PJADEName = "unknown";
  private String PTeamName = "unknown";
  private DSScenario PScenario; // active scenario
  private DSBody PBody = null;
  private boolean inicialized = false;
  private int PHoldsBlockType;

  private int PLastActionResult = DSStatusIndexes.__action_unknown_action;
  private String PLastAction = "unknown";
  private String PLastActionParams = "success";

  public void setGUI(dsGUI gui) {
    PGUI = gui;
  }

  public dsGUI getGUI() {
    return (PGUI);
  }

  public void setGUIFocus(boolean focus) {
    PGUIFocus = focus;
  }

  public boolean getGUIFocus() {
    return (PGUIFocus);
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

  public DSSynchronize getSynchronizer() {
    return (PSynchronizer);
  }

  // 0 : __simStart
  // INIT

  public void inicialized() {
    inicialized = true;
  }

  public boolean needsInit() {
    return (!inicialized);
  }

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
    if (PGUIFocus) PGUI.setLastActionResult(actionResult);
  }

  public int getLastActionResult() {
    return (PLastActionResult);
  }

  // 13 : __score

  public void setScore(Collection<Parameter> parameters) {
    int score = Integer.valueOf(parameters.iterator().next().toString());
    PScore = score;
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
  }

  // 15 : __task

  public void removeTask(Collection<Parameter> parameters) {
    PCommander.taskExpired(parameters.iterator().next().toString());
  }

  public void setTask(Collection<Parameter> parameters) {
    System.out.println(parameters.toString());
    /*
           parameters = [taskName, deadline, reward, [req(x,y,type]*]]
    */

    DSTask task;
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
        new DSTask(name, deadline, reward, body),
        PStep); // nebo primo do GroupOptionsPool? Necht je to pres deSouches
  }

  // 16 : __attached

  // TODO

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

  // TELO AGENTA

  public void setBody(DSBody body) {
    PBody = body;
  }

  public DSBody getBody() {
    return (PBody);
  }

  // MAPA

  public DSMap getMap() {
    return (PMap);
  }

  public void setMap(DSMap map) {
    PMap = map;
  }

  // ROZHLED

  public DSAgentOutlook getOutlook() {
    return (POutlook);
  }

  // SOCIAL

  public void setGroup(DSGroup group) {
    PGroup = group;
    setMap(PGroup.getMap());
  }

  public DSGroup getGroup() {
    return (PGroup);
  }

  public int getGroupSize() {
    return (PGroup.getMembers().size());
  }

  // AKTUALNI SCENAR

  public void setScenario(DSScenario scenario) {
    if (scenario == null) return;
    PScenario = scenario;
    if (PGUIFocus) PGUI.setScenario(scenario.getName());
  }

  public DSScenario getScenario() {
    return (PScenario);
  }

  public void setHoldsBolockType(int blockType) {
    PHoldsBlockType = blockType;
  }

  public int getHoldsBolockType() {
    return PHoldsBlockType;
  }

  /*
             FUNKCNI PREDSTAVY (vypoctove)
  */

  boolean isNeighbour(Point point1, Point point2)
        // jsou tyto body sousede v ctyrokoli?
      {
    if (DSMap.distance(point1, point2) == 1) return (true);

    return (false);
  }

  public boolean friendAroundCell(Point position, DSAgent agent) {
    // je pritel ze skupiny na zaklade jeho skutecne pozice v ctyrokoli?
    // melo by stacit skontrolovat grupu, protoze blok shani jen mastergrupa, takze jej muze take
    // jen ta drzet

    LinkedList<DSAgent> members = (LinkedList<DSAgent>) PAgent.getGroup().getMembers().clone();
    for (DSAgent friend : members) {
      if (isNeighbour(position, friend.getMapPosition())) return (true);
    }
    return (true);
  }

  public Point nearestObject(DSAgent agent, int type) {
    return (getMap().nearestObject(type, agent.getMapPosition()));
  }

  public Point nearestDispenser(int type) {
    return (nearestObject(PAgent, DSCell.__DSDispenser + type));
    // return(PMap.nearestObject(DSCell.__DSDispenser+type,PMap.getAgentPos())); // for this agent
    // 0,0
  }

  public Point nearestFreeBlock(int type) {
    Point blockAt = getMap().nearestFreeBlock(type, PAgent.getMapPosition());
    if (blockAt != null) return (blockAt);
    return (null);
  }

  public Point nearestGoal() {
    return (nearestObject(PAgent, DSCell.__DSGoalArea));
    //        return(PMap.nearestObject(DSCell.__DSGoalArea,new Point(0,0)));
  }

  public DSBeliefBase(DSAgent agent, DSSynchronize synchronizer) {
    PAgent = agent;
    PRoles = new DSRoles();
    POutlook = new DSAgentOutlook();
    PSynchronizer = synchronizer;
  }
}

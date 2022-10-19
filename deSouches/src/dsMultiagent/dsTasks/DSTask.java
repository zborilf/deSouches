package dsMultiagent.dsTasks;

import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;

import java.awt.*;
import java.util.LinkedList;

public class DSTask {


  protected String PTaskName;
  String PTaskNickname;
  int PDeadline;
  int PReward;
  DSTaskType PTaskType;
  DSBody PTaskBody;
  LinkedList<Integer> PTypesNeeded;
  DSTaskMember[] PSubtaskRoutes={null,null,null,null};



  public int getReward(){
    return(PReward);
  }

  public Point getGoalArea(){
    if(PSubtaskRoutes[0]!=null)
      return(PSubtaskRoutes[0].getGoalPosition());
    return(null);
  }

  public String getName() {
    return (PTaskName);
  }


  public String getNickName() {
    return (PTaskNickname);
  }


  public void setSubtaskRoute(int subtaskNumber, DSTaskMember PSubtaskRoute) {
    this.PSubtaskRoutes[subtaskNumber] = PSubtaskRoute;
  }

  public String task2String(int step){
    String t="Task type "+PTaskType+"\n";
    t=t+">> task body "+"at "+PTaskBody.bodyToString()+"\n";
    for(int i=0; i<PTypesNeeded.size();i++) {
      t=t+">>> Type needed <"+PTypesNeeded.get(i)+
              ">\n>>>> route: ";
      t=t+PSubtaskRoutes[i].taskMember2String()+" ["+PSubtaskRoutes[i].costEstimation() +"]\n";
    }
    t=t+">>>> deadline at: "+PDeadline+", remains: "+(PDeadline-step)+"\n";
    t=t+">>>> cost estimated to "+subtaskCostEstimation()+"\n\n";
    return(t);
  }

  public int goalDistanceMax() {
    Point gp = PSubtaskRoutes[0].getGoalPosition();
    DSMap map = PSubtaskRoutes[0].getAgent().getGroup().getMap();
    int gd = map.distance(PSubtaskRoutes[0].getAgent().getMapPosition(), gp);
    int gd2 = 0;
    for (int i = 1; i < PTypesNeeded.size(); i++) {
      gd2 = map.distance(PSubtaskRoutes[i].getAgent().getMapPosition(), gp);
      if (gd < gd2)
        gd = gd2;
    }
    return(gd);
  }

  public int subtaskCostEstimation() {
    int ce = PSubtaskRoutes[0].costEstimation();
    for (int i = 1; i < PTypesNeeded.size(); i++) {
      if (ce < PSubtaskRoutes[i].costEstimation())
        ce = PSubtaskRoutes[i].costEstimation();
    }
    return(ce);
  }

  public DSTaskMember getSubtaskRoutes(int subtaskNumber) {
    return PSubtaskRoutes[subtaskNumber];
  }

  public LinkedList<Integer> getTypesNeeded() {
    return (PTypesNeeded);
  }

  int taskType() {
    LinkedList<DSBody> bodies = new LinkedList<DSBody>();
    DSBody body = new DSBody();

    //  DVOJICKY

    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    bodies.add(body);
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    bodies.add(body);
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    bodies.add(body);
    body = new DSBody();

    //  TROJICKY
    // 4
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(2, 1, 0, 0));
    bodies.add(body);
    // 5
    body = new DSBody();
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    bodies.add(body);
    // 6
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-2, 1, 0, 0));
    bodies.add(body);
    // 7
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(0, 3, 0, 0));
    bodies.add(body);
    // 8
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    bodies.add(body);
    // 9
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    bodies.add(body);
    // 10
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    bodies.add(body);
    // 11
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    bodies.add(body);
    // 12
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    bodies.add(body);
    // 13
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    bodies.add(body);

    //  CTVERICKY

    // 14
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-2, 1, 0, 0));
    body.addCell(new DSCell(-3, 1, 0, 0));
    bodies.add(body);
    // 15
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-2, 1, 0, 0));
    body.addCell(new DSCell(-2, 2, 0, 0));
    bodies.add(body);
    // 16
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    body.addCell(new DSCell(-2, 2, 0, 0));
    bodies.add(body);
    // 17
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    body.addCell(new DSCell(-1, 3, 0, 0));
    bodies.add(body);
    // 18
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    bodies.add(body);
    // 19
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    body.addCell(new DSCell(-2, 2, 0, 0));
    bodies.add(body);
    // 20
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    body.addCell(new DSCell(-1, 3, 0, 0));
    bodies.add(body);
    // 21
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(0, 3, 0, 0));
    body.addCell(new DSCell(-1, 3, 0, 0));
    bodies.add(body);
    // 22
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(0, 3, 0, 0));
    body.addCell(new DSCell(0, 4, 0, 0));
    bodies.add(body);
    // 23
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(0, 3, 0, 0));
    body.addCell(new DSCell(1, 3, 0, 0));
    bodies.add(body);
    // 24
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    body.addCell(new DSCell(1, 3, 0, 0));
    bodies.add(body);
    // 25
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    body.addCell(new DSCell(2, 2, 0, 0));
    bodies.add(body);

    // 26
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    bodies.add(body);
    // 27
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    body.addCell(new DSCell(1, 3, 0, 0));
    bodies.add(body);
    // 28
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    body.addCell(new DSCell(2, 2, 0, 0));
    bodies.add(body);
    // 29
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(2, 1, 0, 0));
    body.addCell(new DSCell(2, 2, 0, 0));
    bodies.add(body);
    // 30
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(2, 1, 0, 0));
    body.addCell(new DSCell(3, 1, 0, 0));
    bodies.add(body);
    // 31
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    bodies.add(body);
    // 32
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(0, 3, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    bodies.add(body);
    // 33
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    bodies.add(body);
    // 34
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    bodies.add(body);
    // 35
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(2, 1, 0, 0));
    bodies.add(body);
    // 36
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-2, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    bodies.add(body);
    // 37
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(2, 1, 0, 0));
    bodies.add(body);
    // 38
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-1, 2, 0, 0));
    bodies.add(body);
    // 39
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(0, 3, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    bodies.add(body);
    // 40
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    bodies.add(body);
    // 41
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(1, 1, 0, 0));
    body.addCell(new DSCell(1, 2, 0, 0));
    bodies.add(body);
    // 42
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    body.addCell(new DSCell(0, 2, 0, 0));
    body.addCell(new DSCell(-1, 1, 0, 0));
    body.addCell(new DSCell(-2, 1, 0, 0));
    bodies.add(body);
    // A na zaver jednicka
    // 43
    body = new DSBody();
    body.addCell(new DSCell(0, 1, 0, 0));
    bodies.add(body);

    int i = 1;
    for (DSBody bodyL : bodies) {
      if (PTaskBody.matchBody(bodyL))
        return (i); //                System.out.println(PName+" taskType: " + i);
      i++;
    }

    return (0);
  }

  public int getTaskTypeNumber() {
    if (PTaskType == null) return (0);
    return (PTaskType.getTaskType());
  }


  public DSTaskType getTaskType() {
    return (PTaskType);
  }

  public int getBoxType(int agent) {
    return (PTaskType.getBlockType(agent, PTaskBody));
  }

  public DSBody getTaskBody() {
    return (PTaskBody);
  }

  public int getDeadline() {
    return (PDeadline);
  }

  public DSBody getTaskArea() {
    return (PTaskType.getTaskArea());
  }

  /*
  public void suceeded(){

  }
*/

  public DSTask(
      String name, int deadline, int reward, DSBody body, int step) { // , LinkedList<Integer> typesNeeded){
    PTaskNickname = name+"_"+step;
    PTaskName = name;
    PDeadline = deadline;
    PReward = reward;
    PTaskBody = body;
    switch (taskType()) {
      case 1:
        PTaskType = new DSTaskType1();
        break;
      case 2:
        PTaskType = new DSTaskType2();
        break;
      case 3:
        PTaskType = new DSTaskType3();
        break;
      case 4:
        PTaskType = new DSTaskType4();
        break;
      case 5:
        PTaskType = new DSTaskType5();
        break;
      case 6:
        PTaskType = new DSTaskType6();
        break;
      case 7:
        PTaskType = new DSTaskType7();
        break;
      case 8:
        PTaskType = new DSTaskType8();
        break;
      case 9:
        PTaskType = new DSTaskType9();
        break;
      case 10:
        PTaskType = new DSTaskType10();
        break;
      case 11:
        PTaskType = new DSTaskType11();
        break;
      case 12:
        PTaskType = new DSTaskType12();
        break;
      case 13:
        PTaskType = new DSTaskType13();
        break;
      case 14:
        PTaskType = new DSTaskType14();
        break;
      case 15:
        PTaskType = new DSTaskType15();
        break;
      case 16:
        PTaskType = new DSTaskType16();
        break;
      case 17:
        PTaskType = new DSTaskType17();
        break;
      case 18:
        PTaskType = new DSTaskType18();
        break;
      case 19:
        PTaskType = new DSTaskType19();
        break;
      case 20:
        PTaskType = new DSTaskType20();
        break;
      case 21:
        PTaskType = new DSTaskType21();
        break;
      case 22:
        PTaskType = new DSTaskType22();
        break;
      case 23:
        PTaskType = new DSTaskType23();
        break;
      case 24:
        PTaskType = new DSTaskType24();
        break;
      case 25:
        PTaskType = new DSTaskType25();
        break;
      case 26:
        PTaskType = new DSTaskType26();
        break;
      case 27:
        PTaskType = new DSTaskType27();
        break;
      case 28:
        PTaskType = new DSTaskType28();
        break;
      case 29:
        PTaskType = new DSTaskType29();
        break;
      case 30:
        PTaskType = new DSTaskType30();
        break;
      case 31:
        PTaskType = new DSTaskType31();
        break;
      case 32:
        PTaskType = new DSTaskType32();
        break;
      case 33:
        PTaskType = new DSTaskType33();
        break;
      case 34:
        PTaskType = new DSTaskType34();
        break;
      case 35:
        PTaskType = new DSTaskType35();
        break;
      case 36:
        PTaskType = new DSTaskType36();
        break;
      case 37:
        PTaskType = new DSTaskType37();
        break;
      case 38:
        PTaskType = new DSTaskType38();
        break;
      case 39:
        PTaskType = new DSTaskType39();
        break;
      case 40:
        PTaskType = new DSTaskType40();
        break;
      case 41:
        PTaskType = new DSTaskType41();
        break;
      case 42:
        PTaskType = new DSTaskType42();
        break;
      case 43:
        PTaskType = new DSTaskType43();
        break;
      default:
        PTaskType = null;
        return;
    }

    PTypesNeeded = PTaskType.getTypesNeeded(getTaskBody());
  }
}

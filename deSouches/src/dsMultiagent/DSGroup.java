package dsMultiagent;

import deSouches.utils.HorseRider;
import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.dsTasks.DSTask;
import java.awt.*;
import java.io.FileWriter;
import java.util.*;

public class DSGroup {
  private static final String TAG = "DSGroup";

  public DSAgent PMaster;
  DSMap PGroupMap;

  LinkedList<DSAgent> PMembers;
  HashMap<DSTask, Point> PGoalAreas;
  boolean PMasterGroup;


  public DSAgent getMaster() {
    return (PMaster);
  }

  public void setMasterGroup() {
    PMasterGroup = true;
    PGroupMap.setMasterMap();
  }

  public boolean isMasterGroup() {
    return (PMasterGroup);
  }

  public LinkedList<DSAgent> getMembers() {
    return (PMembers);
  }

  public LinkedList<DSAgent> getMembersByRole(String role, boolean notBusy) {
    // gets member by role, if notBusy, only those not working on a task

    LinkedList<DSAgent> membersRole = new LinkedList<DSAgent>();
    LinkedList<DSAgent> members = getMembers();
    if (members == null) return (null);
    for (DSAgent member : members)
      if (member.getActualRole().compareTo(role) == 0)
        if((!notBusy)||(!member.isBusyTask()))
          membersRole.add(member);
    return (membersRole);
  }

  public int getNumber() {
    return (PMaster.getNumber());
  }

  public void releaseGoalArea(DSTask task) {
    HorseRider.inform(TAG, "getGoalArea: removed GA for" + task.getName());
    PGoalAreas.remove(task);
  }

  public boolean standsMemberAt(Point position){
    LinkedList<DSAgent> members=(LinkedList<DSAgent>)PMembers.clone();
    for(DSAgent agent:members)
      if(agent.getMap().isAgentBodyAt(position,agent))
        return(true);
      return(false);
  }


  public LinkedList<DSAgent> getMembersList() {
    return ((LinkedList<DSAgent>) PMembers.clone());
  }

  public LinkedList<DSAgent> getFreeAgents(int priority) {
    LinkedList<DSAgent> allAgents = getMembersList();
    LinkedList<DSAgent> freeAgents = new LinkedList<DSAgent>();
    for (DSAgent agent : allAgents) if (!agent.isBusy(priority)) freeAgents.add(agent);
    return (freeAgents);
  }


  public DSMap getMap() {
    return (PGroupMap);
  }

  public LinkedList<Point> allObjects(int type) {
    return (PGroupMap.getTypePositions(type));
  }

  public String printGroup() {
    String st1 = "Leader {" + PMaster.getEntityName() + "}";
    String st2="";

    LinkedList<DSAgent> agentSet = ((LinkedList<DSAgent>) PMembers.clone());

    for (DSAgent agent : agentSet) {
      st2 = agent.getEntityName() + ", ";
      st1 = st1.concat(st2);
    }

    System.out.println(st1);

    return(st1);

  }


  public synchronized boolean absorbGroup(DSGroup groupToAbsorb, Point displacement,
                                              FileWriter output) {

    if (groupToAbsorb == this) return (true);

    LinkedList<DSAgent> newMembers = groupToAbsorb.getMembers();

    LinkedList<DSAgent> membersCl = (LinkedList<DSAgent>) newMembers.clone();

    String st="";
    if (isMasterGroup()) st="MasterGroupExtended: ";
    else st="groupExtended: ";


    st=st+printGroup();
    st=st+(" by group ");
    st=st+groupToAbsorb.printGroup();
    st=st+" step " + getMembers().getFirst().getStep();

    try {
      output.write(st + "\n");
      output.flush();
    }catch(Exception e){};


    for (var newMember : membersCl) {
      PMembers.add(newMember);
      PGroupMap.addAgent(newMember, displacement);
      newMember.getGroup().removeAgent(newMember);
      newMember.setGroup(this);
      try{
        newMember.getOutput().write("Absorbovan o "+displacement+"\n");
        newMember.getOutput().flush();
      }catch(Exception e){};
    }

    //    PMaster.getCommander().groupRemoved(groupToAbsorb);
    PMaster.getCommander().groupExtendedBy(this, groupToAbsorb);
    PGroupMap.mergeMaps(groupToAbsorb.getMap(), displacement);

    return (true);
  }

  private void removeAgent(DSAgent member) {
    PMembers.remove(member);
    PGroupMap.removeAgent(member);
  }

  public DSGroup(DSAgent agent) {
    PMaster = agent;
    PMembers = new LinkedList();
    PMembers.add(agent);
    PGoalAreas = new HashMap();
    PGroupMap = new DSMap(PMaster);
    PMasterGroup = false;
  }

  @Override
  public String toString() {
    // group will be identified by leader name
    return "skupina " + PMaster.getEntityName();
  }
}

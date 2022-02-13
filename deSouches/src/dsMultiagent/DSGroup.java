package dsMultiagent;

import deSouches.utils.HorseRider;
import dsAgents.DSAgent;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsReasoningModule.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.dsTasks.DSTask;

import java.awt.*;
import java.util.*;

public class DSGroup {
    private static final String TAG = "DSGroup";


    public DSAgent PMaster;
    DSMap PGroupMap;
    Random PRandom;

    LinkedList<DSAgent> PMembers;
    HashMap<DSTask, Point> PGoalAreas;
    boolean PMasterGroup;


    public String getMaster(){
        return(PMaster.getEntityName());
    }

    public void setMasterGroup(){
        PMasterGroup=true;
    }

    public boolean isMasterGroup(){
        return(PMasterGroup);
    }

    public LinkedList<DSAgent> getMembers(){
        return(PMembers);
    }

    public int getNumber(){
        return(PMaster.getNumber());
    }



    public void releaseGoalArea(DSTask task){
        HorseRider.inform(TAG, "getGoalArea: removed GA for"+task.getName());
        PGoalAreas.remove(task);
    }

    public Point getUGuardedGoalArea(){
        Random random=new Random();
        LinkedList<Point> goals=allObjects(DSCell.__DSGoal);
        if(goals==null)
            return(null);
        if(goals.size()==0)
            return(null);
        int select=random.nextInt(goals.size());
        Point goalArea=goals.get(select);
        return(goalArea);
    }

    public class CustomComparator implements Comparator<Point> {

        @Override
        public int compare(Point o1, Point o2) {

            Point obstacle1= PGroupMap.nearestObject(DSCell.__DSObstacle,o1);
            Point obstacle2= PGroupMap.nearestObject(DSCell.__DSObstacle,o2);
            if(DSMap.distance(o1,obstacle1)>DSMap.distance(o2,obstacle2))
                return -1;
            if(DSMap.distance(o1,obstacle1)==DSMap.distance(o2,obstacle2))
                return 0;
            return 1;
        }
    }

    LinkedList<Point> sortByObstacleDistance(LinkedList<Point> goals){
        LinkedList<Point> sortedGoals;
      //  Collections.sort(goals,new CustomComparator());
        return(goals);
    }

    public Point getGoalArea(DSTask task, int step){ // TODO vylepsit
        if(PGoalAreas.containsKey(task))
            return(PGoalAreas.get(task));
        boolean conflict;
        LinkedList<Point> goals=allObjects(DSCell.__DSGoal);
    //    goals=sortByObstacleDistance(goals);
        Collections.shuffle(goals); // lets try this, select the goal area randomly
        for(Point position:goals) {
            conflict=goalHasConflictForTask(position,task, step);
            if(!conflict) {
                registerGoal(task, position);
                return (position);
            }
        }
        return(null);
    }
    public void registerGoal(DSTask task, Point position){
        PGoalAreas.put(task, position);
        HorseRider.inform(TAG, "getGoalArea: new "+PGoalAreas.keySet().size()+". goal area for "+task.getName()+" at "+position);

    }

    public boolean goalHasConflictForTask(Point goalPosition, DSTask task, int step){
        boolean conflict=false;
        if (PGroupMap.isObstacleAt(goalPosition, new DSBody(), task.getTaskArea(),step))
            conflict=true;
        for (DSTask task2 : PGoalAreas.keySet()) {
            HorseRider.inform(TAG, "getGoalArea: porovn pos" + goalPosition + " a area " + task.getTaskArea().getBodyList() +
                    " s areou pro task " + task2.getName()+" na pozici "+ PGoalAreas.get(task2) + " a  " +
                    task2.getTaskArea().getBodyList());
            if (DSMap.areasConflict(goalPosition, task.getTaskArea(), PGoalAreas.get(task2), task2.getTaskArea())) {
                HorseRider.inform(TAG, "getGoalArea: KONFLIKT");
                conflict = true;
            }
        }
        return conflict;
    }


    public LinkedList<DSAgent> getMembersList(){
        LinkedList<DSAgent> membersCl=(LinkedList<DSAgent>)PMembers.clone();
        LinkedList<DSAgent> membersLst= new LinkedList<DSAgent>();
        for(DSAgent agent:membersCl)
            membersLst.add(agent);
        return(membersLst);
    }

    public LinkedList<DSAgent> getFreeAgents(int priority){
        LinkedList<DSAgent> allAgents=getMembersList();
        LinkedList<DSAgent> freeAgents= new LinkedList<DSAgent>();
        for(DSAgent agent:allAgents)
            if(!agent.isBusy(priority))
                freeAgents.add(agent);
            return(freeAgents);
    }

    public LinkedList<DSAgent> getBusyAgents(int priority){
        LinkedList<DSAgent> allAgents=getMembersList();
        LinkedList<DSAgent> busyAgents= new LinkedList<DSAgent>();
        for(DSAgent agent:allAgents)
            if(agent.isBusy(priority))
                busyAgents.add(agent);
        return(busyAgents);
    }


    public DSMap getMap(){
        return(PGroupMap);
    }


    public LinkedList<Point> allObjects(int type) {
        return(PGroupMap.allObjects(type));

        /*
     //   LinkedList<Point> objectPositionsAgt;
        LinkedList<Point> objectPositionsGrp = new LinkedList<Point>();
        Point objectPosition;
        if (!PMembers.containsKey(PMaster))
            return (null);
        Set<DSAgent> agentSet=((HashMap<DSAgent,Point>)PMembers.clone()).keySet();
        for (DSAgent agentServ : agentSet) {
            objectPositionsAgt = agentServ.getMap().allObjects(type);
            if(objectPositionsAgt!=null)
                for (Point positionAgt : objectPositionsAgt) {
                    objectPosition = transferPosition(positionAgt, PMaster, agentServ);
                    if (!objectPositionsGrp.contains(objectPosition))
                        objectPositionsGrp.add(objectPosition);
                }
        }
        return (objectPositionsGrp);*/
    }



    public void printGroup(){
        String st1= "MASTER{"+PMaster.getEntityName()+"}";
        String st2;

        LinkedList<DSAgent> agentSet=((LinkedList<DSAgent>)PMembers.clone());

        for(DSAgent agent:agentSet){
            st2=agent.getEntityName()+", ";
            st1=st1.concat(st2);
        }
        LinkedList<Point> goals=allObjects(DSCell.__DSGoal);
        LinkedList<Point> d1=allObjects(DSCell.__DSDispenser);
        LinkedList<Point> d2=allObjects(DSCell.__DSDispenser+1);
        LinkedList<Point> d3=allObjects(DSCell.__DSDispenser+2);

        HorseRider.inquire(TAG, "printGroup: "+"GGG : "+st1+"\nGoals at:"+goals+"\nD1 at:"+d1+"\nD2 at:"+d2+"\nD3 at:"+d3);
    }

    int getBusyMembersCount(int priority){
        int count=0;
        LinkedList<DSAgent> membersC = (LinkedList<DSAgent>)PMembers.clone();
        for(DSAgent agent:membersC)
            if(agent.isBusy(priority))
                count++;
            return(count);
    }

    /*
    public boolean isCapable(DSTask task,  int priority){
        LinkedList<Integer> types=task.getTypesNeeded();
        if(allObjects(DSCell.__DSGoal).size()==0)
            return(false);
        if ((PMembers.size() - getBusyMembersCount(priority))<types.size()) {
            return(false);
        }
        for(int i:types)
            if(allObjects(DSCell.__DSDispenser+i).size()==0)
                return(false);
        return(true);
    }
    */

    // is Capable funguje pro libovolnou velikost tasku
    public boolean isCapable(DSTask task,  int priority){
        LinkedList<Integer> types=task.getTypesNeeded();
        LinkedList goals=allObjects(DSCell.__DSGoal);
        if(goals==null)
            return(false);
        if(goals.size()==0)
            return(false);
        // ok, skupina zna nejakej plac pro submitnuti
        if ((PMembers.size() - getBusyMembersCount(priority))<types.size()) {
            return(false);
        }
        for(int i:types) {
            LinkedList dispensers=allObjects(DSCell.__DSDispenser + i);
            if (dispensers == null)
                return (false);
            if (dispensers.size() == 0)
                return (false);
        }
        return(true);
    }



    public synchronized boolean absorbGroup(DSGroup groupToAbsorb, Point displacement){

        // fromAgent se musi posunout o displacement, aby byl v koordinatech skupiny toAgenta
        // je treba toto zobecnit na posunuti mezi skupinama, tedy mastrama vyse uvedenych

       /* if(step<=PLastStep)
            return(false);
        PLastStep=step;*/
        if(groupToAbsorb==this)
            return(true);

         System.out.println("Skupina "+this.getMaster()+" absorbuje skupinu "+groupToAbsorb.getMaster()+" disp "+displacement);

        LinkedList<DSAgent> newMembers=groupToAbsorb.getMembers();
        Point originalDisplacement, newDisplacement;

        LinkedList<DSAgent> membersCl=(LinkedList<DSAgent>)newMembers.clone();

        for(DSAgent newMember:membersCl){
            PMembers.add(newMember);
            PGroupMap.addAgent(newMember,displacement);
            newMember.setGroup(this);
            System.out.println("addAgent "+newMember+" disp "+displacement);

            newMember.setGroup(this);
        }
        PMaster.getCommander().groupRemoved(groupToAbsorb);
        PMaster.getCommander().groupExtendedBy(this,groupToAbsorb);
   //     group.getMap().printMap(group.PMaster.getEntityName());
   //     PGroupMap.printMap(PMaster.getEntityName());

        PGroupMap.mergeMaps(groupToAbsorb.getMap(),displacement);
   //     PGroupMap.printMap(PMaster.getEntityName());
        return(true);
    }

    public DSGroup(DSAgent agent){
        PMaster=agent;
        Random random=new Random();
        PMembers=new LinkedList<DSAgent>();
        PMembers.add(agent);
        PGoalAreas= new HashMap<DSTask, Point>();
        PGroupMap=new DSMap(PMaster);
        PMasterGroup=false;
    }
}
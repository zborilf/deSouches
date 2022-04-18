package dsAgents.dsPerceptionModule;

import dsAgents.dsBeliefBase.DSBeliefBase;
import dsAgents.dsBeliefBase.dsBeliefs.DSBeliefsIndexes;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.*;
import dsMultiagent.dsTasks.DSTask;
import dsAgents.dsPerceptionModule.dsSyntax.DSPercepts;
import eis.PerceptUpdate;
import eis.iilang.*;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class DSPerceptor {


    private static final String TAG = "DSProcessPercepts";
    LinkedList<Point> PFriendsSeen;
    DSPercepts PPercepts;


    public boolean disabled(Collection<Percept> percepts){
        try {
            if (percepts.stream().filter(prc -> prc.getName().equals("deactivated")).iterator().
                    next().getParameters().get(0).toString().contentEquals("true"))
                return (true);
        }catch(Exception e){};
        return(false);
    }


    /*
            Friends (agents) seen
     */


    void clearFriendsList(){
        PFriendsSeen.clear();
    }


    public static Point getPositionFromDirection(String direction){
        Point directionPosition;
        int x=0, y=0;
        if(direction.contentEquals("n"))
            y=-1;
        else
        if(direction.contentEquals("s"))
            y=1;
        else
        if(direction.contentEquals("w"))
            x=-1;
        else
        if(direction.contentEquals("e"))
            x=1;
        return(new Point(x,y));
    }

    public static String getDirectionFromPosition(Point position){
        if(position.x==0) {
            if (position.y == 1)
                return ("s");
            if (position.y == -1)
                return ("n");
        }
        if(position.y==0) {
            if (position.x == 1)
                return ("e");
            if (position.x == -1)
                return ("w");
        }
        return("");
    }

    public boolean seesBlocksInBody(Collection<Percept> percepts){
        Iterator<Percept> perceptI = percepts.stream().filter(prc -> prc.getName().equals("attached")).iterator();
        return(perceptI.hasNext());
    }

    public DSBody getBodyFromPercepts(Collection<Percept> percepts) {
        Iterator<Percept> perceptI = percepts.stream().filter(prc -> prc.getName().equals("attached")).iterator(); //.findFirst().get();
        Percept percept;
        DSBody body=new DSBody();
        while (perceptI.hasNext()) {
            percept = perceptI.next();

        Point pp=new Point(Integer.parseInt(percept.getParameters().get(0).toString()),
        Integer.parseInt(percept.getParameters().get(1).toString()));
            System.out.println("body part " + pp);

//            body.addCell(new DSCell(percept.))
        }
        return(null);
    }

    public LinkedList<DSTask> getTasksFromPercepts(Collection<Percept> percepts){
        Iterator<Percept> perceptI;
        LinkedList<DSTask> taskList=new LinkedList<DSTask>();
        LinkedList<Integer> typesNeeded;
        Percept percept;
        DSTask task;
        String name;
        int deadline;
        int reward;
        DSBody body;
        DSCell cell;
        ParameterList parameters;

        perceptI=percepts.stream().filter(prc -> prc.getName().equals("task")).iterator(); //.findFirst().get();
        while(perceptI.hasNext()) {
            percept=perceptI.next();
            name=percept.getParameters().get(0).toString();
            deadline=Integer.parseInt(percept.getParameters().get(1).toString());
            reward=Integer.parseInt(percept.getParameters().get(2).toString());
            parameters=(ParameterList)percept.getParameters().get(3);
            body=new DSBody();
            typesNeeded=new LinkedList<Integer>();
            for(Parameter parameter:parameters){
                Function pl=(Function)parameter;
                int x = Integer.parseInt(((Function) parameter).getParameters().get(0).toString());
                int y = Integer.parseInt(((Function) parameter).getParameters().get(1).toString());
                String typeS = ((Function) parameter).getParameters().get(2).toString();
                int type=PPercepts.blockTypeByName(typeS);
                cell=new DSCell( x, y, type+DSCell.__DSBlock, 0);
                if(Math.abs(x)+Math.abs(y)==1) {
                    body.insertFirstCell(cell);
                    typesNeeded.add(0,type);
                }
                else {
                    body.addCell(cell);
             //       typesNeeded.add(type);
                }
            }
            task=new DSTask(name,deadline,reward,body);//,typesNeeded);
            taskList.add(task);

        }
        return(taskList);
    }


    synchronized public boolean actualizeMap(DSMap map, DSAgentOutlook outlook, Point agentPos, int vision,
                                String PTeamName, int step) {

        clearFriendsList();
        DSCells newOutlook=outlook.getCells();

        // clear map -> seen empty, clear it, if it is older than step

        LinkedList<DSCell> cells;

        for(int i = -vision;i <= vision; i++)
            for (int j = -vision+Math.abs(i); Math.abs(j) + Math.abs(i) <= vision; j++) {
                if(Math.abs(i)+Math.abs(j)<=vision) {
                    map.removeOlder(new Point(i + agentPos.x, j + agentPos.y), step,
                            (i==0)&&(j==0));
                    cells=newOutlook.getAllAt(new Point(i, j));
                    if(cells!=null)
                        for(DSCell cell:cells){
                            DSCell newCell=new DSCell(cell.getX()+agentPos.x,cell.getY()+agentPos.y,
                                    cell.getType(),cell.getTimestamp());
                            map.updateCell(newCell);
                    }
                }
            }
   return(true);
    }


    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    *
    *       Update BB for percepts
    */


    public static void processPercepts(DSBeliefBase BB, PerceptUpdate percepts){


/*
     for delete list
 */

        Iterator<Percept> newDeletePercepts=percepts.getDeleteList().iterator();

        Percept percept;
        String perceptName;
        Collection<Parameter> perceptParams;

        while(newDeletePercepts.hasNext()) {
            percept=newDeletePercepts.next();
            perceptName=percept.getName();
            perceptParams=percept.getParameters();

            switch(DSBeliefsIndexes.getIndex(perceptName)){

                case DSBeliefsIndexes.__thing:
                    BB.deleteThingFromOutlook(perceptParams);
                    break;

                case DSBeliefsIndexes.__name:
                    break;
                case DSBeliefsIndexes.__team:
                    break;
                case DSBeliefsIndexes.__teamSize:
                    break;
                case DSBeliefsIndexes.__steps:
                    break;
                case DSBeliefsIndexes.__step:
                    break;

                case DSBeliefsIndexes.__roleZone:
                    BB.leavesRoleZone(perceptParams);
                    break;

                case DSBeliefsIndexes.__goalZone:
                    BB.leavesGoalZone(perceptParams);
                    break;

                case DSBeliefsIndexes.__lastAction:
                    break;
                case DSBeliefsIndexes.__lastActionParams:
                    break;

                case DSBeliefsIndexes.__lastActionResult:
                    break;
                case DSBeliefsIndexes.__energy:
                    break;
                case DSBeliefsIndexes.__score:

            }
        }


        Iterator<Percept> newAddPercepts=percepts.getAddList().iterator();

      //  System.out.println(newAddPercepts);

        while(newAddPercepts.hasNext()) {
            percept=newAddPercepts.next();
            perceptName=percept.getName();
            perceptParams=percept.getParameters();

            switch(DSBeliefsIndexes.getIndex(perceptName)){

                case DSBeliefsIndexes.__thing:
                    BB.addThingToOutlook(perceptParams);
                    break;

                case DSBeliefsIndexes.__name:
                    BB.setName(perceptParams);
                    break;
                case DSBeliefsIndexes.__team:
                    break;
                case DSBeliefsIndexes.__teamSize:
                    BB.setTeamSize(perceptParams);
                    break;

                case DSBeliefsIndexes.__steps:

                case DSBeliefsIndexes.__role:
                    BB.processRole(perceptParams);
                    break;

                case DSBeliefsIndexes.__roleZone:
                    BB.standsAtRoleZone(perceptParams);
                    break;

                case DSBeliefsIndexes.__goalZone:
                    BB.standsAtGoalZone(perceptParams);
                break;

                case DSBeliefsIndexes.__step:
                    BB.setStep(perceptParams);
                    break;

                case DSBeliefsIndexes.__lastAction:
                    BB.setLastAction(perceptParams);
                    break;

                    case DSBeliefsIndexes.__lastActionParams:
                        BB.setLastActionParams(perceptParams);
                        break;


                    case DSBeliefsIndexes.__lastActionResult:
                        BB.setLastActionResult(perceptParams);
                        break;

                case DSBeliefsIndexes.__energy:
                    BB.setEnergy(perceptParams);
                    break;

                case DSBeliefsIndexes.__score:

            }
        }


        if(BB.getGUIFocus()) {
            String outlookString = BB.getOutlook().stringOutlook(BB.getVision(), BB.getName());
            BB.getGUI().textMapClear();
            BB.getGUI().writeTextOutlook("OUTLOOK:\n" + outlookString);
        }
    }



    public DSPerceptor(){
        PPercepts=new DSPercepts();
        PFriendsSeen=new LinkedList<Point>();

    }

}

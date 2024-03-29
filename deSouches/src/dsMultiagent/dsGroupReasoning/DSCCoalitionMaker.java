package dsMultiagent.dsGroupReasoning;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class DSCCoalitionMaker {
    ArrayList<DSCCoalition> PCoalitions=new ArrayList<DSCCoalition>();


    final static ArrayList<Point> dispensersB1 =
            new ArrayList(Arrays.asList(
                    new Point(17, 5),
                    new Point(33, 2),
                    new Point(16, 12),
                    new Point(60, 16),
                    new Point(17, 44),
                    new Point(23, 50),
                    new Point(41, 51),
                    new Point(17, 61),
                    new Point(39, 63)
            ));

    final static ArrayList<Point> dispensersB2 =
            new ArrayList(Arrays.asList(
                    new Point(45, 4),
                    new Point(31, 14),
                    new Point(47, 18),
                    new Point(1, 31),
                    new Point(30, 35),
                    new Point(54, 29),
                    new Point(19, 42)
            ));


    final static  ArrayList<Point> dispensersB3 =
            new ArrayList(Arrays.asList(
                    new Point(7, 6),
                    new Point(44, 17),
                    new Point(18, 33),
                    new Point(38, 31),
                    new Point(50, 42),
                    new Point(32, 51),
                    new Point(2, 63)
            ));


    final static ArrayList<ArrayList<Point>> tasks =
            new ArrayList(Arrays.asList(
                    dispensersB1,
                    dispensersB2,
                    dispensersB3
            ));


    final static ArrayList<Point> goals =
            new ArrayList(Arrays.asList(
                    new Point(26, 2),
                    new Point(28, 17),
                    new Point(43, 37)
            ));


    final static ArrayList<Point> workers =
            new ArrayList(Arrays.asList(
                    new Point(20, 4),
                    new Point(3, 2),
                    new Point(38, 9),
                    new Point(34, 31),
                    new Point(52, 35),
                    new Point(69, 40),
                    new Point(42, 53),
                    new Point(23, 74),
                    new Point(37, 77),
                    new Point(84, 85)
            ));



    /*
            find optimal coallition
     */


    public int distanceManhattan(Point a, Point b){
        return(Math.abs(a.x-b.x)+Math.abs(a.y-b.y));
    }

    public int computePrice(DSMap map, Point start, Point through, Point end){
        int d1=map.distance(start,through);
        int d2=map.distance(through,end);
        return(d1+ d2);
    }

    public synchronized ArrayList<DSCCoalition> proposeTaskCoallitions(
            LinkedList<DSAgent> agents,
            LinkedList<LinkedList<Point>> subtasks,
            LinkedList<Point> goals,
            int notLongerThan
    )       // void je prozatim
    {
        ArrayList<Point> agentPositions = new ArrayList<Point>();
        for (DSAgent agent : agents) {
            agentPositions.add(agent.getMapPosition());
        }

        int noSubtasks = subtasks.size();
        DSCCoalitionStructures coalition = new DSCCoalitionStructures(noSubtasks);
        DSCCoalitionMember taskItem;
        Point bestDispenser;
        DSCCoalition coal = null;
        ArrayList<DSCCoalition> coalitions = new ArrayList<DSCCoalition>();

        int bestPrice = -1;
        int price;
        int subtaskNumber = 1;
        for (Point worker : agentPositions)
            for (Point goal : goals) {
                subtaskNumber = 1;
                for (LinkedList<Point> subtask : subtasks) {
                    bestDispenser = subtask.get(0);
                    for (Point dispenser : subtask) {
                        price = computePrice(agents.getFirst().getMap(), worker, dispenser, goal);
                        if ((bestPrice == -1) || (price < bestPrice)) {
                            bestPrice = price;
                            bestDispenser = dispenser;
                        }
                    }

                    DSAgent workerAgent=null;

                    // find agent from 'agents' by position 'worker'
                    for(DSAgent workerAgent2:agents){
                        if((workerAgent2.getMapPosition().getX()==worker.x)&&
                                (workerAgent2.getMapPosition().getY()==worker.y))
                            workerAgent=workerAgent2;
                    }

                    if(bestPrice<notLongerThan) {
                        taskItem = new DSCCoalitionMember(subtaskNumber, workerAgent, worker, bestDispenser, goal, bestPrice);
                        coalition.addToTasks(taskItem);
                    }

                    bestPrice = -1;
                    subtaskNumber++;
                }
                subtaskNumber = 1;
            }


        ArrayList<DSCCoalitionMember> tasks=coalition.getTasks();

        for(DSCCoalitionMember task: tasks) {

            // brand new one with the new tasks

            coal = new DSCCoalition(noSubtasks, task); // TEMP
            coalitions.add(coal);
            if(coal.completeCoalition()) // one block tasks
                PCoalitions.add(coal);

            // try to add task to every existing coals
            for (DSCCoalition coal2 : coalitions) {
                if (coal2.addMember(task))       // coal extended
                    if (coal2.completeCoalition())   // and become complete
                        PCoalitions.add(coal2);
            }
        }

        return(PCoalitions);
    }


}

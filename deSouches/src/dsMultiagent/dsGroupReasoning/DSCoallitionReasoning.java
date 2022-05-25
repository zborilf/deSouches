package dsMultiagent.dsGroupReasoning;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import dsMultiagent.DSGroup;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

import static dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap.distance;

public class DSCoallitionReasoning {

    static int distanceThrough(Point to, Point from, Point through){
        return(DSMap.distance(from, through)+ DSMap.distance(through, to));
    }

    public synchronized static LinkedList<HashMap<DSAgent, Integer>>
             proposeTaskCoallition(LinkedList<DSAgent> workers,
                            LinkedList<LinkedList<Point>> dispensersForTypes,
                                LinkedList<LinkedList<Point>> goalZones) {
        /* for the mastergroup and given types it finds optimal agent group
            at least one worker/constructor for the first type
         */

        // take the first from each goal zone

        int nOTasks=dispensersForTypes.size();
        int nOAgents=workers.size();
        int nOGoals=goalZones.size();

        // goals - tasks - agents price matrix
        int[][][] gTAPriceMatrix= new int[nOGoals][nOTasks][nOAgents];

        System.out.println();
        System.out.println("Hledam koalici");
        for(LinkedList<Point> goalZone:goalZones){
            Point goal=goalZone.getFirst();
            // and compute the best coalition
            // CZ            kazdemu agenty pripradim opt. cenu pro kazdy typ (max 8*4 ) k danemu cili
            for(DSAgent agent:workers){
                Point agentLocation=agent.getMapPosition();
                for(LinkedList<Point> dispensersForType:dispensersForTypes) {
                    int bestDistance = -1;
                    for (Point dispenserLocation : dispensersForType) {
                        int distance = distanceThrough(goal, agentLocation, dispenserLocation);
                        System.out.println("Agent " + agent.getEntityName() + " from " + agentLocation.toString() + " through " +
                                dispenserLocation.toString() + " to " + goal.toString() + " is " + distance);
                        if ((bestDistance == -1) || (distance < bestDistance))
                            bestDistance = distance;
                    }
                    System.out.println("Best distance is " + bestDistance);
                }
            }
        }

        return(null);
    }

}

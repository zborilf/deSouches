package dsMultiagent;

import dsAgents.DSAgent;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;


public class DSSynchronize {

    private static final String TAG = "DSSynchronize";

    HashMap<Integer, DSFriendsSeen> PObservations;
    int PPopulationSize;
//    int[][] PSynchronizedAgents;

    class DSFriendsSeen{
        HashMap<DSAgent,LinkedList<Point>> PFriendsSeen;

        // agent at agG1Pos in S1 sees agent at agG2Ois in S2 at agDisplacement
        // return displacement between S1 and S2 as

        /*
        Princip synchronizace na mapě
        Vstup. Agent a1 (pozorovatel) v soustavě S1 má pozici [x1,y1] a agent a2 v soustavě S2 má pozici [x2’,y2’].
        V reálném prostředí je agent a1 vzdálen od agenta a2 o [Dx,Dy].  O kolik se musí posunout pozice v soustavě S2,
        aby odpovídala soustavě S1?
        Řešení. Pokud se agent a1 posune na pozici [x1+Dx, x2+Dy], pak bude na stejné fyzické pozici. Proto pokud se má
        soustava posunout o nějaké [Dsx,Dsy] tak, aby odpovídala soustavě S1, pak musí platit vztah
        x1+Dx=x2+Dsx a y1+Dy=y2’+Dsy. Proto Dsx=x1+Dx-x2’ a Dsy=y1+Dy-y2’. Což je posunutí [x1+Dx-x2’, y1+Dy-y2’].
        */

        Point getGroupDisplacement(Point agG1Pos, Point agG2Pos, Point agDisplacement){
            return(new Point((int)(agG1Pos.getX()+agDisplacement.x- agG2Pos.getX()),
                    (int)(agG1Pos.getY()+agDisplacement.y- agG2Pos.getY())));
        }

        void synchronizeAgents(){
            LinkedList<Point> observation, observation2;
            DSAgent fa;
            Point np;
            int noMatches=0;

            for(DSAgent agent1:PFriendsSeen.keySet()) {
                observation = PFriendsSeen.get(agent1);
                for (Point p : observation) {
                    np = new Point(-p.x, -p.y);
                    fa=null;
                    if((np.x!=0)||(np.y!=0)) {
                        noMatches = 0;
                        for (DSAgent agent2 : PFriendsSeen.keySet()) {
                            if (agent1 != agent2) {
                                observation2 = PFriendsSeen.get(agent2);
                                for (Point p2 : observation2)
                                    if (((np.x == p2.x) && (np.y == p2.y)) && ((np.x != 0) || (np.y != 0))) {
                                        noMatches++;
                                        fa=agent2;
                                    }
                            }
                        }
                        if(noMatches==1)
                            // absorbuje bud mastergrupa, nebo z nemastergrup ta s mensim ID sveho mastera
                        if((agent1.getGroup().isMasterGroup())||
                                ((!fa.getGroup().isMasterGroup())&&(agent1.getGroup().getNumber()<fa.getGroup().getNumber())))
                        {
                            Point displacement=getGroupDisplacement(agent1.getPosition(), fa.getPosition(), new Point(p.x,p.y));

                            agent1.getGroup().absorbGroup(fa.getGroup(),displacement);
                        }
                    }
                }
            }
        }


        void addFriends(DSAgent agent, LinkedList<Point> friends){
            PFriendsSeen.put(agent,friends);
        }

        boolean isComplete(int size){
            return(PFriendsSeen.keySet().size()==size);
        }

        DSFriendsSeen(){
            PFriendsSeen=new HashMap<DSAgent,LinkedList<Point>>();
        }
    }

    public synchronized LinkedList addObservation(DSAgent agent, int step, LinkedList<Point>observation, int teamSize){
        DSFriendsSeen dsfs=PObservations.get(step);
        if(dsfs==null)
            dsfs=new DSFriendsSeen();
        // agent vidi pratele na vzdalenosti observation
        dsfs.addFriends(agent,observation);
        PObservations.put(step,dsfs);
        if(dsfs.isComplete(teamSize))
            dsfs.synchronizeAgents();

        return(null);
    }


    public DSSynchronize(){
                PObservations = new HashMap<Integer, DSFriendsSeen>();
    }
}

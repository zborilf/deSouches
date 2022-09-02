package dsMultiagent;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import java.awt.*;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;

import static dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell.*;

public class DSSynchronize {
  private static final String TAG = "DSSynchronize";

  HashMap<Integer, DSFriendsSeen> PObservations = new HashMap<>();
  DSMap PMasterMap;

  DSGroup PMasterGroup;

  public DSGroup getMasterGroup() {
    return (PMasterGroup);
  }

/*
      Estimates size of the area by dispensers positions
 */

  LinkedList<LinkedList<Point>> divideByRowsColumns(LinkedList<Point> points, boolean row){
    LinkedList<LinkedList<Point>> inRCPointsList=new LinkedList<LinkedList<Point>>();
    LinkedList<Point> pointsC=(LinkedList<Point>)points.clone();
    LinkedList<Point> inRCPoints;
    while(!pointsC.isEmpty()){
      int rc;
      if(row)
        rc=pointsC.getFirst().y;
      else
        rc=pointsC.getFirst().x;
      inRCPoints=new LinkedList<Point>();
      for(Point point:pointsC)
        if((row&&(point.y==rc))||((!row&&(point.x==rc))))
          inRCPoints.add(point);

        inRCPointsList.add(inRCPoints);

        for(Point point:inRCPoints)
          pointsC.remove(point);
    }
    return(inRCPointsList);
  }

  HashMap<Integer, Integer> estimateSize(LinkedList<Point> points, HashMap<Integer, Integer> estimations, boolean width){
    LinkedList<LinkedList<Point>> dividedPoints;
    dividedPoints=divideByRowsColumns(points, width);
    for(LinkedList<Point> pointsInRow:dividedPoints)
      if(pointsInRow.size()>1) {
       int we;
       if(width)
         we=Math.abs(pointsInRow.get(0).x - pointsInRow.get(1).x);
        else
          we=Math.abs(pointsInRow.get(0).y - pointsInRow.get(1).y);
        if(we>0)  // avoit double occurence of the same in dscells
          if(estimations.containsKey(we)) {
           int count=estimations.get(we);
           count++;
           estimations.put(we, count);
        }
       else
         estimations.put(we,1);
      }
      return(estimations);
  }

  public int getBiggest(HashMap<Integer, Integer> estimations,int mincount){
    int size=0;
    mincount--;
    for(int es:estimations.keySet())
      if(estimations.get(es)>mincount){
        size=es;
        mincount=estimations.get(es);
      }
    return(size);
  }

  public synchronized void estimateSize(){
    HashMap<Integer, Integer> estimationsW=new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> estimationsH=new HashMap<Integer, Integer>();
    LinkedList<Point> d1= PMasterGroup.getMap().getTypePositions(DSCell.__DSDispenser + 0);
    estimationsW=estimateSize(d1,estimationsW,true);
    estimationsH=estimateSize(d1,estimationsH,false);
    d1=PMasterGroup.getMap().getTypePositions(DSCell.__DSDispenser + 1);
    estimationsW=estimateSize(d1,estimationsW,true);
    estimationsH=estimateSize(d1,estimationsH,false);


    PMasterGroup.getMap().setWidth(getBiggest(estimationsW,3));
    PMasterGroup.getMap().setHeight(getBiggest(estimationsH,3));

  }

  public boolean masterGroupCandidate(DSGroup group) {
    // chceck whether this group is good enaugh to be
    // when NOAgents > 5 TODO ... udelat chytreji
    if(PMasterGroup!=null)
      return(false);

    int ga = group.getMap().getMapCells().getAllType(__DSGoalArea).size();
    int za = group.getMap().getMapCells().getAllType(__DSRoleArea).size();
    int d0 = group.getMap().getMapCells().getAllType(__DSDispenser + 0).size();
    int d1 = group.getMap().getMapCells().getAllType(__DSDispenser + 1).size();
    int d2 = group.getMap().getMapCells().getAllType(__DSDispenser + 2).size();

    if (((ga * za * d0) > 0) && (group.getMembers().size() > 5)) {
      PMasterGroup = group;
      group.setMasterGroup();
      return(true);
    }

    return (false);
  }

  class DSFriendsSeen {
    HashMap<DSAgent, LinkedList<Point>> PFriendsSeen = new HashMap<>();

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



    Point getGroupDisplacement(Point agG1Pos, Point agG2Pos, Point agDisplacement) {
      return (new Point(
              (int) (agG1Pos.getX() + agDisplacement.x - agG2Pos.getX()),
              (int) (agG1Pos.getY() + agDisplacement.y - agG2Pos.getY())));
    }


    int mn = 0;


    synchronized void synchronizeAgents(FileWriter output) {
      LinkedList<DSAgent> agentList;
      HashMap<Point, LinkedList<DSAgent>> table = new HashMap<Point, LinkedList<DSAgent>>();
      DSAgent agent1, agent2;


      for (DSAgent a : PFriendsSeen.keySet()) {
        for (Point point : PFriendsSeen.get(a)) {
          if((point.x!=0)||(point.y!=0))
            if (table.containsKey(point)) {
              agentList = table.get(point);
              agentList.add(a);
            } else {
              agentList = new LinkedList<DSAgent>();
              agentList.add(a);
              table.put(point, agentList);
           }
        }
      }

      boolean token=true;

      LinkedList<Point> pointsClone = new LinkedList<Point>();
      for (Point p : table.keySet())
        pointsClone.add(p);
      for (Point p : pointsClone)
        if (table.containsKey(p)) {
          if (table.get(p).size() > 1)
            table.remove(p);
          else {
            Point np = new Point(-p.x, -p.y);
            if (table.containsKey(np)) {
              // odpovidajici protikus existuje, sloucime do puvodni a protikus odstranime



              agent1 = table.get(p).getFirst();
              agent2 = table.get(np).getFirst();

              if ((agent1.getGroup() != agent2.getGroup()))
              // absorbuje bud mastergrupa, nebo z nemastergrup ta s mensim ID sveho mastera
              {
                if ((agent1.getGroup().isMasterGroup())
                        || ((!agent2.getGroup().isMasterGroup())
                        && (agent1.getGroup().getNumber() < agent2.getGroup().getNumber()))) {
                  Point displacement =
                          getGroupDisplacement(
                                  agent1.getMapPosition(), agent2.getMapPosition(), new Point(p.x, p.y));

                  try {
                    output.write(">>" + agent1.getEntityName() + " sees " + agent2.getEntityName() + " disp " + displacement +
                            " token "+token+"\n");
                    output.flush();
                  } catch (Exception e) {
                  };

                  if(token) {
                    agent1.getGroup().absorbGroup(agent2.getGroup(), displacement, output);
                    token=false;
                  }      // for sure, only one sync per round // TODO ... opravdu je to nutne?

                }


              }
            }
          }
        }
    }


    void addFriends(DSAgent agent, LinkedList<Point> friends) {
      PFriendsSeen.put(agent, friends);
    }


    synchronized boolean isComplete(int size) {
      return (PFriendsSeen.keySet().size() == size);
    }

  }

    // observations for agent synchronization
    public synchronized boolean addObservation(
            FileWriter output,
            DSAgent agent, int step, LinkedList<Point> observation, int teamSize) {

      DSFriendsSeen dsfs = PObservations.get(step);
      if (dsfs == null) dsfs = new DSFriendsSeen();
      // agent vidi pratele na vzdalenosti observation
      dsfs.addFriends(agent, observation);
      PObservations.put(step, dsfs);


      if (dsfs.isComplete(teamSize)) {
        if(step>0)    // bastl. pro nulu to buhviproc blblo
           dsfs.synchronizeAgents(output);
        return (true);
      }
      return (false);
    }


}


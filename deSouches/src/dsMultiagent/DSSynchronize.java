package dsMultiagent;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import java.awt.*;
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

    boolean masterGroupCandidate(DSGroup group){
      // chceck whether this group is good enaugh to be
      // when NOAgents > 5 TODO ... udelat chytreji
      int ga=group.getMap().getMap().getAllType(__DSGoalArea).size();
      int za=group.getMap().getMap().getAllType(__DSRoleArea).size();
      int d0=group.getMap().getMap().getAllType(__DSDispenser+0).size();
      int d1=group.getMap().getMap().getAllType(__DSDispenser+1).size();
      int d2=group.getMap().getMap().getAllType(__DSDispenser+2).size();

      if(((ga*za*d0*d1*d2)>0) && (group.getMembers().size()>5)) {
        PMasterGroup=group;
        group.setMasterGroup();
      }

      return(false);
    }

    Point getGroupDisplacement(Point agG1Pos, Point agG2Pos, Point agDisplacement) {
      return (new Point(
              (int) (agG1Pos.getX() + agDisplacement.x - agG2Pos.getX()),
              (int) (agG1Pos.getY() + agDisplacement.y - agG2Pos.getY())));
    }


    synchronized void synchronizeAgents() {
      final int LOWESTVISION = 5;

      // higher probability of available info

      try {
        wait(50);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }


      for (DSAgent agent1 : PFriendsSeen.keySet()) {
        LinkedList<Point> observation = PFriendsSeen.get(agent1);
        for (Point p : observation) {
          if ((p.x == 0) && (p.y == 0)) {
            continue; // self
          }
          Point np = new Point(-p.x, -p.y);
          DSAgent fa = null;
          int noMatches = 0;
          for (DSAgent agent2 : PFriendsSeen.keySet()) {
            if (agent1 == agent2) {
              continue;
            }

            LinkedList<Point> reverseObserv = PFriendsSeen.get(agent2);
            // must match all observations within his range
            boolean match =
                    observation.stream()
                            .filter(x -> DSMap.distance(p, x) <= LOWESTVISION)
                            .allMatch(obs -> reverseObserv.contains(new Point(obs.x - p.x, obs.y - p.y)));
            boolean reversematch =
                    reverseObserv.stream()
                            .filter(x -> DSMap.distance(np, x) <= LOWESTVISION)
                            .allMatch(obs -> observation.contains(new Point(obs.x + p.x, obs.y + p.y)));
            if (match && reversematch) {
              noMatches++;
              fa = agent2;
            }
          }

          if ((noMatches == 1) && (agent1.getGroup() != fa.getGroup()))
          // absorbuje bud mastergrupa, nebo z nemastergrup ta s mensim ID sveho mastera
          {
            if ((agent1.getGroup().isMasterGroup())
                    || ((!fa.getGroup().isMasterGroup())
                    && (agent1.getGroup().getNumber() < fa.getGroup().getNumber()))) {
              Point displacement =
                      getGroupDisplacement(
                              agent1.getMapPosition(), fa.getMapPosition(), new Point(p.x, p.y));
              agent1.getGroup().absorbGroup(fa.getGroup(), displacement);
              if(PMasterGroup==null)
                masterGroupCandidate(agent1.getGroup());
            }
          } else if ((noMatches == 1)
                  && (agent1.getGroup() == fa.getGroup())
                  && DSMap.distance(agent1.getMapPosition(), fa.getMapPosition()) > LOWESTVISION) {
            System.err.println(
                    "ODHAD VELIKOST MAPY x:"
                            + Math.abs(agent1.getMapPosition().x - fa.getMapPosition().x)
                            + "+"
                            + p.x
                            + " Y: "
                            + Math.abs(agent1.getMapPosition().y - fa.getMapPosition().y)
                            + "+"
                            + p.y);
          }
        }
      }
    }



    void addFriends(DSAgent agent, LinkedList<Point> friends) {
      PFriendsSeen.put(agent, friends);
    }

    boolean isComplete(int size) {
      return (PFriendsSeen.keySet().size() == size);
    }
  }

  // observations for agent synchronization
  public synchronized boolean addObservation(
      DSAgent agent, int step, LinkedList<Point> observation, int teamSize) {

    DSFriendsSeen dsfs = PObservations.get(step);
    if (dsfs == null) dsfs = new DSFriendsSeen();
    // agent vidi pratele na vzdalenosti observation
    dsfs.addFriends(agent, observation);
    PObservations.put(step, dsfs);


    if (dsfs.isComplete(teamSize)) {
      dsfs.synchronizeAgents();
      return(true);
    }
    return(false);
  }


  /*

  public synchronized DSMap synchronizePosition(
      DSAgent agent, int absX, int absY) { // agent nastavi jako PMAp
    int oldX = agent.getMapPosition().x;
    int oldY = agent.getMapPosition().y;
    Point displacement = new Point(absX - oldX, absY - oldY);

    DSMap map = agent.getMap();

    if (PMasterMap == null) {
      map.setMasterMap();
      PMasterMap = map;
      PMasterGroup = agent.getGroup();
      PMasterGroup.setMasterGroup();

      System.out.println(
          " Delam mastermapu " + agent.getEntityName() + " krok " + agent.getStep() + "\n");

      if ((displacement.x == 0) && (displacement.y == 0)) return (agent.getMap());
      map.shiftMap(displacement);

      return (map);
    }

    if (PMasterMap == map) {
      if ((displacement.x == 0) && (displacement.y == 0)) {
        return (map);
      }
      map.setAgentPos(agent, new Point(absX, absY));
      return (map);
    }

    map.shiftMap(displacement);

    System.out.println(" Pridavam do mastermapy  ");
    agent.getGroup().printGroup();
    System.out.println(" step " + agent.getStep() + "\n");

    PMasterGroup.absorbGroup(agent.getGroup(), new Point(0, 0)); // already shifted
    return (PMasterMap);
  }

   */


}

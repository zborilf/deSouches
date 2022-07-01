package dsMultiagent;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

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

    Point getGroupDisplacement(Point agG1Pos, Point agG2Pos, Point agDisplacement) {
      return (new Point(
          (int) (agG1Pos.getX() + agDisplacement.x - agG2Pos.getX()),
          (int) (agG1Pos.getY() + agDisplacement.y - agG2Pos.getY())));
    }

    synchronized void synchronizeAgents() {
      LinkedList<Point> observation, observation2;
      DSAgent fa;
      Point np;
      int noMatches = 0;

      for (DSAgent agent1 : PFriendsSeen.keySet()) {
        observation = PFriendsSeen.get(agent1);
        for (Point p : observation) {
          np = new Point(-p.x, -p.y);
          fa = null;
          if ((np.x != 0) || (np.y != 0)) {
            noMatches = 0;
            for (DSAgent agent2 : PFriendsSeen.keySet()) {
              if (agent1 != agent2) {
                observation2 = PFriendsSeen.get(agent2);
                for (Point p2 : observation2)
                  if (((np.x == p2.x) && (np.y == p2.y))) {
                    noMatches++;
                    fa = agent2;
                  }
              }
            }
            if ((noMatches == 1) && (agent1.getGroup() != fa.getGroup()))
            // absorbuje bud mastergrupa, nebo z nemastergrup ta s mensim ID sveho mastera
            {
              if ((agent1.getGroup().isMasterGroup())
                  || ((!fa.getGroup().isMasterGroup())
                      && (agent1.getGroup().getNumber() < fa.getGroup().getNumber()))) {
                System.err.println(
                    "MERGE: " + fa.getEntityName() + " a " + agent1.getEntityName() + " " + np);
                Point displacement =
                    getGroupDisplacement(
                        agent1.getMapPosition(), fa.getMapPosition(), new Point(p.x, p.y));
                agent1.getGroup().absorbGroup(fa.getGroup(), displacement);
              }
            } else if ((noMatches == 1)
                && (agent1.getGroup() == fa.getGroup())
                && DSMap.distance(agent1.getMapPosition(), fa.getMapPosition()) > 20) {
              System.err.println("PREKROCENI MAPY - porovnat seen/map vector");
              // prekorceni mapy TODO:l prodiskutovat co s tim (pokd nesedi x vektor nebo y vektor
              // prekroceni)
              // zatim blbe funguje synchronizace idk
            }
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
  public synchronized void addObservation(
      DSAgent agent, int step, LinkedList<Point> observation, int teamSize) {
    DSFriendsSeen dsfs = PObservations.get(step);
    if (dsfs == null) dsfs = new DSFriendsSeen();
    // agent vidi pratele na vzdalenosti observation
    dsfs.addFriends(agent, observation);
    PObservations.put(step, dsfs);
    if (dsfs.isComplete(teamSize)) dsfs.synchronizeAgents();
  }

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
}

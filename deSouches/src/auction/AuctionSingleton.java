package auction;

import dsAgents.DSAgent;
import dsMultiagent.DSGroup;
import java.awt.*;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionSingleton {
  private static volatile AuctionSingleton singleton;

  private AuctionSingleton() {}

  public static AuctionSingleton getInstance() {

    if (singleton == null) singleton = new AuctionSingleton();

    return singleton;
  }

  List<DSAgent> auctionParticipants = new CopyOnWriteArrayList<>();

  public synchronized void sellItem(DSAgent a, DSGroup group, Point p, int initialPrice) {
    // TODO:L necekane jen pro synchronizovane agenty

    int curPrice = initialPrice;
    DSAgent winingAgent = a;
    ListIterator<DSAgent> iter = auctionParticipants.listIterator();
    while (iter.hasNext()) {
      DSAgent curAgent = iter.next();

      // i can only communicate with agent within same group = same coordinates
      if (curAgent.getGroup() != group) continue;

      int curBid = curAgent.getAuctionBid(p);
      if (curBid > curPrice) {
        winingAgent = curAgent;
        curPrice = curBid;
      }
    }

    // auction is over
    // winingAgent.hearOrder()
    if (!winingAgent.roamList.contains(p)) {
      winingAgent.roamList.add(p);

      // sort for winning agent by distance TODO:l by whole path length
      DSAgent finalWiningAgent = winingAgent;
      finalWiningAgent.roamList.sort(
          (p1, p2) -> {
            int distanceP2 = (int) p2.distance(finalWiningAgent.getMapPosition());
            int distanceP1 = (int) p1.distance(finalWiningAgent.getMapPosition());

            return distanceP1 - distanceP2;
          });
    }
  }

  public void participateAuctions(DSAgent agent) {
    if (!auctionParticipants.contains(agent)) {
      auctionParticipants.add(agent);
    }
  }

  public void leaveAuctions(DSAgent agent) {
    if (auctionParticipants.contains(agent)) {
      auctionParticipants.remove(agent);
    }
  }
}

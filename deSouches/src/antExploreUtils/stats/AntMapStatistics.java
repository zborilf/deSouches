package antExploreUtils.stats;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCells;
import dsMultiagent.DSGroup;
import java.awt.*;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AntMapStatistics {
  // todo velikost mapy, pocet sektoru,
  public int completedStep = -1;
  private static final Logger loggerGroup = Logger.getLogger("loggerGroup");
  private static final Logger loggerCells = Logger.getLogger("loggerCells");
  private static final Logger loggerRevisited = Logger.getLogger("loggerRevisited");
  private static final Logger loggerMapSize = Logger.getLogger("loggerMapSize");
  private static final Formatter format =
      new Formatter() {
        @Override
        public String format(LogRecord record) {
          return record.getMessage() + "\n";
        }
      };

  static {
    try {

      // file output
      try {
        Path path = Paths.get("logs");
        Files.createDirectory(path);
      } catch (FileAlreadyExistsException ignored) {
      }

      ArrayList<FileHandler> files =
          new ArrayList<>(
              Arrays.asList(
                  new FileHandler("logs/usedGroups.csv"),
                  new FileHandler("logs/knownCells.csv"),
                  new FileHandler("logs/revisitedCells.csv"),
                  new FileHandler("logs/mapSize.csv")));

      files.forEach(x -> x.setFormatter(format));
      files.forEach(x -> x.setFormatter(format));

      // assign handlers and create headers
      loggerGroup.setUseParentHandlers(false);
      loggerGroup.addHandler(files.get(0));
      loggerGroup.info("step,active groups,all groups");
      loggerCells.setUseParentHandlers(false);
      loggerCells.addHandler(files.get(1));
      loggerCells.info("step,group,cells");
      loggerRevisited.setUseParentHandlers(false);
      loggerRevisited.addHandler(files.get(2));
      loggerRevisited.info(
          "step,group,average revisited time from available,revisited cell count,all cell count");
      loggerMapSize.setUseParentHandlers(false);
      loggerMapSize.addHandler(files.get(3));
      loggerMapSize.info("step,group,size");

    } catch (SecurityException | IOException e) {
      e.printStackTrace();
    }
  }

  public void numberOfUsedGroups(DSAgent agent, ConcurrentHashMap<DSGroup, Integer> agentStep) {

    int maintainedGroups = 0, allGroups = 0;
    for (DSGroup k : agentStep.keySet()) {
      allGroups++;
      if (!k.getMembers().isEmpty()) {
        maintainedGroups++;
      }
    }
    loggerGroup.info(agent.getStep() + ", " + maintainedGroups + ", " + allGroups);
  }

  private ArrayList<Integer> allKnownCellsByGroup(ConcurrentHashMap<DSGroup, Integer> agentStep) {
    ArrayList<Integer> known = new ArrayList<>();
    for (DSGroup g : agentStep.keySet()) {
      // filter unique points
      LinkedList<DSCell> allCells = g.getMap().getMap().getCells();

      // remove duplicate points ?
      HashSet<Point> uniqueSet = new HashSet<>();
      allCells.forEach(c -> uniqueSet.add(c.getPosition()));
      known.add(uniqueSet.size());
    }
    return known;
  }

  private ArrayList<Point> uniquePointForGroup(DSAgent agent) {
    LinkedList<DSCell> allCells = agent.getGroup().getMap().getMap().getCells();

    // remove duplicate points
    HashSet<Point> uniqueSet = new HashSet<>();
    allCells.forEach(c -> uniqueSet.add(c.getPosition()));
    return new ArrayList<>(uniqueSet);
  }

  public void knownCellsStats(DSAgent agent) {
    ArrayList<Point> knownListCount = uniquePointForGroup(agent);
    loggerCells.info(
        agent.getStep() + ", " + agent.getGroup() + ", " + (long) knownListCount.size());
  }

  public void revisitedStats(DSAgent agent) {
    int cellsRevisited = 0, avgSum = 0;
    ArrayList<Point> allPoints = uniquePointForGroup(agent);
    int allGroupCells = allPoints.size();

    // due to multithreading other agents may have deleted some cells
    for (Point c : allPoints) {
      int revisitTime = agent.getGroup().getMap().getMap().getRevisitAt(c);

      if (revisitTime != 0) {
        cellsRevisited++;
        avgSum += revisitTime;
      }
    }
    double avg = avgSum / (double) cellsRevisited;

    loggerRevisited.info(
        agent.getStep()
            + ", "
            + agent.getGroup()
            + ", "
            + avg
            + ", "
            + cellsRevisited
            + ", "
            + allGroupCells);
  }

  public void mapSizeStats(DSAgent agent) {
    DSCells groupMap = agent.getGroup().getMap().getMap();
    Point tlc = groupMap.getTLC();
    Point brc = groupMap.getBRC();
    int size = (brc.x - tlc.x) * (brc.y - tlc.y);

    loggerMapSize.info(agent.getStep() + ", " + agent.getGroup() + ", " + size);
  }
}

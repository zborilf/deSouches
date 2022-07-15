package antExploreUtils.stats;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCells;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSMap;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AntMapStatistics {
  public int completedStep = -1;
  private static final Logger loggerGroup = Logger.getLogger("loggerGroup");
  private static final Logger loggerCells = Logger.getLogger("loggerCells");
  private static final Logger loggerRevisited = Logger.getLogger("loggerRevisited");
  private static final Logger loggerMapSize = Logger.getLogger("loggerMapSize");

  private static final Logger loggerFinalBlocks = Logger.getLogger("loggerFinalBlocks");
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
                  new FileHandler("logs/mapSize.csv"),
                  new FileHandler("logs/finalBlocks.csv")));

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
      loggerFinalBlocks.setUseParentHandlers(false);
      loggerFinalBlocks.addHandler(files.get(4));
      loggerFinalBlocks.info("step,group,dispenser,role,task,goal");

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

  private int getDeposits(List<DSCell> cellList) {
    // i dotykem pres hranu pocitma jako jedno lozisko
    var clone = new ArrayList<>(cellList);

    int unique = 0;
    while (!clone.isEmpty()) {
      var current = clone.remove(0);

      int lastStep = 0;
      // var neighbours = new HashSet<>(clone.stream().filter(c ->
      // DSMap.distance(current.getPosition(), c.getPosition()) == 1).toList());
      var neighbours =
          new HashSet<>(
              clone.stream()
                  .filter(c -> current.getPosition().distance(c.getPosition()) < 2)
                  .toList());
      while (neighbours.size() != lastStep) {
        lastStep = neighbours.size();
        var newNeighs = new ArrayList<DSCell>();
        for (var cell : neighbours) {
          // newNeighs.addAll(clone.stream().filter(c -> DSMap.distance(cell.getPosition(),
          // c.getPosition()) == 1).toList());
          newNeighs.addAll(
              clone.stream()
                  .filter(c -> DSMap.distance(cell.getPosition(), c.getPosition()) < 2)
                  .toList());
        }
        neighbours.addAll(newNeighs);
      }
      clone.removeAll(neighbours);
      unique++;
    }
    return unique;
  }

  public void absoluteObjectsStats(DSAgent agent) {
    var cells = agent.getGroup().getMap().getMap().getCells();
    final long dispensers = cells.stream().filter(c -> c.getType() >= DSCell.__DSDispenser).count();
    var roleList = cells.stream().filter(c -> c.getType() == DSCell.__DSRoleArea).toList();
    final long roles = getDeposits(roleList);
    var taskList = cells.stream().filter(c -> c.getType() == DSCell.__DSTaskArea).toList();
    final long tasks = getDeposits(taskList);
    var goalList = cells.stream().filter(c -> c.getType() == DSCell.__DSGoalArea).toList();
    final long goals = getDeposits(goalList);

    loggerFinalBlocks.info(
        agent.getStep()
            + ", "
            + agent.getGroup()
            + ", "
            + dispensers
            + ", "
            + roles
            + ", "
            + tasks
            + ", "
            + goals);
  }

  public void mapSizeStats(DSAgent agent) {
    DSCells groupMap = agent.getGroup().getMap().getMap();
    Point tlc = groupMap.getTLC();
    Point brc = groupMap.getBRC();
    int size = (brc.x - tlc.x) * (brc.y - tlc.y);

    loggerMapSize.info(agent.getStep() + ", " + agent.getGroup() + ", " + size);
  }
}

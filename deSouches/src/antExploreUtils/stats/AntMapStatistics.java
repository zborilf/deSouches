package antExploreUtils.stats;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;
import dsMultiagent.DSGroup;
import java.awt.*;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AntMapStatistics {
  // todo velikost mapy, pocet sektoru,
  public int completedStep = -1;
  private static Logger logger = Logger.getLogger("AntMapStatistics");
  private static final Formatter format =
      new Formatter() {
        @Override
        public String format(LogRecord record) {
          DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
          return "["
              + formatter.format(new Date(record.getMillis()))
              + "] "
              + record.getLevel()
              + ": "
              + record.getMessage()
              + "\n";
        }
      };

  static {
    try {
      logger.setUseParentHandlers(false);

      // file output
      try {
        Path path = Paths.get("logs");
        Files.createDirectory(path);
      } catch (FileAlreadyExistsException ignored) {
      }
      FileHandler fh = new FileHandler("logs/AntMapStatistics.log");

      fh.setFormatter(format);

      logger.addHandler(fh);
      logger.setLevel(Level.ALL);
      fh.setLevel(Level.ALL);

    } catch (SecurityException | IOException e) {
      e.printStackTrace();
    }
  }

  private int numberOfUsedGroupsStep = -1;

  public void numberOfUsedGroups(DSAgent agent, ConcurrentHashMap<DSGroup, Integer> agentStep) {
    // TODO:l snizuje se ? -> doufam
    // alternativne step old

    synchronized (this) {
      if (numberOfUsedGroupsStep == agent.getStep()) return;
      numberOfUsedGroupsStep = agent.getStep();
    }

    int maintainedGroups = 0, allGroups = 0;
    int step = 0;
    for (DSGroup k : agentStep.keySet()) {
      allGroups++;
      if (!k.getMembers().isEmpty()) {
        maintainedGroups++;
        step = k.getMembers().get(0).getStep();
      }
    }
    logger.info("pocet group : " + maintainedGroups + "/" + allGroups + " at step: " + step);
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

  public void knownCellsStats(DSAgent agent, ConcurrentHashMap<DSGroup, Integer> agentStep) {
    // number of cells known by largest group on steps

    ArrayList<Integer> knownListCount = allKnownCellsByGroup(agentStep);
    Optional<Integer> max = knownListCount.stream().max(Integer::max);
    logger.info("max bunek objeveno skupinou : " + max + " at step: " + agent.getStep());

    OptionalDouble avg = knownListCount.stream().mapToDouble(a -> a).average();
    logger.info("average bunek objeveno skupinou : " + avg + " at step: " + agent.getStep());

    Optional<Integer> min = knownListCount.stream().max(Integer::min);
    logger.info("average bunek objeveno skupinou : " + min + " at step: " + agent.getStep());
  }

  public void revisitedStats(DSAgent agent, ConcurrentHashMap<DSGroup, Integer> agentStep) {
    for (DSGroup g : agentStep.keySet()) {

      int groupRevisited = 0, avgSum = 0;

      LinkedList<DSCell> allCells = g.getMap().getMap().getCells();

      // remove duplicates
      HashSet<Point> uniqueSet = new HashSet<>();
      allCells.forEach(c -> uniqueSet.add(c.getPosition()));

      int allGroupCells = uniqueSet.size();

      for (Point c : uniqueSet) {
        DSCell newestc = g.getMap().getMap().getNewestAt(c);

        // due to multithreading other agents may have deleted given cell
        if (newestc == null) {
          continue;
        }

        if (newestc.revisitedInSteps() != 0) {
          groupRevisited++;
          avgSum += newestc.revisitedInSteps();
        }
      }
      double avg = avgSum / (double) groupRevisited;

      logger.info(
          "skupina: "
              + g
              + " prumer znovuobjeveni dostupnych : "
              + avg
              + " z bunek: "
              + groupRevisited
              + "/"
              + allGroupCells
              + " at step: "
              + agent.getStep());
    }
  }
}

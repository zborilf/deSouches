package dsAgents;

import dsAgents.dsReasoningModule.dsPlans.DSPlan;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.*;

public class dsGUI {
  private JTextField dsgValuePanel;
  private JPanel dsgMainPanel;
  private JLabel dsgLastActionLabel;
  private JTextField dsgAgentNameValue;
  private JTextField dsgLastActionValue;
  private JTextField dsgLAResultValue;
  private JLabel dsgAgentNameLabel;
  private JLabel dfgLAResultLabel;
  private JTextField dsgStepValue;
  private JLabel dsgLAParamsLabel;
  private JTextField dsgLAParamsValue;
  private JTextField dsgEnergyValue;
  private JTextArea dsgPlan;
  private JPanel dsgValuesPanel;
  private JPanel dsgMap;
  private JTextArea dsgTextMap;
  private JScrollBar scrollBar1;
  private JTextField dsgXYValue;
  private JPanel dsgAgentsPanel;
  private JComboBox dsgAgentsSelect;
  private JTextArea dsgTextOutlook;
  private JTextArea dsgPheroOutlook;
  private JTextField dsgGoalArea;
  private JTextField dsgScenarioArea;
  private JCheckBox textMapSwitchCheckBox;
  private JTextField dsgXValue;
  private JTextField dsgYValue;
  private JTextArea dsgLogText;
  private JTable dsgMapTable;

  private DeSouches PCommander;

  public dsGUI() {
    dsgAgentsSelect.addItemListener(
        new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            PCommander.changeGUIFocus(e.getItem().toString());
          }
        });
  }

  public void setAgentName(String agentName) {
    dsgAgentNameValue.setText(agentName);
  }

  public void setStep(int step) {
    dsgStepValue.setText(String.valueOf(step));
  }

  public void setLastAction(String lastAction) {
    dsgLastActionValue.setText(lastAction);
  }

  public void setLastActionParams(String laParams) {
    dsgLAParamsValue.setText(laParams);
  }

  public void setXY(int x, int y) {
    dsgXYValue.setText(x + " // " + y);
  }

  public void setLastActionResult(String lastAction) {
    dsgLAResultValue.setText(lastAction);
  }

  public void setEnergy(String energy) {
    dsgEnergyValue.setText(energy);
  }

  public void setScenario(String scenario) {
    dsgScenarioArea.setText(scenario);
  }

  public void noticeLastGoal(String goal) {
    dsgGoalArea.setText(goal);
  }

  public void writePlan(DSPlan plan) {
    if (plan == null) dsgPlan.setText(" -- no plan -- ");
    else dsgPlan.setText(plan.plan2text());
  }

  public void textMapClear() {
    if (!textMapSwitchCheckBox.isSelected()) dsgTextMap.setText("");
  }

  public void writeTextOutlook(String text) {
    dsgTextOutlook.setText(text + "\n");
  }

  public void writePheroOutlook(String text) {
    dsgPheroOutlook.setText(text + "\n");
  }

  public void setTextMap(String text) {
    if (!textMapSwitchCheckBox.isSelected()) dsgTextMap.setText(text + "\n");
  }

  public void setPheromoneTextMap(String text) {
    if (textMapSwitchCheckBox.isSelected()) dsgTextMap.setText(text + "\n");
  }

  public synchronized void registerAgent(String agent) {
    // add in sorted order
    final int BEGIN_NUMBER = 6;
    int thisAgentN = Integer.parseInt(agent.substring(BEGIN_NUMBER));
    int count = dsgAgentsSelect.getItemCount();

    ArrayList<Integer> items = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      int agentN =
          Integer.parseInt(dsgAgentsSelect.getItemAt(i).toString().substring(BEGIN_NUMBER));
      items.add(agentN);
    }

    if (items.size() == 0) {
      dsgAgentsSelect.addItem(agent);
    } else {
      int firstHigherIndex = count;
      for (int i = 0; i < count; i++) {
        if (items.get(i) > thisAgentN) {
          firstHigherIndex = i;
          break;
        }
      }
      dsgAgentsSelect.insertItemAt(agent, firstHigherIndex);
    }
  }

  void setCommander(DeSouches commander) {
    PCommander = commander;
  }

  public static dsGUI createGUI(int number, DeSouches commander) {
    JFrame frame = new JFrame("GUI for agent " + number);
    frame.setSize(new Dimension(650, 100));
    dsGUI gui = new dsGUI();
    gui.setCommander(commander);
    frame.setContentPane(gui.dsgMainPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocation(new Point(number * 40, number * 20));
    frame.setVisible(true);
    return (gui);
  }

  private void createUIComponents() {
    // TODO: place custom component creation code here
  }
}

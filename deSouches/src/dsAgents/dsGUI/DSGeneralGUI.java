package dsAgents.dsGUI;

import dsAgents.DeSouches;

import java.awt.*;
import javax.swing.*;

public class DSGeneralGUI {
  private JPanel dsgGMainPanel;
  private JTextArea dsgGeneralText;
  private JScrollBar scrollBar1;

  DeSouches PCommander;

  void setCommander(DeSouches commander) {
    PCommander = commander;
  }

  public void clearTasks() {
    dsgGeneralText.setText("");
  }

  public void addTask(String task) {
    dsgGeneralText.append(task + "\n");
  }

  public static DSGeneralGUI createGUI(DeSouches commander) {
    JFrame frame = new JFrame("GUI for General ");

    DSGeneralGUI gui = new DSGeneralGUI();
    gui.setCommander(commander);
    frame.setContentPane(gui.dsgGMainPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocation(new Point(200, 200));
    frame.setSize(new Dimension(650, 400));
    frame.setVisible(true);
    return (gui);
  }
}

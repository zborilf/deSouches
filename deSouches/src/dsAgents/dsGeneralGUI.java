package dsAgents;

import java.awt.*;
import javax.swing.*;

public class dsGeneralGUI {
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

  public static dsGeneralGUI createGUI(DeSouches commander) {
    JFrame frame = new JFrame("GUI for General ");

    dsGeneralGUI gui = new dsGeneralGUI();
    gui.setCommander(commander);
    frame.setContentPane(gui.dsgGMainPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocation(new Point(200, 200));
    frame.setSize(new Dimension(650, 400));
    frame.setVisible(true);
    return (gui);
  }
}

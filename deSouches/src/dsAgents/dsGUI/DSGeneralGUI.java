package dsAgents.dsGUI;

import dsAgents.DeSouches;

import java.awt.*;
import javax.swing.*;

public class DSGeneralGUI {
  private JPanel dsgGMainPanel;
  private JTextArea dsgGeneralText;

  DeSouches PCommander;
  JFrame PFrame;

  void setCommander(DeSouches commander) {
    PCommander = commander;
  }

  public void clearTasks() {
    dsgGeneralText.setText("");
  }

  public void addText(String task) {
    dsgGeneralText.append(task + "\n");
  }

  public void setFrame(JFrame frame){
    PFrame=frame;
  }

  public void addTaskFrame(JFrame frame){
    JDesktopPane pane=(JDesktopPane) PFrame.getContentPane();
    pane.add(frame);
  }


    public static DSGeneralGUI createGUI(DeSouches commander) {
    JFrame frame = new JFrame("GUI for General ");

    DSGeneralGUI gui = new DSGeneralGUI();
    gui.setCommander(commander);
    JDesktopPane PDP=new JDesktopPane();
//    frame.setContentPane(PDP);
//     frame.setContentPane(gui.dsgGMainPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocation(new Point(200, 200));
    frame.setSize(new Dimension(650, 400));
    frame.setVisible(true);
    gui.setFrame(frame);
    return(gui);
  }
}

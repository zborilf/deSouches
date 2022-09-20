package dsAgents.dsGUI;

import dsAgents.DeSouches;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

public class DSFightersGUI {
  private JPanel dsgGMainPanel;
  private JTextArea dsgFightersTerminal;
  private JPanel dsgFightersPanel;
  private JTextArea dsgFTerminal;

  HashMap<String, JPanel> PFighterPanels;

  DeSouches PCommander;
  JFrame PFrame;


  void setCommander(DeSouches commander) {
    PCommander = commander;
  }

  public void clearTasks() {

  }

  public void addText(String text) {
      dsgFightersTerminal.append(text+"\n");
  }


  public void setFrame(JFrame frame){
    PFrame=frame;
  }

  /*
  public void addTaskFrame(JFrame frame){
    JDesktopPane pane=(JDesktopPane) PFrame.getContentPane();
    pane.add(frame);
  }
   */

  public DSFightersGUI(){
  }

  public static DSFightersGUI createGUI(DeSouches commander) {


    JFrame frame = new JFrame("GUI for General ");

    DSFightersGUI gui = new DSFightersGUI();
    gui.setCommander(commander);

    frame.setContentPane(gui.dsgGMainPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocation(new Point(200, 200));
    frame.setSize(new Dimension(650, 400));
    frame.setVisible(true);
    gui.setFrame(frame);
    return (gui);
  }

  private void createUIComponents() {
    // TODO: place custom component creation code here
    PFighterPanels=new HashMap<>();
  }
}

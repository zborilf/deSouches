package dsAgents;

import javax.swing.*;
import java.awt.*;

public class dsGeneralGUI {
    private JPanel dsgGMainPanel;
    private JTextArea dsgGeneralText;
    private JScrollBar scrollBar1;

    DeSouches PCommander;

    void setCommander(DeSouches commander) {
        PCommander=commander;
    }

    public void clearTasks(){
        dsgGeneralText.setText("");
    }

    public void addTask(String task){
        dsgGeneralText.append(task+"\n");
    }

    public static dsGeneralGUI createGUI(int number, DeSouches commander){
        JFrame frame=new JFrame("GUI for General "+number);
        frame.setSize(new Dimension(650,100));
        dsGeneralGUI gui=new dsGeneralGUI();
        gui.setCommander(commander);
        frame.setContentPane(gui.dsgGMainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocation(new Point(number*40,number*20));
        frame.setVisible(true);
        return(gui);
    }

}

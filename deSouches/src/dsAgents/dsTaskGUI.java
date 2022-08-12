package dsAgents;

import dsMultiagent.dsTasks.DSTask;

import javax.swing.*;
import java.awt.*;

public class dsTaskGUI {
    private JPanel dsgMainPanel;
    private JTextField dsgDispenserM;
    private JTextField dsgDispenserL1;
    private JTextField dsgDispenserL2;
    private JTextField dsgDispenserL3;
    private JTextField dsgGoalM;
    private JTextField dsgGoalL1;
    private JTextField dsgGoalL2;
    private JTextField dsgGoalL3;
    private JTextField dsgPositionM;
    private JTextField dsgPositionL3;
    private JTextField dsgPositionL1;
    private JTextField dsgPositionL2;
    private JTextField dsgActivityM;
    private JTextField dsgActivityL1;
    private JTextField dsgActivityL2;
    private JTextField dsgActivityL3;
    private JTextField dsgAgentL3;
    private JTextField dsgAgentM;
    private JTextField dsgAgentL2;
    private JTextField dsgAgentL1;
    private JPanel dsgSubPanel;
    private JPanel dsgRolesPanel;
    private JPanel dsgDispensersPanel;
    private JPanel dsgGoalAreasPanel;
    private JPanel dsgRoleLabelPanel;
    private JPanel dsgDispensersLabelPanel;
    private JPanel dsgGoalAreaLabelPanel;
    private JPanel dsgPositionLabelPanel;
    private JPanel dsgPositionsPanel;
    private JPanel dsgActivityPanel;
    private JPanel dsgActivityLabelPanel;
    private JPanel dsgAgentLabelPanel;
    private JLabel dsgActivityLabel;
    private JLabel dsgPositionLabel;
    private JLabel dsgAreaLabel;
    private JLabel dsgDispensersLabel;
    private JLabel dsgStepLabel;
    private JLabel dsgMasterLabel;
    private JLabel dsgL1Label;
    private JLabel dsgL2Label;
    private JLabel dsgL3Label;
    private JLabel dsgAgentLabel;
    private JTextArea dsgTaskTerminal;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    String getAgentName(DSAgent agent){
        if(agent==null)
            return("No agent");
        return(agent.getAgentName());
    }


    String getAgentPosition(DSAgent agent){
        if(agent==null)
            return(" -- ");
        Point point=agent.getMapPosition();
        return("["+point.x+","+point.y+"]");
    }


    String getPosition(Point point){
        if(point==null)
            return(" -- ");
        return("["+point.x+","+point.y+"]");
    }



    public void setDsgTaskText(DSTask task){
        DSAgent M,L1, L2,L3;
        L2=null; L3=null; //M, L1 are always some

        M=task.getSubtaskRoutes(0).getAgent();
        L1=task.getSubtaskRoutes(1).getAgent();

        if(task.getSubtaskRoutes(2)!=null) {
            L2=task.getSubtaskRoutes(2).getAgent();
            dsgAgentL2.setText(getAgentName(L2));
            dsgDispenserL2.setText(getPosition(task.getSubtaskRoutes(2).getDispenserPosition()));
            dsgGoalL2.setText(getPosition(task.getSubtaskRoutes(2).getGoalPosition()));
            dsgPositionL2.setText(getAgentPosition(L2));
            dsgActivityL2.setText(L2.getLastGoal());

        }
        if(task.getSubtaskRoutes(3)!=null){
            L3=task.getSubtaskRoutes(3).getAgent();
            dsgAgentL3.setText(getAgentName(L3));
            dsgDispenserL3.setText(getPosition(task.getSubtaskRoutes(3).getDispenserPosition()));
            dsgGoalL3.setText(getPosition(task.getSubtaskRoutes(3).getGoalPosition()));
            dsgPositionL3.setText(getAgentPosition(L3));
            dsgActivityL3.setText(L3.getLastGoal());
        }

        dsgAgentM.setText(getAgentName(M));
        dsgAgentL1.setText(getAgentName(L1));

        dsgActivityM.setText(M.getLastGoal());
        dsgActivityL1.setText(L1.getLastGoal());

        dsgDispenserM.setText(getPosition(task.getSubtaskRoutes(0).getDispenserPosition()));
        dsgDispenserL1.setText(getPosition(task.getSubtaskRoutes(1).getDispenserPosition()));

        dsgGoalM.setText(getPosition(task.getSubtaskRoutes(0).getGoalPosition()));
        dsgGoalL1.setText(getPosition(task.getSubtaskRoutes(1).getGoalPosition()));

        dsgPositionM.setText(getAgentPosition(M));
        dsgPositionL1.setText(getAgentPosition(L1));

        dsgStepLabel.setText(String.valueOf(M.getStep()));

    }

    public void addText2Terminal(String text){
        dsgTaskTerminal.append(text+"\n");
    }

    public static dsTaskGUI createGUI() {

        JFrame frame = new JFrame("GUI for task");

        dsTaskGUI gui = new dsTaskGUI();
        frame.setContentPane(gui.dsgMainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(new Point(350, 350));
        frame.pack();

        frame.setVisible(true);
        return (gui);
    }


}

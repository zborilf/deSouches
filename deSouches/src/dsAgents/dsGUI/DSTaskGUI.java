package dsAgents.dsGUI;

import dsAgents.DSAgent;
import dsMultiagent.dsTasks.DSTask;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;


public class DSTaskGUI {
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
    private JTextField dsgScenarioM;
    private JTextField dsgScenarioL1;
    private JTextField dsgScenarioL2;
    private JTextField dsgScenarioL3;
    private JFormattedTextField dsgTaskTerminalFormatted;

    private JInternalFrame PFrame;

    static int __tasks=0;
    int PLine=1;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


    public JInternalFrame getFrame(){
        return PFrame;
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


    public void suceeded(){
        dsgRoleLabelPanel.setBackground(Color.GREEN);
    }

    public synchronized void failed(String reason){
        dsgRoleLabelPanel.setBackground(Color.PINK);
        dsgTaskTerminal.append("FAILED: "+reason);
    }


    public synchronized void setDsgTaskText(DSTask task, int step, Point gp1,Point gp2,Point gp3,Point gp4){

        DSAgent M, L1, L2, L3;

        L1=null; L2=null; L3=null; //M is always some
        dsgStepLabel.setText(step+"/"+String.valueOf(task.getDeadline())+" e:"+task.goalDistanceMax());
        M=task.getSubtaskRoutes(0).getAgent();

        if(task.getSubtaskRoutes(1)!=null) {
            L1 = task.getSubtaskRoutes(1).getAgent();
            dsgAgentL1.setText(getAgentName(L1));
            dsgDispenserL1.setText(getPosition(task.getSubtaskRoutes(1).getDispenserPosition()));
            dsgGoalL1.setText(getPosition(gp2));
            dsgPositionL1.setText(getAgentPosition(L1));
            dsgScenarioL1.setText(L1.getScenarioName());
            dsgActivityL1.setText(L1.getLastGoal());
        }
        if(task.getSubtaskRoutes(2)!=null) {
            L2=task.getSubtaskRoutes(2).getAgent();
            dsgAgentL2.setText(getAgentName(L2));
            dsgDispenserL2.setText(getPosition(task.getSubtaskRoutes(2).getDispenserPosition()));
            dsgGoalL2.setText(getPosition(gp3));
            dsgPositionL2.setText(getAgentPosition(L2));
            dsgScenarioL2.setText(L2.getScenarioName());
            dsgActivityL2.setText(L2.getLastGoal());

        }
        if(task.getSubtaskRoutes(3)!=null){
            L3=task.getSubtaskRoutes(3).getAgent();
            dsgAgentL3.setText(getAgentName(L3));
            dsgDispenserL3.setText(getPosition(task.getSubtaskRoutes(3).getDispenserPosition()));
            dsgGoalL3.setText(getPosition(gp4));
            dsgPositionL3.setText(getAgentPosition(L3));
            dsgScenarioL3.setText(L3.getScenarioName());
            dsgActivityL3.setText(L3.getLastGoal());
        }

        dsgAgentM.setText(getAgentName(M));

        dsgActivityM.setText(M.getLastGoal());

        dsgDispenserM.setText(getPosition(task.getSubtaskRoutes(0).getDispenserPosition()));

        dsgGoalM.setText(getPosition(gp1));

        dsgScenarioM.setText(M.getScenarioName());

        dsgPositionM.setText(getAgentPosition(M));

        
    }

    public void addText2Terminal(String text){
        dsgTaskTerminal.append(text+"\n");
        try {
            int so = dsgTaskTerminal.getLineStartOffset(PLine);
            int eo = dsgTaskTerminal.getLineEndOffset(PLine);
            Highlighter.HighlightPainter hp=new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
        }catch(Exception e){};
        PLine++;
    }

    public DSTaskGUI(JInternalFrame frame){
        PFrame=frame;
    }

    public static DSTaskGUI createTaskFrame() {

        JInternalFrame frame = new JInternalFrame("GUI for task", true, true, true);
      //  frame.setPreferredSize(new Dimension(900,300));
        frame.pack();
        DSTaskGUI gui = new DSTaskGUI(frame);
        frame.setContentPane(gui.dsgMainPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        frame.setLocation(new Point((__tasks/12)*300+(__tasks % 12)*20, (__tasks % 12)*60+60));
        __tasks++;

        frame.pack();
        frame.setVisible(true);
        return (gui);

    }


}

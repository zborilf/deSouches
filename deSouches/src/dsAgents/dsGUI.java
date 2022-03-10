package dsAgents;

import dsAgents.dsReasoningModule.dsPlans.DSPlan;

import javax.swing.*;
import java.awt.*;

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
    private JTextField dsgXValue;
    private JTextField dsgYValue;
    private JTextArea dsgLogText;
    private JTable dsgMapTable;

    public void setAgentName(String agentName){
        dsgAgentNameValue.setText(agentName);
    }

    public void setStep(int step){
        dsgStepValue.setText(String.valueOf(step));
    }

    public void setLastAction(String lastAction){
        dsgLastActionValue.setText(lastAction);
    }

    public void setLastActionParams(String laParams){
              dsgLAParamsValue.setText(laParams);
    }

    public void setXY(int x, int y) {
        dsgXYValue.setText(x+" / "+y);
    }

    public void setLastActionResult(String lastAction){
        dsgLAResultValue.setText(lastAction);
    }
    public void setEnergy(String energy){
        dsgEnergyValue.setText(energy);
    }

    public void setScenarion(String scenario){
        dsgPlan.append(scenario+'\n');
    }

    public void writePlan(DSPlan plan){
        dsgPlan.setText(plan.plan2text());
    }

    public void textMapClear(){
        dsgTextMap.setText("");
    }

    public void writeTextMap(String text){
        dsgTextMap.setText(text+"\n");
    }

    public void appendTextMap(String text){
        dsgTextMap.append(text+"\n");
    }

    public static dsGUI createGUI(int number){
        JFrame frame=new JFrame("GUI for agent "+number);
        frame.setSize(new Dimension(650,100));
        dsGUI gui=new dsGUI();
        frame.setContentPane(gui.dsgMainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocation(new Point(number*40,number*20));
        frame.setVisible(true);
        return(gui);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

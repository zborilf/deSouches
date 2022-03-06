package dsAgents.dsPerceptionModule;

import dsAgents.DSAgent;

import javax.swing.*;
import java.awt.*;

public class DSAgentGUI {
    private JPanel rootPanel;
    private JLabel dsgLAResultLabel;
    private JLabel dsgLastActionLabel;
    private JLabel dsgAgentNameLabel;
    private JTextField dsgAgentNameValue;
    private JTextField dsgLastActionValue;
    private JTextField dsgLAResultValue;
    private JLabel dsgEnergyLabel;
    private JTextField dsgEnergyValue;
    private JLabel dsgRoleLabel;
    private JTextField dsgRoleValue;

/*
    public void setAgentName(String name){
        dsgAgentNameValue.setText(name);
    }

    public void setLastAction(String lastAction){
        dsgAgentNameValue.setText(lastAction);
    }

    public void setLAResult(String lAResult){
        dsgAgentNameValue.setText(lAResult);
    }

  /*  public DSAgentGUI(){
        System.out.println("---------------!!!!!!!!_----------");
    }*/

    public static void createGUI(){
        JFrame frame=new JFrame("De_Souches_GUI");
        frame.setSize(new Dimension(300,100));
        frame.setContentPane(new DSAgentGUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

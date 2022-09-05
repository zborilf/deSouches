package dsAgents.dsGUI;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;

public class DSGTaskPoolFrame {

    JFrame PFrame;
    JDesktopPane PDesktopPane;

    public void addTaskFrame(JInternalFrame frame){
        PDesktopPane.add(frame);
        frame.toFront();
    }


    ActionListener alButtonCascade=new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JInternalFrame[] frames = PDesktopPane.getAllFrames();
            int i=0;
            for (JInternalFrame frame : frames) {
                frame.setSize(new Dimension(1200, 300));
                frame.setLocation((i/12)*300+(i % 12)*20, (i-1 % 12)*60+20);
                i++;
            }
        }
    };

    public DSGTaskPoolFrame(){
        PFrame=new JFrame("Tasks pursuaded");
        JPanel mainPanel=new JPanel();
        mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        JButton buttonCascade=new JButton("Cascade button");
        buttonCascade.addActionListener(alButtonCascade);
        JPanel buttonPanel=new JPanel();
        buttonPanel.add(buttonCascade);
        mainPanel.add(buttonPanel,BorderLayout.NORTH);
        PDesktopPane= new JDesktopPane();
        JPanel panePanel=new JPanel();
        panePanel.setForeground(Color.GREEN);
        panePanel.add(PDesktopPane);
        mainPanel.add(panePanel,BorderLayout.SOUTH);
//        PDeskto   pPane.setPreferredSize(new Dimension(1000,500));
        PFrame.setContentPane(PDesktopPane);
        PFrame.setSize(new Dimension(2000,1000));
        PFrame.show();
        /*
        JPanel mainPanel=new JPanel();
        mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        JButton buttonCascade=new JButton("Cascade");
        buttonCascade.addActionListener(alButtonCascade);
        mainPanel.add(buttonCascade,BorderLayout.WEST);
        JButton buttonTiles=new JButton("Tiles");
        mainPanel.add(buttonTiles,BorderLayout.WEST);
        mainPanel.add(PDesktopPane, BorderLayout.SOUTH);
        PFrame.add(mainPanel,BorderLayout.NORTH);
        PFrame.setSize(new Dimension(1000,800));
        PFrame.show();
         */
    }
}

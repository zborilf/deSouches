package dsAgents.dsGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class DSGTaskPoolFrame {

    private final static int __cascade=1;
    private final static int __tile=2;


    JFrame PFrame;
    JDesktopPane PDesktopPane;
    JCheckBox PActiveCHB;
    JCheckBox PCompletedCHB;
    JCheckBox PFailedCHB;
    JCheckBox POneBlock;
    JCheckBox PTwoBlocks;
    JCheckBox PThreeBlocks;
    JCheckBox PFourBlocks;
    JCheckBox PUpdate;

    int style=__cascade;

    LinkedList<DSTaskGUI> PTaskGUIs=new LinkedList();


    public boolean checkedNOSubtasks(int i) {
        switch (i) {
            case 1:
                return (POneBlock.isSelected());
            case 2:
                return (PTwoBlocks.isSelected());
            case 3:
                return (PThreeBlocks.isSelected());
            case 4:
                return (PFourBlocks.isSelected());
        }
        return (false);
    }

    public boolean checkState(int state) {
        switch (state) {
            case DSTaskGUI.__task_completed:
                return (PCompletedCHB.isSelected());
            case DSTaskGUI.__task_running:
                return (PActiveCHB.isSelected());
            case DSTaskGUI.__task_failed:
                return (PFailedCHB.isSelected());
        }

    return (false);
    }


    public LinkedList<JInternalFrame> getRequested() {
        LinkedList<JInternalFrame> ifl=new LinkedList();
        for (DSTaskGUI TG : PTaskGUIs) {
            if(checkedNOSubtasks(TG.getNOSubtasks())&&checkState(TG.getState()))
                ifl.add(TG.getFrame());
            else
                TG.getFrame().setVisible(false);
        }
        return(ifl);
    }


    void setFrames(){
        LinkedList<JInternalFrame> ilf=getRequested();
        int i=0;
        for(JInternalFrame frame:ilf) {
            if(style==__tile)
                PDesktopPane.getDesktopManager().
                    setBoundsForFrame(frame,(i / 5)*300,(i % 5) *200,300,200);
            else
                PDesktopPane.getDesktopManager().
                    setBoundsForFrame(frame,i*10,(i % 10) *60,1000,300);

            frame.setVisible(true);
            frame.toFront();
            i++;
        }
    }


    public void addTaskFrame(DSTaskGUI taskGUI){
        PTaskGUIs.add(taskGUI);
        JInternalFrame frame=taskGUI.getFrame();
        PDesktopPane.add(frame);
        if(PUpdate.isSelected())
            setFrames();
    }


        ActionListener alButtonTile = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                style=__tile;
                setFrames();
            }
        };

        ActionListener alButtonCascade = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                style=__cascade;
                setFrames();
                }
        };


    ActionListener alCheckboxChanged = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            setFrames();
        }
    };

    public DSGTaskPoolFrame() {
            PFrame = new JFrame("Tasks pursuaded");
            JPanel mainPanel = new JPanel();
            PDesktopPane = new JDesktopPane();
            JPanel panePanel = new JPanel();
            panePanel.setForeground(Color.GREEN);
            panePanel.add(PDesktopPane);
            mainPanel.add(panePanel, BorderLayout.SOUTH);
//        PDeskto   pPane.setPreferredSize(new Dimension(1000,500));
            PFrame.setContentPane(PDesktopPane);

            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("deSouches");
            JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
            menu.add(rbMenuItem);


            PActiveCHB = new JCheckBox("Active");
            PCompletedCHB = new JCheckBox("Completed");
            PFailedCHB = new JCheckBox("Failed");


            PActiveCHB.setSelected(true);
            PCompletedCHB.setSelected(true);
            PFailedCHB.setSelected(true);


            PActiveCHB.setMnemonic(KeyEvent.VK_A);
            PCompletedCHB.setMnemonic(KeyEvent.VK_C);
            PFailedCHB.setMnemonic(KeyEvent.VK_F);

            menu.setMnemonic(KeyEvent.VK_M);
            menuBar.add(menu);

            menuBar.add(new JLabel("Status: "));


            menuBar.add(PActiveCHB);
            menuBar.add(PCompletedCHB);
            menuBar.add(PFailedCHB);

            menuBar.add(new JLabel("     Tasks: "));
            POneBlock = new JCheckBox("1B");
            PTwoBlocks = new JCheckBox("2B");
            PThreeBlocks = new JCheckBox("3B");
            PFourBlocks = new JCheckBox("4B");
            PUpdate = new JCheckBox("Update");

            POneBlock.setSelected(true);
            PTwoBlocks.setSelected(true);
            PThreeBlocks.setSelected(true);
            PFourBlocks.setSelected(true);
            PUpdate.setSelected(true);

            POneBlock.setMnemonic(KeyEvent.VK_1);
            PTwoBlocks.setMnemonic(KeyEvent.VK_2);
            PThreeBlocks.setMnemonic(KeyEvent.VK_3);
            PFourBlocks.setMnemonic(KeyEvent.VK_4);

            PActiveCHB.addActionListener(alCheckboxChanged);
            PCompletedCHB.addActionListener(alCheckboxChanged);
            PFailedCHB.addActionListener(alCheckboxChanged);
            POneBlock.addActionListener(alCheckboxChanged);
            PTwoBlocks.addActionListener(alCheckboxChanged);
            PThreeBlocks.addActionListener(alCheckboxChanged);
            PFourBlocks.addActionListener(alCheckboxChanged);

            menuBar.add(POneBlock);
            menuBar.add(PTwoBlocks);
            menuBar.add(PThreeBlocks);
            menuBar.add(PFourBlocks);

            JButton bcascade = new JButton("Cascade");
            menuBar.add(bcascade);
            JButton btile = new JButton("Tile");
            menuBar.add(btile);

            menuBar.add(PUpdate);

            bcascade.addActionListener(alButtonCascade);
            btile.addActionListener(alButtonTile);

            menuBar.setVisible(true);


            PFrame.setJMenuBar(menuBar);

            PFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            PFrame.setSize(new Dimension(2000, 1000));
            PFrame.show();
        }
    }


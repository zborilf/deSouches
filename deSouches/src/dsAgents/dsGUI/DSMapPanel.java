package dsAgents.dsGUI;

import javax.swing.*;
import java.awt.*;

public class DSMapPanel extends JPanel {


    @Override
    public void paintComponent(Graphics g) {
        super.paint(g);
        g.drawRect(10,10,100,100);
    }

    public DSMapPanel(){
        super();
        this.setPreferredSize(new Dimension(600, 75));
    }

}

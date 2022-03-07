package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

/*
    2022, actual outlook of an agent (process add/delete lists)
 */

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class DSAgentOutlook {
    HashMap<Point, DSCell> POutlook;
    int PStep;

    public void processAddThing(int y, int x, String type, String params, int step){
        PStep=step;
        DSCell cell=new DSCell(x, y, type, params, step);
        POutlook.put(new Point(x,y),cell);
    }

    public void processDeleteThing(int y, int x, String type, String params, int step){
        PStep=step;
        POutlook.remove(new Point(x,y));
    }

    public LinkedList<DSCell> getCellsList(int vision){
        LinkedList<DSCell> cl=new LinkedList<DSCell>();
        for(int i = -vision;i <= vision; i++)
            for (int j = -vision; j <= vision; j++)
                if(POutlook.get(new Point(i,j))!=null)
                    cl.add(POutlook.get(new Point(i,j)));
                else
                    if(Math.abs(i)+Math.abs(j)<=vision)     // sees nothing
                    cl.add(new DSCell(i,j,DSCell.__DSClear,PStep));
        return(cl);
    }

    public String stringOutlook(int vision){

        String so="";

        for(int i = -vision;i <= vision; i++) {
            for (int j = -vision; j <= vision; j++) {
                if(Math.abs(i)+Math.abs(j)>vision)
                    so = so + "    ";
                else{
                    if ((j == i) && (i == 0))
                        so = so + " AA ";
                    else{
                        if (POutlook.containsKey(new Point(i, j)))
                            so = so + DSCell.getTypeSign(POutlook.get(new Point(i, j)).getType());
                        else
                            so = so + " .. ";
                    }
                }
            }
                so=so+'\n';
        }
        return(so);
    }

    public DSAgentOutlook(){
        POutlook=new HashMap<Point, DSCell>();
    }

}

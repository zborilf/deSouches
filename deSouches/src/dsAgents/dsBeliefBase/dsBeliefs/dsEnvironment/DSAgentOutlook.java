package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

/*
    2022, actual outlook of an agent (process add/delete lists)
    Public
    public int getStep()
    public void processAddThing(int x, int y, String type, String params, int step)


 */

import java.awt.*;
import java.util.LinkedList;

public class DSAgentOutlook {
    DSCells POutlook;
    int PStep;

    public int getStep(){
        return(PStep);
    }

    public void processAddThing(int x, int y, String type, String params, int step){
        PStep=step;
        DSCell cell=new DSCell(x, y, type, params, step);
        POutlook.put(cell);
    }

    public void processDeleteThing(int x, int y, String type, String params, int step){
        PStep=step;
        POutlook.removeCell(x,y, DSCell.getThingTypeIndex(type,params));
    }

    public DSCells getCells(){
        return(POutlook);
    }

    // list of friend agents in actual outlook

    public LinkedList<Point> getFriendsSeen(int vision){
        LinkedList<Point> flist=new LinkedList<Point>();
        for(int i = -vision;i <= vision; i++)
            for (int j = -vision; j <= vision; j++) {
                Point point=new Point(i,j);
                if (POutlook.getKeyType(point,DSCell.__DSEntity_Friend)!=null)
                       flist.add(point);
            }
        return(flist);
    }

    public String stringOutlook(int vision, String agentname){

        String so="";

        for(int j = -vision;j <= vision; j++) {
            for (int i = -vision; i <= vision; i++) {
                if(Math.abs(i)+Math.abs(j)>vision)
                    so = so + "   ";
                else{
                    if ((j == i) && (i == 0))
                        so = so + " AA";
                    else{
                        if (POutlook.containsKey(new Point(i, j)))
                            so = so + DSCell.getTypeSign(POutlook.getFirst(new Point(i, j)).getType());
                        else
                            so = so + " ..";
                    }
                }
            }
                so=so+'\n';
        }
        return(so);
    }

    public DSAgentOutlook(){
        POutlook=new DSCells();
    }

}

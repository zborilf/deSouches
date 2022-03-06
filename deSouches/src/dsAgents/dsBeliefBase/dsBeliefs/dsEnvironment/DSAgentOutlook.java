package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

/*
    2022, actual outlook of an agent (process add/delete lists)
 */

import eis.iilang.Parameter;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

public class DSAgentOutlook {
    HashMap<Point, DSCell> POutlook;


    Rectangle getScale(){
        int minx=0;
        int maxx=0;
        int miny=0;
        int maxy=0;

        for(Point point:POutlook.keySet()){
            if(point.x<minx)
                minx=point.x;
            if(point.x>maxx)
                maxx=point.x;
            if(point.y<miny)
                miny=point.y;
            if(point.y>maxy)
                maxy=point.y;
        }
        return(new Rectangle(minx,miny,maxx-minx+1,maxy-miny+1));
    }

    public void processAddThing(int y, int x, String type, String params, int step){
        DSCell cell=new DSCell(x, y, type, params, step);
        POutlook.put(new Point(x,y),cell);
    }

    public void processDeleteThing(int y, int x, String type, String params, int step){
         POutlook.remove(new Point(x,y));
    }

    public String stringOutlook(int vision){
        Rectangle rec;
        rec=getScale();

        String so="";


        for(int i=rec.y;i<rec.y+rec.height;i++) {
            for (int j = rec.x; j < rec.x + rec.width; j++) {
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

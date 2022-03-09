package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class DSCells {
    LinkedList<DSCell> PCells;


    public LinkedList<DSCell> getCells(){
        return(PCells);
    }

    // top left corner
    public Point getTLC(){
        if (PCells.isEmpty())
                return null;
        Point tlc =PCells.getFirst().getPosition();
        for(DSCell cell:PCells) {
            if (cell.getX() <= tlc.x)
                tlc.x=cell.getX();
            if (cell.getY() <= tlc.y)
                tlc.y=cell.getY();
        }
            return(tlc);
    }
// bottom right corner
    public Point getBRC() {
        if (PCells.isEmpty())
            return null;
        Point brc = PCells.getFirst().getPosition();
        for (DSCell cell : PCells){
            if (cell.getX() >= brc.x)
                brc.x = cell.getX();
        if (cell.getY() >= brc.y)
            brc.y = cell.getY();
        }
        return(brc);
    }


    public void put(DSCell element){
        PCells.add(element);
    }

    public void removeAt(Point point){
        LinkedList<DSCell> newList=new LinkedList<DSCell>();
        for(DSCell element:PCells)
            if((element.getPosition().x!=point.x)||(element.getPosition().y!=point.y))
                newList.add(element);

            PCells=newList;
    }

    public void removeOlder(Point point, int step) {
        LinkedList<DSCell> newList=new LinkedList<DSCell>();
        for(DSCell element:PCells)
            if((element.getPosition().x!=point.x)||(element.getPosition().y!=point.y)||(element.getTimestamp()>step))
                    newList.add(element);
            PCells=newList;
    }

        public void removeCell(int x, int y, int type){
        LinkedList<DSCell> newList=new LinkedList<DSCell>();
        for(DSCell element:PCells)
            if((element.getPosition().x!=x)||(element.getPosition().y!=y)||(element.getType()!=type))
                newList.add(element);

        PCells=newList;
    }

    public LinkedList<DSCell> getInRange(int vision){
        LinkedList<DSCell> newList=new LinkedList<DSCell>();
        for(DSCell element:PCells)
            if(Math.abs(element.getPosition().x)+Math.abs(element.getPosition().y)<=vision)
                newList.add(element);

            return(newList);
    }

    public DSCell getFirst(Point point){
        LinkedList<DSCell> newList=new LinkedList<DSCell>();
        for(DSCell element:PCells)
            if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y))
                return(element);

            return(null);
    }

    public DSCell getKeyType(Point point,int type){
        for(DSCell element:PCells)
            if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y)&&(element.getType()==type))
                return(element);

        return(null);

    }

    public boolean containsKey(Point point){
        for(DSCell element:PCells)
            if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y))
                return(true);

            return(false);
    }

    public DSCells(){
        PCells=new LinkedList<DSCell>();
    }
}

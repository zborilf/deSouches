package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import java.awt.*;
import java.util.*;

public class DSCells {
    Map<Point,DSCell> PCells = new Hashtable<>();
    Point tlc, brc;

    public Map<Point, DSCell> getCells(){
        return PCells;
    }

    // top left corner
    public Point getTLC(){
        if (PCells.isEmpty())
            return null;
        Point tlc = PCells.keySet().stream().findFirst().get();
        for(DSCell cell: PCells.values()) {
            if (cell.getX() <= tlc.x)
                tlc.x=cell.getX();
            if (cell.getY() <= tlc.y)
                tlc.y=cell.getY();
        }
        return tlc;
    }
// bottom right corner
    public Point getBRC() {
        if (PCells.isEmpty())
            return null;
        brc = PCells.keySet().stream().findFirst().get();
        for (DSCell cell : PCells.values()){
            if (cell.getX() >= brc.x)
                brc.x = cell.getX();
            if (cell.getY() >= brc.y)
                brc.y = cell.getY();
        }
        return brc;
    }


    synchronized  public void put(DSCell element){
        PCells.put(new Point(element.getX(),element.getY()),element);
    }

    public void removeAt(Point point){
        if(PCells.containsKey(point))
            PCells.remove(point);
    }

    synchronized public void removeOlder(Point point, int step) {
        DSCell old = PCells.get(point);
        if(old == null)
            return;
        if (old.getTimestamp()<=step)
            PCells.remove(point);
    }

    public void removeCell(int x, int y, int type){
        Point p = new Point(x,y);
        if(PCells.containsKey(p))
            PCells.remove(p);
    }

    public LinkedList<DSCell> getInRange(int vision){
        LinkedList<DSCell> newList=new LinkedList<DSCell>();
        for(DSCell element : PCells.values())
            if(Math.abs(element.getPosition().x)+Math.abs(element.getPosition().y)<=vision)
                newList.add(element);

        return(newList);
    }

    public DSCell getFirst(Point point){
        for(DSCell element: PCells.values())
            if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y))
                return(element);
        return(null);
    }

    //TODO: always returns at max 1 elemtent
    public LinkedList<DSCell> getAllAt(Point point){
        LinkedList<DSCell> cells=new LinkedList<DSCell>();
        for(DSCell element : PCells.values())
            if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y))
                cells.add(element);
        return(cells);
    }

    public DSCell getKeyType(Point point,int type){
        for(DSCell element : PCells.values())
            if((element.getPosition().x==point.x)&&(element.getPosition().y==point.y)&&(element.getType()==type))
                return(element);
        return(null);

    }

    public LinkedList<DSCell> getAllType(int type){
        LinkedList<DSCell> cells=new LinkedList<DSCell>();
        for(DSCell element : PCells.values())
            if(element.getType()==type)
                cells.add(element);
        return(cells);
    }

    public boolean containsKey(Point point){
        return PCells.containsKey(point);
    }

}
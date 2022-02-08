

/*

 * Mapa je m��ka s neur�it�mi rozm�ry. Je implementov�na jako seznam pozorovan�ch pozic a jejich naposledy zji�t�n�ch stav�.
 * Mapa je p�i�azena jednomu nebo v�ce agent�m. Na po��tku m� ka�d� agent svoji mapu a koordin�ty jsou po��t�ny od po��te�n� pozice
 * ka�d�ho agenta. V p��pad� �e se agenti potkaj�, slou�� sv� mapy, koordin�ty jsou (*n�jak*) p�epo��t�ny a agenti tuto mapu pak sd�l�.
 * Do mapy si agent zna�� pozorov�n� s �asov�m raz�tkem po��zen� pozorov�n�. Aktu�ln� jsou takov� stavy jednotliv�ch pozic v map�

 */


package dsAgents.dsBeliefs.dsEnvironment;

import deSouches.utils.HorseRider;
import dsAgents.DSAgent;
import dsAgents.dsPerceptionModule.DSPerceptor;

import java.awt.*;
import java.util.*;

public class DSMap {

    private static final String TAG = "DSMap";
 //   int PStep = 0;
    HashMap<Point, DSCell> PMap;
    HashMap<Integer, LinkedList<DSCell>> PCells; // bude to nahashovane podle typu, kazdy typ svuj list
    // DSDispenserPool PDispenserPool; ... predelat horni na LL<DSCellNode> a hotovo
    DSAgent PAgent;
    HashMap <DSAgent,Point> PAgentPosition;
    final static int _maxDistance=5000;

    int PY, PX, PXMin, PXMax, PYMin, PYMax;
    private Border border = new Border();

    void updateXYMinMax(int x, int y) {
        if (x > PXMax) PXMax = x;
        if (x < PXMin) PXMin = x;
        if (y > PYMax) PYMax = y;
        if (y < PYMin) PYMin = y;
        return;
    }


    public DSCell getCellAt(Point position) {
        return (PMap.get(position));

    }
    public Point getAgentPos() { // Master
        return(new Point(PX,PY));
    }

    public Point getAgentPos(DSAgent agent) {
        return(PAgentPosition.get(agent));
    }


    public static int distance(Point a, Point b) {
        if((a==null)||(b==null))
            return(_maxDistance);
        return (Math.abs(a.x - b.x) + Math.abs(a.y-b.y)); // Manhattan
    }

    public static boolean closer(Point a, Point b, Point c) {
        return (distance(a, b) < distance(a, c));
    }

    public synchronized void mergeMaps(DSMap map, Point displacement) {
        HashMap<Point, DSCell> mapClone=(HashMap<Point, DSCell>)map.getPositions().clone();
        DSCell cell;
        for(Point cellPosition:mapClone.keySet()){
            cell=mapClone.get(cellPosition);
            cell.PX=cell.PX+displacement.x;
            cell.PY=cell.PY+displacement.y;
         //   updateXYMinMax(cell.PX, cell.PY);
            updateCell(cell);
        }
        return;
    }

    public HashMap<Point, DSCell> getPositions(){
        return PMap;
    }


    public static synchronized boolean areasConflict(Point p1, DSBody b1, Point p2, DSBody b2){
        for(DSCell cell1:b1.getBodyList())
            for(DSCell cell2:b2.getBodyList())
                if((cell1.getX()+p1.x==cell2.getX()+p2.x)&&
                        (cell1.getX()+p1.y==cell2.getX()+p2.y))
                    return(true);
        return(false);
    }

    boolean isFriendAt(Point position){
        HashMap<DSAgent,Point> agentPositions=(HashMap<DSAgent,Point>)(PAgentPosition.clone());
        Set<DSAgent> agents=agentPositions.keySet();
        for(DSAgent agent:agents){
            Point agentPos=(Point)agent.getPosition().clone();
            if((position.x==agentPos.x)&&(position.y==agentPos.y))
                return(true);
        }
        return(false);
    }

    boolean isAgentBody(Point position,DSBody body){
        int Tx=position.x-PX;
        int Ty=position.y-PY;
        return(body.pointInBody(body, new Point(Tx,Ty)));
    }

    public boolean isObstacleAt(Point position, DSBody agentbody, DSBody body, int step) {
        if(border.isOutside(position))
            return(true);
        for(DSCell bodyItem:body.getBodyList()) {
            DSCell node = PMap.get(new Point(position.x + bodyItem.getX(), position.y + bodyItem.getY()));
            if (node != null) {
                border.isOutside(node.getPosition());
                if(isFriendAt(node.getPosition()))
                    return(true);
                if (isAgentBody(node.getPosition(), agentbody))
                    // prekazka je ve skutecnosti agent sam
                    return (false);

                if ((node.getType() == DSCell.__DSObstacle))
                    return (true);
                // Pratele nejsou na pame, ale zname jejich presne aktualni pozice


                else //TODO to dole omezit casove souc_cas-timestamp (cca 5 kroku)
                if((step-node.getTimestamp())<5)
                     if ((node.getType() == DSCell.__DSEntity_Enemy)
                             || (node.getType() == DSCell.__DSEntity_Friend)
                                 || ((node.getType() >= DSCell.__DSBlock) && (node.getType() < DSCell.__DSDispenser)))
                            return(true);
            }
        }
        return(false);
    }

    boolean isPositionInCellList(LinkedList<DSCell> cells, Point position){
        if(cells==null)
            return(false);
        LinkedList<DSCell> cellsC=(LinkedList<DSCell>)cells.clone();
        for(DSCell cell:cellsC)
            if((cell.getX()==position.getX())&&(cell.getY()==position.getY()))
                return(true);
            return(false);
    }

    boolean isOjectAt(Point position, int objectType){

        LinkedList<DSCell> objects=PCells.get(objectType);

        if(isPositionInCellList(objects,position))
            return(true);
        return(false);
    }


    public String oppositeDirection(String dir){
        if(dir.contentEquals("n"))
            return("s");
        if(dir.contentEquals("s"))
            return("n");
        if(dir.contentEquals("w"))
            return("e");
        if(dir.contentEquals("e"))
            return("w");
        return("");
    }


    public String getFreeNeighbour(Point place, DSAgent agent){
        if(!isObstacleAt(new Point(place.x+1,place.y), agent.getBody(), agent.getBody(),agent.getStep()))
            return("e");
        else
        if(!isObstacleAt(new Point(place.x-1,place.y), agent.getBody(), agent.getBody(),agent.getStep()))
            return("w");
        else
        if(!isObstacleAt(new Point(place.x,place.y+1), agent.getBody(), agent.getBody(),agent.getStep()))
            return("s");
        else
        if(!isObstacleAt(new Point(place.x,place.y-1), agent.getBody(), agent.getBody(),agent.getStep()))
            return("n");
        return("");
    }


    public Point getFreeNeighbourPosition(Point place, DSAgent agent){
        Point direction=DSPerceptor.getPositionFromDirection(getFreeNeighbour(place, agent));
        if(direction==null)
            return(null);
        return(new Point(place.x+direction.x,place.y+direction.y));
    }

    boolean checkIsSoldierAtExcept(Point position, Point except){
        if((position.x==except.x)&&(position.y==except.y))
            return(false);
        if(isOjectAt(position,DSCell.__DSEntity_Friend))
            return(true);
        if(isOjectAt(position,DSCell.__DSEntity_Enemy))
            return(true);
        return(false);
    }

    boolean isSoldierAroundExcept(Point position, Point except){
        Point pos;
        pos=new Point(position.x+1,position.y);
        if(checkIsSoldierAtExcept(pos,except))
            return(true);
        pos=new Point(position.x-1,position.y);
        if(checkIsSoldierAtExcept(pos,except))
            return(true);
        pos=new Point(position.x,position.y+1);
        if(checkIsSoldierAtExcept(pos,except))
            return(true);
        pos=new Point(position.x,position.y-1);
        if(checkIsSoldierAtExcept(pos,except))
            return(true);
        return(false);
    }

    public String getFreeNeighbourObject(int objectType, DSAgent agent){
        Point position;
        position=new Point(agent.getPosition().x+1,agent.getPosition().y);
        if(isOjectAt(position, objectType))
            if(!isSoldierAroundExcept(position,agent.getPosition()))
            return("e");
        position=new Point(agent.getPosition().x-1,agent.getPosition().y);
        if(isOjectAt(position, objectType))
            if(!isSoldierAroundExcept(position,agent.getPosition()))
                return("w");
        position=new Point(agent.getPosition().x,agent.getPosition().y+1);
        if(isOjectAt(position, objectType))
            if(!isSoldierAroundExcept(position,agent.getPosition()))
                return("s");
        position=new Point(agent.getPosition().x,agent.getPosition().y-1);
        if(isOjectAt(position, objectType))
            if(!isSoldierAroundExcept(position,agent.getPosition()))
                return("n");

        return("");
    }

    public LinkedList<Point> allObjects(int type) {
        LinkedList<DSCell> objects = PCells.get(type);
        LinkedList<Point> objectPositions = new LinkedList<Point>();
        if(objects==null)
            return(null);
        objects=(LinkedList<DSCell>)objects.clone();
        for (DSCell cell : objects) {
            objectPositions.add(cell.getPosition());
        }
        return (objectPositions);
    }

    public Point objectAroundCell(Point position, int objectType){// TODO tohle zobecnit na body

        LinkedList<DSCell> objects=PCells.get(objectType);

        if(isPositionInCellList(objects,new Point((int)position.getX()+1,(int)position.getY())))
            return(new Point((int)position.getX()+1,(int)position.getY()));

        if(isPositionInCellList(objects,new Point((int)position.getX()-1,(int)position.getY())))
            return(new Point((int)position.getX()-1,(int)position.getY()));

        if(isPositionInCellList(objects,new Point((int)position.getX(),(int)position.getY()+1)))
            return(new Point((int)position.getX(),(int)position.getY()+1));

        if(isPositionInCellList(objects,new Point((int)position.getX(),(int)position.getY()-1)))
            return(new Point((int)position.getX(),(int)position.getY()-1));

        return(null);
    }



    class sortByDistance implements Comparator<Point>
    {
        Point referencePoint;
        public int compare(Point a, Point b){
            if(distance(referencePoint, a)==distance(referencePoint, b))
                return(0);
            if (closer(referencePoint, a, b))
            return(-1);
                    else return(1);
        }
            public sortByDistance(Point rp){
            referencePoint=rp;
        }
    }

    LinkedList<Point> objectsSortedByDistance(int type, Point position){
        LinkedList<Point> objects=allObjects(type);
        if(objects==null)
            return(null);
        objects.sort(new sortByDistance(position));
        return(objects);
    }

    public Point nearestFreeBlock(int blockType, Point position){ // nejlbizsi blok od pozice nesousedici s agentem F/E
        LinkedList<Point> blocksSorted=objectsSortedByDistance(DSCell.__DSBlock+blockType, position);
        if(blocksSorted==null)
            return(null);
        for(Point position2:blocksSorted)
            if(objectAroundCell(position2,DSCell.__DSEntity_Enemy)==null)
                if(objectAroundCell(position2,DSCell.__DSEntity_Friend)==null)
                    return(position2);
            return(null);
    }


    public Point nearestObject(int type, Point agentPosition) {

        LinkedList<DSCell> objects=PCells.get(type);

        if(objects==null)
            return(null);
        if(objects.size()==0)
            return(null);

        LinkedList<DSCell> objects2=(LinkedList<DSCell>)objects.clone();

        Point nearest=objects.getFirst().getPosition();

        for(DSCell cell:objects2) {
            if(closer(agentPosition,cell.getPosition(),nearest))
                nearest=cell.getPosition();
        }
        return(nearest);
    }




    synchronized void removeCell(DSCell cellToRemove){
        if(cellToRemove==null)
            return;
        int type=cellToRemove.getType();
        if(PCells.get(type)==null)
            return;
        LinkedList<DSCell> cellsC=PCells.get(type);
        if(cellsC==null)
            return;
        cellsC=(LinkedList<DSCell>)cellsC.clone();
        LinkedList<DSCell> newCells=new LinkedList<DSCell>();
        if(cellsC!=null) {
            for (DSCell cell : cellsC) {
                if ((cell.getX() != cellToRemove.getX()) ||
                        (cell.getY() != cellToRemove.getY()))
                    newCells.add(cell);
            }
            PCells.remove(type);
            PCells.put(type,newCells);
        }
    }

    synchronized boolean clearMap(int radius, Point agentPos, int step) {
        Point point;
        DSCell cell;
        updateXYMinMax(agentPos.x-radius,agentPos.y-radius);
        updateXYMinMax(agentPos.x+radius,agentPos.y+radius);

        for(int j=-radius;j<=radius;j++)
            for(int i=-radius;i<=radius;i++) {
                if(Math.abs(i)+Math.abs(j)>radius)continue;
                point=new Point(agentPos.x+i,agentPos.y+j);
                if(PMap.containsKey(point)) {
                    removeCell(PMap.get(point));
                    PMap.remove(point);
                    PMap.put(point, new DSCell(point.x, point.y, DSCell.__DSClear, step));
                }
            }

        return(true);
    }


    synchronized void clearCells(int radius, Point agentPosition) {
        try {
            Iterator<Integer> typesI = ((Iterator<Integer>) (PCells.keySet().iterator()));
            LinkedList<DSCell> oldList;
            LinkedList<DSCell> newList;
            int type;
            for (Iterator<Integer> iterator = typesI; typesI.hasNext(); ) {
                type = iterator.next();
                oldList = (LinkedList<DSCell>) PCells.get(type).clone();
                newList = new LinkedList<DSCell>();
                for (DSCell cell : oldList)
                    if (Math.abs(cell.getX() - agentPosition.x) + Math.abs(cell.getY() - agentPosition.y) > radius)
                        newList.add(cell);
                PCells.put(type, newList);
            }
        }catch(Exception e){};
    }
     /*
        for(int type:typesI) {
            oldList = (LinkedList<DSCell>) PCells.get(type).clone();
            newList = new LinkedList<DSCell>();
            for (DSCell cell : oldList)
                if (Math.abs(cell.getX() - agentPosition.x) + Math.abs(cell.getY() - agentPosition.y) > radius)
                    newList.add(cell);
            PCells.put(type, newList);
        }*/



    public synchronized void clearArea(int radius, Point agentPosition, int step){
        clearMap(radius, agentPosition, step);
        clearCells(radius, agentPosition);
    }


    public boolean updateCell(DSCell cell) {
        int x=cell.getX();
        int y=cell.getY();
        updateXYMinMax(x, y);
        Point point = new Point(x, y);
        DSCell oldCell = PMap.get(point);
        if (oldCell != null)
            if (oldCell.getTimestamp() > cell.getTimestamp()) {
                return (false);
            }
        PMap.put(point, cell);

        // PCells
//        System.out.println("++ vkladam pro "+PAgent.getEntityName()+" ... "+cell.cellToString());
        if(PCells!=null)
        if(PCells.containsKey(cell.PType)) {
            if (PCells.get(cell.PType) != null) {
                LinkedList<DSCell> oldList =  PCells.get(cell.PType);
                LinkedList<DSCell> newList = new LinkedList<DSCell>();
                if(oldList!=null) {
                    LinkedList<DSCell> oldList2 = (LinkedList<DSCell>) oldList.clone();

                    for (DSCell cell2 : oldList2)
                        if ((cell2.getX() != cell.getX()) || (cell2.getY() != cell.getY()))
                            newList.add(cell2);
                }
                newList.add(cell);
                PCells.put(cell.PType,newList);
            }
        }

        else{
            LinkedList<DSCell> cellList=new LinkedList<DSCell>();
            cellList.add(cell);
            PCells.put(cell.PType,cellList);
        }
        return(true);
    }

    public synchronized void printFriends(){
        String st="Map members for "+PAgent.getEntityName();
        Set<DSAgent> agents=((HashMap<DSAgent,Point>)(PAgentPosition.clone())).keySet();
        for(DSAgent agent:agents){
            st=st+"/ "+agent.getAgentName()+"  @ ["+agent.getPosition().x+","+agent.getPosition().y+"] ";
        }
        System.out.println(st);
    }

    public synchronized void printCells(){
        String s="Objekty na mape "+PAgent.getEntityName();
        Set<Integer> types=((HashMap<Integer,DSCell>)PCells.clone()).keySet();
        for(int type:types) {
            s=s.concat(type + ":");
            LinkedList<DSCell> cells=(LinkedList<DSCell>)PCells.get(type).clone();
            if(cells!=null)
                for (DSCell cell : cells)
                    if(cell!=null)
                        s=s.concat("[" + cell.PX + ";" + cell.PY + " |"+cell.getTimestamp()+"],");
                s=s.concat("\n");
        }
        System.out.println(s);
    }

    public void printMap(String agentName){
        DSCell node;
        StringBuilder textMap = new StringBuilder();
        for(int j=PYMin;j<=PYMax;j++) {
            for(int i=PXMin;i<=PXMax;i++) {
                node=PMap.get(new Point(i,j));
                if(node!=null) {
                    if((j==0)&&(i==0)) {
                        textMap.append("X");
                    }else{
                        if (node.getType() == DSCell.__DSClear)
                            textMap.append(".");
                        if (node.getType() == DSCell.__DSObstacle)
                            textMap.append("O");
                        if (node.getType() == DSCell.__DSEntity_Friend)
                            textMap.append("F");

                        if (node.getType() == DSCell.__DSEntity_Enemy)
                            textMap.append("E");
                        if (node.getType() == DSCell.__DSMarker)
                            textMap.append("M");
                        if (node.getType() == DSCell.__DSGoal)
                            textMap.append("G");
                        if (node.getType() >= DSCell.__DSDispenser) {
                            int disType = node.getType() - DSCell.__DSDispenser;
                            textMap.append(disType);
                        }
                        if ((node.getType() >= DSCell.__DSBlock) && (node.getType() < DSCell.__DSDispenser))
                            textMap.append("B");
                    }
                }
                else {
                    textMap.append("-");
                }
                if(border.isOutside(new Point(i,j)))
                    textMap.append("*");
                textMap.append(" ".repeat(textMap.length()%2));
            }
            textMap.append(" \n");
        }
        HorseRider.inquire(TAG, "printMap: "+agentName+" Map min: ["+PXMin+","+PYMin+"] max:"+PXMax+","+PYMax+"]\n"+textMap);
        Point d0=nearestObject(DSCell.__DSDispenser,new Point(PX,PY));
        Point d1=nearestObject(DSCell.__DSDispenser+1,new Point(PX,PY));
        Point d2=nearestObject(DSCell.__DSDispenser+2,new Point(PX,PY));
        Point g=nearestObject(DSCell.__DSGoal,new Point(PX,PY) );
        printCells();
        printFriends();
        HorseRider.inform(TAG, "printMap: "+"Nearest dispenser 0 at "+d0+" d1 at "+d1+" d2 at "+d2+", nearest goal at"+g);
    }


    public boolean moveBy(int x, int y) {
        PX=PX+x;
        PY=PY+y;
        updateXYMinMax(PX,PY);
        return(true);
    }

    public boolean moveBy(DSAgent agent, int x, int y){
        Point pos=PAgentPosition.get(agent);
        pos.x=pos.x+x;
        pos.y=pos.y+y;
        return(true);
    }


    public void addAgent(DSAgent agent, Point displacement){
        System.out.println("Agent "+agent.getEntityName()+" position was "+agent.getPosition());
        Point pos=new Point((int)(displacement.x+agent.getPosition().getX()),
                (int)(displacement.y+agent.getPosition().getY()));
        PAgentPosition.put(agent,pos);
    }

    public void addNewAgent(DSAgent agent, Point position){
        PAgentPosition.put(agent,position);
    }

    public DSMap(DSAgent agent){
        PMap=new HashMap<Point, DSCell>();
     //   PDispensers=new HashMap<Integer,LinkedList<Point>>();
        PX=0; PY=0;
        PXMin=0;PXMax=0;PYMin=0;PYMax=0;
        PCells=new HashMap<Integer,LinkedList<DSCell>>();
        PAgent=agent;
        PAgentPosition=new HashMap<DSAgent, Point>();
        PAgentPosition.put(agent, new Point(0,0));
    }

    public void setBorder(Point agentPos, String plannedDirection) {
        border.addBorder(agentPos,plannedDirection);
        HorseRider.warn(TAG, "setBorder: "+agentPos+" "+plannedDirection);
     //   printMap("Border set");
    }

    private class Border { // TODO: this expects the border to always be box !!!!
        private boolean westIsSet = false;
        private boolean eastIsSet = false;
        private boolean northIsSet = false;
        private boolean southIsSet = false;
        private int westBorder;
        private int eastBorder;
        private int northBorder;
        private int southBorder;

        boolean isOutside(Point at) {
            if (westIsSet && at.getX() <= westBorder) {
                return true;
            }
            if (eastIsSet && at.getX() >= eastBorder) {
                return true;
            }
            if (northIsSet && at.getY() <= northBorder) {
                return true;
            }
            //noinspection RedundantIfStatement
            if (southIsSet && at.getY() >= southBorder) {
                return true;
            }
            return false;
        }

        void addBorder(Point agentPos, String direction) {
            switch (direction) {
                case "w":
                    westIsSet = true;
                    westBorder = agentPos.x - 1;
                    break;
                case "e":
                    eastIsSet = true;
                    eastBorder = agentPos.x + 1;
                    break;
                case "n":
                    northIsSet = true;
                    northBorder = agentPos.y - 1;
                    break;
                case "s":
                    southIsSet = true;
                    southBorder = agentPos.y + 1;
                    break;
                default:
                    break;
            }
        }
    }

}

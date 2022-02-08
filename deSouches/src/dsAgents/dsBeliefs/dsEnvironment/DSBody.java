package dsAgents.dsBeliefs.dsEnvironment;


import java.awt.*;
import java.util.LinkedList;

public class DSBody {
    LinkedList<DSCell> PBody;

    boolean contains(Point direction){
        for(DSCell cell:PBody)
            if((cell.getX()==direction.x)&&(cell.getY()==direction.y))
                return(true);
        return(false);
    }

    public static DSBody getDoubleBody(Point blockPos){
        DSBody body=new DSBody();
        body.addCell(new DSCell(blockPos.x,blockPos.y,0,0));
        return(body);
    }

    public boolean isCellAt(Point position){
        for(DSCell cell:PBody)
            if ((cell.getX()==position.x)&&(cell.getY()==position.y))
                return(true);
        return(false);
    }

    public int getCellType(Point position){
        for(DSCell cell:PBody)
            if ((cell.getX()==position.x)&&(cell.getY()==position.y))
                return(cell.getType());

            return(-1);
    }

    public LinkedList<DSCell> getBodyList()
    {
        return(PBody);
    }

    public String bodyToString(){
        String st="";
        for(DSCell cell:PBody)
            st=st+" ["+cell.getX()+","+cell.getY()+":"+cell.getType()+"]";
        return(st);
    }

    public static synchronized DSBody shiftBody(Point displacement, DSBody body){
        // pouzito v A*
        DSBody shiftedBody=new DSBody();
        shiftedBody.getBodyList().clear();
        for(DSCell cell:body.getBodyList()){
            shiftedBody.addCell(new DSCell(cell.getX()+displacement.x,
                    cell.getY()+displacement.y,
                    cell.getType(), cell.getTimestamp()));
        }
        return(shiftedBody);
    }

    public LinkedList<String> getAllDirectionsAttached(){
        LinkedList<String> directions=new LinkedList<String>();
        if(isCellAt(new Point (-1,0)))
            directions.add("w");
        if(isCellAt(new Point (1,0)))
            directions.add("e");
        if(isCellAt(new Point (0,-1)))
            directions.add("n");
        if(isCellAt(new Point (0,1)))
            directions.add("s");
        return(directions);
    }

    public Point blockAttached(int type) {
        for (DSCell cell : PBody)
            if ((cell.getType() == DSCell.__DSBlock + type))
                if ((Math.abs(cell.getX()) + Math.abs(cell.getY()) == 1))
                    return (new Point(cell.getX(), cell.getY()));
        return (null);
    }


    boolean pointInBody(DSBody body, Point point){
        for(DSCell cell2:body.getBodyList())
            if((point.x==cell2.getX())&&(point.y==cell2.getY()))
                return(true);
        return(false);
    }

    public boolean matchBody(DSBody body){
        if(PBody.size()!=body.getBodyList().size())
            return(false);

        for(DSCell cell:PBody){
            if(!pointInBody(body,cell.getPosition()))
                    return(false);
        }
        return(true);
    }

    DSCell rotateCW(DSCell cl){
        int sx,sy;
        if(cl.getX()==0)
            return(new DSCell(-cl.getY(),0,cl.getType(),cl.getTimestamp()));
        if(cl.getY()==0)
            return(new DSCell(0,cl.getX(),cl.getType(),cl.getTimestamp()));

        sx=Integer.signum(cl.getY())*-1;
        sy=Integer.signum(cl.getX());

        return(new DSCell(Math.abs(cl.getY())*sx,Math.abs(cl.getX())*sy,cl.getType(),cl.getTimestamp()));
    }

    DSCell rotateCCW(DSCell cl){
        int sx,sy;
        if(cl.getX()==0)
            return(new DSCell(cl.getY(),0,cl.getType(),cl.getTimestamp()));
        if(cl.getY()==0)
            return(new DSCell(0,-cl.getX(),cl.getType(),cl.getTimestamp()));

        sx=Integer.signum(cl.getX());
        sy=Integer.signum(cl.getY())*-1;
        return(new DSCell(Math.abs(cl.getY())*sx,Math.abs(cl.getX())*sy,cl.getType(),cl.getTimestamp()));
    }

    public void rotateBody(boolean cw){ //true cw, false ccw
        LinkedList<DSCell> newBody=new LinkedList<DSCell>();
        DSCell cl2;
        for(DSCell cl:PBody) {
            if(cw)
                cl2=rotateCW(cl);
            else
                cl2=rotateCCW(cl);
            newBody.add(cl2);
        }
        PBody=newBody;
    }

    public void addCell(DSCell cell) {
        PBody.add(cell);
        var b = true;
    }

    public void insertFirstCell(DSCell cell) {
        PBody.add(0,cell);
        var b = true;
    }


    public DSBody cloneBody(){
        DSBody newBody=new DSBody();
        for(DSCell cell:PBody)
            if((cell.getX()!=0)||(cell.getY()!=0))
            newBody.addCell(cell);
        return(newBody);
    }

    public void resetBody(){
        PBody=new LinkedList<DSCell>();
        PBody.add(new DSCell(0,0,DSCell.__DSAgent,0));  // at least, agent is at [0,0]
    }

    public void removePart(Point position) {
        LinkedList<DSCell> newBody=new LinkedList<DSCell>();
        for(DSCell cell:PBody)
            if((cell.getX()!=position.x)||(cell.getY()!=position.y))
                    newBody.add(cell);
            PBody=newBody;
    }


    public DSBody() {
        resetBody();
    }

}

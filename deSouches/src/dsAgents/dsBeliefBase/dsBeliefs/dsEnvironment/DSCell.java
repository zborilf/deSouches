package dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment;

import java.awt.*;


public class DSCell {

    public static int __DSClear=0;
    public static int __DSObstacle=1;
    public static int __DSEntity_Friend=2;
    public static int __DSEntity_Enemy=3;
    public static int __DSMarker=4;
    public static int __DSBorder=5;
    public static int __DSGoal=6;
    public static int __DSAgent=7;
    public static int __DSBlock=50;
    public static int __DSDispenser=100;


    int PType, PX, PY, PTimeStamp;

    public void setType(int type) {
        PType=type;
    }

    public int getType() {
        return(PType);
    }

    public int getX() {
        return(PX);
    }

    public int getY() {
        return(PY);
    }

    public Point getPosition() { return(new Point(PX,PY));}

    public int getTimestamp() {
        return(PTimeStamp);
    }

    public void setX(int x) {
        PX=x;
    }

    public void setY(int y) {
        PY=y;
    }


    public boolean positionMatch(int x, int y) {
        if((x==PX)&&(y==PY))
            return(true);
        return(false);
    }

    public String cellToString(){
        String st="Cell at ["+PX+","+PY+"]"+"/"+PType+"/"+PTimeStamp;
        return(st);
    }

    public DSCell(int x, int y, int type, int timestamp) {
        // bude tam pozice rel. k agentovi, objekt, casove razitko
        PX=x;
        PY=y;
        PType=type;
        PTimeStamp=timestamp;
    }
}

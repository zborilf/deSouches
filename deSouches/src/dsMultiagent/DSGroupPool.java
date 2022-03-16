package dsMultiagent;

import deSouches.utils.HorseRider;
import dsMultiagent.dsTasks.DSTask;

import java.util.LinkedList;

public class DSGroupPool {
    private static final String TAG = "DSGroupPool";
    LinkedList<DSGroup> PGroups=new LinkedList<DSGroup>();

    public void printGroups(){
        LinkedList<DSGroup> groupsClone;
        groupsClone=(LinkedList<DSGroup>)PGroups.clone();
        for(DSGroup group:groupsClone){
            group.printGroup();
        }
    }

    public boolean addGroup(DSGroup group){
        PGroups.add(group);
        return(true);
    }

    public boolean removeGroup(DSGroup group){
        if(!PGroups.contains(group))
            return(false);
        PGroups.remove(group);
        return(true);
    }

    public LinkedList<DSGroup> getCapableGroups(DSTask task, int priority){
        LinkedList<DSGroup> capableGroups=new LinkedList<DSGroup>();
        LinkedList<DSGroup> groups=(LinkedList<DSGroup>)PGroups.clone();
        for(DSGroup group:groups){
              if(group.isCapable(task, priority)) {
                capableGroups.add(group);
            }
        }
        return(capableGroups);
    }

    public LinkedList<DSGroup> getGroups(){
        return(PGroups);
    }

}

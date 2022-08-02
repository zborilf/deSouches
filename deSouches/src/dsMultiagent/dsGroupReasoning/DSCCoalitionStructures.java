package dsMultiagent.dsGroupReasoning;

import java.util.ArrayList;
import java.util.Comparator;

public class DSCCoalitionStructures {

    int PNOTasks;
    ArrayList<DSCTaskItem> PTasks;
    ArrayList<DSCTaskItem> PCoalition;


    class dscComparator implements Comparator<DSCTaskItem> {
        public int compare(DSCTaskItem item1, DSCTaskItem item2){
            if(item1.getPPrice()>item2.getPPrice())
                return(1);
            if(item1.getPPrice()<item2.getPPrice())
                return(-1);
            return(0);
        }

        public boolean equals(Object item){
            return(false);
        }
    }

    public boolean addToTasks(DSCTaskItem item){
        int i=0;
        while((i<PTasks.size())&&(item.getPPrice()>PTasks.get(i).getPPrice()))
            i++;
        PTasks.add(i,item);
        return(true);
    }

    public void printTasks(){
        for(DSCTaskItem item:PTasks)
            item.printTask();
    }

    public DSCCoalitionStructures(int noTasks){
        PNOTasks=noTasks;
        PTasks=new ArrayList<DSCTaskItem>();
    }


}

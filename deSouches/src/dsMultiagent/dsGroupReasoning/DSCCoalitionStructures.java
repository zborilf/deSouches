package dsMultiagent.dsGroupReasoning;

import java.util.ArrayList;
import java.util.Comparator;

public class DSCCoalitionStructures {

    int PNOTasks;
    ArrayList<DSCCoalitionMember> PTasks;
    ArrayList<DSCCoalitionMember> PCoalition;


    class dscComparator implements Comparator<DSCCoalitionMember> {
        public int compare(DSCCoalitionMember item1, DSCCoalitionMember item2){
            if(item1.getPrice()>item2.getPrice())
                return(1);
            if(item1.getPrice()<item2.getPrice())
                return(-1);
            return(0);
        }

        public boolean equals(Object item){
            return(false);
        }
    }

    public boolean addToTasks(DSCCoalitionMember item){
        int i=0;
        while((i<PTasks.size())&&(item.getPrice()>PTasks.get(i).getPrice()))
            i++;
        PTasks.add(i,item);
        return(true);
    }

    public void printTasks(){
        for(DSCCoalitionMember item:PTasks)
            item.printTask();
    }

    public DSCCoalitionStructures(int noTasks){
        PNOTasks=noTasks;
        PTasks=new ArrayList<DSCCoalitionMember>();
    }


}

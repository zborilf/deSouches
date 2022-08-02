package dsMultiagent.dsGroupReasoning;
import java.util.ArrayList;

public class DSCCoalition {
    private final int PNOTasks;
    private ArrayList<DSCTaskItem> PMembers;

    public boolean addMember(DSCTaskItem member){
        // only when goal matches, taskID is unique
        if(PMembers.get(0).getPGoal()!=member.getPGoal())
            return(false);
        for(DSCTaskItem oldMember:PMembers)
            if((oldMember.getPTaskID()==member.getPTaskID())||(oldMember.getPAgentPosition()==member.getPAgentPosition()))
                return(false);
        PMembers.add(member);
        return(true);
    }

    public boolean completeCoalition(){
        return(PMembers.size()==PNOTasks);
    }

    public void printCoalition(){
        System.out.println("Coalition:");
        for(DSCTaskItem item:PMembers)
            item.printTask();
    }

    public DSCCoalition(int noTasks, DSCTaskItem firstMember){
        PNOTasks=noTasks;
        PMembers=new ArrayList<DSCTaskItem>();
        PMembers.add(firstMember);
    }
}

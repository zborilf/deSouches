package dsMultiagent.dsGroupReasoning;
import java.util.ArrayList;

public class DSCCoalition {
    private final int PNOTasks;
    private ArrayList<DSCCoalitionMember> PMembers;

    public boolean addMember(DSCCoalitionMember member){
        // only when goal matches, taskID is unique
        if(PMembers.get(0).getGoal()!=member.getGoal())
            return(false);
        for(DSCCoalitionMember oldMember:PMembers)
            if((oldMember.getTaskID()==member.getTaskID())||(oldMember.getAgentPosition()==member.getAgentPosition()))
                return(false);
        PMembers.add(member);
        return(true);
    }

    public ArrayList<DSCCoalitionMember> getCoalitionMembers() {
        return PMembers;
    }

    public boolean completeCoalition(){
        return(PMembers.size()==PNOTasks);
    }

    public void printCoalition(){
        System.out.println("Coalition:");
        for(DSCCoalitionMember item:PMembers)
            item.printTask();
    }

    public String coalition2String(){
        String c="Coalition:\n";
        for(DSCCoalitionMember item:PMembers)
            c=c+item.task2String();
        return(c);
    }

    public DSCCoalition(int noTasks, DSCCoalitionMember firstMember){
        PNOTasks=noTasks;
        PMembers=new ArrayList<DSCCoalitionMember>();
        PMembers.add(firstMember);
    }
}

package dsMultiagent.dsGroupOptions;

import dsMultiagent.dsTasks.DSTask;

public class dsGroupTaskOption extends dsGroupOption{

    DSTask PTask;

    public DSTask getTask(){
        return(PTask);
    }

    public dsGroupTaskOption(DSTask task){
        POptionType=__taskOption;
        PTask=task;
        POptionName=task.getName();
    }
}

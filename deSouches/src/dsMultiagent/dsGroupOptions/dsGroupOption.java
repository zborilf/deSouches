package dsMultiagent.dsGroupOptions;

import dsMultiagent.dsTasks.DSTask;

public class dsGroupOption {

    public final static int __taskOption=1;
    public final static int __dispenserSpottedOption=2;
    public final static int __goalZoneSpottedOption=3;
    public final static int __roleZoneSpottedOption=4;
    public final static int __scenarioCompletedOption=5;
    public final static int __scenarionFailedOption=6;


    int POptionType;
    String POptionName;

    public String getOptionName(){
        return(POptionName);
    }

}

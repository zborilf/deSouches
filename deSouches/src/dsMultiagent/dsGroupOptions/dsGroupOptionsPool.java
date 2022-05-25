package dsMultiagent.dsGroupOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class dsGroupOptionsPool {
    HashMap<String, dsGroupOption> POptions;

    public List<dsGroupOption> getOptions(){
        HashMap<String, dsGroupOption> optionsClone=(HashMap<String, dsGroupOption>)POptions.clone();
        List<dsGroupOption> options=optionsClone.values().stream().toList();
        return(options);
    }

    public boolean removeOption(String key){
        POptions.remove(key);
        return(true);
    }

    public boolean addOption(dsGroupOption option){
        if(POptions.containsKey(option.getOptionName()))
            return(false);
        POptions.put(option.getOptionName(),option);
        return(true);
    }

    public dsGroupOptionsPool(){
        POptions=new HashMap<String, dsGroupOption>();

    }
}

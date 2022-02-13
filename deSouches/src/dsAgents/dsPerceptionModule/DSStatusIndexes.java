package dsAgents.dsPerceptionModule;

import java.util.HashMap;
import java.util.Map;

public class DSStatusIndexes {
    public final static int __action_success=1;
    public final static int __action_partial_success=2;
    public final static int __action_failed=3;
    public final static int __action_failed_parameter=4;
    public final static int __action_failed_target=5;
    public final static int __action_failed_blocked=6;
    public final static int __action_failed_path=7;
    public final static int __action_failed_partner=8;
    public final static int __action_failed_resources=9;
    public final static int __action_failed_location=10;
    public final static int __action_failed_random=11;
    public final static int __action_failed_status=12;
    public final static int __action_failed_role=13;
    public final static int __action_unknown_action=14;



    static Map<String, Integer> _resultMap=new HashMap<String, Integer>()
    {{
        put("success", __action_success);
        put("partial_success", __action_partial_success);
        put("failed", __action_failed);
        put("failed_parameter", __action_failed_parameter);
        put("failed_target", __action_failed_target);
        put("failed_blocked", __action_failed_blocked);
        put("failed_path", __action_failed_path);
        put("failed_partner", __action_failed_partner);
        put("failed_resource", __action_failed_resources);
        put("failed_location", __action_failed_location);
        put("failed_random", __action_failed_random);
        put("failed_status", __action_failed_status);
        put("failed_role", __action_failed_role);
        put("unknown_action", __action_unknown_action);
    }};

    public static int getIndex(String beliefName){
        if(_resultMap.containsKey(beliefName))
            return(_resultMap.get(beliefName));
        return(-1);
    }

}

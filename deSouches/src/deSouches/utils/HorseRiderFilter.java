package deSouches.utils;

import java.util.Map;

/**
 * @author : Vaclav Uhlir
 * @since : 11.9.2019
 **/
final class HorseRiderFilter {

    /** Tag filter, !is overwritten by {@link #DEBUG_level} and turnoffs !  */
    static final Map<String, Integer> SUB_RULES = Map.of(
            "DSAStar", 3,       // don't show wtf-s for DSAStar
            "DSAStarItem", 0,    // show only errors for DSAStarItem
            "DSGoalRoam", 2    // show only errors for DSAStarItem
    );

    // global turn off
    /** DEBUG level detailed  */
    static final boolean WTF = true;        // 4
    /** DEBUG level debug  */
    static final boolean DEBUG = true;      // 3
    /** DEBUG level verbose  */
    static final boolean INFORM = true;     // 2
    /** DEBUG level warning  */
    static final boolean WARNING = true;    // 1
    /** DEBUG level error  */
    static final boolean ERROR = true;      // 0

    // ease of use level cutoff
    /** DEBUG level detailed  */
    static final int DEBUG_level = 1;
}

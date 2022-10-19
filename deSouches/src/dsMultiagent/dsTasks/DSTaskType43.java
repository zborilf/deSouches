package dsMultiagent.dsTasks;

import dsAgents.DSAgent;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSBody;
import dsAgents.dsBeliefBase.dsBeliefs.dsEnvironment.DSCell;

import java.awt.*;

public class DSTaskType43 extends DSTaskType{
    DSBody PFormationBody;


    @Override
    public DSBody getTaskBody() {
        return null;
    }

    public Point formationPosition(DSAgent agent, Point position) {
        if (agent == PMaster) return (new Point(position.x, position.y));
        return null;
    }

    @Override
    public Point blockPosition(int agent) {
        if (agent == 1) return (new Point(0, 1));
        return (null);
    }

    @Override
    public Point blockPosition(DSAgent agent) {
        return (blockPosition(1));
    }

    public DSTaskType43() {
        super();
        PTaskTypeNumber = 43;
        PTaskArea.addCell(new DSCell(0, 1, 0, 0));
    }
}





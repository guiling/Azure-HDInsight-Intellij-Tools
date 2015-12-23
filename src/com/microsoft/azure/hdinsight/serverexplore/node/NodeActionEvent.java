package com.microsoft.azure.hdinsight.serverexplore.node;

import java.util.EventObject;

/**
 * Created by joezhang on 15-12-2.
 */

public class NodeActionEvent extends EventObject {
    public NodeActionEvent(NodeAction action) {
        super(action);
    }

    public NodeAction getAction() {
        return (NodeAction) getSource();
    }
}

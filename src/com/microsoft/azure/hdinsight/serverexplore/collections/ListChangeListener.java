package com.microsoft.azure.hdinsight.serverexplore.collections;

import java.util.EventListener;

/**
 * Created by joezhang on 15-12-2.
 */
public interface ListChangeListener extends EventListener {
    void listChanged(ListChangedEvent e);
}

package com.microsoft.azure.hdinsight.serverexplore.node;

import com.microsoft.azure.hdinsight.common.AzureCmdException;
import com.microsoft.azure.hdinsight.components.DefaultLoader;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Semaphore;

/**
 * Created by joezhang on 15-12-2.
 */

public class EventHelper {
    public interface EventStateHandle {
        boolean isEventTriggered();
    }

    public interface EventWaitHandle {
        void waitEvent(@NotNull Runnable callback) throws AzureCmdException;
    }

    public interface EventHandler {
        EventWaitHandle registerEvent()
                throws AzureCmdException;

        void unregisterEvent(@NotNull EventWaitHandle waitHandle)
                throws AzureCmdException;

        void interruptibleAction(@NotNull EventStateHandle eventState)
                throws AzureCmdException;

        void eventTriggeredAction() throws AzureCmdException;
    }

    private static class EventSyncInfo implements EventStateHandle {
        private final Object eventSync = new Object();
        Semaphore semaphore = new Semaphore(0);

        EventWaitHandle eventWaitHandle;
        boolean registeredEvent = false;
        boolean eventTriggered = false;
        AzureCmdException exception;

        public boolean isEventTriggered() {
            synchronized (eventSync) {
                return eventTriggered;
            }
        }
    }

    public static void runInterruptible(@NotNull final EventHandler eventHandler)
            throws AzureCmdException {
        final EventSyncInfo eventSyncInfo = new EventSyncInfo();

        eventSyncInfo.eventWaitHandle = eventHandler.registerEvent();
        eventSyncInfo.registeredEvent = true;

        DefaultLoader.getIdeHelper().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventSyncInfo.eventWaitHandle.waitEvent(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (eventSyncInfo.eventSync) {
                                if (eventSyncInfo.registeredEvent) {
                                    eventSyncInfo.registeredEvent = false;
                                    eventSyncInfo.eventTriggered = true;
                                    eventSyncInfo.semaphore.release();
                                }
                            }
                        }
                    });
                } catch (AzureCmdException ignored) {
                }
            }
        });

        DefaultLoader.getIdeHelper().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventHandler.interruptibleAction(eventSyncInfo);

                    synchronized (eventSyncInfo.eventSync) {
                        if (eventSyncInfo.registeredEvent) {
                            eventSyncInfo.registeredEvent = false;
                            eventSyncInfo.semaphore.release();
                        }
                    }
                } catch (AzureCmdException ex) {
                    synchronized (eventSyncInfo.eventSync) {
                        if (eventSyncInfo.registeredEvent) {
                            eventSyncInfo.registeredEvent = false;
                            eventSyncInfo.exception = ex;
                            eventSyncInfo.semaphore.release();
                        }
                    }
                }
            }
        });

        try {
            eventSyncInfo.semaphore.acquire();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } finally {
            eventHandler.unregisterEvent(eventSyncInfo.eventWaitHandle);
        }

        synchronized (eventSyncInfo.eventSync) {
            if (!eventSyncInfo.eventTriggered) {
                if (eventSyncInfo.exception != null) {
                    throw eventSyncInfo.exception;
                }

                eventHandler.eventTriggeredAction();
            }
        }
    }
}

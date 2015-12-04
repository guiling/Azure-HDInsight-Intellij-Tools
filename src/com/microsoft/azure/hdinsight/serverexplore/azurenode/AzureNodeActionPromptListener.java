package com.microsoft.azure.hdinsight.serverexplore.azurenode;

import com.microsoft.azure.hdinsight.components.DefaultLoader;
import com.microsoft.azure.hdinsight.serverexplore.node.Node;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.util.concurrent.Callable;

/**
 * Created by joezhang on 15-12-2.
 */

public abstract class AzureNodeActionPromptListener extends AzureNodeActionListener {
    private String promptMessage;
    private int optionDialog;

    public AzureNodeActionPromptListener(@NotNull Node azureNode,
                                         @NotNull String promptMessage,
                                         @NotNull String progressMessage) {
        super(azureNode, progressMessage);
        this.promptMessage = promptMessage;
    }

    @NotNull
    @Override
    protected Callable<Boolean> beforeAsyncActionPerfomed() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                DefaultLoader.getIdeHelper().invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        optionDialog = JOptionPane.showOptionDialog(null,
                                promptMessage,
                                "Service explorer",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                new String[]{"Yes", "No"},
                                null);
                    }
                });

                return (optionDialog == JOptionPane.YES_OPTION);
            }
        };
    }
}
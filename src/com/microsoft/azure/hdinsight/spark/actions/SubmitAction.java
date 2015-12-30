package com.microsoft.azure.hdinsight.spark.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.microsoft.azure.hdinsight.common.DefaultLoader;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;
import com.microsoft.azure.hdinsight.common.IDEHelper;
import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.projects.HDInsightModuleType;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.serverexplore.HDExploreException;
import com.microsoft.azure.hdinsight.spark.UI.SparkSubmissionDialog;
import com.microsoft.azure.hdinsight.spark.UI.SparkSubmissionToolWindowFactory;

import javax.swing.*;
import java.util.List;

/**
 * Created by guizha on 8/21/2015.
 */
public class SubmitAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        List<IClusterDetail>cachedClusterDetails = HDInsightHelper.getInstance().getcachedClusterDetails();
        if(cachedClusterDetails == null){
            ToolWindow sparkSubmissionToolWindow =
                    ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(SparkSubmissionToolWindowFactory.SPARK_SUBMISSION_WINDOW);
            sparkSubmissionToolWindow.show(() -> {
                DefaultLoader.getIdeHelper().executeOnPooledThread(() -> {
                    try {
                        HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo("Info : Listing spark clusters ....");
                        HDInsightHelper.getInstance().getClusterDetails();
                        List<IClusterDetail> newCachedClusterDetails = HDInsightHelper.getInstance().getcachedClusterDetails();
                        HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo("Info : List spark clusters successfully");

                        SparkSubmissionDialog sparkSubmissionDialog = new SparkSubmissionDialog(anActionEvent.getProject(), newCachedClusterDetails);
                        sparkSubmissionDialog.setVisible(true);

                    } catch (HDExploreException e) {
                        HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setError("Error : Failed to list spark clusters. " + e.getMessage());
                    }
                });
            });}
        else{
            SparkSubmissionDialog sparkSubmissionDialog = new SparkSubmissionDialog(anActionEvent.getProject(),cachedClusterDetails);
            sparkSubmissionDialog.setVisible(true);
        }
    }

    public void update(AnActionEvent event) {
        final Module module = event.getData(LangDataKeys.MODULE);
        VirtualFile selectedFile = CommonDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        event.getPresentation().setEnabledAndVisible(module != null
                && module.getOptionValue(Module.ELEMENT_TYPE).equals(HDInsightModuleType.getInstance().getId())
                && PluginUtil.isModuleRoot(selectedFile, module));
    }
}

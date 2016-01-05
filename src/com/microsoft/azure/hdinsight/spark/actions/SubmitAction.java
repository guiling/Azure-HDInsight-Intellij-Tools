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
import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.projects.HDInsightModuleType;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.spark.UI.SparkSubmissionDialog;
import com.microsoft.azure.hdinsight.spark.UI.SparkSubmissionToolWindowFactory;

import java.util.List;

/**
 * Created by guizha on 8/21/2015.
 */
public class SubmitAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        List<IClusterDetail>cachedClusterDetails = HDInsightHelper.getInstance().getCachedClusterDetails();
        if(cachedClusterDetails == null){
            ToolWindow sparkSubmissionToolWindow =
                    ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow(SparkSubmissionToolWindowFactory.SPARK_SUBMISSION_WINDOW);
            sparkSubmissionToolWindow.show(() -> {
                DefaultLoader.getIdeHelper().executeOnPooledThread(() -> {

                        HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().clearAll();
                        HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo("Info : Listing spark clusters ....");
                        HDInsightHelper.getInstance().getClusterDetails();
                        List<IClusterDetail> newCachedClusterDetails = HDInsightHelper.getInstance().getCachedClusterDetails();
                     if(HDInsightHelper.getInstance().isListClusterSuccess()) {
                         HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo("Info : List spark clusters successfully");
                         SparkSubmissionDialog sparkSubmissionDialog = new SparkSubmissionDialog(anActionEvent.getProject(), newCachedClusterDetails);
                         sparkSubmissionDialog.setVisible(true);
                     }else{
                         HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setError("Error : Failed to list spark clusters.");
                     }});
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

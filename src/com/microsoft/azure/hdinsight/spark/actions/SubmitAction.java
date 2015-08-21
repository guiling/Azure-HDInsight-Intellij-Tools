package com.microsoft.azure.hdinsight.spark.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleTypeId;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.azure.hdinsight.common.PluginUtil;
import com.microsoft.azure.hdinsight.projects.HDInsightModuleType;
import com.microsoft.azure.hdinsight.spark.UI.SparkSubmissionDialog;

/**
 * Created by guizha on 8/21/2015.
 */
public class SubmitAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        SparkSubmissionDialog sparkSubmissionDialog = new SparkSubmissionDialog();
        sparkSubmissionDialog.setVisible(true);
    }

    public void update(AnActionEvent event) {
        final Module module = event.getData(LangDataKeys.MODULE);
        VirtualFile selectedFile = CommonDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        event.getPresentation().setEnabledAndVisible(module != null
                && module.getOptionValue(Module.ELEMENT_TYPE).equals(HDInsightModuleType.getInstance().getId())
                && PluginUtil.isModuleRoot(selectedFile, module));
    }
}

package com.microsoft.azure.hdinsight.projects;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.microsoft.azure.hdinsight.projects.UI.HDInsightTemplatesPanel;

import javax.swing.*;

/**
 * Created by zhax on 8/21/2015.
 */
public class HDInsightModuleWizardStep extends ModuleWizardStep {
    private final HDInsightTemplatesPanel templatesPanel = new HDInsightTemplatesPanel();
    private HDInsightModuleBuilder builder;

    public HDInsightModuleWizardStep(HDInsightModuleBuilder builder) {
        this.builder = builder;
    }

    @Override
    public JComponent getComponent() {
        return templatesPanel;
    }

    @Override
    public void updateDataModel() {
        builder.setSelectedTemplate(templatesPanel.getSelectedTemplate());
    }


    @Override
    public boolean validate() throws ConfigurationException {
        if (templatesPanel.getSelectedTemplate() == null) {
            throw new ConfigurationException("Specify HDInsight Template");
        }

        return super.validate();
    }
}

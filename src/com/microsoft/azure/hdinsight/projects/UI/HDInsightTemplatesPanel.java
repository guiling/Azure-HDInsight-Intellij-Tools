package com.microsoft.azure.hdinsight.projects.UI;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.microsoft.azure.hdinsight.projects.HDInsightTemplateItem;
import com.microsoft.azure.hdinsight.projects.HDInsightTemplates;

import javax.swing.*;
import java.awt.*;

/**
 * Created by zhax on 8/21/2015.
 */
public class HDInsightTemplatesPanel extends JPanel {
    private JScrollPane scrollPanel;
    private JList templatesList;

    public HDInsightTemplatesPanel() {
        super(new BorderLayout());

        this.createUIComponents();
        scrollPanel = new JBScrollPane(templatesList);
        add(scrollPanel, BorderLayout.CENTER);
    }

    public HDInsightTemplateItem getSelectedTemplate(){
        return (HDInsightTemplateItem) this.templatesList.getSelectedValue();
    }

    private void createUIComponents() {
        DefaultListModel<HDInsightTemplateItem> listModel = new DefaultListModel<HDInsightTemplateItem>();
        java.util.List<HDInsightTemplateItem> templates = HDInsightTemplates.getTemplates();
        for(HDInsightTemplateItem templateItem : templates) {
            listModel.addElement(templateItem);
        }

        templatesList = new JBList();
        templatesList.setModel(listModel);
        templatesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}

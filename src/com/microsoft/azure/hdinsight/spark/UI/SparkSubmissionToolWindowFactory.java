package com.microsoft.azure.hdinsight.spark.UI;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Created by joezhang on 15-12-23.
 */
public class SparkSubmissionToolWindowFactory implements ToolWindowFactory {

    public static final String SPARK_SUBMISSION_WINDOW = "Spark Submission";
    private JEditorPane jEditorPanel;
    private String fontName;
    private StringBuilder stringBuilder = new StringBuilder();
    private String toolWindowText;

    private PropertyChangeSupport changeSupport;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        HDInsightHelper.getInstance().setSparkSubmissionToolWindowFactory(this);

        jEditorPanel = new JEditorPane();
        fontName = jEditorPanel.getFont().getFamily();
        toolWindow.getComponent().add(new JBScrollPane(jEditorPanel));

        jEditorPanel.setEditable(false);
        jEditorPanel.setOpaque(false);
        jEditorPanel.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));

        jEditorPanel.addHyperlinkListener((event) -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    } catch (Exception e1) {
                    }
                }
            }
        });

        PropertyChangeListener propertyChangeListener = (evt) -> {
            if (evt.getPropertyName().equals("toolWindowText")) {
                SwingUtilities.invokeLater(() -> {
                    jEditorPanel.setText(evt.getNewValue().toString());
                });
            }
        };

        jEditorPanel.addPropertyChangeListener(propertyChangeListener);
        changeSupport = new PropertyChangeSupport(jEditorPanel);
        changeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public void setHyperlink(String hyperlinkUrl, String anchorText) {
        String hyperLink = String.format("<a href=\"%s\"><font face=\"%s\">%s</font></a><br />", hyperlinkUrl,fontName, anchorText);
        stringBuilder.append(hyperLink);
        setToolWindowText(stringBuilder.toString());
    }

    public void setError(String errorInfo) {
        String errorText = String.format("<font color=\"red\" face=\"%s\">%s</font><br />", fontName, errorInfo);
        stringBuilder.append(errorText);
        setToolWindowText(stringBuilder.toString());
    }

    public void setInfo(String info) {
        String infoText = String.format("<font face=\"%s\">%s</font><br />", fontName, info);
        stringBuilder.append(infoText);
        setToolWindowText(stringBuilder.toString());
    }


    private StringBuilder previousBuilder;

    public void setDuplicatedInfo(String info) {
        if (previousBuilder == null) {
            previousBuilder = new StringBuilder(stringBuilder);
        }else {
            stringBuilder =  new StringBuilder(previousBuilder);
        }

        String infoText = String.format("<font face=\"%s\">%s</font><br />", fontName, info);
        stringBuilder.append(infoText);
        setToolWindowText(stringBuilder.toString());
    }

    public void clearAll() {
        if(previousBuilder != null) {
            previousBuilder.setLength(0);
            previousBuilder = null;
        }

        stringBuilder.setLength(0);
    }

    private void setToolWindowText(String toolWindowText) {
        changeSupport.firePropertyChange("toolWindowText", this.toolWindowText, toolWindowText);
        this.toolWindowText = toolWindowText;
    }
}

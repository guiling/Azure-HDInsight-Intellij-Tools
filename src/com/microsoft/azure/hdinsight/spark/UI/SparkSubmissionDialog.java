package com.microsoft.azure.hdinsight.spark.UI;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.microsoft.azure.hdinsight.common.DefaultLoader;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;
import com.microsoft.azure.hdinsight.common.StringHelper;
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.storage.BlobContainer;
import com.microsoft.azure.hdinsight.sdk.storage.CallableSingleArg;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.sdk.storage.StorageClientImpl;
import com.microsoft.azure.hdinsight.spark.UIHelper.InteractiveRenderer;
import com.microsoft.azure.hdinsight.spark.UIHelper.InteractiveTableModel;
import com.microsoft.azure.hdinsight.spark.common.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class SparkSubmissionDialog extends JDialog {

    private JPanel contentPane;

    private final int leftControlWidth = 200;
    private final int rightControlWidth = 300;
    private final int controlHeight = 25;
    private final int tableHeight = 120;
    private final int margin = 10;
    private final String DialogTitle = "Spark Submission";

    private ComboBox clustersListComboBox;
    private JTextField mainClassTextField;
    private JBTable jobConfigurationTable;
    private JTextField commandLineTextField;
    private JTextField referencedJarsTextField;
    private JTextField referencedFilesTextField;
    private ComboBox timeoutComboBox;
    private Project project;

    private Map<String, IClusterDetail> mapClusterNameToClusterDetail = new HashMap<>();
    private Map<String, Integer> mapTimeoutTextToSeconds = new HashMap<>();

    public SparkSubmissionDialog(Project project, List<IClusterDetail> cachedClusterDetails) {
        setContentPane(contentPane);
        setModal(true);
        setSize(new Dimension(550, 520));
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle(DialogTitle);
        contentPane.setLayout(new GridBagLayout());
        this.project = project;

        addSparkClustersLineItem();
        addMainClassNameLineItem();
        addConfigurationLineItem();
        addCommandlineArgsLineItem();
        addTimeoutLineItem();
        addReferencedJarsLineItem();
        addReferencedFilesLineItem();
        addOperationJPanel();

        initializeControls(cachedClusterDetails);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initializeControls(List<IClusterDetail> cachedClusterDetails) {
        if (cachedClusterDetails != null) {
            for (IClusterDetail clusterDetail : cachedClusterDetails) {
                mapClusterNameToClusterDetail.put(clusterDetail.getName(), clusterDetail);
                clustersListComboBox.addItem(clusterDetail.getName());
            }
        }

        mapTimeoutTextToSeconds.put("2 minus", 120);
        mapTimeoutTextToSeconds.put("5 minus", 300);
        mapTimeoutTextToSeconds.put("10 minus", 600);
        mapTimeoutTextToSeconds.put("30 minus", 1800);
        for (String timeoutKey : mapTimeoutTextToSeconds.keySet()) {
            timeoutComboBox.addItem(timeoutKey);
        }

        timeoutComboBox.setSelectedItem("2 minus");
    }

    private void addSparkClustersLineItem() {
        JLabel sparkClusterLabel = new JLabel("Spark clusters");
        sparkClusterLabel.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        sparkClusterLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c11 = new GridBagConstraints();
        c11.gridx = 0;
        c11.gridy = 0;
        c11.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(sparkClusterLabel, c11);

        clustersListComboBox = new ComboBox();
        clustersListComboBox.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c12 = new GridBagConstraints();
        c12.gridx = 1;
        c12.gridy = 0;
        c12.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(clustersListComboBox, c12);
    }

    private void addMainClassNameLineItem() {
        JLabel sparkClusterLabel = new JLabel("Main class name");
        sparkClusterLabel.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        sparkClusterLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c21 = new GridBagConstraints();
        c21.gridx = 0;
        c21.gridy = 1;
        c21.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(sparkClusterLabel, c21);

        mainClassTextField = new JTextField();
        mainClassTextField.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c22 = new GridBagConstraints();
        c22.gridx = 1;
        c22.gridy = 1;
        c22.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(mainClassTextField, c22);
    }

    private void addConfigurationLineItem() {
        JLabel jobConfigurationLabel = new JLabel("Job configurations");
        jobConfigurationLabel.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        jobConfigurationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c31 = new GridBagConstraints();
        c31.gridx = 0;
        c31.gridy = 2;
        c31.anchor = GridBagConstraints.NORTH;
        c31.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(jobConfigurationLabel, c31);

        String[] columns = {"Key", "Value", ""};

        jobConfigurationTable = new JBTable();
        InteractiveTableModel tableModel = new InteractiveTableModel(columns);
        jobConfigurationTable.setModel(tableModel);
        jobConfigurationTable.setSurrendersFocusOnKeystroke(true);
        jobConfigurationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobConfigurationTable.setColumnSelectionAllowed(true);

        if (!tableModel.hasEmptyRow()) {
            tableModel.addEmptyRow();
        }

        TableColumn hidden = jobConfigurationTable.getColumnModel().getColumn(InteractiveTableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new InteractiveRenderer(InteractiveTableModel.HIDDEN_INDEX));

        JBScrollPane scrollPane = new JBScrollPane(jobConfigurationTable);
        jobConfigurationTable.setFillsViewportHeight(true);
        scrollPane.setPreferredSize(new Dimension(rightControlWidth, tableHeight));

        GridBagConstraints c32 = new GridBagConstraints();
        c32.gridx = 1;
        c32.gridy = 2;
        c32.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(scrollPane, c32);
    }

    private void addCommandlineArgsLineItem() {
        JLabel commandLineArgs = new JLabel("Command line arguments");
        commandLineArgs.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        commandLineArgs.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c41 = new GridBagConstraints();
        c41.gridx = 0;
        c41.gridy = 3;
        c41.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(commandLineArgs, c41);

        commandLineTextField = new JTextField();
        commandLineTextField.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c42 = new GridBagConstraints();
        c42.gridx = 1;
        c42.gridy = 3;
        c42.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(commandLineTextField, c42);
    }

    private void addReferencedJarsLineItem() {
        JLabel commandLineArgs = new JLabel("Referenced Jars");
        commandLineArgs.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        commandLineArgs.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c51 = new GridBagConstraints();
        c51.gridx = 0;
        c51.gridy = 4;
        c51.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(commandLineArgs, c51);

        referencedJarsTextField = new JTextField();
        referencedJarsTextField.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c52 = new GridBagConstraints();
        c52.gridx = 1;
        c52.gridy = 4;
        c52.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(referencedJarsTextField, c52);
    }

    private void addReferencedFilesLineItem() {
        JLabel commandLineArgs = new JLabel("Referenced Files");
        commandLineArgs.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        commandLineArgs.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c61 = new GridBagConstraints();
        c61.gridx = 0;
        c61.gridy = 5;
        c61.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(commandLineArgs, c61);

        referencedFilesTextField = new JTextField();
        referencedFilesTextField.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c62 = new GridBagConstraints();
        c62.gridx = 1;
        c62.gridy = 5;
        c62.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(referencedFilesTextField, c62);
    }

    private void addTimeoutLineItem() {
        JLabel submissionTimeOutLabel = new JLabel("Submission timeout");
        submissionTimeOutLabel.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        submissionTimeOutLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c71 = new GridBagConstraints();
        c71.gridx = 0;
        c71.gridy = 6;
        c71.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(submissionTimeOutLabel, c71);

        timeoutComboBox = new ComboBox();
        timeoutComboBox.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c72 = new GridBagConstraints();
        c72.gridx = 1;
        c72.gridy = 6;
        c72.insets = new Insets(margin, margin, margin, margin);
        contentPane.add(timeoutComboBox, c72);
    }


    private void addOperationJPanel() {
        JPanel operationPanel = new JPanel();
        operationPanel.setLayout(new FlowLayout());
        JButton buttonSubmit = new JButton("Submit");
        JButton buttonCancel = new JButton("Cancel");
        JButton buttonHelper = new JButton("Help >>");

        operationPanel.add(buttonSubmit);
        operationPanel.add(buttonCancel);
        operationPanel.add(buttonHelper);

        GridBagConstraints c82 = new GridBagConstraints();
        c82.gridx = 1;
        c82.gridy = 7;
        c82.insets = new Insets(margin, margin, 0, margin);
        c82.anchor = GridBagConstraints.EAST;
        contentPane.add(operationPanel, c82);

        buttonSubmit.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        buttonHelper.addActionListener(e -> OnHelper());
    }

    private void onOK() {
        ToolWindow sparkSubmissionToolWindow = ToolWindowManager.getInstance(this.project).getToolWindow(SparkSubmissionToolWindowFactory.SPARK_SUBMISSION_WINDOW);
        // TODO: check submission parameters
        sparkSubmissionToolWindow.show(() -> submit());
        dispose();
    }

    private void submit() {
        DefaultLoader.getIdeHelper().executeOnPooledThread(() -> {
            try {
                HDInsightHelper.getInstance()
                        .getSparkSubmissionToolWindowFactory().clearAll();

                IClusterDetail selectedClusterDetail = mapClusterNameToClusterDetail.get(clustersListComboBox.getSelectedItem().toString());
                HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo(String.format("Info : Begin to submit application to %s cluster ...", selectedClusterDetail.getName()));
                if (!selectedClusterDetail.isConfigInfoAvailable()) {
                    selectedClusterDetail.getConfigurationInfo();
                }

                String buildJarPath = "/home/joezhang/SimpleApp3.jar";
                HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo(String.format("Info : Get target jar from %s.", buildJarPath));

                String uniqueFolderId = UUID.randomUUID().toString();

                String fileOnBlobPath = SparkSubmitHelper.getInstance().uploadFileToAzureBlob(buildJarPath, selectedClusterDetail.getStorageAccount(), selectedClusterDetail.getStorageAccount().getDefaultContainer(), uniqueFolderId);

                List<String> referencedJarsList = new ArrayList<>();
                String referencedJars = referencedJarsTextField.getText();
                if(referencedJars != null && !StringHelper.isNullOrWhiteSpace(referencedJars)){
                    referencedJarsList = SparkSubmitHelper.getInstance().uploadFileListToAzureBlob(referencedJars, selectedClusterDetail.getStorageAccount(), selectedClusterDetail.getStorageAccount().getDefaultContainer(), uniqueFolderId);
                }

                List<String> referencedFileList = new ArrayList<>();
                String referencedFiles = referencedFilesTextField.getText();
                if(referencedFiles != null && !StringHelper.isNullOrWhiteSpace(referencedFiles)){
                    referencedFileList = SparkSubmitHelper.getInstance().uploadFileListToAzureBlob(referencedFiles, selectedClusterDetail.getStorageAccount(), selectedClusterDetail.getStorageAccount().getDefaultContainer(), uniqueFolderId);
                }

                // TODO: set submit timeout
                SparkBatchSubmission.getInstance().setCredentialsProvider(selectedClusterDetail.getHttpUserName(), selectedClusterDetail.getHttpPassword());
                HttpResponse response = SparkBatchSubmission.getInstance().createBatchSparkJob(selectedClusterDetail.getConnectionUrl() + "/livy/batches",
                        constructSubmissionParameter(fileOnBlobPath, referencedFileList, referencedJarsList));
                if (response.getStatusCode() == 201 || response.getStatusCode() == 200) {
                    HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setInfo("Submit to spark cluster successfully.");

                    String jobLink = String.format("%s/sparkhistory", selectedClusterDetail.getConnectionUrl());
                    HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setHyperlink(jobLink, "See spark job view from " + jobLink);
                    SparkSubmitResponse sparkSubmitResponse = new Gson().fromJson(response.getMessage(), new TypeToken<SparkSubmitResponse>() {
                    }.getType());

                    SparkSubmitHelper.getInstance().printRunningLogStreamingly(sparkSubmitResponse.getId(), selectedClusterDetail);
                } else {
                    HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setError(String.format("Error : Failed to submit to spark cluster. error code : %d, reason :  %s.",
                            response.getStatusCode(), response.getReason()));
                }

            } catch (Exception exception) {
                HDInsightHelper.getInstance().getSparkSubmissionToolWindowFactory().setError("Error : Failed to submit application to spark cluster. Exception : " + exception.toString());
            }
        });
    }

    private SparkSubmissionParameter constructSubmissionParameter(String fileOnBlobPath, List<String>referencedFileList, List<String>referencedJarList) {
        String className = mainClassTextField.getText();
        String commandLine = commandLineTextField.getText();

        List<String> argsList = new ArrayList<>();
        for (String singleArs : commandLine.split(" ")) {
            if (!StringHelper.isNullOrWhiteSpace(singleArs)) {
                argsList.add(singleArs.trim());
            }
        }

        Map<String, Object> jobConfigMap = new HashMap<>();
        TableModel tableModel = jobConfigurationTable.getModel();
        for (int index = 0; index < tableModel.getRowCount(); index++) {
            if (!StringHelper.isNullOrWhiteSpace((String) tableModel.getValueAt(index, 0))) {
                jobConfigMap.put((String) tableModel.getValueAt(index, 0), tableModel.getValueAt(index, 1));
            }
        }

        return new SparkSubmissionParameter(fileOnBlobPath, className, referencedFileList, referencedJarList, argsList, jobConfigMap);
    }

    private void onCancel() {
        dispose();
    }

    private void OnHelper() {
    }
}

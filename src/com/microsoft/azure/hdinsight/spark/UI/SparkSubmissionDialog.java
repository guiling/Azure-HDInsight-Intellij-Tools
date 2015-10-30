package com.microsoft.azure.hdinsight.spark.UI;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.microsoft.azure.hdinsight.spark.UIHelper.InteractiveRenderer;
import com.microsoft.azure.hdinsight.spark.UIHelper.InteractiveTableModel;
import com.microsoft.azure.hdinsight.spark.common.SparkBatchSubmission;
import com.microsoft.azure.hdinsight.spark.common.SparkInteractiveSessions;
import com.microsoft.azure.hdinsight.spark.common.SparkSubmissionParameter;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class SparkSubmissionDialog extends JDialog {

    private JPanel contentPane;

    private final int leftControlWidth = 200;
    private final int rightControlWidth = 300;
    private final int controlHeight = 23;
    private final int tableHeight = 120;
    private final int margin = 10;
    private final String DialogTitle = "Spark Submission";

    public SparkSubmissionDialog() {
        setContentPane(contentPane);
        setModal(true);
        setSize(new Dimension(550, 380));
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle(DialogTitle);
        contentPane.setLayout(new GridBagLayout());

        addSparkClustersLineItem();
        addConfigurationLineItem();
        addCommandlineArgsLineItem();
        addTimeoutLineItem();
        addOperationJPanel();

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void addTimeoutLineItem() {
        JLabel submissionTimeOutLabel = new JLabel("Submission timeout");
        submissionTimeOutLabel.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        submissionTimeOutLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c41 = new GridBagConstraints();
        c41.gridx = 0;
        c41.gridy = 3;
        c41.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(submissionTimeOutLabel, c41);

        ComboBox timeOutCombobox = new ComboBox();
        timeOutCombobox.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c42 = new GridBagConstraints();
        c42.gridx = 1;
        c42.gridy = 3;
        c42.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(timeOutCombobox, c42);
    }

    private void addCommandlineArgsLineItem() {
        JLabel commandLineArgs = new JLabel("Command line arguments");
        commandLineArgs.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        commandLineArgs.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c31 = new GridBagConstraints();
        c31.gridx = 0;
        c31.gridy = 2;
        c31.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(commandLineArgs, c31);

        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c32 = new GridBagConstraints();
        c32.gridx = 1;
        c32.gridy = 2;
        c32.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(textField, c32);
    }

    private void addConfigurationLineItem() {
        JLabel jobConfigurationLabel = new JLabel("Job configurations");
        jobConfigurationLabel.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        jobConfigurationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c21 = new GridBagConstraints();
        c21.gridx = 0;
        c21.gridy = 1;
        c21.anchor = GridBagConstraints.NORTH;
        c21.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(jobConfigurationLabel, c21);

        String []columns = {"Key", "Value", ""};

        JBTable jbTable = new JBTable();
        InteractiveTableModel tableModel = new InteractiveTableModel(columns);
        jbTable.setModel(tableModel);
        jbTable.setSurrendersFocusOnKeystroke(true);
        jbTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jbTable.setColumnSelectionAllowed(true);

        if (!tableModel.hasEmptyRow()) {
            tableModel.addEmptyRow();
        }

        TableColumn hidden = jbTable.getColumnModel().getColumn(InteractiveTableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new InteractiveRenderer(InteractiveTableModel.HIDDEN_INDEX));

        JBScrollPane scrollPane = new JBScrollPane(jbTable);
        jbTable.setFillsViewportHeight(true);
        scrollPane.setPreferredSize(new Dimension(rightControlWidth, tableHeight));

        GridBagConstraints c22 = new GridBagConstraints();
        c22.gridx = 1;
        c22.gridy = 1;
        c22.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(scrollPane, c22);
    }

    private void addSparkClustersLineItem() {
        JLabel sparkClusterLabel = new JLabel("Spark clusters");
        sparkClusterLabel.setPreferredSize(new Dimension(leftControlWidth, controlHeight));
        sparkClusterLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints c11 = new GridBagConstraints();
        c11.gridx = 0;
        c11.gridy = 0;
        c11.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(sparkClusterLabel, c11);

        ComboBox sparkClusterCombobox = new ComboBox();
        sparkClusterCombobox.setPreferredSize(new Dimension(rightControlWidth, controlHeight));
        GridBagConstraints c12 = new GridBagConstraints();
        c12.gridx = 1;
        c12.gridy = 0;
        c12.insets = new Insets(margin,margin,margin,margin);
        contentPane.add(sparkClusterCombobox, c12);
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

        GridBagConstraints c52 = new GridBagConstraints();
        c52.gridx = 1;
        c52.gridy = 4;
        c52.insets = new Insets(margin,margin,0,margin);
        c52.anchor = GridBagConstraints.EAST;
        contentPane.add(operationPanel, c52);

        buttonSubmit.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        buttonHelper.addActionListener(e -> OnHelper());
    }

    private void onOK() {
//     add your code here
//        try {
//            SparkBatchSubmission.getInstance().setCredentialsProvider("admin", "HADFwfp543j95fpj8!");
//            SparkSubmissionParameter parameter = new SparkSubmissionParameter("wasb://defaultcontainer@sparkwithlivy.blob.core.windows.net/user/spark/SimpleApp3.jar","SimpleApp");
//            String result = SparkBatchSubmission.getInstance().createBatchSparkJob("https://sparkwithlivy10.hdinsight-stable.azure-test.net/livy/batches", parameter);
//            result = SparkBatchSubmission.getInstance().getBatchSparkJobStatus("https://sparkwithlivy10.hdinsight-stable.azure-test.net/livy/batches", "10");
//            result = SparkBatchSubmission.getInstance().getBatchJobFullLog("https://sparkwithlivy10.hdinsight-stable.azure-test.net/livy/batches", "10");
//            String a = result;
//        }
//        catch (IOException exception){
//
//
//        }

        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void OnHelper(){
    }
}

package com.microsoft.azure.hdinsight.serverexplore.UI;

import com.microsoft.azure.hdinsight.common.HDInsightHelper;
import com.microsoft.azure.hdinsight.sdk.cluster.HDInsightClusterDetail;
import com.microsoft.azure.hdinsight.serverexplore.hdinsightnode.HDInsightRootModule;

import javax.swing.*;
import java.awt.event.*;

public class DeleteHDInsightClusterDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField TextField;

    private HDInsightClusterDetail clusterDetail;

    public DeleteHDInsightClusterDialog(HDInsightClusterDetail clusterDetail) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.clusterDetail = clusterDetail;
        setTitle("Delete HDInsight Cluster");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {

        HDInsightRootModule rootModule =  HDInsightHelper.getInstance().getServerExplorerRootModule();

        if(rootModule != null)
        {
            rootModule.removeHDInsightAdditionalCluster(clusterDetail);
        }

        dispose();
    }

    private void onCancel() {
        dispose();
    }
}

package com.microsoft.azure.hdinsight.serverexplore.UI;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.microsoft.azure.hdinsight.common.HDInsightHelper;
import com.microsoft.azure.hdinsight.common.StringHelper;
import com.microsoft.azure.hdinsight.sdk.cluster.HDInsightAdditionalClusterDetail;
import com.microsoft.azure.hdinsight.sdk.common.HDIException;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.serverexplore.hdinsightnode.HDInsightRootModule;
import com.microsoft.azure.hdinsight.common.AddHDInsightAdditionalClusterImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ltian on 12/17/2015.
 */
public class AddNewClusterFrom extends DialogWrapper {

    private Project project;
    private String clusterName;
    private String userName;
    private String password;
    private String errorMessage;

    private boolean isCarryOnNextStep;
    private List<StorageAccount> storageAccounts = null;

    private JPanel addNewClusterPanel;
    private JTextField clusterNameFiled;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JTextField errorMessageField;
    private JPanel buttonPanel;
    private JButton Okbutton;
    private JButton cancelButton;

    private static final Pattern HTTPS_URL_PATTERN = Pattern.compile("https://[^/]+");
    private static final String URL_PREFIX = "https://";

    public AddNewClusterFrom(final Project project) {
        super(project, true);
        this.init();
        this.project = project;

        this.setTitle("Add New HDInsight Cluster");
        this.setModal(true);

        addActionListener();
    }

    private void addActionListener() {
        Okbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                synchronized (AddNewClusterFrom.class) {

                    isCarryOnNextStep = true;
                    errorMessage = null;
                    errorMessageField.setVisible(false);

                    String clusterNameOrUrl = clusterNameFiled.getText().trim().toLowerCase();
                    userName = userNameField.getText().trim().toLowerCase();
                    password = String.valueOf(passwordField.getPassword());

                    HDInsightRootModule hdInsightRootModule = HDInsightHelper.getInstance().getServerExplorerRootModule();

                    if (hdInsightRootModule == null) {
                        return;
                    }

                    if (StringHelper.isNullOrWhiteSpace(clusterNameOrUrl) || StringHelper.isNullOrWhiteSpace(userName) || StringHelper.isNullOrWhiteSpace(password)) {
                        errorMessage = "Cluster Name, User Name and Password shouldn't be empty";
                        isCarryOnNextStep = false;
                    } else {
                        clusterName = getClusterName(clusterNameOrUrl);

                        if (clusterName == null) {
                            errorMessage = "Wrong cluster name or endpoint";
                            isCarryOnNextStep = false;
                        } else if (hdInsightRootModule.isHDInsightAdditionalClusterExist(clusterName)) {
                            errorMessage = "Cluster already exist!";
                            isCarryOnNextStep = false;
                        }
                    }

                    if (isCarryOnNextStep) {
                        getStorageAccounts();
                    }

                    if (isCarryOnNextStep) {
                        if (storageAccounts != null && storageAccounts.size() >= 1) {
                            HDInsightAdditionalClusterDetail hdInsightClusterDetail = new HDInsightAdditionalClusterDetail(clusterName, userName, password, storageAccounts);

                            hdInsightRootModule.addHDInsightAdditionalCluster(hdInsightClusterDetail);

                            close(DialogWrapper.OK_EXIT_CODE, true);
                        }
                    } else {
                        errorMessageField.setText(errorMessage);
                        errorMessageField.setVisible(true);
                    }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close(DialogWrapper.CANCEL_EXIT_CODE, true);
            }
        });
    }

    //format input string
    private static String getClusterName(String userNameOrUrl) {
        if (userNameOrUrl.startsWith(URL_PREFIX)) {
            return StringHelper.getClusterNameFromEndPoint(userNameOrUrl);
        } else {
            return userNameOrUrl;
        }
    }

    private void getStorageAccounts() {
        addNewClusterPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    storageAccounts = AddHDInsightAdditionalClusterImpl.getStorageAccounts(clusterName, userName, password);
                    isCarryOnNextStep = true;
                } catch (HDIException e) {
                    isCarryOnNextStep = false;
                    errorMessage = e.getMessage();
                }
            }
        }, ModalityState.NON_MODAL);

        addNewClusterPanel.setCursor(Cursor.getDefaultCursor());
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[0];
    }

    @NotNull
    @Override
    protected Action[] createLeftSideActions() {
        return new Action[0];
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return addNewClusterPanel;
    }
}



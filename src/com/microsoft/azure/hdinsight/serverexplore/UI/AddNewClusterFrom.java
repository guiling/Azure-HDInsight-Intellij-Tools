package com.microsoft.azure.hdinsight.serverexplore.UI;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.microsoft.azure.hdinsight.common.StringHelper;
import com.microsoft.azure.hdinsight.sdk.cluster.HDInsightClusterDetail;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import com.microsoft.azure.hdinsight.serverexplore.hdinsightnode.HDInsightRootModule;
import com.microsoft.azure.hdinsight.spark.common.AddNewClusterImpl;
import com.microsoft.azure.hdinsight.spark.common.StorageAccountResolveException;
import com.microsoft.azure.hdinsight.spark.common.UnAuthorizedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
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

    private static final String DEFAULT_CLUSTER_ENDPOINT = "https://%s.azurehdinsight.net";
    private static final Pattern HTTPS_URL_PATTERN = Pattern.compile("https://[^/]+");
    private static final String URL_PREFIX = "https://";

    //format input string
    private static String getClusterName(String userNameOrUrl) throws Exception {
        userNameOrUrl = userNameOrUrl.trim().toLowerCase();

        if (userNameOrUrl.startsWith(URL_PREFIX)) {
            String userName = userNameOrUrl.split("\\.")[0].substring(8);
            return userName;
        } else return userNameOrUrl;
    }

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

                    String clusterNameOrUrl = clusterNameFiled.getText().trim();
                    userName = userNameField.getText().trim();
                    password = String.valueOf(passwordField.getPassword());

                    if (StringHelper.isNullOrWhiteSpace(clusterNameOrUrl) || StringHelper.isNullOrWhiteSpace(userName) || StringHelper.isNullOrWhiteSpace(password)) {
                        errorMessage = "Cluster Name, User Name and Password shouldn't be empty";
                        isCarryOnNextStep = false;
                    } else {
                        try {
                            clusterName = getClusterName(clusterNameOrUrl);

                            if(HDInsightRootModule.getInstance().IsHDInsightAdditionalClusterExist(clusterName))
                            {
                                errorMessage = "Cluster already exist!";
                                isCarryOnNextStep = false;
                            }

                        } catch (Exception exception) {
                            errorMessage = "Wrong cluster name or cluster endpoint";
                            isCarryOnNextStep = false;
                        }
                    }

                    if (isCarryOnNextStep){
                        addNewClusterPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    storageAccounts = AddNewClusterImpl.getStorageAccounts(clusterName, userName, password);
                                    isCarryOnNextStep = true;
                                } catch (UnAuthorizedException e1) {
                                    isCarryOnNextStep = false;
                                    errorMessage = "Wrong User Name or Password";
                                } catch (UnknownHostException e2) {
                                    isCarryOnNextStep = false;
                                    errorMessage = "Wrong Cluster Name or Cluster Url";
                                } catch (StorageAccountResolveException e3) {
                                    isCarryOnNextStep = false;
                                    errorMessage = "Not Support Cluster";
                                } catch (Exception e4) {
                                    isCarryOnNextStep = false;
                                    errorMessage = "Something wrong with the cluster! Please try again later";
                                }
                            }
                        }, ModalityState.NON_MODAL);

                        addNewClusterPanel.setCursor(Cursor.getDefaultCursor());
                    }

                    if (isCarryOnNextStep) {
                        if (storageAccounts != null && storageAccounts.size() >= 1) {
                            HDInsightClusterDetail hdInsightClusterDetail = new HDInsightClusterDetail(clusterName, userName, password, storageAccounts);

                            HDInsightRootModule.getInstance().addHDInsightAdditionalCluster(hdInsightClusterDetail);

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



package com.microsoft.azure.hdinsight.serverexplore.UI;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ltian on 12/17/2015.
 */
public class AddNewClusterFrom extends DialogWrapper {

    private String clusterName;
    private String userName;
    private String password;
    private Project project;

    private JPanel panel;
    private JPanel addNewClusterPanel;
    private JButton okButton;
    private JEditorPane clusterNameFiled;
    private JPasswordField passwordField;
    private JButton cancelButton;
    private JEditorPane userNameField;

    public AddNewClusterFrom(final Project project)
    {
        super(project, true);
        this.project = project;

        this.setTitle("Add New Cluster");
        this.setModal(true);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel()
    {
        return addNewClusterPanel;
    }
}



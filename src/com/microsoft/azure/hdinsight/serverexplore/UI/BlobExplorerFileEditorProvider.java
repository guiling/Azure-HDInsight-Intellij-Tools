package com.microsoft.azure.hdinsight.serverexplore.UI;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.azure.hdinsight.common.IDEHelperImpl;
import com.microsoft.azure.hdinsight.sdk.storage.BlobContainer;
import com.microsoft.azure.hdinsight.sdk.storage.StorageAccount;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * Created by guizha on 12/14/2015.
 */
public class BlobExplorerFileEditorProvider implements FileEditorProvider, DumbAware {
    public static Key<BlobContainer> CONTAINER_KEY = new Key<BlobContainer>("blobContainer");

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        StorageAccount storageAccount = virtualFile.getUserData(IDEHelperImpl.STORAGE_KEY);
        BlobContainer blobContainer = virtualFile.getUserData(CONTAINER_KEY);

        return (storageAccount != null && blobContainer != null);
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        BlobExplorerFileEditor blobExplorerFileEditor = new BlobExplorerFileEditor();

        StorageAccount storageAccount = virtualFile.getUserData(IDEHelperImpl.STORAGE_KEY);
        BlobContainer blobContainer = virtualFile.getUserData(CONTAINER_KEY);

//        blobExplorerFileEditor.setBlobContainer(blobContainer);
//        blobExplorerFileEditor.setStorageAccount(storageAccount);
//        blobExplorerFileEditor.setProject(project);
//
//        blobExplorerFileEditor.fillGrid();

        return new FileEditor() {
            @NotNull
            @Override
            public JComponent getComponent() {
                return null;
            }

            @Nullable
            @Override
            public JComponent getPreferredFocusedComponent() {
                return null;
            }

            @NotNull
            @Override
            public String getName() {
                return null;
            }

            @NotNull
            @Override
            public FileEditorState getState(@NotNull FileEditorStateLevel fileEditorStateLevel) {
                return null;
            }

            @Override
            public void setState(@NotNull FileEditorState fileEditorState) {

            }

            @Override
            public boolean isModified() {
                return false;
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public void selectNotify() {

            }

            @Override
            public void deselectNotify() {

            }

            @Override
            public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

            }

            @Override
            public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

            }

            @Nullable
            @Override
            public BackgroundEditorHighlighter getBackgroundHighlighter() {
                return null;
            }

            @Nullable
            @Override
            public FileEditorLocation getCurrentLocation() {
                return null;
            }

            @Nullable
            @Override
            public StructureViewBuilder getStructureViewBuilder() {
                return null;
            }

            @Override
            public void dispose() {

            }

            @Nullable
            @Override
            public <T> T getUserData(@NotNull Key<T> key) {
                return null;
            }

            @Override
            public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

            }
        };
    }

    @Override
    public void disposeEditor(@NotNull FileEditor fileEditor) {
        Disposer.dispose(fileEditor);
    }

    @NotNull
    @Override
    public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element) {
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "Azure-Storage-Blob-Editor";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}

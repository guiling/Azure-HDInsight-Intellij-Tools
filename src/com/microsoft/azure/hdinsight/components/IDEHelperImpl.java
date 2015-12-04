package com.microsoft.azure.hdinsight.components;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by joezhang on 15-12-2.
 */
public class IDEHelperImpl implements IDEHelper {
    @Override
    public void invokeLater(@NotNull Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(runnable, ModalityState.any());
    }

    @Override
    public void invokeAndWait(@NotNull Runnable runnable) {
        ApplicationManager.getApplication().invokeAndWait(runnable, ModalityState.any());
    }

    @Override
    public void executeOnPooledThread(@NotNull Runnable runnable) {
        ApplicationManager.getApplication().executeOnPooledThread(runnable);
    }

    @Override
    public void runInBackground(@Nullable final Object project, @NotNull final String name, final boolean canBeCancelled,
                                final boolean isIndeterminate, @Nullable final String indicatorText,
                                final Runnable runnable) {
        // background tasks via ProgressManager can be scheduled only on the
        // dispatch thread
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ProgressManager.getInstance().run(new Task.Backgroundable((Project) project,
                        name, canBeCancelled) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        if (isIndeterminate) {
                            indicator.setIndeterminate(true);
                        }

                        if (indicatorText != null) {
                            indicator.setText(indicatorText);
                        }

                        runnable.run();
                    }
                });
            }
        }, ModalityState.any());
    }

    @Nullable
    @Override
    public String getProperty(@NotNull String name) {
        return PropertiesComponent.getInstance().getValue(name);
    }

    @NotNull
    @Override
    public String getProperty(@NotNull String name, @NotNull String defaultValue) {
        return PropertiesComponent.getInstance().getValue(name, defaultValue);
    }

    @Override
    public void setProperty(@NotNull String name, @NotNull String value) {
        PropertiesComponent.getInstance().setValue(name, value);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().saveSettings();
            }
        }, ModalityState.any());
    }

    @Override
    public void unsetProperty(@NotNull String name) {
        PropertiesComponent.getInstance().unsetValue(name);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().saveSettings();
            }
        }, ModalityState.any());
    }

    @Override
    public boolean isPropertySet(@NotNull String name) {
        return PropertiesComponent.getInstance().isValueSet(name);
    }

    @Nullable
    @Override
    public String getProperty(@NotNull Object projectObject, @NotNull String name) {
        return PropertiesComponent.getInstance((Project) projectObject).getValue(name);
    }

    @NotNull
    @Override
    public String getProperty(@NotNull Object projectObject, @NotNull String name, @NotNull String defaultValue) {
        return PropertiesComponent.getInstance((Project) projectObject).getValue(name, defaultValue);
    }

    @Override
    public void setProperty(@NotNull Object projectObject, @NotNull String name, @NotNull String value) {
        final Project project = (Project) projectObject;
        PropertiesComponent.getInstance(project).setValue(name, value);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                project.save();
            }
        }, ModalityState.any());
    }

    @Override
    public void unsetProperty(@NotNull Object projectObject, @NotNull String name) {
        final Project project = (Project) projectObject;
        PropertiesComponent.getInstance(project).unsetValue(name);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                project.save();
            }
        }, ModalityState.any());
    }

    @Override
    public boolean isPropertySet(@NotNull Object projectObject, @NotNull String name) {
        return PropertiesComponent.getInstance((Project) projectObject).isValueSet(name);
    }

    @Nullable
    @Override
    public String[] getProperties(@NotNull String name) {
        return PropertiesComponent.getInstance().getValues(name);
    }

    @Override
    public void setProperties(@NotNull String name, @NotNull String[] value) {
        PropertiesComponent.getInstance().setValues(name, value);
        ApplicationManager.getApplication().saveSettings();
    }
}

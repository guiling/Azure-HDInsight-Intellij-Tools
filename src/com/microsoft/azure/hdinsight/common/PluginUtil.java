package com.microsoft.azure.hdinsight.common;

import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

/**
 * Created by guizha on 8/21/2015.
 */
public class PluginUtil {
    public static boolean isModuleRoot(VirtualFile moduleFolder, Module module) {
        return moduleFolder != null && ProjectRootsUtil.isModuleContentRoot(moduleFolder, module.getProject());
    }

    public static final Icon getIcon(String iconPath){
        return IconLoader.getIcon(iconPath);
    }
}
/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a node in the dependency tree. This class is <strong>not</strong>
 * part of the public API.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class TreeNode {

    /**
     * The module class of the module represented by this node.
     */
    private CcmModule module;
    /**
     * The module info for the module.
     */
    private ModuleInfo moduleInfo;
    /**
     * The modules depending on the module represented by this tree node.
     */
    private List<TreeNode> dependentModules;
    /**
     * The modules the module represented by this tree node depends on.
     */
    private List<TreeNode> dependsOn;

    /**
     * Creates a new empty tree node.
     */
    public TreeNode() {
        super();

        dependentModules = new ArrayList<>();
        dependsOn = new ArrayList<>();
    }

    /**
     * Creates a tree node for the provided module. Them module info for the
     * module is loaded automatically.
     * 
     * @param module 
     */
    public TreeNode(final CcmModule module) {
        this();

        this.module = module;
        final ModuleInfo info = new ModuleInfo();
        info.load(module);
        moduleInfo = info;
    }

    public CcmModule getModule() {
        return module;
    }

    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public List<TreeNode> getDependentModules() {
        return Collections.unmodifiableList(dependentModules);
    }

    void setDependentModules(final List<TreeNode> dependentModules) {
        this.dependentModules = dependentModules;
    }

    void addDependentModule(final TreeNode node) {
        dependentModules.add(node);
    }

    void removeDependentModule(final TreeNode node) {
        dependentModules.remove(node);
    }

    public List<TreeNode> getDependsOn() {
        return Collections.unmodifiableList(dependsOn);
    }

    void setDependsOn(final List<TreeNode> dependsOn) {
        this.dependsOn = dependsOn;
    }

    void addDependsOn(final TreeNode node) {
        dependsOn.add(node);
    }

    void removeDependsOn(final TreeNode node) {
        dependsOn.remove(node);
    }

    @Override
    public int hashCode() {
        int hash = 5;

        final ModuleInfo info = new ModuleInfo();
        info.load(module);

        final String moduleName = info.getModuleName();
        final String version = info.getModuleVersion();

        hash = 37 * hash + Objects.hashCode(moduleName);
        hash = 37 * hash + Objects.hashCode(version);

        return hash;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof TreeNode)) {
            return false;
        }

        final TreeNode other = (TreeNode) object;
        final String name = moduleInfo.getModuleName();
        final String otherName = other.getModuleInfo().getModuleName();

        if (!name.equals(otherName)) {
            return false;
        }

        final String version = moduleInfo.getModuleVersion();
        final String otherVersion = other.getModuleInfo().getModuleVersion();

        return version.equals(otherVersion);
    }

}

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
package org.libreccm.modules.dependencytree;

import org.libreccm.modules.ModuleDescriptor;
import org.libreccm.modules.ModuleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a node in the dependency tree. 
 * 
 * @see DependencyTreeManager
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class TreeNode {
    
    private ModuleDescriptor module;
    private List<TreeNode> dependentModules;
    private List<TreeNode> dependsOn;
    
    public TreeNode() {
        super();
        
        dependentModules = new ArrayList<>();
        dependsOn = new ArrayList<>();
    }
    
    public TreeNode(final ModuleDescriptor module) {
        this();
        
        this.module = module;
    }

    public ModuleDescriptor getModule() {
        return module;
    }

    public void setModule(final ModuleDescriptor module) {
        this.module = module;
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
        
        final String moduleName = ModuleUtil.getModuleName(module);
        final String version = ModuleUtil.getVersion(module);
        
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
        final String name = ModuleUtil.getModuleName(module);
        final String otherName = ModuleUtil.getModuleName(other.getModule());
        
        if (!name.equals(otherName)) {
            return false;
        }
        
        final String version = ModuleUtil.getVersion(module);
        final String otherVersion = ModuleUtil.getVersion(other.getModule());
        
        return version.equals(otherVersion);
    }
    
}

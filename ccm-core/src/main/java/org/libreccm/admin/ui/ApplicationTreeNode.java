/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class encapsulating the information displayed in a application tree.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ApplicationTreeNode implements Serializable, 
                                     Comparable<ApplicationTreeNode> {

    private static final long serialVersionUID = -3123536103514717506L;

    protected static final String ROOT = "@ROOT@";
    

    /**
     * ID of the node. If the node is the root node, the ID will be
     * {@link #ROOT}. If the node is an application type node the ID will be the
     * name of the application type class. If it is an application (instance)
     * node, the name will be the UUID of the instance.
     */
    private String nodeId;

    /**
     * Type of the the node.
     */
    private ApplicationTreeNodeType nodeType;

    /**
     * The title of the node.
     */
    private String title;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    public ApplicationTreeNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(final ApplicationTreeNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public int compareTo(final ApplicationTreeNode other) {
        
        int result = title.compareTo(other.getTitle());
        if (result == 0) {
            result = nodeType.compareTo(other.getNodeType());
        } 
        
        if (result == 0) {
            result = nodeId.compareTo(other.getNodeId());
        }
        
        return result;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.nodeId);
        hash = 53 * hash + Objects.hashCode(this.nodeType);
        hash = 53 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ApplicationTreeNode)) {
            return false;
        }
        final ApplicationTreeNode other = (ApplicationTreeNode) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(this.nodeId, other.getNodeId())) {
            return false;
        }
        if (!Objects.equals(this.title, other.getTitle())) {
            return false;
        }
        return this.nodeType == other.getNodeType();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ApplicationTreeNode;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "nodeId = \"%s\", "
                                 + "nodeType = \"%s\", "
                                 + "title = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             nodeId,
                             nodeType,
                             title,
                             data);
    }

}

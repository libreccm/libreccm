/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

/**
 * Model for an individual breadcrumb in the breadcrumb trail of a folder.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FolderBreadcrumbsModel {
    
    /**
     * A path token of the folder.
     */
    private String pathToken;
    
    /**
     * The full path of the folder.
     */
    private String path;
    
    /**
     * Is the folder the currently selected folder.
     */
    private boolean currentFolder;

    public String getPathToken() {
        return pathToken;
    }

    public void setPathToken(final String pathToken) {
        this.pathToken = pathToken;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public boolean isCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(final boolean currentFolder) {
        this.currentFolder = currentFolder;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentFolderBreadcrumbModel {
    
    private String pathToken;
    
    private String path;
    
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

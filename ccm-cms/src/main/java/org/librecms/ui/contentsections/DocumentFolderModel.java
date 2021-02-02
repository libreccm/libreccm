/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("DocumentFolderModel")
public class DocumentFolderModel {

    private long count;

    private int firstResult;

    private int maxResults;

    private List<DocumentFolderRowModel> rows;

    private List<DocumentFolderBreadcrumbModel> breadcrumbs;

    private String path;
    
    private boolean canCreateSubFolders;


    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

    public int getFirstResult() {
        return firstResult;
    }

    protected void setFirstResult(final int firstResult) {
        this.firstResult = firstResult;
    }

    public int getMaxResults() {
        return maxResults;
    }

    protected void setMaxResults(final int maxResults) {
        this.maxResults = maxResults;
    }

    public long getNumberOfPages() {
        return (long) Math.ceil((double) count / maxResults);
    }

    public long getCurrentPage() {
        return (long) Math.ceil((double) firstResult / maxResults) + 1;
    }

    public List<DocumentFolderRowModel> getRows() {
        return Collections.unmodifiableList(rows);
    }

    protected void setRows(final List<DocumentFolderRowModel> rows) {
        this.rows = new ArrayList<>(rows);
    }

    public List<DocumentFolderBreadcrumbModel> getBreadcrumbs() {
        return Collections.unmodifiableList(breadcrumbs);
    }

    public void setBreadcrumbs(
        final List<DocumentFolderBreadcrumbModel> breadcrumbs
    ) {
        this.breadcrumbs = new ArrayList<>(breadcrumbs);
    }

    public String getPath() {
        return path;
    }
    
    protected void setPath(final String path) {
        this.path = path;
    }
    
    public boolean isCanCreateSubFolders() {
        return canCreateSubFolders;
    }

    protected void setCanCreateSubFolders(final boolean canCreateSubFolders) {
        this.canCreateSubFolders = canCreateSubFolders;
    }

}

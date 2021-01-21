/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FolderBrowserModel {

    private long count;

    private int firstResult;

    private int maxResults;

    private List<FolderBrowserRowModel> rows;

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

    public List<FolderBrowserRowModel> getRows() {
        return Collections.unmodifiableList(rows);
    }

    protected void setRows(final List<FolderBrowserRowModel> rows) {
        this.rows = new ArrayList<>(rows);
    }

}

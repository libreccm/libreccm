/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorizationTree {

    private String domainKey;

    private String domainTitle;

    private String domainDescription;

    private CategorizationTreeNode root;

    private List<String> assignedCategories;

    public String getDomainKey() {
        return domainKey;
    }

    public void setDomainKey(final String domainKey) {
        this.domainKey = domainKey;
    }

    public String getDomainTitle() {
        return domainTitle;
    }

    public void setDomainTitle(final String domainTitle) {
        this.domainTitle = domainTitle;
    }

    public String getDomainDescription() {
        return domainDescription;
    }

    public void setDomainDescription(final String domainDescription) {
        this.domainDescription = domainDescription;
    }

    public CategorizationTreeNode getRoot() {
        return root;
    }

    public void setRoot(final CategorizationTreeNode root) {
        this.root = root;
    }

    public List<String> getAssignedCategories() {
        return Collections.unmodifiableList(assignedCategories);
    }

    public void setAssignedCategories(final List<String> assignedCategories) {
        this.assignedCategories = new ArrayList<>(assignedCategories);
    }

}

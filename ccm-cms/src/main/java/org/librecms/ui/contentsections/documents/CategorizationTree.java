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
package org.librecms.ui.contentsections.documents;

import org.libreccm.categorization.Domain;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A tree structure of a category system (domain) with markers for the
 * categories assigned to the curent content item.
 *
 * @see CategorizationStep
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorizationTree {

    /**
     * The domain key of the category system.
     *
     * @see Domain#domainKey
     */
    private String domainKey;

    /**
     * The title of the domain. This value is determined from
     * {@link Domain#title} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }
     */
    private String domainTitle;

    /**
     * The description of the domain. This value is determined from
     * {@link Domain#description} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }
     */
    private String domainDescription;

    /**
     * The node for the root category of the domain.
     */
    private CategorizationTreeNode root;

    /**
     * A list of the paths of the categories assigned to the current content
     * item.
     */
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

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

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Model for providing data about a {@link ContentSection}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("ContentSectionModel")
public class ContentSectionModel {

    /**
     * Used to check permissions on the {@link ContentSection}.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * The content section.
     */
    private ContentSection section;

    /**
     * The asset folder tree of the section.
     */
    private List<AssetFolderTreeNode> assetFolders;

    /**
     * The document folder tree of the section.
     */
    private List<DocumentFolderTreeNode> documentFolders;

    /**
     * Sets the section for the model
     *
     * @param section The content section.
     */
    protected void setSection(final ContentSection section) {
        this.section = Objects.requireNonNull(
            section, "Parameter section can't be null"
        );
    }

    /**
     * Get the label of the content section.
     *
     * @return The label of the content section.
     */
    public String getSectionName() {
        return Optional
            .ofNullable(section)
            .map(ContentSection::getLabel)
            .orElse("");
    }

    public List<AssetFolderTreeNode> getAssetFolders() {
        return Collections.unmodifiableList(assetFolders);
    }

    protected void setAssetFolders(
        final List<AssetFolderTreeNode> assetFolders
    ) {
        this.assetFolders = new ArrayList<>(assetFolders);
    }

    public List<DocumentFolderTreeNode> getDocumentFolders() {
        return Collections.unmodifiableList(documentFolders);
    }

    protected void setDocumentFolders(
        final List<DocumentFolderTreeNode> documentFolders
    ) {
        this.documentFolders = new ArrayList<>(documentFolders);
    }

    /**
     * Can the current user administer the categories of the domains/category
     * sytems assigned to the section?
     *
     * @return
     */
    public boolean getCanAdministerCategories() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        );
    }

    /**
     * Can the current user administer the content types assigned to the
     * section?
     *
     * @return
     */
    public boolean getCanAdministerContentTypes() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        );
    }

    /**
     * Can the current user administer the lifecycle definitions of the section?
     *
     * @return
     */
    public boolean getCanAdministerLifecycles() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_LIFECYLES, section
        );
    }

    /**
     * Can the current user administer the roles of the section?
     *
     * @return
     */
    public boolean getCanAdministerRoles() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        );
    }

    /**
     * Can the current user administer the workflows templates of the section?
     *
     * @return
     */
    public boolean getCanAdministerWorkflows() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_WORKFLOWS, section
        );
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("ContentSectionModel")
public class ContentSectionModel {

    @Inject
    private PermissionChecker permissionChecker;

    private ContentSection section;

    private List<AssetFolderTreeNode> assetFolders;

    private List<DocumentFolderTreeNode> documentFolders;

    protected void setSection(final ContentSection section) {
        this.section = Objects.requireNonNull(
            section, "Parameter section can't be null"
        );
    }

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

    public boolean getCanAdministerCategories() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        );
    }

    public boolean getCanAdministerContentTypes() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        );
    }

    public boolean getCanAdministerLifecycles() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_LIFECYLES, section
        );
    }

    public boolean getCanAdministerRoles() {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        );
    }

    public boolean getCanAdministerWorkflows() {
        return permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_WORKFLOWS, section
        );
    }

}

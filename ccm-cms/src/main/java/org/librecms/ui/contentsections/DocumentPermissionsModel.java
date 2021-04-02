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

import org.librecms.contentsection.privileges.ItemPrivileges;

/**
 * Model for the permissions of a document/{@link ContentItem}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentPermissionsModel implements PermissionsModel {

    /**
     * Has the {@link ItemPrivileges#ADMINISTER} privilege been granted for the
     * content item?
     */
    private boolean grantedAdminister;

    /**
     * Has the {@link ItemPrivileges#APPLY_ALTERNATE_WORKFLOW} privilege been
     * granted for the content item?
     */
    private boolean grantedApplyAlternateWorkflow;

    /**
     * Has the {@link ItemPrivileges#APPROVE} privilege been granted for the
     * content item?
     */
    private boolean grantedApprove;

    /**
     * Has the {@link ItemPrivileges#CATEGORIZE} privilege been granted for the
     * content item?
     */
    private boolean grantedCategorize;

    /**
     * Has the {@link ItemPrivileges#CREATE_NEW} privilege been granted for the
     * content item?
     */
    private boolean grantedCreateNew;

    /**
     * Has the {@link ItemPrivileges#DELETE} privilege been granted for the
     * content item?
     */
    private boolean grantedDelete;

    /**
     * Has the {@link ItemPrivileges#EDIT} privilege been granted for the
     * content item?
     */
    private boolean grantedEdit;

    /**
     * Has the {@link ItemPrivileges#PREVIEW} privilege been granted for the
     * content item?
     */
    private boolean grantedPreview;

    /**
     * Has the {@link ItemPrivileges#PUBLISH} privilege been granted for the
     * content item?
     */
    private boolean grantedPublish;

    /**
     * Has the {@link ItemPrivileges#VIEW_PUBLISHED} privilege been granted for
     * the content item?
     */
    private boolean grantedViewPublished;

    public boolean isGrantedAdminister() {
        return grantedAdminister;
    }

    public void setGrantedAdminister(final boolean grantedAdminister) {
        this.grantedAdminister = grantedAdminister;
    }

    public boolean isGrantedApplyAlternateWorkflow() {
        return grantedApplyAlternateWorkflow;
    }

    public void setGrantedApplyAlternateWorkflow(
        boolean grantedApplyAlternateWorkflow) {
        this.grantedApplyAlternateWorkflow = grantedApplyAlternateWorkflow;
    }

    public boolean isGrantedApprove() {
        return grantedApprove;
    }

    public void setGrantedApprove(final boolean grantedApprove) {
        this.grantedApprove = grantedApprove;
    }

    public boolean isGrantedCategorize() {
        return grantedCategorize;
    }

    public void setGrantedCategorize(final boolean grantedCategorize) {
        this.grantedCategorize = grantedCategorize;
    }

    public boolean isGrantedCreateNew() {
        return grantedCreateNew;
    }

    public void setGrantedCreateNew(final boolean grantedCreateNew) {
        this.grantedCreateNew = grantedCreateNew;
    }

    public boolean isGrantedDelete() {
        return grantedDelete;
    }

    public void setGrantedDelete(final boolean grantedDelete) {
        this.grantedDelete = grantedDelete;
    }

    public boolean isGrantedEdit() {
        return grantedEdit;
    }

    public void setGrantedEdit(final boolean grantedEdit) {
        this.grantedEdit = grantedEdit;
    }

    public boolean isGrantedPreview() {
        return grantedPreview;
    }

    public void setGrantedPreview(final boolean grantedPreview) {
        this.grantedPreview = grantedPreview;
    }

    public boolean isGrantedPublish() {
        return grantedPublish;
    }

    public void setGrantedPublish(final boolean grantedPublish) {
        this.grantedPublish = grantedPublish;
    }

    public boolean isGrantedViewPublished() {
        return grantedViewPublished;
    }

    public void setGrantedViewPublished(final boolean grantedViewPublished) {
        this.grantedViewPublished = grantedViewPublished;
    }

}

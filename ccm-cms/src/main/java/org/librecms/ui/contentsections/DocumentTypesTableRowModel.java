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

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for a row in the table of document types/cintent types assigned to a
 * {@link ContentSection}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see ConfigurationDocumentTypesController
 * @see ContentType
 */
public class DocumentTypesTableRowModel {

    /**
     * The display name of the content type.
     */
    private String displayName;

    /**
     * The class implementing the type.
     */
    private String contentItemClass;

    /**
     * The label of the content type. This value is determined from
     * {@link ContentType#label} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String label;

    /**
     * The description of the content type. This value is determined from
     * {@link ContentType#description} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String description;

    /**
     * The mode of the type.
     */
    private String mode;

    /**
     * The label of the default lifecycle of the type. This value of determined
     * from the label of the default lifecycle using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String defaultLifecycleLabel;

    /**
     * The UUID of the default lifecycle.
     */
    private List<String> defaultLifecycleUuid;

     /**
     * The label of the default workflow of the type. This value of determined
     * from the label of the default workflow using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String defaultWorkflowLabel;

    /**
     * The UUID of the default workflow.
     */
    private List<String> defaultWorkflowUuid;

    /**
     * The UUID of the type.
     */
    private String uuid;

    /**
     * The permissions of the current user for the type.
     */
    private List<DocumentTypePermissionModel> permissions;

    public String getContentItemClass() {
        return contentItemClass;
    }

    public void setContentItemClass(final String contentItemClass) {
        this.contentItemClass = contentItemClass;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public String getDefaultLifecycleLabel() {
        return defaultLifecycleLabel;
    }

    public void setDefaultLifecycleLabel(final String defaultLifecycleLabel) {
        this.defaultLifecycleLabel = defaultLifecycleLabel;
    }

    public String getDefaultWorkflowLabel() {
        return defaultWorkflowLabel;
    }

    public void setDefaultWorkflowLabel(final String defaultWorkflowLabel) {
        this.defaultWorkflowLabel = defaultWorkflowLabel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public List<String> getDefaultLifecycleUuid() {
        return Collections.unmodifiableList(defaultLifecycleUuid);
    }

    public void setDefaultLifecycleUuid(final List<String> defaultLifecycleUuid) {
        this.defaultLifecycleUuid = new ArrayList<>(defaultLifecycleUuid);
    }

    public List<String> getDefaultWorkflowUuid() {
        return Collections.unmodifiableList(defaultWorkflowUuid);
    }

    public void setDefaultWorkflowUuid(final List<String> defaultWorkflowUuid) {
        this.defaultWorkflowUuid = new ArrayList<>(defaultWorkflowUuid);
    }

    public List<DocumentTypePermissionModel> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    public void setPermissions(
        final List<DocumentTypePermissionModel> permissions) {
        this.permissions = new ArrayList<>(permissions);
    }

}

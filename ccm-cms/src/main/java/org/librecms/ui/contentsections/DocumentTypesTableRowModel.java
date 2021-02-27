/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentTypesTableRowModel {

    private String displayName;

    private String contentItemClass;

    private String label;

    private String description;

    private String mode;

    private String defaultLifecycleLabel;

    private List<String> defaultLifecycleUuid;

    private String defaultWorkflowLabel;

    private List<String> defaultWorkflowUuid;

    private String uuid;

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

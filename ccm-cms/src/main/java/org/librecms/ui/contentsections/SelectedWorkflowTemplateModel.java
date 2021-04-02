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

import org.libreccm.workflow.Workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model providing the data about the selected workflow template for the
 * workflow details view.
 *
 * @see ConfigurationWorkflowController
 * @see Workflow
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedWorkflowTemplateModel")
public class SelectedWorkflowTemplateModel {

    /**
     * The ID of the selected workflow template.
     */
    private long workflowId;

    /**
     * The UUID of the selected workflow template.
     */
    private String uuid;

    /**
     * The display name of the selected workflow template.
     */
    private String displayName;

    /**
     * The localized titles of the selected workflow template.
     */
    private Map<String, String> name;

    /**
     * Locales for which no title value has been definied yet.
     */
    private List<String> unusedNameLocales;

    /**
     * The localized descriptions of the selected workflow template.
     */
    private Map<String, String> description;

    /**
     * The locales for which no localized description has definied yet.
     */
    private List<String> unusedDescriptionLocales;

    /**
     * The tasks of the selected workflow template.
     */
    private List<WorkflowTaskTemplateListModel> tasks;

    public long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(final long workflowId) {
        this.workflowId = workflowId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getName() {
        return Collections.unmodifiableMap(name);
    }

    public void setName(Map<String, String> name) {
        this.name = new HashMap<>(name);
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public void setDescription(final Map<String, String> description) {
        this.description = new HashMap<>(description);
    }

    public List<WorkflowTaskTemplateListModel> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void setTasks(final List<WorkflowTaskTemplateListModel> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public List<String> getUnusedNameLocales() {
        return Collections.unmodifiableList(unusedNameLocales);
    }

    public void setUnusedNameLocales(final List<String> unusedNameLocales) {
        this.unusedNameLocales = new ArrayList<>(unusedNameLocales);
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    public void setUnusedDescriptionLocales(
        final List<String> unusedDescriptionLocales
    ) {
        this.unusedDescriptionLocales
            = new ArrayList<>(unusedDescriptionLocales);
    }

    public boolean getHasUnusedNameLocales() {
        return !unusedNameLocales.isEmpty();
    }

    public boolean getHasUnusedDescriptionLocales() {
        return !unusedDescriptionLocales.isEmpty();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

}

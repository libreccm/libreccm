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
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.Workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model providing the data of the selected task of a workflow template for the
 * details view of a task.
 *
 * @see ConfigurationWorkflowController
 * @see Workflow
 * @see Task
 * @see AssignableTask
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedWorkflowTaskTemplateModel")
public class SelectedWorkflowTaskTemplateModel {

    /**
     * The ID of the task.
     */
    private long taskId;

    /**
     * The UUID of the task.
     */
    private String uuid;

    /**
     * The display label of the task. This value is determined from
     * {@link Task#label} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String displayLabel;

    /**
     * The localized labels of the task.
     */
    private Map<String, String> label;

    /**
     * The locales for which no localized label has been defined yet.
     */
    private List<String> unusedLabelLocales;

    /**
     * The localized descriptions of the task.
     */
    private Map<String, String> description;

    /**
     * The locales for which no localized description has been defined yet.
     */
    private List<String> unusedDescriptionLocales;

    /**
     * Tasks that block the selected task.
     */
    private List<WorkflowTaskTemplateListModel> blockedTasks;

    /**
     * Task that are blocked by the selected tak.
     */
    private List<WorkflowTaskTemplateListModel> blockingTasks;

    /**
     * Ohter that don't block the selected task.
     */
    private Map<String, String> noneBlockingTasks;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(final long taskId) {
        this.taskId = taskId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getLabel() {
        return Collections.unmodifiableMap(label);
    }

    public void setLabel(final Map<String, String> label) {
        this.label = new HashMap<>(label);
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public void setDescription(final Map<String, String> description) {
        this.description = new HashMap<>(description);
    }

    public List<WorkflowTaskTemplateListModel> getBlockedTasks() {
        return Collections.unmodifiableList(blockedTasks);
    }

    public void setBlockedTasks(
        final List<WorkflowTaskTemplateListModel> blockedTasks
    ) {
        this.blockedTasks = new ArrayList<>(blockedTasks);
    }

    public List< WorkflowTaskTemplateListModel> getBlockingTasks() {
        return Collections.unmodifiableList(blockingTasks);
    }

    public void setBlockingTasks(
        final List<WorkflowTaskTemplateListModel> blockingTasks
    ) {
        this.blockingTasks = new ArrayList<>(blockingTasks);
    }

    public List<String> getUnusedLabelLocales() {
        return Collections.unmodifiableList(unusedLabelLocales);
    }

    public void setUnusedLabelLocales(final List<String> unusedLabelLocales) {
        this.unusedLabelLocales = new ArrayList<>(unusedLabelLocales);
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

    public boolean getHasUnusedLabelLocales() {
        return !unusedLabelLocales.isEmpty();
    }

    public boolean getHasUnusedDescriptionLocales() {
        return !unusedDescriptionLocales.isEmpty();
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(final String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public Map<String, String> getNoneBlockingTasks() {
        return Collections.unmodifiableMap(noneBlockingTasks);
    }

    public void setNoneBlockingTasks(
        final Map<String, String> noneBlockingTasks
    ) {
        this.noneBlockingTasks = new HashMap<>(noneBlockingTasks);
    }

}

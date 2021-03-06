/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedWorkflowTaskTemplateModel")
public class SelectedWorkflowTaskTemplateModel {

    private long taskId;

    private String uuid;

    private String displayLabel;

    private Map<String, String> label;

    private List<String> unusedLabelLocales;

    private Map<String, String> description;

    private List<String> unusedDescriptionLocales;

    private List<WorkflowTaskTemplateListModel> blockedTasks;

    private List<WorkflowTaskTemplateListModel> blockingTasks;

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

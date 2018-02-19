/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.cms.ui.ContentItemPage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.User;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskState;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.lifecycle.Lifecycle;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;
import org.librecms.lifecycle.LifecycleManager;
import org.librecms.lifecycle.LifecycleRepository;
import org.librecms.lifecycle.Phase;
import org.librecms.lifecycle.PhaseDefinition;
import org.librecms.lifecycle.PhaseRepository;
import org.librecms.workflow.CmsTask;
import org.librecms.workflow.CmsTaskType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ItemLifecycleAdminController implements Serializable {

    private static final Logger LOGGER = LogManager
        .getLogger(ItemLifecycleAdminController.class);
    private static final long serialVersionUID = -6482423583933975632L;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private LifecycleDefinitionRepository lifecycleDefRepo;

    @Inject
    private LifecycleManager lifecycleManager;

    @Inject
    private LifecycleRepository lifecycleRepo;

    @Inject
    private PhaseRepository phaseRepo;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private TaskManager taskManager;

    @Inject
    private WorkflowManager workflowManager;

    @Inject
    private WorkflowRepository workflowRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isAssignedToAbstractCategory(final ContentItem item) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    item.getObjectId())));

        final long count = contentItem
            .getCategories()
            .stream()
            .map(Categorization::getCategory)
            .filter(Category::isAbstractCategory)
            .count();

        return count > 0;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<LifecycleDefinition> getLifecycleDefinitions(
        final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentSection with ID %d in the database.",
                    section.getObjectId())));

        return contentSection
            .getLifecycleDefinitions()
            .stream()
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<LifecycleDefinition> getDefaultLifecycle(
        final ContentItem item) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    item.getObjectId())));

        final LifecycleDefinition definition = contentItem
            .getContentType()
            .getDefaultLifecycle();

        if (definition == null) {
            return Optional.empty();
        } else {
            return Optional
                .of(lifecycleDefRepo
                    .findById(definition.getDefinitionId())
                    .get());
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public LifecycleDefinition getDefinitionOfLifecycle(final ContentItem item) {

//        final ContentItem contentItem = itemRepo
//            .findById(item.getObjectId())
//            .orElseThrow(() -> new IllegalArgumentException(String
//            .format("No ContentItem with ID %d in the database.",
//                    item.getObjectId())));
        final ContentItem liveItem = itemManager
            .getLiveVersion(item, ContentItem.class)
            .get();

        return liveItem.getLifecycle().getDefinition();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<ItemPhaseTableRow> findPhasesOfLifecycle(
        final Lifecycle lifecycle) {

        Objects.requireNonNull(lifecycle);

        final Lifecycle ofLifecycle = lifecycleRepo
            .findById(lifecycle.getLifecycleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Lifecycle with ID %d in the database.",
                    lifecycle.getLifecycleId())));

        return ofLifecycle
            .getPhases()
            .stream()
            .map(this::buildItemPhaseTableRow)
            .collect(Collectors.toList());

    }

    private ItemPhaseTableRow buildItemPhaseTableRow(final Phase phase) {

        final PhaseDefinition definition = phase.getDefinition();

        final ItemPhaseTableRow row = new ItemPhaseTableRow();
        row.setName(globalizationHelper
            .getValueFromLocalizedString(definition.getLabel()));
        row.setDescription(globalizationHelper
            .getValueFromLocalizedString(definition.getDescription()));
        row.setStartDate(phase.getStartDateTime());
        row.setEndDate(phase.getEndDateTime());

        return row;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getPublishingTabUrl(final ContentItem item) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    item.getObjectId())));

        return ContentItemPage.getItemURL(contentItem,
                                          ContentItemPage.PUBLISHING_TAB);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void repulish(final ContentItem item) {

        Objects.requireNonNull(item);

        final Optional<ContentItem> liveItem = itemManager
            .getLiveVersion(item, ContentItem.class);
        if (liveItem.isPresent()) {
            final ContentItem contentItem = itemRepo
                .findById(item.getObjectId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No ContentItem with Id %d in the database.",
                        item.getObjectId())));
            itemManager.publish(contentItem);
            final Workflow workflow = contentItem.getWorkflow();
            if (workflow != null
                    && workflow.isActive()
                    && workflow.getTasksState() == TaskState.ENABLED) {

                workflowManager.finish(contentItem.getWorkflow());
            }
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void publish(final String itemUuid,
                        final long cycleDefId,
                        final Date endDate,
                        final String workflowUuid,
                        final User user) {

        final ContentItem contentItem = itemRepo
            .findByUuid(itemUuid)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with UUID %s in the database.",
                    itemUuid)));

        final LifecycleDefinition cycleDef = lifecycleDefRepo
            .findById(cycleDefId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No LifecycleDefinition with ID %d in the database.",
                    cycleDefId)));

        if (itemManager.isLive(contentItem)) {
            contentItem.setLifecycle(null);
            itemRepo.save(contentItem);
        }

        final ContentItem pending = itemManager.publish(contentItem, cycleDef);
        final Lifecycle lifecycle = pending.getLifecycle();

        if (endDate != null) {

            // update individual phases
            final List<Phase> phases = lifecycle.getPhases();

            for (final Phase phase : phases) {
                final Date thisStart = phase.getStartDateTime();
                if (thisStart.compareTo(endDate) > 0) {
                    phase.setStartDateTime(endDate);
                    phaseRepo.save(phase);
                }
            }
        }

        lifecycleManager.startLifecycle(lifecycle);

        if (workflowUuid != null) {
            final Workflow workflow = workflowRepo
                .findByUuid(workflowUuid)
                .get();
            finish(workflow, contentItem, user);
        }
    }

    private void finish(final Workflow workflow,
                        final ContentItem item,
                        final User user) {

        if (workflow != null && user != null) {

            final List<Task> enabledTasks = workflowManager
                .findEnabledTasks(workflow);
            for (final Task task : enabledTasks) {
                LOGGER.debug("Task is {}.", task.getUuid());
                if (task instanceof CmsTask) {
                    final CmsTask cmsTask = (CmsTask) task;

                    if (cmsTask.getTaskType() == CmsTaskType.DEPLOY) {
                        LOGGER.debug("Found DEPLOY task.");
                        taskManager.finish(cmsTask);
                    }
                }
            }

            final CMSConfig cmsConfig = confManager
                .findConfiguration(CMSConfig.class);

            if (cmsConfig.isDeleteWorkflowAfterPublication()) {
                workflowRepo.delete(workflow);
            } else {
                // restart the workflow by recreating it
                // from the same workflow template
                final Workflow template = workflow.getTemplate();
                if (template == null) {
                    return;
                }
                workflowRepo.delete(workflow);
                final Workflow restarted = workflowManager.createWorkflow(
                    template, item);
                // Startring the workflow will probably do the wrong thing, because most of the time
                // the current user would be a publisher, not an author 
                workflowRepo.save(restarted);
            }
        }
    }

}

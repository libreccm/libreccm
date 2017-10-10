/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.cms.ui.ContentItemPage;

import org.libreccm.security.Role;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskRepository;
import org.libreccm.workflow.Workflow;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.workflow.CmsTask;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TaskFinishFormController {

    @Inject
    private ContentItemRepository itemRepo;
    
    @Inject
    private TaskRepository taskRepo;

    @Inject
    private TaskManager taskManager;
    
    @Inject
    private AssignableTaskManager assignableTaskManager;

    @Inject
    private Shiro shiro;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssignableTask> findEnabledTasks(final Workflow workflow) {
        final User user = shiro.getUser().get();
        final List<Role> roles = user.getRoleMemberships().stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        return assignableTaskManager.findAssignedTasks(workflow, roles);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void addComment(final CmsTask task, final String comment) {

        final Task theTask = taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Task with ID %d in the database.",
                    task.getTaskId())));

        taskManager.addComment(theTask, comment);
    }
    
    @Transactional
    public void lock(final AssignableTask task) {
        
        final AssignableTask theTask = (AssignableTask) taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Task with ID %d in the database.",
                    task.getTaskId())));
        
        assignableTaskManager.unlockTask(theTask);
        assignableTaskManager.lockTask(theTask);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public void enable(final Task task) {
        
        final Task theTask = taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Task with ID %d in the database.",
                    task.getTaskId())));
        
        taskManager.enable(theTask);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public void finish(final CmsTask task) {
        
        final Task theTask = taskRepo
            .findById(task.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Task with ID %d in the database.",
                    task.getTaskId())));
        
        taskManager.finish(theTask);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getContentItemPublishUrl(final ContentItem item) {
        
        final ContentItem contentItem = itemRepo
        .findById(item.getObjectId())
        .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.", 
                    item.getObjectId())));
        
        return ContentItemPage.getItemURL(contentItem, 
                                          ContentItemPage.PUBLISHING_TAB);
            
        
    }
    
}

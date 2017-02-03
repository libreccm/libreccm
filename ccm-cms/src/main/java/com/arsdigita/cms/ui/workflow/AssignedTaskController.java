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

import com.arsdigita.bebop.table.RowData;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskRepository;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Controller for the assigned task components.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssignedTaskController {

    @Inject
    private WorkflowManager workflowManager;

    @Inject
    private AssignableTaskRepository userTaskRepo;

    @Inject
    private Shiro shiro;
    
    @Inject
    private ConfigurationManager confManager;
    
    private Locale defaultLocale;
    
    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }
    

    @Transactional(Transactional.TxType.REQUIRED)
    public List<RowData<Long>> getAssignedTasks(final Workflow workflow) {
        final User user = shiro.getUser().get();
        final List<AssignableTask> tasks = userTaskRepo.getAssignedTasks(user,
                                                                   workflow);

        return tasks
            .stream()
            .map(task -> createRowData(task))
            .collect(Collectors.toList());

    }

    private RowData<Long> createRowData(final AssignableTask task) {
        
        
        final RowData<Long> rowData = new RowData<>(3);
        
        rowData.setRowKey(task.getTaskId());
        
        // Change when Workflow forms provide fields to enter localised label.
        rowData.setColData(0, task.getLabel().getValue(defaultLocale));
        
        if (task.isLocked()) {
            rowData.setColData(1, task.getLockingUser().getName());
        } else {
            rowData.setColData(1,"");
        }
        
        rowData.setColData(2, "");
        
        
        return rowData;
    }

}

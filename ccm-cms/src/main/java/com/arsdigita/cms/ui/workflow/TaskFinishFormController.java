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

import org.libreccm.security.Role;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskManager;
import org.libreccm.workflow.Workflow;

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
    private AssignableTaskManager taskManager;
    
    @Inject
    private Shiro shiro;
    
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssignableTask> findEnabledTasks(final Workflow workflow) {
        final User user = shiro.getUser().get();
        final List<Role> roles = user.getRoleMemberships().stream()
        .map(membership -> membership.getRole())
        .collect(Collectors.toList());
        
        return taskManager.findAssignedTasks(workflow, roles);
    }
    
}

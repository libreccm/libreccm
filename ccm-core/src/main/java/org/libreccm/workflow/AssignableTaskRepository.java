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
package org.libreccm.workflow;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.security.Role;
import org.libreccm.security.User;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

/**
 * Repository for assignable tasks.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssignableTaskRepository
    extends AbstractEntityRepository<Long, AssignableTask> {

    @Override
    public Class<AssignableTask> getEntityClass() {
        return AssignableTask.class;
    }

    @Override
    public boolean isNew(final AssignableTask task) {
        return task.getTaskId() == 0;
    }

    public List<AssignableTask> findEnabledTasksForWorkflow(
        final User user, final Workflow workflow) {
        final TypedQuery<AssignableTask> query = getEntityManager()
            .createNamedQuery(
                "UserTask.findEnabledTasksForWorkflow", AssignableTask.class);
        query.setParameter("user", user);
        query.setParameter("workflow", workflow);

        return query.getResultList();
    }

    public List<AssignableTask> getAssignedTasks(final User user,
                                                 final Workflow workflow) {
        final TypedQuery<AssignableTask> query = getEntityManager()
            .createNamedQuery(
                "UserTask.findAssignedTasks", AssignableTask.class);
        final List<Role> roles = user.getRoleMemberships()
            .stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        query.setParameter("roles", roles);
        query.setParameter("workflow", workflow);

        return query.getResultList();
    }

}

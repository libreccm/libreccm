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

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Task}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TaskRepository extends AbstractEntityRepository<Long, Task> {

    private static final long serialVersionUID = -8366096936911158514L;

    @Override
    public Class<Task> getEntityClass() {
        return Task.class;
    }

    @Override
    public String getIdAttributeName() {
        return "taskId";
    }

    @Override
    public boolean isNew(final Task task) {
        return task.getTaskId() == 0;
    }

    @Override
    protected void initNewEntity(final Task task) {
        super.initNewEntity(task);
        task.setUuid(UUID.randomUUID().toString());
    }

    /**
     * Finds a {@link Task} by its uuid.
     *
     * @param uuid The uuid of the item to find
     *
     * @return An optional either with the found item or empty
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Task> findByUuid(final String uuid) {
        final TypedQuery<Task> query = getEntityManager().createNamedQuery(
            "Task.findByUuid", Task.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}

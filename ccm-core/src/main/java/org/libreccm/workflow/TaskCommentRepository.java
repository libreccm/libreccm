/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
import java.util.Optional;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * 
 */
@RequestScoped
public class TaskCommentRepository
    extends AbstractEntityRepository<Long, TaskComment> {

    private static final long serialVersionUID = -420902242220205847L;

    @Override
    public Class<TaskComment> getEntityClass() {
        return TaskComment.class;
    }

    @Override
    public String getIdAttributeName() {
        return "commentId";
    }
    
    @Override
    public Long getIdOfEntity(final TaskComment entity) {
        return entity.getCommentId();
    }

    @Override
    public boolean isNew(TaskComment entity) {
        return entity.getCommentId() == 0;
    }

    /**
     * Finds a {@link TaskComment} by its uuid.
     *
     * @param uuid The uuid of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<TaskComment> findByUuid(final String uuid) {
        final TypedQuery<TaskComment> query = getEntityManager()
            .createNamedQuery("TaskComment.findByUuid", TaskComment.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}

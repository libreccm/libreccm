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
package org.libreccm.core;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * A repository class for retrieving, storing and deleting {@link Group}s.
 * 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class GroupRepository extends AbstractEntityRepository<Long, Group> {

    @Inject
    private transient EntityManager entityManager;

    @Override
    public Class<Group> getEntityClass() {
        return Group.class;
    }

    @Override
    public boolean isNew(final Group entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null.");
        }
        
        return entity.getSubjectId() == 0;
    }

    public Group findByGroupName(final String groupName) {
        final TypedQuery<Group> query = entityManager.createNamedQuery(
            "Group.findGroupByName", Group.class);
        query.setParameter("groupName", groupName);

        final List<Group> result = query.getResultList();

        //Check if result list is empty and if not return the first element.
        //If their is a result than there can only be one because the
        //name column of group has a unique constraint.
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

}

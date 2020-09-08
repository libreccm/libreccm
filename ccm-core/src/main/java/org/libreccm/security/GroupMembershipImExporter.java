/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.security;

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Exporter/Importer for {@link GroupMembership} entities.
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Processes(GroupMembership.class)
public class GroupMembershipImExporter 
    extends AbstractEntityImExporter<GroupMembership> {
    
    @Inject
    private EntityManager entityManager;

    @Override
    protected Class<GroupMembership> getEntityClass() {

        return GroupMembership.class;
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {

        final Set<Class<? extends Exportable>> entities = new HashSet<>();
        entities.add(User.class);
        entities.add(Group.class);

        return entities;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final GroupMembership entity) {

        entity.setMembershipId(0);
        entityManager.persist(entity);
    }

}

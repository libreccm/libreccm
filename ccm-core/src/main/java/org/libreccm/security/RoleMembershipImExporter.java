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
 * Exporter/Importer for {@link RoleMembership}s.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Processes(RoleMembership.class)
public class RoleMembershipImExporter
    extends AbstractEntityImExporter<RoleMembership> {

    @Inject
    private EntityManager entityManager;

    @Override
    protected Class<RoleMembership> getEntityClass() {

        return RoleMembership.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final RoleMembership entity) {

        entityManager.persist(entity);
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {

        final Set<Class<? extends Exportable>> classes = new HashSet<>();
        classes.add(User.class);
        classes.add(Group.class);
        classes.add(Role.class);
        
        return classes;
    }

}

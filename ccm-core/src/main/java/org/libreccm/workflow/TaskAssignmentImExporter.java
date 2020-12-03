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

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Exporter/Importer for {@link TaskAssignment}s.
 * 
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(TaskAssignment.class)
public class TaskAssignmentImExporter 
    extends AbstractEntityImExporter<TaskAssignment> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Class<TaskAssignment> getEntityClass() {

        return TaskAssignment.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final TaskAssignment entity) {
        
        entityManager.persist(entity);
        
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        
        final Set<Class<? extends Exportable>> classes = new HashSet<>();
        classes.add(AssignableTask.class);
        
        return classes;
    }
}

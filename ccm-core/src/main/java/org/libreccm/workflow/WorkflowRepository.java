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
import org.libreccm.core.CcmObject;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Repository for {@link Workflow}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class WorkflowRepository extends AbstractEntityRepository<Long, Workflow> {

    @Override
    public Class<Workflow> getEntityClass() {
        return Workflow.class;
    }

    @Override
    public boolean isNew(final Workflow workflow) {
        return workflow.getWorkflowId() == 0;
    }

    @Override
    protected void initNewEntity(final Workflow workflow) {
        super.initNewEntity(workflow);
        workflow.setUuid(UUID.randomUUID().toString());
    }

    /**
     * Finds the workflow for an given object if the object has workflow.
     *
     * @param object The object
     * @return An {@link Optional} containing the workflow assigned to the
     * {@code object} if the object has a workflow. Otherwise an empty
     * {@link Optional} is returned.
     */
    public Optional<Workflow> findWorkflowForObject(final CcmObject object) {
        if (object == null) {
            throw new IllegalArgumentException(
                    "Can't find a workflow for object null.");
        }

        final TypedQuery<Workflow> query = getEntityManager().createNamedQuery(
                "Workflow.findForObject", Workflow.class);
        query.setParameter("object", object);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}

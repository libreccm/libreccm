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
import java.util.Optional;
import java.util.UUID;

/**
 * A repository for {@link WorkflowTemplate}s.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class WorkflowTemplateRepository
    extends AbstractEntityRepository<Long, WorkflowTemplate> {

    @Override
    public Class<WorkflowTemplate> getEntityClass() {
        return WorkflowTemplate.class;
    }

    @Override
    public boolean isNew(final WorkflowTemplate template) {
        return template.getWorkflowId() == 0;
    }
    
    @Override
    public void initNewEntity(final WorkflowTemplate workflowTemplate) {
        super.initNewEntity(workflowTemplate);
        workflowTemplate.setUuid(UUID.randomUUID().toString());
    }

    /**
     * Find a {@link WorkflowTemplate} by its UUID.
     *
     * @param uuid The UUID of the {@link WorkflowTemplate} to find.
     *
     * @return An {@link Optional} containing the {@link WorkflowTemplate}
     * identified by the provided UUID.
     */
    public Optional<WorkflowTemplate> findByUuid(final String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "The UUID of the WorkflowTemplate to retrieve can't be " +
                            "null or empty.");
        }

        final TypedQuery<WorkflowTemplate> query = getEntityManager()
                .createNamedQuery("WorkflowTemplate.findByUuid",
                        WorkflowTemplate.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch(NoResultException ex) {
            return Optional.empty();
        }
    }
}

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
package org.libreccm.categorization;

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;
import org.libreccm.web.CcmApplication;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

/**
 * Exporter/Importer for {@link DomainOwnership} entities.
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(DomainOwnership.class)
public class DomainOwnershipImExporter
    extends AbstractEntityImExporter<DomainOwnership> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Class<DomainOwnership> getEntityClass() {
        return DomainOwnership.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final DomainOwnership entity) {
        entityManager.persist(entity);
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        final Set<Class<? extends Exportable>> classes = new HashSet<>();
        classes.add(CcmApplication.class);
        classes.add(Domain.class);

        return classes;
    }

    @Override
    protected DomainOwnership reloadEntity(final DomainOwnership entity) {
        try {
            return entityManager
                .createNamedQuery(
                    "DomainOwnership.findById",
                    DomainOwnership.class
                )
                .setParameter(
                    "ownershipId",
                    Objects.requireNonNull(entity.getOwnershipId())
                ).getSingleResult();
        } catch (NoResultException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "DomainOwnership entity %s not found in database.",
                    Objects.toString(entity)
                )
            );
        }
    }

}

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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Exporter/Importer for {@link Categorization} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(Categorization.class)
public class CategorizationImExporter
    extends AbstractEntityImExporter<Categorization> {

    @Inject
    private EntityManager entityManager;

    @Inject
    private Instance<CategorizationImExporterDependenciesProvider> dependenciesProviders;

    @Override
    public Class<Categorization> getEntityClass() {
        return Categorization.class;
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        final Set<Class<? extends Exportable>> entities = new HashSet<>();
        entities.add(Category.class);

        dependenciesProviders.forEach(
            provider -> entities.addAll(provider.getCategorizableEntities())
        );

        return entities;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final Categorization entity) {
        entityManager.persist(entity);
    }

    @Override
    protected Categorization reloadEntity(final Categorization entity) {
        try {
            return entityManager.createNamedQuery(
                "Categorization.findById",
                Categorization.class
            ).setParameter(
                "categorizationId",
                Objects.requireNonNull(entity).getCategorizationId()
            ).getSingleResult();
        } catch (NoResultException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Categorization entity %s was not found in the database.",
                    Objects.toString(entity)
                )
            );
        }
    }

}

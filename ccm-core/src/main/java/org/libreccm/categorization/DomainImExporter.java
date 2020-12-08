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

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Exporter/Importer for {@link Domain} entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(Domain.class)
public class DomainImExporter extends AbstractEntityImExporter<Domain> {

    @Inject
    private DomainRepository domainRepository;

    @Override
    public Class<Domain> getEntityClass() {

        return Domain.class;
    }

    @Override
    protected void saveImportedEntity(final Domain entity) {

        domainRepository.save(entity);
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {

        return Collections.emptySet();
    }

    @Override
    protected Domain reloadEntity(final Domain entity) {
        return domainRepository
            .findById(Objects.requireNonNull(entity).getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Domain entity %s was not found in the database.",
                        Objects.toString(entity)
                    )
                )
            );
    }

}

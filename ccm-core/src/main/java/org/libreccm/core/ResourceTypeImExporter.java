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
package org.libreccm.core;

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Processes(ResourceType.class)
public class ResourceTypeImExporter 
    extends AbstractEntityImExporter<ResourceType>{
    
    @Inject
    private ResourceTypeRepository repository;

    @Override
    public Class<ResourceType> getEntityClass() {
        return ResourceType.class;
    }

    @Override
    protected void saveImportedEntity(final ResourceType entity) {
        repository.save(entity);
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        return Collections.emptySet();
    }

    @Override
    protected ResourceType reloadEntity(final ResourceType entity) {
        return repository
            .findById(
                Objects.requireNonNull(entity).getResourceTypeId()
            )
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "The provided ResourceType %s was not found in the "
                            + "database.",
                        Objects.toString(entity)
                    )
                )
            );
    }
    
    
    
}

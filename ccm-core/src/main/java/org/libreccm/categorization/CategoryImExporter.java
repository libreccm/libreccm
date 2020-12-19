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
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Exporter/Importer for {@link Category} entities.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(Category.class)
public class CategoryImExporter extends AbstractEntityImExporter<Category> {

    @Inject
    private CategoryRepository categoryRepository;

    @Override
    public Class<Category> getEntityClass() {
        return Category.class;
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        final Set<Class<? extends Exportable>> entities = new HashSet<>();
        entities.add(Domain.class);

        return entities;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final Category entity) {
        categoryRepository.save(entity);
    }

    @Override
    protected Category reloadEntity(final Category entity) {
        return categoryRepository
            .findById(Objects.requireNonNull(entity).getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Category entity %s does not exist in the database.",
                        Objects.toString(entity)
                    )
                )
            );
    }

}

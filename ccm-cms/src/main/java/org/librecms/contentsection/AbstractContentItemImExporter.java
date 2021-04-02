/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.contentsection;

import org.libreccm.categorization.Category;
import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractContentItemImExporter<T extends ContentItem>
    extends AbstractEntityImExporter<T> {

    @Inject
    private ContentItemRepository itemRepository;

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {

        final Set<Class<? extends Exportable>> entities = new HashSet<>();
        entities.add(Category.class);
        entities.add(ContentSection.class);

        return entities;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveImportedEntity(final T entity) {
        itemRepository.save(entity);
    }

    @Override
    protected T reloadEntity(final T entity) {
        return itemRepository
            .findById(
                Objects.requireNonNull(entity).getObjectId(), getEntityClass()
            ).orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "ContentItem entity %s not found in database.",
                        Objects.toString(entity)
                    )
                )
            );
    }

}

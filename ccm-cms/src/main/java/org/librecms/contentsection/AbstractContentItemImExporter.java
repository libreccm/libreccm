/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

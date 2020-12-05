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
package org.libreccm.imexport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

/**
 * Base class for importers/exporters. Implementations must be annotated with
 * {@link Procsses} to register the implementation in the Import/Export service.
 *
 * Implementations must also be CDI beans. It is recommended that the beans are
 * {@link RequestScoped}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The type of the entity which is processed by the implementation.
 */
public abstract class AbstractEntityImExporter<T extends Exportable> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Returns the Entity class which is handled by the implementation. This
     * should be the same values than the class in the {@link Proceesses}
     * annotation. This duplication is necessary because we need the value in
     * qualifier annotation to be able to request an implementation for a
     * specific class from the CDI container. But we can't access the
     * annotations in a portable way in the rest of the code because CDI
     * containers usually create a {@link java.lang.reflect.Proxy} class and
     * there is no portable way to unproxy a class.
     *
     * @return The Entity class which is handled by the implementation.
     */
    public abstract Class<T> getEntityClass();

    /**
     * A set of entities which should be processed before this implementation is
     * used. We can't use an annotation for this because we can't access the
     * annotations in a portable way in the rest of the code because CDI
     * containers usually create a {@link java.lang.reflect.Proxy} class and
     * there is no portable way to unproxy a class.
     *
     *
     * @return A {@link Set} of exportable entity classes which should be
     *         processed before the entities which are processed by this
     *         implementation. If the implementation has no dependencies an
     *         empty {@link Set} should be returned.
     */
    protected abstract Set<Class<? extends Exportable>> getRequiredEntities();

    @Transactional(Transactional.TxType.REQUIRED)
    public T importEntity(final String data) throws ImportExpection {

        try {
            final T entity = objectMapper.readValue(data, getEntityClass());
            saveImportedEntity(entity);
            return entity;
        } catch (IOException ex) {
            throw new ImportExpection(ex);
        }

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String exportEntity(final Exportable entity) throws ExportException {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException ex) {
            throw new ExportException(String.format(
                "Failed to export entity \"%s\" of type \"%s\".",
                entity.getUuid(),
                getEntityClass().getName()),
                                      ex);
        }

    }

    protected abstract void saveImportedEntity(T entity);

}

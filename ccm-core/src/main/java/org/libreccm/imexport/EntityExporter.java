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

import javax.enterprise.context.RequestScoped;

/**
 * Interface for exporters. Implementation must be annotated with
 * {@link Exports} to register the implementation in the Import/Export system.
 *
 * Implementations must be CDI beans with annotated with {@link RequestScoped}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The type of the entity which is processed by the implementation.
 */
public interface EntityExporter<T extends Exportable> {

    /**
     * Converts the provided entity to a JSON object and an optional array of
     * associations.
     *
     * @param entity The entity to export.
     *
     * @return The JSON representation of the entity.
     */
    ExportedEntity exportEntity(T entity);

}

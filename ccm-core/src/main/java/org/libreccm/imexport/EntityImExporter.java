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
import javax.json.JsonObject;

/**
 * Interface for importers/exporters. Implementations must be annotated with
 * {@link Procsses} to register the implementation in the Import/Export service.
 * 
 * Implementations must also be CDI beans. It is recommended that the beans
 * are {@link RequestScoped}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The type of the entity which is processed by the implementation.
 */
public interface EntityImExporter<T extends Exportable> {
    
    T importEntity(JsonObject data);
    
    JsonObject exportEntity(T entity);
    
}

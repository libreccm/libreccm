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

import java.util.Objects;
import java.util.Optional;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * A transfer object used by {@link Exporter} to wrap the exported object and
 * optionally the associations extracted from the object.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExportedEntity {
    
    private final JsonObject entity;
    private final JsonArray associations;
    
    public ExportedEntity(final JsonObject entity) {
        
        this.entity = Objects.requireNonNull(entity);
        this.associations = null;
    }
    
    public ExportedEntity(final JsonObject entity,
                          final JsonArray associations) {
        
        this.entity = Objects.requireNonNull(entity);
        this.associations = Objects.requireNonNull(associations);
    }
    
    public JsonObject getEntity() {
        return entity;
    }
    
    public Optional<JsonArray> getAssociations() {
        return Optional.of(associations);
    }
    
}

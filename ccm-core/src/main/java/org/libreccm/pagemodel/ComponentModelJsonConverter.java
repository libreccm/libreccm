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
package org.libreccm.pagemodel;

import javax.json.JsonObject;

/**
 * Interface which the JSON converters for {@link ComponentModel}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface ComponentModelJsonConverter {
    
    /**
     * Convert a {@link ComponentModel} to JSON.
     * 
     * @param componentModel The {@link ComponentModel} to convert.
     * @return The JSON representation of the provided {@link ComponentModel}.
     */
    JsonObject toJson(ComponentModel componentModel);
    
    /**
     * Read the values of a {@link ComponentModel} from a JSON object.
     * 
     * @param jsonObject The JSON object with the values.
     * @return The {@link ComponentModel}.
     */
    ComponentModel fromJson(JsonObject jsonObject);
    
}

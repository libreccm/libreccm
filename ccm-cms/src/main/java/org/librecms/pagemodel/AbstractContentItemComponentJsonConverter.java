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
package org.librecms.pagemodel;

import org.libreccm.pagemodel.AbstractComponentModelJsonConverter;

import java.util.Objects;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractContentItemComponentJsonConverter
    extends AbstractComponentModelJsonConverter {

    private static final String MODE = "mode";

    protected void convertContentItemComponentPropertiesToJson(
        final ContentItemComponent component,
        final JsonObjectBuilder objectBuilder) {

        Objects.requireNonNull(component);
        Objects.requireNonNull(objectBuilder);

        objectBuilder.add(MODE, component.getMode());
    }

    protected void readContentItemComponentPropertiesFromJson(
        final JsonObject jsonObject, final ContentItemComponent component) {

        Objects.requireNonNull(jsonObject);
        Objects.requireNonNull(component);
        
        if (!jsonObject.isNull(MODE)) {
            
            component.setMode(jsonObject.getString(MODE));
        }
    }

}

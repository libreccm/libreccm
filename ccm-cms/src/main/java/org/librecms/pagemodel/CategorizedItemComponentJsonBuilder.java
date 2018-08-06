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

import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ConvertsComponentModel;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@ConvertsComponentModel(componentModel = CategorizedItemComponent.class)
public class CategorizedItemComponentJsonBuilder
    extends AbstractContentItemComponentJsonConverter {

    @Override
    public JsonObject toJson(final ComponentModel componentModel) {

        Objects.requireNonNull(componentModel);

        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        if (!(componentModel instanceof CategorizedItemComponent)) {
            throw new IllegalArgumentException(
                "This converter only processes CategorizedItemComponents.");
        }

        final CategorizedItemComponent component
                                       = (CategorizedItemComponent) componentModel;
        
        convertBasePropertiesToJson(component, objectBuilder);
        convertContentItemComponentPropertiesToJson(component, objectBuilder);

        return objectBuilder.build();
    }

    @Override
    public ComponentModel fromJson(final JsonObject jsonObject) {
        
        Objects.requireNonNull(jsonObject);
        
        final CategorizedItemComponent component = new CategorizedItemComponent();
        
        readBasePropertiesFromJson(jsonObject, component);
        readContentItemComponentPropertiesFromJson(jsonObject, component);
        
        return component;
    }

}

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

import java.util.Objects;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Converter for the basic properties of a {
 *
 * @ComponentModel}. Can be used as base for implementations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractComponentModelJsonConverter
    implements ComponentModelJsonConverter {

    /**
     * Converts the basic properties of a {@link ComponentModel} to JSON.
     *
     * @param componentModel The {@link ComponentModel}.
     * @param objectBuilder  The {@link JsonObjectBuilder} to use.
     */
    protected void convertBasePropertiesToJson(
        final ComponentModel componentModel,
        final JsonObjectBuilder objectBuilder) {

        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(objectBuilder);

        objectBuilder
            .add("componentModelId",
                 Long.toString(componentModel.getComponentModelId()))
            .add("uuid", componentModel.getUuid())
            .add("modelUuid", componentModel.getModelUuid())
            .add("key", componentModel.getKey())
            .add("type", componentModel.getClass().getName());

        if (componentModel.getIdAttribute() != null) {
            objectBuilder.add("idAttribute",
                              componentModel.getIdAttribute());
        }

        if (componentModel.getClassAttribute() != null) {
            objectBuilder.add("classAttribute",
                              componentModel.getClassAttribute());
        }

        if (componentModel.getStyleAttribute() != null) {
            objectBuilder.add("styleAttribute",
                              componentModel.getStyleAttribute());
        }
    }

    /**
     * Read the basic properties of a {@link ComponentModel} from a
     * {@link JsonObject}.
     *
     * @param jsonObject The {@link JsonObject}.
     * @param componentModel The {@link ComponentModel}.
     */
    protected void readBasePropertiesFromJson(
        final JsonObject jsonObject, final ComponentModel componentModel) {

        Objects.requireNonNull(jsonObject);
        Objects.requireNonNull(componentModel);

        componentModel.setComponentModelId(
            jsonObject.getInt("componentModelId"));
        componentModel.setUuid(jsonObject.getString("uuid"));
        componentModel.setModelUuid(jsonObject.getString("modelUuid"));
        componentModel.setKey(jsonObject.getString("key"));

        if (jsonObject.getString("idAttribute") != null) {
            componentModel.setIdAttribute(jsonObject.getString("idAttribute"));
        }

        if (jsonObject.getString("classAttribute") != null) {
            componentModel
                .setClassAttribute(jsonObject.getString("classAttribute"));
        }

        if (jsonObject.getString("styleAttribute") != null) {
            componentModel
                .setStyleAttribute(jsonObject.getString("styleAttribute"));
        }
    }

}

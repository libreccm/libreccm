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
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ConvertsComponentModel;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@ConvertsComponentModel(componentModel = ItemListComponent.class)
public class ItemListComponentJsonConverter
    extends AbstractComponentModelJsonConverter {

    private static final String LIST_ORDER = "listOrder";
    private static final String PAGE_SIZE = "pageSize";
    private static final String LIMIT_TO_TYPE = "limitToType";
    private static final String DESCENDING = "descending";

    @Override
    public JsonObject toJson(final ComponentModel componentModel) {

        Objects.requireNonNull(componentModel);

        if (!(componentModel instanceof ItemListComponent)) {
            throw new IllegalArgumentException(
                "This implementation does only handle ItemListComponents.");
        }

        final ItemListComponent itemList = (ItemListComponent) componentModel;
        final JsonObjectBuilder builder = Json.createObjectBuilder();

        convertBasePropertiesToJson(itemList, builder);

        builder.add(DESCENDING, itemList.isDescending());
        builder.add(LIMIT_TO_TYPE, itemList.getLimitToType());
        builder.add(PAGE_SIZE, itemList.getPageSize());

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        if (itemList.getListOrder() != null) {

            itemList
                .getListOrder()
                .stream()
                .forEach(value -> arrayBuilder.add(value));
        }
        builder.add(LIST_ORDER, arrayBuilder.build());

        return builder.build();
    }

    @Override
    public ComponentModel fromJson(final JsonObject jsonObject) {

        Objects.requireNonNull(jsonObject);

        final ItemListComponent itemList = new ItemListComponent();
        readBasePropertiesFromJson(jsonObject, itemList);

        if (!jsonObject.isNull(DESCENDING)) {
            itemList.setDescending(jsonObject.getBoolean(DESCENDING));
        }
        
        if (!jsonObject.isNull(LIMIT_TO_TYPE)) {
            itemList.setLimitToType(jsonObject.getString(LIMIT_TO_TYPE));
        }
        
        if (!jsonObject.isNull(PAGE_SIZE)) {
            itemList.setPageSize(jsonObject.getInt(PAGE_SIZE));
        }
        
        if (!jsonObject.isNull(LIST_ORDER)) {
            
            itemList.setListOrder(
                jsonObject
                    .getJsonArray(LIST_ORDER)
            .stream()
            .map(value -> value.toString())
            .collect(Collectors.toList()));
            
        }

        return itemList;
    }

}

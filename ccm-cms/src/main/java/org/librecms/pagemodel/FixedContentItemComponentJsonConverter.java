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

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ConvertsComponentModel;
import org.librecms.contentsection.ContentItemRepository;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@ConvertsComponentModel(componentModel = FixedContentItemComponent.class)
public class FixedContentItemComponentJsonConverter
    extends AbstractContentItemComponentJsonConverter {

    private static final String CONTENT_ITEM = "contentItem";
    
    @Inject
    private ContentItemRepository itemRepo;

    @Override
    public JsonObject toJson(final ComponentModel componentModel) {

        Objects.requireNonNull(componentModel);

        if (!(componentModel instanceof FixedContentItemComponent)) {
            throw new IllegalArgumentException(
                "This converter only processes FixedContentItemComponents.");
        }

        final FixedContentItemComponent component
                                            = (FixedContentItemComponent) componentModel;

        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        convertBasePropertiesToJson(component, objectBuilder);
        convertContentItemComponentPropertiesToJson(component, objectBuilder);

        if (component.getContentItem() != null) {
            objectBuilder.add(CONTENT_ITEM,
                              component.getContentItem().getUuid());
        }

        return objectBuilder.build();
    }

    @Override
    public ComponentModel fromJson(final JsonObject jsonObject) {

        Objects.requireNonNull(jsonObject);

        final FixedContentItemComponent component
                                            = new FixedContentItemComponent();

        readBasePropertiesFromJson(jsonObject, component);
        readContentItemComponentPropertiesFromJson(jsonObject, component);

        if (!jsonObject.isNull(CONTENT_ITEM)) {
            
            final String uuid = jsonObject.getString(CONTENT_ITEM);
            
            component
                .setContentItem(itemRepo
                    .findByUuid(uuid)
                    .orElseThrow(() -> new UnexpectedErrorException(
                        String.format("No ContentItem with UUID \"%s\" exists.",
                                      uuid))));
            
        }
        
        return component;
    }

}

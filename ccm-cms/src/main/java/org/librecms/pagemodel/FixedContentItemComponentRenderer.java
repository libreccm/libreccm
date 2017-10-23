/*
 * Copyright (C) 2017 LibreCCM Foundation.
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

import org.libreccm.pagemodel.ComponentModelType;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@ComponentModelType(componentModel = FixedContentItemComponent.class)
public class FixedContentItemComponentRenderer
    extends AbstractContentItemComponentRenderer<FixedContentItemComponent> {

    @Inject
    private ContentItemRepository itemRepo;

    @Override
    protected ContentItem getContentItem(
        final FixedContentItemComponent componentModel,
        final Map<String, Object> parameters) {

        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        if (componentModel.getContentItem() == null) {
            throw new NotFoundException("No ContentItem configured.");
        }

        return itemRepo
            .findById(componentModel.getContentItem().getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    componentModel.getContentItem().getObjectId())));
    }

}

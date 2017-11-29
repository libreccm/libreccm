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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.core.CcmObject;
import org.librecms.contentsection.ContentItem;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static org.librecms.pages.PagesConstants.*;

import org.libreccm.pagemodel.RendersComponent;
import org.librecms.contentsection.ContentItemVersion;

/**
 * Renderer for the {@link GreetingItemComponent}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@RendersComponent(componentModel = GreetingItemComponent.class)
public class GreetingItemComponentRenderer
    extends AbstractContentItemComponentRenderer<GreetingItemComponent> {

    private static final Logger LOGGER = LogManager
        .getLogger(GreetingItemComponentRenderer.class);

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private CategoryManager categoryManager;

    @Override
    protected ContentItem getContentItem(
        final GreetingItemComponent componentModel,
        final Map<String, Object> parameters) {

        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        if (!parameters.containsKey(PARAMETER_CATEGORY)) {
            throw new IllegalArgumentException(
                "The parameters map passed to this GreetingItem component does "
                    + "not include the parameter \"category\"");
        }

        if (!(parameters.get(PARAMETER_CATEGORY) instanceof Category)) {
            throw new IllegalArgumentException(String
                .format("The parameters map passed to this GreetingItem "
                            + "component contains the parameter \"category\", but the "
                        + "parameter is not of type \"%s\" but of type \"%s\".",
                        Category.class.getName(),
                        parameters.get(PARAMETER_CATEGORY).getClass().getName()));
        }

        final Category category = categoryRepo
            .findById(((CcmObject) parameters.get(PARAMETER_CATEGORY))
                .getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No category with ID %d in the database.",
            ((CcmObject) parameters.get(PARAMETER_CATEGORY)).getObjectId())));

        final Optional<CcmObject> indexObj = categoryManager
            .getIndexObject(category)
            .stream()
            .filter(object -> object instanceof ContentItem)
            .filter(item -> {
                return ((ContentItem) item)
                    .getVersion() == ContentItemVersion.LIVE;
            })
            .findFirst();

        if (indexObj.isPresent()) {

            if (indexObj.get() instanceof ContentItem) {
                return (ContentItem) indexObj.get();
            } else {
//                throw new NotFoundException(String
//                    .format(
//                        "The index item %s of category %s does not have "
//                            + "a live version.",
//                        Objects.toString(indexObj),
//                        Objects.toString(category)));
                LOGGER.debug("The index item {} of category {} does not have "
                                 + "a live version.",
                             Objects.toString(indexObj),
                             Objects.toString(category));
                return null;
            }
        } else {
            LOGGER.debug("The category {} does not have a index item.",
                         Objects.toString(category));
            return null;
//            throw new NotFoundException(String
//                .format("The category %s does not have a index item.",
//                        Objects.toString(category)));
        }
    }

}

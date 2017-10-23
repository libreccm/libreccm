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

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.core.CcmObject;
import org.libreccm.pagemodel.ComponentModelType;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemVersion;

import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.ws.rs.NotFoundException;

import static org.librecms.pages.PagesConstants.*;

/**
 * Renderer for the {@link CategorizedItemComponent}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@ComponentModelType(componentModel = CategorizedItemComponent.class)
public class CategorizedItemComponentRenderer
    extends AbstractContentItemComponentRenderer<CategorizedItemComponent> {

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private EntityManager entityManager;

    @Override
    protected ContentItem getContentItem(
        final CategorizedItemComponent componentModel,
        final Map<String, Object> parameters) {

        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        if (!parameters.containsKey(PARAMETER_CATEGORY)) {
            throw new IllegalArgumentException(String
                .format("The parameters map passed to this component does "
                            + "not include the parameter \"%s\"",
                        PARAMETER_CATEGORY));
        }

        if (!parameters.containsKey(PARAMETER_ITEMNAME)) {
            throw new IllegalArgumentException(String
                .format("The parameters map passed to this component does "
                            + "not include the parameter \"%s\"",
                        PARAMETER_ITEMNAME));
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

        final String itemName = (String) parameters.get(PARAMETER_ITEMNAME);

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ContentItem> criteriaQuery = builder
            .createQuery(ContentItem.class);

        final Root<ContentItem> from = criteriaQuery.from(ContentItem.class);
        final Join<ContentItem, Categorization> join = from
            .join("categories");

        final TypedQuery<ContentItem> query = entityManager
            .createQuery(criteriaQuery
                .select(from)
                .where(builder.and(
                    builder.equal(from.get("displayName"), itemName),
                    builder.equal(from.get("version"), ContentItemVersion.DRAFT),
                    builder.equal(join.get("category"), category)
                )));

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            throw new NotFoundException(String
                .format("No ContentItem with name \"%s\" in Category \"%s\".",
                        itemName,
                        Objects.toString(category)));
        }
    }

}

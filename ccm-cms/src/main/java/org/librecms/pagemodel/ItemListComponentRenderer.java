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

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;

import java.util.Map;

import javax.enterprise.context.RequestScoped;

import org.libreccm.pagemodel.ComponentRenderer;
import org.librecms.contentsection.ContentItem;
import org.librecms.pagemodel.contentitems.AbstractContentItemRenderer;
import org.librecms.pagemodel.contentitems.ContentItemRenderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import static org.librecms.pages.PagesConstants.*;

import org.libreccm.pagemodel.RendersComponent;

/**
 * Renderer for the {@link ItemListComponent}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@RendersComponent(componentModel = ItemListComponent.class)
public class ItemListComponentRenderer
    implements ComponentRenderer<ItemListComponent> {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentItemRenderers itemRenderers;

    @Inject
    private EntityManager entityManager;

    @Inject
    private HttpServletRequest request;

    @Override
    public Map<String, Object> renderComponent(
        final ItemListComponent componentModel,
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

        final Category category = (Category) parameters.get(PARAMETER_CATEGORY);
        final Locale language;
        if (parameters.containsKey(PARAMETER_LANGUAGE)) {
            language = new Locale((String) parameters.get(PARAMETER_LANGUAGE));
        } else {
            final KernelConfig kernelConfig = confManager
                .findConfiguration(KernelConfig.class);
            language = kernelConfig.getDefaultLocale();
        }

        final List<Category> categories;
        if (componentModel.isDescending()) {
            categories = collectCategories(category);
        } else {
            categories = new ArrayList<>();
        }
        categories.add(category);

        final Class<? extends ContentItem> limitToType = getLimitToType(
            componentModel);

        final List<? extends ContentItem> items = findItems(
            limitToType,
            categories,
            componentModel.getListOrder(),
            componentModel.getPageSize());

        final Map<String, Object> result = new HashMap<>();
        result.put("items",
                   items
                       .stream()
                       .map(item -> renderItem(item, language))
                       .collect(Collectors.toList()));
        return result;
    }

    private List<Category> collectCategories(final Category category) {

        if (category.getSubCategories().isEmpty()) {
            return Collections.emptyList();
        } else {
            final List<Category> categories = new ArrayList<>();
            for (final Category subCategory : category.getSubCategories()) {
                categories.add(subCategory);
                categories.addAll(collectCategories(subCategory));
            }
            return categories;
        }
    }

    private List<? extends ContentItem> findItems(
        final Class<? extends ContentItem> limitToType,
        final List<Category> categories,
        final List<String> listOrder,
        final int pageSize) {

        final CriteriaBuilder criteriaBuilder = entityManager
            .getCriteriaBuilder();
        final CriteriaQuery<? extends ContentItem> criteriaQuery
                                                       = criteriaBuilder
                .createQuery(limitToType);
        final Root<? extends ContentItem> from = criteriaQuery
            .from(limitToType);
        final Join<? extends ContentItem, Categorization> catJoin = from
            .join("categories");

        criteriaQuery.where(catJoin.get("category").in(categories));
//        criteriaQuery
//            .where(criteriaBuilder
//                .and(catJoin.get("category").in(categories),
//                     criteriaBuilder.equal(catJoin.get("index"), false)));

        criteriaQuery
            .orderBy(listOrder
                .stream()
                .map(order -> createOrder(order, from, criteriaBuilder))
                .collect(Collectors.toList()));

        return entityManager
            .createQuery(criteriaQuery)
            .setFirstResult(getOffset(pageSize))
            .setMaxResults(pageSize)
            .getResultList();
    }

    private Class<? extends ContentItem> getLimitToType(
        final ItemListComponent componentModel) {

        final String className = componentModel.getLimitToType();

        if (className == null
                || className.matches("\\s*")) {
            return ContentItem.class;
        } else {
            final Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                throw new UnexpectedErrorException(ex);
            }

            if (ContentItem.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                final Class<? extends ContentItem> type
                                                       = (Class<? extends ContentItem>) clazz;
                return type;
            } else {
                throw new UnexpectedErrorException(String
                    .format(
                        "The type \"%s\" set in ItemList is not a subtype of "
                            + "\"%s\".",
                        clazz.getName(),
                        ContentItem.class.getName()));
            }
        }
    }

    private Order createOrder(final String order,
                              final Root<? extends ContentItem> from,
                              final CriteriaBuilder criteriaBuilder) {

        if (order.endsWith(" ASC")) {
            final String colName = order
                .substring(0, order.length() - " ASC".length());
            return (criteriaBuilder.asc(from.get(colName)));
        } else if (order.endsWith(" DESC")) {
            final String colName = order
                .substring(0, order.length() - " DESC".length());
            return criteriaBuilder.desc(from.get(colName));
        } else {
            return criteriaBuilder.asc(from.get(order));
        }
    }

    private int getOffset(final int pageSize) {

        if (request.getParameterMap().containsKey("page")) {
            final String value = request.getParameter("page");
            if (value.matches("\\d*")) {
                final int page = Integer.valueOf(value);

                return page * pageSize;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private Map<String, Object> renderItem(final ContentItem item,
                                           final Locale language) {

        final AbstractContentItemRenderer renderer = itemRenderers
            .findRenderer(item.getClass());
        return renderer.render(item, language);
    }

}

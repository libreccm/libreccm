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

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.ComponentBuilder;
import org.libreccm.pagemodel.ComponentModelType;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.librecms.pages.PagesConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@ComponentModelType(componentModel = GreetingItem.class)
public class GreetingItemBuilder implements ComponentBuilder<GreetingItem> {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private ContentItemL10NManager iteml10nManager;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private PermissionChecker permissionChecker;

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Map<String, Object> buildComponent(
        final GreetingItem componentModel,
        final Map<String, Object> parameters) {

        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        if (!parameters.containsKey(PARAMETER_CATEGORY)) {
            throw new IllegalArgumentException("The parameters map passed to "
                                                   + "this GreetingItem component does not include the parameter "
                                               + "\"category\"");
        }

        if (!(parameters.get(PARAMETER_CATEGORY) instanceof Category)) {
            throw new IllegalArgumentException(String
                .format("The parameters map passed to "
                            + "this GreetingItem component contains the parameter "
                        + "\"category\", but the parameter is not of type"
                            + "\"%s\" but of type \"%s\".",
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
            .getIndexObject(category);

        if (indexObj.isPresent()) {

            if (indexObj.get() instanceof ContentItem) {

                final ContentItem indexItem;
                if (itemManager.isLive((ContentItem) indexObj.get())) {
                    indexItem = itemManager
                        .getLiveVersion((ContentItem) indexObj.get(),
                                        ContentItem.class)
                        .get();
                } else {
                    throw new NotFoundException(String
                        .format(
                            "The index item %s of category %s does not have "
                                + "a live version.",
                            Objects.toString(indexObj),
                            Objects.toString(category)));
                }

                if (permissionChecker.isPermitted(ItemPrivileges.VIEW_PUBLISHED,
                                                  indexItem)) {
                    return generateGreetingItem(componentModel,
                                                parameters,
                                                indexItem);
                } else {
                    throw new WebApplicationException(
                        "You are not permitted to view the view version of this item.",
                        Response.Status.UNAUTHORIZED);
                }
            } else {
                throw new NotFoundException(String
                    .format("The index object %s of category %s is not a "
                                + "ContentItem.",
                            Objects.toString(indexObj),
                            Objects.toString(category)));
            }
        } else {
            throw new NotFoundException(String
                .format("The category %s does not have a index item.",
                        Objects.toString(category)));
        }
    }

    private Map<String, Object> generateGreetingItem(
        final GreetingItem componentModel,
        final Map<String, Object> parameters,
        final ContentItem item) {

        final String language = (String) parameters.get(PARAMETER_LANGUAGE);

        if (iteml10nManager.hasLanguage(item, new Locale(language))) {

            final BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(item.getClass());
            } catch (IntrospectionException ex) {
                throw new UnexpectedErrorException(ex);
            }

            final PropertyDescriptor[] properties = beanInfo
                .getPropertyDescriptors();

            final Map<String, Object> result = new HashMap<>();

            for (final PropertyDescriptor propertyDescriptor : properties) {
                renderProperty(propertyDescriptor, componentModel, item, result);
            }

            return result;
        } else {
            throw new NotFoundException("Requested language is not available.");
        }
    }

    private void renderProperty(final PropertyDescriptor propertyDescriptor,
                                final GreetingItem componentModel,
                                final ContentItem item,
                                final Map<String, Object> result) {

        final String propertyName = propertyDescriptor.getName();
        if (componentModel.getExcludedPropertyPaths().contains(propertyName)) {
            return;
        }

        final Method readMethod = propertyDescriptor.getReadMethod();
        if (Collection.class.isAssignableFrom(propertyDescriptor
            .getPropertyType())) {

            final Map<String, Object> associated;
            try {
                associated = generateAssociatedObject(readMethod.invoke(item));
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnsupportedOperationException(ex);
            }
            result.put(propertyName, associated);
        } else if (isValueType(propertyDescriptor.getPropertyType())) {
            try {
                result.put(propertyName, readMethod.invoke(item));
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }
        } else {
            //ToDo
        }

    }

    private Map<String, Object> generateAssociatedObject(final Object obj) {

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass());
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final PropertyDescriptor[] properties = beanInfo
            .getPropertyDescriptors();

        final Map<String, Object> result = new HashMap<>();
        for (final PropertyDescriptor propertyDescriptor : properties) {
            final Class<?> type = propertyDescriptor.getPropertyType();
            if (isValueType(type)) {
                final Method readMethod = propertyDescriptor.getReadMethod();
                try {
                    result.put(propertyDescriptor.getName(),
                               readMethod.invoke(obj));
                } catch (IllegalAccessException
                         | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }
            }
        }

        return result;
    }

    private boolean isValueType(final Class<?> typeToTest) {
        final Class<?>[] types = new Class<?>[]{
            Boolean.class,
            Boolean.TYPE,
            Character.class,
            Character.TYPE,
            Byte.class,
            Byte.TYPE,
            Double.class,
            Double.TYPE,
            Float.class,
            Float.TYPE,
            Integer.class,
            Integer.TYPE,
            Long.class,
            Long.TYPE,
            Short.class,
            Short.TYPE,
            String.class
        };

        for (final Class<?> type : types) {
            if (type.isAssignableFrom(typeToTest)) {
                return true;
            }
        }

        return typeToTest.isArray()
                   && (typeToTest.getComponentType()
                           .isAssignableFrom(Byte.class)
                       || typeToTest.getComponentType()
                       .isAssignableFrom(Byte.TYPE));
    }

}

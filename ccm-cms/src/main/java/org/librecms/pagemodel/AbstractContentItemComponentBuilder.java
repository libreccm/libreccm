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

import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.pagemodel.ComponentBuilder;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.librecms.pages.PagesConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractContentItemComponentBuilder<T extends ContentItemComponent>
    implements ComponentBuilder<T> {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentItemL10NManager iteml10nManager;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private PermissionChecker permissionChecker;

    protected abstract ContentItem getContentItem(
        T componentModel, final Map<String, Object> parameters);

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Map<String, Object> buildComponent(
        final T componentModel,
        final Map<String, Object> parameters) {

        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        final ContentItem contentItem = getContentItem(componentModel,
                                                       parameters);

        if (Boolean.TRUE.equals(parameters.get("showDraftItem"))) {

            final ContentItem draftItem = itemManager
                .getDraftVersion(contentItem, contentItem.getClass());

            if (permissionChecker.isPermitted(ItemPrivileges.PREVIEW, draftItem)) {
                final Map<String, Object> result = generateItem(componentModel,
                                                                parameters,
                                                                draftItem);
                result.put("showDraftItem", Boolean.TRUE);

                return result;
            } else {
                throw new WebApplicationException(
                    "You are not permitted to view the draft version of this item.",
                    Response.Status.UNAUTHORIZED);
            }

        } else {

            final ContentItem liveItem = itemManager
                .getLiveVersion(contentItem, contentItem.getClass())
                .orElseThrow(() -> new NotFoundException(
                "This content item does not "
                    + "have a live version."));

            if (permissionChecker.isPermitted(ItemPrivileges.VIEW_PUBLISHED,
                                              liveItem)) {
                return generateItem(componentModel,
                                    parameters,
                                    liveItem);
            } else {
                throw new WebApplicationException(
                    "You are not permitted to view the live version of "
                        + "this item.",
                    Response.Status.UNAUTHORIZED);
            }
        }
    }

    protected Map<String, Object> generateItem(
        final T componentModel,
        final Map<String, Object> parameters,
        final ContentItem item) {

        final Locale language;
        if (parameters.containsKey("language")) {
            language = new Locale((String) parameters.get(PARAMETER_LANGUAGE));
        } else {
            final KernelConfig kernelConfig = confManager
                .findConfiguration(KernelConfig.class);
            language = kernelConfig.getDefaultLocale();
        }

        if (iteml10nManager.hasLanguage(item, language)) {
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
                renderProperty(propertyDescriptor,
                               componentModel,
                               language,
                               item,
                               result);
            }

            return result;
        } else {
            throw new NotFoundException("Requested language is not available.");
        }
    }

    protected void renderProperty(final PropertyDescriptor propertyDescriptor,
                                  final T componentModel,
                                  final Locale language,
                                  final ContentItem item,
                                  final Map<String, Object> result) {

        final String propertyName = propertyDescriptor.getName();
        if (componentModel.getExcludedPropertyPaths().contains(propertyName)) {
            return;
        }

        final Method readMethod = propertyDescriptor.getReadMethod();
        if (Collection.class.isAssignableFrom(propertyDescriptor
            .getPropertyType())) {

            final Collection<?> collection;
            try {
                collection = (Collection<?>) readMethod.invoke(item);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }

            final List<Map<String, Object>> associatedObjs = new ArrayList<>();
            for (final Object obj : collection) {
                associatedObjs.add(generateAssociatedObject(obj));
            }

            result.put(propertyName, associatedObjs);
        } else if (isValueType(propertyDescriptor.getPropertyType())) {
            try {
                result.put(propertyName, readMethod.invoke(item));
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }
        } else if (LocalizedString.class.isAssignableFrom(propertyDescriptor
            .getPropertyType())) {

            final LocalizedString localizedString;
            try {
                localizedString = (LocalizedString) readMethod.invoke(item);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }

            result.put(propertyName, localizedString.getValue(language));
        } else {
            final Map<String, Object> associatedObj;
            try {
                associatedObj
                    = generateAssociatedObject(readMethod.invoke(item));
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }
            result.put(propertyName, associatedObj);
        }

    }

    protected Map<String, Object> generateAssociatedObject(final Object obj) {

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

    protected boolean isValueType(final Class<?> typeToTest) {
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

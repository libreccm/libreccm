/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.core.CoreConstants;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Provides several methods for managing {@link PageModel}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageModelManager {

    @Inject
    private EntityManager entityManager;

    @Inject
    private PageModelRepository pageModelRepo;

    @Inject
    private ComponentModelRepository componentModelRepo;

    private final Map<String, PageModelComponentModel> components
                                                       = new HashMap<>();

    /**
     * Called by CDI after an instance of this class is created. Initialises the
     * {@link #components} by retrieving the data about all available
     * {@link ComponentModel}s.
     */
    @PostConstruct
    private void init() {
        final ServiceLoader<CcmModule> modules = ServiceLoader.load(
                CcmModule.class);

        for (CcmModule module : modules) {
            final Module moduleData = module.getClass().getAnnotation(
                    Module.class);

            final PageModelComponentModel[] models = moduleData
                    .pageModelComponentModels();

            for (PageModelComponentModel model : models) {
                components.put(model.modelClass().getName(),
                               model);
            }
        }
    }

    /**
     * Creates a new {@link PageModel} for the provided application. The tries
     * to retrieve the appropriate page model by using
     * {@link PageModelRepository#findByApplicationAndName(org.libreccm.web.CcmApplication, java.lang.String)}.
     * Please note that this method will always return the <strong>live</strong>
     * version of the page model.
     *
     * @param name The name of the new page model. Must be unique for the
     * application.
     * @param application The application for which the {@link PageModel} is
     * created.
     * @param type Type of the page model (view technology).
     *
     * @return The new {@link PageModel}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public PageModel createPageModel(final String name,
                                     final CcmApplication application,
                                     final String type) {

        if (application == null) {
            throw new IllegalArgumentException(
                    "Can't create a page model for application null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "The name of a page model can't be null or empty.");
        }

        final long count = pageModelRepo.countByApplicationAndName(application,
                                                                   name);

        if (count > 0) {
            throw new IllegalArgumentException(String.format(
                    "A page model with the name \"%s\" for the application \"%s\" "
                    + "already exists.",
                    name,
                    application.getPrimaryUrl()));
        }

        final PageModel pageModel = new PageModel();

        pageModel.setName(name);
        pageModel.setApplication(application);
        pageModel.setType(type);
        pageModel.setVersion(PageModelVersion.DRAFT);

        return pageModel;
    }

    /**
     * Retrieves the draft version of a {@link PageModel}. To invoke this method
     * the current user needs a permission granting the
     * {@link CoreConstants#PRIVILEGE_ADMIN} privilege.
     *
     * @param pageModel The {@link PageModel} for which the draft version is
     * retrieved.
     * @return The draft version of the provided {@link PageModel}.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public PageModel getDraftVersion(
            @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
            final PageModel pageModel) {

        if (pageModel == null) {
            throw new IllegalArgumentException(
                    "Can't get draft version for page model null.");
        }

        final TypedQuery<PageModel> query = entityManager.createNamedQuery(
                "PageModel.findDraftVersion", PageModel.class);
        query.setParameter("uuid", pageModel.getModelUuid());

        return query.getSingleResult();
    }

    /**
     * Checks if a {@link PageModel} has a live version.
     *
     * @param pageModel The {@link PageModel} to check for a live version.
     * @return {@code true} if there is a live version for the provided
     * {@link PageModel}, {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isLive(final PageModel pageModel) {
        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
                "PageModel.hasLiveVersion", Boolean.class);
        query.setParameter("uuid", pageModel.getModelUuid());

        return query.getSingleResult();
    }

    /**
     *
     * @param pageModel
     * @return
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<PageModel> getLiveVersion(final PageModel pageModel) {

        if (isLive(pageModel)) {
            final TypedQuery<PageModel> query = entityManager.createNamedQuery(
                    "PageModel.findLiveVersion",
                    PageModel.class);
            query.setParameter("uuid", pageModel.getModelUuid());
            return Optional.of(query.getSingleResult());
        } else {
            return Optional.empty();
        }
    }

    public PageModel publish(final PageModel pageModel) {
        final PageModel draftModel = getDraftVersion(pageModel);
        final PageModel liveModel;

        if (isLive(pageModel)) {
            liveModel = getLiveVersion(pageModel).get();
        } else {
            liveModel = new PageModel();
        }

        liveModel.setVersion(PageModelVersion.LIVE);
        liveModel.setModelUuid(draftModel.getModelUuid());

        for (Map.Entry<Locale, String> entry : draftModel.getTitle().getValues()
                .entrySet()) {
            liveModel.getTitle().addValue(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Locale, String> entry : liveModel.getDescription()
                .getValues().entrySet()) {
            liveModel.getDescription().addValue(entry.getKey(),
                                                entry.getValue());
        }

        liveModel.setApplication(draftModel.getApplication());
        liveModel.setType(draftModel.getType());

        liveModel.clearComponents();
        for (final ComponentModel draft : draftModel.getComponents()) {
            final ComponentModel live = publishComponentModel(draft);
            addComponentModel(liveModel, live);
        }

        return liveModel;
    }

    @SuppressWarnings("unchecked")
    private ComponentModel publishComponentModel(final ComponentModel draftModel) {

        final Class<? extends ComponentModel> clazz = draftModel.getClass();

        final ComponentModel liveModel;
        try {
            liveModel = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new UncheckedWrapperException(ex);
        }

        liveModel.setModelUuid(draftModel.getModelUuid());

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException ex) {
            throw new UncheckedWrapperException(ex);
        }

        for (final PropertyDescriptor propertyDescriptor : beanInfo.
                getPropertyDescriptors()) {
            final Class<?> propType = propertyDescriptor.getPropertyType();
            final Method readMethod = propertyDescriptor.getReadMethod();
            final Method writeMethod = propertyDescriptor.getWriteMethod();

            if (propertyIsExcluded(propertyDescriptor.getName())) {
                continue;
            }

            if (writeMethod == null) {
                continue;
            }

            if (propType != null
                        && propType.isAssignableFrom(List.class)) {

                final List<Object> source;
                final List<Object> target;
                try {
                    source = (List<Object>) readMethod.invoke(draftModel);
                    target = (List<Object>) readMethod.invoke(liveModel);
                } catch (IllegalAccessException
                                 | IllegalArgumentException
                                 | InvocationTargetException ex) {
                    throw new UncheckedWrapperException(ex);
                }

                target.addAll(source);
            } else if (propType != null
                               && propType.isAssignableFrom(Map.class)) {

                final Map<Object, Object> source;
                final Map<Object, Object> target;

                try {
                    source = (Map<Object, Object>) readMethod.invoke(draftModel);
                    target = (Map<Object, Object>) readMethod.invoke(liveModel);
                } catch (IllegalAccessException
                                 | IllegalArgumentException
                                 | InvocationTargetException ex) {
                    throw new UncheckedWrapperException(ex);
                }

                source.forEach((key, value) -> target.put(key, value));

            } else if (propType != null
                               && propType.isAssignableFrom(Set.class)) {

                final Set<Object> source;
                final Set<Object> target;

                try {
                    source = (Set<Object>) readMethod.invoke(draftModel);
                    target = (Set<Object>) readMethod.invoke(liveModel);
                } catch (IllegalAccessException
                                 | IllegalArgumentException
                                 | InvocationTargetException ex) {
                    throw new UncheckedWrapperException(ex);
                }

                target.addAll(source);
            } else {
                final Object value;
                try {
                    value = readMethod.invoke(draftModel);
                    writeMethod.invoke(liveModel, value);
                } catch (IllegalAccessException
                                 | IllegalArgumentException
                                 | InvocationTargetException ex) {
                    throw new UncheckedWrapperException(ex);
                }
            }
        }

        componentModelRepo.save(liveModel);

        return liveModel;
    }

    private boolean propertyIsExcluded(final String name) {
        final String[] excluded = new String[]{
            "uuid",
            "modelUuid"
        };

        boolean result = false;
        for (final String current : excluded) {
            if (current.equals(name)) {
                result = true;
                break;
            }
        }

        return result;
    }

    public List<PageModelComponentModel> findAvailableComponents() {
        final List<PageModelComponentModel> list = new ArrayList<>(components
                .values());
        list.sort((component1, component2) -> {
            return component1.modelClass().getName().compareTo(
                    component2.modelClass().getName());
        });

        return list;
    }

    public Optional<PageModelComponentModel> findComponentModel(
            final String className) {

        if (components.containsKey(className)) {
            return Optional.of(components.get(className));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Add a {@link ComponentModel} to a {@link PageModel}.
     *
     * @param pageModel The {@link PageModel} to which component model is added.
     * @param componentModel The {@link ComponentModel} to add.
     */
    public void addComponentModel(final PageModel pageModel,
                                  final ComponentModel componentModel) {

        if (pageModel == null) {
            throw new IllegalArgumentException(
                    "Can't add a component model to page model null.");
        }

        if (componentModel == null) {
            throw new IllegalArgumentException(
                    "Can't add component model null to a page model.");
        }

        pageModel.addComponent(componentModel);
        componentModel.setPageModel(pageModel);

        pageModelRepo.save(pageModel);
        componentModelRepo.save(componentModel);
    }

    /**
     * Removes a {@link ComponentModel} from a {@link PageModel}.
     *
     * @param pageModel The {@link PageModel} from which the
     * {@link ComponentModel} is removed.
     * @param componentModel The {@link ComponentModel} to remove. The component
     * model is also removed from the database.
     */
    public void removeComponentModel(final PageModel pageModel,
                                     final ComponentModel componentModel) {

        if (pageModel == null) {
            throw new IllegalArgumentException(
                    "Can't remove a component model from page model null.");
        }

        if (componentModel == null) {
            throw new IllegalArgumentException(
                    "Can't remove component model null from a page model.");
        }

        pageModel.removeComponent(componentModel);
        componentModel.setPageModel(null);

        pageModelRepo.save(pageModel);
        componentModelRepo.delete(componentModel);
    }

}

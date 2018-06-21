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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CoreConstants;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;
import org.libreccm.pagemodel.styles.CssProperty;
import org.libreccm.pagemodel.styles.MediaQuery;
import org.libreccm.pagemodel.styles.MediaRule;
import org.libreccm.pagemodel.styles.Rule;
import org.libreccm.pagemodel.styles.Styles;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides several methods for managing {@link PageModel}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageModelManager {

    private static final Logger LOGGER = LogManager.getLogger(
        PageModelManager.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private ComponentModelRepository componentModelRepo;

    @Inject
    private ContainerModelRepository containerModelRepo;

    @Inject
    private ContainerModelManager containerModelManager;

    @Inject
    private PageModelRepository pageModelRepo;

    private final Map<String, PageModelComponentModel> components
                                                           = new HashMap<>();

    /**
     * Called by CDI after an instance of this class is created. Initialises the
     * {@link #components} by retrieving the data about all available {@link
     * ComponentModel}s.
     */
    @PostConstruct
    private void init() {

        LOGGER.debug("Initalising {}...", PageModelManager.class.getName());

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
        LOGGER.debug("Initalised {}. Found {} ComponentModels.",
                     PageModelManager.class.getName(),
                     components.size());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public PageModel createPageModel(final String name,
                                     final CcmApplication application) {

        return createPageModel(name, application, "");
    }

    /**
     * Creates a new {@link PageModel} for the provided application. The method
     * tries to retrieve the appropriate application by using {@link
     * PageModelRepository#findLiveByApplicationAndName(org.libreccm.web
     * .CcmApplication,
     * java.lang.String)}. Please note that this method will always return the
     * <strong>draft</strong> version of the page model.
     *
     * @param name        The name of the new page model. Must be unique for the
     *                    application.
     * @param application The application for which the {@link PageModel} is
     *                    created.
     * @param type        Type of the page model.
     *
     * @return The new {@link PageModel}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public PageModel createPageModel(final String name,
                                     final CcmApplication application,
                                     final String type) {

        Objects.requireNonNull(application,
                               "Can't create a page model for application "
                                   + "null");
        Objects.requireNonNull(name, "Then name of a Pagemodel can't be null.");
        if (name.isEmpty()
                || name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a PageModel can't be empty.");
        }

        LOGGER.debug(
            "Creating new PageModel with name \"{}\" for application \"{}\" "
                + "and type \"{}\".",
            name,
            application.getPrimaryUrl(),
            type);

        final long count = pageModelRepo.countLiveByApplicationAndName(
            application,
            name);

        if (count > 0) {
            LOGGER.error("A page model with the name \"{}\" for the "
                             + "application \"{}\" already exists.",
                         name,
                         application.getPrimaryUrl());
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
     * the current user needs a permission granting the {@link
     * CoreConstants#PRIVILEGE_ADMIN} privilege.
     *
     * @param pageModel The {@link PageModel} for which the draft version is
     *                  retrieved.
     *
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
     *
     * @return {@code true} if there is a live version for the provided {@link
     *     PageModel}, {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isLive(final PageModel pageModel) {

        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "PageModel.hasLiveVersion", Boolean.class);
        query.setParameter("uuid", pageModel.getModelUuid());

        return query.getSingleResult();
    }

    /**
     * Retrieves the live version of a {@link PageModel}. This method does not
     * require any privileges.
     *
     * @param pageModel The {@link PageModel} of which the live version is
     *                  retrieved.
     *
     * @return An {@link Optional} containing the live version of the provided
     *         {@link PageModel} if there is a live version. Otherwise an empty
     *         {@link Optional} is returned.
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

    /**
     * Publishes the draft version of a {@link PageModel}. If there is already a
     * live version of the provided {@link PageModel} the live version is
     * updated. If no live version exists a new live version is created.
     *
     * @param pageModel The {@link PageModel} to publish.
     *
     * @return The live version of the provided {@link PageModel}.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public PageModel publish(final PageModel pageModel) {

        Objects.requireNonNull(pageModel, "Can't publish PageModel null.");

        LOGGER.debug("Publishing PageModel \"{}\"...", pageModel.getName());

        final PageModel draftModel = getDraftVersion(pageModel);
        final PageModel liveModel;

        final boolean isLive;
        if (isLive(pageModel)) {
            isLive = true;
            liveModel = getLiveVersion(draftModel).get();
        } else {
            liveModel = new PageModel();
            isLive = false;
        }

        liveModel.setName(draftModel.getName());
        liveModel.setVersion(PageModelVersion.LIVE);
        liveModel.setModelUuid(draftModel.getModelUuid());

        for (Map.Entry<Locale, String> entry : draftModel.getTitle().getValues()
            .entrySet()) {
            liveModel.getTitle().addValue(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Locale, String> entry : draftModel.getDescription()
            .getValues().entrySet()) {
            liveModel.getDescription().addValue(entry.getKey(),
                                                entry.getValue());
        }

        liveModel.setApplication(draftModel.getApplication());
        liveModel.setType(draftModel.getType());

        LOGGER.debug("Publishing ContainerModels of PageModel \"{}\"...",
                     draftModel.getName());
        liveModel.clearContainers();

        draftModel
            .getContainers()
            .stream()
            .map(this::publishContainerModel)
            .forEach(liveContainerModel -> addContainerModel(liveModel,
                                                             liveContainerModel));
        liveModel.setLastModified(new Date());

        pageModelRepo.save(liveModel);
        //if (isLive) {
        //    entityManager.merge(liveModel);
        //} else {
        //    entityManager.persist(liveModel);
        //}

        LOGGER.debug("Successfully published PageModel \"{}\".",
                     liveModel.getName());
        return liveModel;
    }

    private ContainerModel publishContainerModel(
        final ContainerModel draftModel) {

        Objects.requireNonNull(draftModel);

        final ContainerModel liveModel = new ContainerModel();
        liveModel.setKey(draftModel.getKey());
        liveModel.setContainerUuid(draftModel.getContainerUuid());

        final Styles draftStyles = draftModel.getStyles();
        if (draftStyles != null) {
            final Styles liveStyles = new Styles();
            liveStyles.setStyleName(draftStyles.getStyleName());
            liveStyles.setRules(draftStyles
                .getRules()
                .stream()
                .map(this::publishRule)
                .collect(Collectors.toList()));
            liveStyles.setMediaRules(draftStyles
                .getMediaRules()
                .stream()
                .map(this::publishMediaRule)
                .collect(Collectors.toList()));
        }

        draftModel
            .getComponents()
            .stream()
            .map(this::publishComponentModel)
            .forEach(
                liveComponentModel -> containerModelManager
                    .addComponentModel(liveModel, liveComponentModel));

        return liveModel;
    }

    private MediaRule publishMediaRule(final MediaRule draftMediaRule) {

        Objects.requireNonNull(draftMediaRule);

        final MediaRule liveMediaRule = new MediaRule();
        final MediaQuery liveMediaQuery = new MediaQuery();
        liveMediaQuery
            .setMaxWidth(draftMediaRule.getMediaQuery().getMaxWidth());
        liveMediaQuery
            .setMediaType(draftMediaRule.getMediaQuery().getMediaType());
        liveMediaQuery
            .setMinWidth(draftMediaRule.getMediaQuery().getMinWidth());

        liveMediaRule.setRules(draftMediaRule
            .getRules()
            .stream()
            .map(this::publishRule)
            .collect(Collectors.toList()));

        return liveMediaRule;
    }

    private Rule publishRule(final Rule draftRule) {

        Objects.requireNonNull(draftRule);

        final Rule liveRule = new Rule();
        liveRule.setSelector(draftRule.getSelector());
        liveRule.setProperties(draftRule
            .getProperties()
            .stream()
            .map(this::publishCssProperty)
            .collect(Collectors.toList()));

        return liveRule;
    }

    private CssProperty publishCssProperty(final CssProperty draftProperty) {

        Objects.requireNonNull(draftProperty);

        final CssProperty liveProperty = new CssProperty();
        liveProperty.setName(draftProperty.getName());
        liveProperty.setValue(draftProperty.getValue());

        return liveProperty;
    }

    /**
     * Helper method for coping the {@link ComponentModel}s from the draft
     * version to the live version.
     *
     * @param draftModel The draft version of the {@link ComponentModel} to copy
     *                   to the live version of its {@link PageModel}.
     *
     * @return The live version of the {@link ComponentModel}.
     */
    @SuppressWarnings("unchecked")
    private ComponentModel publishComponentModel(final ComponentModel draftModel) {

        Objects.requireNonNull(draftModel,
                               "Can't publish ComponentModel null.");

        LOGGER.debug("Publishing ComponentModel \"{}\"...",
                     draftModel.getKey());

        final Class<? extends ComponentModel> clazz = draftModel.getClass();

        final ComponentModel liveModel;
        try {
            liveModel = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new UnexpectedErrorException(ex);
        }

        liveModel.setModelUuid(draftModel.getModelUuid());

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

        for (final PropertyDescriptor propertyDescriptor : beanInfo.
            getPropertyDescriptors()) {

            LOGGER.debug(
                "Publishing property \"{}\" of ComponentModel \"{}\"...",
                propertyDescriptor.getName(),
                draftModel.getKey());

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

                try {
                    final List<Object> source = (List<Object>) readMethod
                        .invoke(draftModel);
                    final List<Object> target = new ArrayList<>();
                    target.addAll(source);
                    writeMethod.invoke(draftModel, target);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }

            } else if (propType != null
                           && propType.isAssignableFrom(Map.class)) {

                final Map<Object, Object> source;
                final Map<Object, Object> target;

                try {
                    source
                        = (Map<Object, Object>) readMethod.invoke(draftModel);
                    target = (Map<Object, Object>) readMethod.invoke(liveModel);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
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
                    throw new UnexpectedErrorException(ex);
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
                    throw new UnexpectedErrorException(ex);
                }
            }
        }

        componentModelRepo.save(liveModel);

        LOGGER.debug("Successfully published ComponentModel \"{}\".",
                     liveModel.getKey());
        return liveModel;
    }

    /**
     * Helper method to determine if a property is excluded from the publishing
     * process.
     *
     * @param name The name of the property.
     *
     * @return {@code true} if the property is excluded from the publishing
     *         process, {@link false} if not.
     */
    private boolean propertyIsExcluded(final String name) {

        final String[] excluded = new String[]{
            "class",
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

    @Transactional(Transactional.TxType.REQUIRED)
    public void addContainerModel(final PageModel pageModel,
                                  final ContainerModel container) {

        Objects.requireNonNull(pageModel);
        Objects.requireNonNull(container);

        pageModel.addContainer(container);
        container.setPageModel(pageModel);

        containerModelRepo.save(container);
        pageModelRepo.save(pageModel);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removeContainerModel(final PageModel pageModel,
                                     final ContainerModel container) {

        Objects.requireNonNull(pageModel);
        Objects.requireNonNull(container);

        if (!container.getComponents().isEmpty()) {

            throw new IllegalArgumentException("Container is not empty.");
        }

        pageModel.removeContainer(container);
        container.setPageModel(null);

        pageModelRepo.save(pageModel);
        containerModelRepo.delete(container);
    }

}

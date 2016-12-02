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

import org.libreccm.core.CoreConstants;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageModelManager {

    @Inject
    private PageModelRepository pageModelRepo;

    @Inject
    private ComponentModelRepository componentModelRepo;

    private final Map<String, PageModelComponentModel> components
                                                           = new HashMap<>();

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
     * Creates a new {@link PageModel} for the provided application.
     *
     * @param name        The name of the new page model. Must be unique for the
     *                    application.
     * @param application The application for which the {@link PageModel} is
     *                    created.
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

        return pageModel;
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
     * @param pageModel      The {@link PageModel} to which component model is
     *                       added.
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
     * @param pageModel      The {@link PageModel} from which the
     *                       {@link ComponentModel} is removed.
     * @param componentModel The {@link ComponentModel} to remove. The component
     *                       model is also removed from the database.
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

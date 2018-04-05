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
package org.libreccm.pagemodel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
public class ContainerModelManager implements Serializable {

    private static final long serialVersionUID = -2793646505397414050L;

    private static final Logger LOGGER = LogManager
        .getLogger(ContainerModelManager.class);

    @Inject
    private ContainerModelRepository containerRepo;

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
        LOGGER.debug("Initalising {}...",
                     ContainerModelManager.class.getName());

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
                     ContainerModelManager.class.getName(),
                     components.size());
    }

    /**
     * Adds a {@link ComponentModel} to a {@link ContainerModel}.
     *
     * @param container      The {@link ContainerModel} to which component model
     *                       is added.
     * @param componentModel The {@link ComponentModel} to add.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void addComponentModel(final ContainerModel container,
                                  final ComponentModel componentModel) {

        if (container == null) {
            throw new IllegalArgumentException(
                "Can't add a component model to page model null.");
        }

        if (componentModel == null) {
            throw new IllegalArgumentException(
                "Can't add component model null to a page model.");
        }

        container.addComponent(componentModel);
        componentModel.setContainer(container);

        containerRepo.save(container);
        componentModelRepo.save(componentModel);
    }

    /**
     * Removes a {@link ComponentModel} from a {@link ContainerModel}.
     *
     * @param container      The {@link ContainerModel} from which the
     *                       {@link ComponentModel} is removed.
     * @param componentModel The {@link ComponentModel} to remove. The component
     *                       model is also removed from the database.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeComponentModel(final ContainerModel container,
                                     final ComponentModel componentModel) {

        if (container == null) {
            throw new IllegalArgumentException(
                "Can't remove a component model from page model null.");
        }

        if (componentModel == null) {
            throw new IllegalArgumentException(
                "Can't remove component model null from a page model.");
        }

        container.removeComponent(componentModel);
        componentModel.setContainer(null);

        containerRepo.save(container);
        componentModelRepo.delete(componentModel);
    }

}

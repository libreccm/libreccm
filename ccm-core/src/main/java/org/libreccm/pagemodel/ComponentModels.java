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
package org.libreccm.pagemodel;

import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * An Utility class which provides access to all component models available.
 *
 * This class is an {@link ApplicationScoped} CDI bean. The {@link #init} method
 * is called by the CDI container after creating an instance of this class. The
 * {@link #init} method retrieves the informations about the available
 * {@link ComponentModel}s from the module classes. For exactly from the
 * {@link Module#pageModelComponentModels()} property of the {@link Module}
 * annotation of the module class.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ComponentModels {

    private final List<PageModelComponentModel> availableComponentModels
                                                    = new ArrayList<>();

    private final Map<String, PageModelComponentModel> componentInfos
                                                           = new HashMap<>();

    /**
     * Creates the list of available {@link ComponentModels}. Called by the CDI
     * container.
     */
    @PostConstruct
    private void init() {

        final ServiceLoader<CcmModule> modules = ServiceLoader
            .load(CcmModule.class);

        for (final CcmModule module : modules) {

            final Module moduleData = module
                .getClass()
                .getAnnotation(Module.class);

            final PageModelComponentModel[] componentModels = moduleData
                .pageModelComponentModels();

            for (final PageModelComponentModel componentModel : componentModels) {
                availableComponentModels.add(componentModel);
                componentInfos.put(componentModel.modelClass().getName(),
                                   componentModel);
            }
        }
    }

    /**
     * Get a list of the available {@link ComponentModel}s.
     *
     * @return A (unmodifiable) list of all available {@link ComponentModel}s.
     */
    public List<PageModelComponentModel> findAvailableComponentModels() {
        return Collections.unmodifiableList(availableComponentModels);
    }

    /**
     * Get the informations about a specific {@link ComponentModel}
     * implementation.
     *
     * @param clazz The class of the {@link ComponentModel} implementation.
     *
     * @return An {@link Optional} containing the informations about the
     *         {@link ComponentModel} implementation. If the class is not a
     *         {@link ComponentModel} implementation or is an unknown
     *         implementation an empty {@link Optional} is returned.
     */
    public Optional<PageModelComponentModel> getComponentModelInfo(
        final Class<? extends ComponentModel> clazz) {

        return getComponentModelInfo(clazz.getName());
    }

    /**
     * Get the informations about a specific {@link ComponentModel}
     * implementation.
     *
     * @param className The name of the class of the {@link ComponentModel}
     *                  implementation.
     *
     * @return An {@link Optional} containing the informations about the
     *         {@link ComponentModel} implementation. If the class is not a
     *         {@link ComponentModel} implementation or is an unknown
     *         implementation an empty {@link Optional} is returned.
     */
    public Optional<PageModelComponentModel> getComponentModelInfo(
        final String className) {

        if (componentInfos.containsKey(className)) {
            return Optional.of(componentInfos.get(className));
        } else {
            return Optional.empty();
        }
    }

}

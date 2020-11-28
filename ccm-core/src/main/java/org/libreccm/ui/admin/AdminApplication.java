/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.ui.IsAuthenticatedFilter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Collects the controllers for the admin application and registers them with
 * JAX-RS.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationPath("/@admin")
public class AdminApplication extends Application {

    private static final Logger LOGGER = LogManager.getLogger(
        AdminApplication.class
    );

    /**
     * Injection point for the admin pages.
     */
    @Inject
    private Instance<AdminPage> adminPages;

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();

        classes.add(IsAuthenticatedFilter.class);

        classes.addAll(
            adminPages
                .stream()
                .map(AdminPage::getControllerClasses)
                .flatMap(controllers -> controllers.stream())
                .collect(Collectors.toSet())
        );

        LOGGER.debug(
            "Adding classes to AdminApplication: {}", Objects.toString(classes)
        );

        return classes;
    }

}

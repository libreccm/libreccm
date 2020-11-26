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
package org.libreccm.ui.admin.applications;

import org.libreccm.ui.admin.AdminConstants;
import org.libreccm.ui.admin.AdminPage;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ApplicationsPage implements AdminPage {

    @Inject
    @Any
    private Instance<ApplicationController> applicationControllers;
    
    @Override
    public Set<Class<?>> getControllerClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(ApplicationsController.class);
        
        applicationControllers
            .stream()
            .map(controller -> controller.getClass())
            .forEach(classes::add);
        
        return classes;
    }

    @Override
    public String getUriIdentifier() {
        return String.format(
            "%s#getApplicationTypes", ApplicationsController.class
                .getSimpleName()
        );
    }

    @Override
    public String getLabelBundle() {
        return AdminConstants.ADMIN_BUNDLE;
    }

    @Override
    public String getLabelKey() {
        return "applications.label";
    }

    @Override
    public String getDescriptionBundle() {
        return AdminConstants.ADMIN_BUNDLE;
    }

    @Override
    public String getDescriptionKey() {
        return "applications.description";
    }

    @Override
    public String getIcon() {
        return "puzzle";
    }

    @Override
    public int getPosition() {
        return 40;
    }

}

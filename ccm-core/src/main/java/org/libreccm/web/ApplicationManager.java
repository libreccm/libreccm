/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.web;

import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ApplicationManager {

    @Inject
    private transient EntityManager entityManager;

    private Map<String, ApplicationType> applicationTypes;

    @PostConstruct
    private void loadApplicationTypes() {
        final ServiceLoader<CcmModule> modules = ServiceLoader.load(
            CcmModule.class);

        for (CcmModule module : modules) {
            final Module moduleData = module.getClass().getAnnotation(
                Module.class);

            final ApplicationType[] appTypes = moduleData.applicationTypes();

            for (ApplicationType appType : appTypes) {
                applicationTypes.put(appType.name(), appType);
            }
        }
    }

    public Map<String, ApplicationType> getApplicationTypes() {
        return Collections.unmodifiableMap(applicationTypes);
    }

    public <T extends CcmApplication> T createInstance(
        final ApplicationType type,
        final String path,
        final Class<T> applicationClass) throws ApplicationCreateException {
        try {
            @SuppressWarnings("unchecked")
            final ApplicationCreator<T> creator = type.creator().newInstance();
            final T application = creator.createInstance(path, type);

            entityManager.persist(application);

            return application;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new ApplicationCreateException("Failed to create application.",
                                                 ex);
        }
    }

    public void deleteInstance(final CcmApplication application) {
        entityManager.remove(application);
    }

    public CcmApplication findApplicationByPath(final String path) {
        final TypedQuery<CcmApplication> query = entityManager.createNamedQuery(
            "retrieveApplicationForPath", CcmApplication.class);
        final List<CcmApplication> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalArgumentException("Ambiguous path.");
        } else {
            return result.get(0);
        }
    }

    public String getServletPath(final CcmApplication application) {
        final String typeName = application.getApplicationType();

        final ApplicationType type = applicationTypes.get(typeName);

        if (type == null) {
            throw new IllegalArgumentException(String.format(
                "Unknown application type \"%s\".", typeName));
        }

        if (type.servletPath().isEmpty()) {
            if (type.servlet().equals(HttpServlet.class)) {
                throw new IllegalArgumentException(String.format(
                    "Application type \"%s\" can no servlet path nor a serlet "
                        + "definition.",
                    typeName));
            } else {
                final Class<? extends HttpServlet> servletClass = type.servlet();

                if (servletClass.isAnnotationPresent(WebServlet.class)) {
                    return servletClass.getAnnotation(WebServlet.class)
                        .urlPatterns()[0];
                } else {
                    throw new IllegalArgumentException(String.format(
                        "Provided servlet for application type \"%s\" has not "
                            + "@WebServlet annotation.",
                        typeName));
                }
            }
        } else {
            return type.servletPath();
        }
    }

}

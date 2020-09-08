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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.transaction.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

/**
 * Manager for application instances.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ApplicationManager implements Serializable {

    private static final long serialVersionUID = -4623791386536335252L;

    private static final Logger LOGGER = LogManager.getLogger(
        ApplicationManager.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private Map<String, ApplicationType> applicationTypes = new HashMap<>();

    /**
     * Init function run by the CDI container after an instance was created.
     * Loads all available application types into the {@link #applicationTypes}
     * map.
     */
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

    /**
     * Get all available application types.
     *
     * @return An unmodifiable {@link Map} containing all available application
     *         types. The key of the map is the name of the application.
     */
    public Map<String, ApplicationType> getApplicationTypes() {
        return Collections.unmodifiableMap(applicationTypes);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends CcmApplication> T createInstance(
        final ApplicationType type,
        final String path,
        final Class<T> applicationClass) throws ApplicationCreateException {
        @SuppressWarnings("unchecked")
        final ApplicationCreator<T> creator = CdiUtil.createCdiUtil().findBean(
            type.creator());
        final T application = creator.createInstance(path, type);

        entityManager.persist(application);

        return application;
    }

    /**
     * Deletes an application instance.
     *
     * @param application The application instance to delete.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteInstance(final CcmApplication application) {
        entityManager.remove(application);
    }

    /**
     * Retrieve an application instance by its path.
     *
     * @param path The path of the application instance to retrieve.
     *
     * @return The application instance.
     */
    public CcmApplication findApplicationByPath(final String path) {
        final TypedQuery<CcmApplication> query = entityManager.createNamedQuery(
            "CcmApplication.retrieveApplicationForPath", CcmApplication.class);
        query.setParameter("path", path);
        final List<CcmApplication> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalArgumentException("Ambiguous path.");
        } else {
            return result.get(0);
        }
    }

    /**
     * Gets the servlet path of an application. Note: It is not required that
     * the path is mapped directly to a servlet. The path can also point to a
     * JAX-RS resource or other classes which process requests.
     *
     * @param application The application instance.
     *
     * @return The servlet path of the application.
     */
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

    /**
     * The the title of an application type.
     *
     * @param applicationType The application type.
     *
     * @return The title of the application type.
     */
    public String getApplicationTypeTitle(
        final ApplicationType applicationType) {

        final String descBundle;
        if (Strings.isBlank(applicationType.descBundle())) {
            descBundle = String.join("", applicationType.name(), "Description");
        } else {
            descBundle = applicationType.descBundle();
        }

        final ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle(
                descBundle, globalizationHelper.getNegotiatedLocale());
            return bundle.getString(applicationType.titleKey());
        } catch (MissingResourceException ex) {
            LOGGER.warn("Failed to find resource bundle '{}'.", ex);

            return applicationType.name();
        }
    }

    /**
     * The the description of an application type.
     *
     * @param applicationType The application type.
     *
     * @return The description of the application type.
     */
    public String getApplicationTypeDescription(
        final ApplicationType applicationType) {

        final String descBundle;
        if (Strings.isBlank(applicationType.descBundle())) {
            descBundle = String.join("", applicationType.name(), "Description");
        } else {
            descBundle = applicationType.descBundle();
        }

        final ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle(
                descBundle, globalizationHelper.getNegotiatedLocale());
            return bundle.getString(applicationType.descKey());
        } catch (MissingResourceException ex) {
            LOGGER.warn("Failed to find resource bundle '{}'.", ex);

            return "";
        }
    }

}

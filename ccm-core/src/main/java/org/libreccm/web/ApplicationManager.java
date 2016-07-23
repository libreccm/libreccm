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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ApplicationManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ApplicationManager.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private Map<String, ApplicationType> applicationTypes = new HashMap<>();

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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteInstance(final CcmApplication application) {
        entityManager.remove(application);
    }

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

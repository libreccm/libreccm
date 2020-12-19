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

import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.ApplicationManager;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Controller for the UI for managing application instances.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/applications")
public class ApplicationsController {

    @Inject
    private ApplicationManager appManager;

    @Inject
    private ApplicationRepository appRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    /**
     * Retrives the avaiable application types, creates
     * {@link ApplicationTypeInfoItem}s for them and makes them available using
     * {@link #models}.
     *
     * @return The template to render.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getApplicationTypes() {
        final List<ApplicationTypeInfoItem> appTypes = appManager
            .getApplicationTypes()
            .entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .map(this::buildTypeInfoItem)
            .sorted()
            .collect(Collectors.toList());

        models.put("applicationTypes", appTypes);

        return "org/libreccm/ui/admin/applications/applicationtypes.xhtml";
    }

    /**
     * Helper method for building an {@link ApplicationTypeInfoItem} for an
     * {@link ApplicationType}.
     *
     * @param applicationType The application type.
     *
     * @return An {@link ApplicationTypeInfoItem} for the provided application
     *         type.
     */
    private ApplicationTypeInfoItem buildTypeInfoItem(
        final ApplicationType applicationType
    ) {
        final ApplicationTypeInfoItem item = new ApplicationTypeInfoItem();
        item.setName(applicationType.name());

        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(applicationType.descBundle());
        item.setTitle(textsUtil.getText(applicationType.titleKey()));
        item.setDescription(textsUtil.getText(applicationType.descKey()));
        item.setSingleton(applicationType.singleton());
        item.setNumberOfInstances(
            appRepository.findByType(applicationType.name()).size()
        );

        final Class<? extends ApplicationController> controllerClass
            = applicationType.applicationController();

        if (!DefaultApplicationController.class.isAssignableFrom(
            controllerClass
        )) {
            item.setControllerLink(
                String.format(
                    "%s#getApplication",
                    controllerClass.getSimpleName())
            );
        }

        return item;
    }

}

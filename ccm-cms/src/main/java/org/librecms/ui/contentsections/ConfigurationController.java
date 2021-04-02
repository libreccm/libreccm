/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller for the index page of the configuration of a
 * {@link ContentSection}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration")
public class ConfigurationController {

    /**
     * Model for the current content section.
     */
    @Inject
    private ContentSectionModel sectionModel;

    /**
     * Common functions for controllers working with content sections.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Checks permissions.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Shows the configuration index page for the current content section.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     *
     * @return The template for the index page.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showConfigurationIndexPage(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!hasRequiredPermission(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }
        return "org/librecms/ui/contentsection/configuration/index.xhtml";
    }

    /**
     * Checks if the current user is permitted to access the configurations page
     * of the content section.
     *
     * @param section The content section.
     *
     * @return {@code true} if the current user is permitted, {@code false}
     *         otherwise.
     */
    private boolean hasRequiredPermission(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
        )
                   || permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_LIFECYLES, section
            )
                   || permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_ROLES, section
            )
                   || permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_WORKFLOWS, section
            );
    }

}

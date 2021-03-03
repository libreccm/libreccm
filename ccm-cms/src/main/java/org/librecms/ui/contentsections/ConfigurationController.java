/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration")
public class ConfigurationController {

    @Inject
    private ContentSectionModel sectionModel;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private PermissionChecker permissionChecker;

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

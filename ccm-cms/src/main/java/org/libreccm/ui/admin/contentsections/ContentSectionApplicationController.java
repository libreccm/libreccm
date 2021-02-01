/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.ui.admin.contentsections;

import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.ui.admin.applications.ApplicationController;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/applications/contentsections")
public class ContentSectionApplicationController
    implements ApplicationController {

    @Inject
    private Models models;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private ContentSectionRepository sectionRepository;

    @Override
    public String getControllerLink() {
        return "applications/contentsections";
    }
    
    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String getApplication() {
        final List<ContentSection> contentSections = sectionRepository.findAll();

        models.put(
            "sections",
            sectionRepository
                .findAll()
                .stream()
                .map(this::buildContentSectionTableRow)
                .sorted()
                .collect(Collectors.toList())
        );

        return "org/libreccm/ui/admin/applications/contentsections/contentsections.xhtml";
    }

    @POST
    @Path("/add")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String addContentSection(
        @FormParam("label") final String label
    ) {
        sectionManager.createContentSection(label);

        return "redirect:applications/contentsections";
    }

    @POST
    @Path("/{sectionId}/update")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateContentSection(
        @PathParam("sectionId") final long sectionId,
        @FormParam("label") final String label
    ) {
        final Optional<ContentSection> result = sectionRepository
            .findById(sectionId);

        if (result.isPresent()) {
            sectionManager.renameContentSection(result.get(), label);
        }

        return "redirect:applications/contentsections";
    }

    @POST
    @Path("/{sectionId}/delete")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteContentSection(
        @PathParam("sectionId") final long sectionId,
        @FormParam("confirmed") final String confirmed
    ) {
        final Optional<ContentSection> result = sectionRepository
            .findById(sectionId);

        if (result.isPresent() && "true".equals(confirmed)) {
            sectionRepository.delete(result.get());
        }

        return "redirect:applications/contentsections";
    }

    private ContentSectionTableRow buildContentSectionTableRow(
        final ContentSection section
    ) {
        final ContentSectionTableRow row = new ContentSectionTableRow();
        row.setSectionId(section.getObjectId());
        row.setUuid(section.getUuid());
        row.setLabel(section.getLabel());

        return row;
    }

}

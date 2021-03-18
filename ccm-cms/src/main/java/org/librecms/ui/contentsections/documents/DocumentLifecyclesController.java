/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.lifecycle.Phase;
import org.librecms.lifecycle.PhaseRepository;
import org.librecms.ui.contentsections.ContentSectionsUi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents/{documentPath:(.+)?}/@lifecycle/")
@Controller
public class DocumentLifecyclesController {
    
    @Inject
    private ContentItemRepository itemRepo;
    
    @Inject
    private ContentSectionsUi sectionsUi;
    
    @Inject
    private DocumentUi documentUi;
    
    @Inject
    private Models models;
    
    @Inject
    private PermissionChecker permissionChecker;
    
    @Inject
    private PhaseRepository phaseRepository;
    
    @POST
    @Path("/phases/{phaseId}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updatePhaseDates(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("phaseId") final long phaseId,
        @FormParam("startDate") final String startDateParam,
        @FormParam("endDate") final String endDateParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        if (item.getLifecycle() != null) {
            final Optional<Phase> phaseResult = item
                .getLifecycle()
                .getPhases()
                .stream()
                .filter(phase -> phase.getPhaseId() == phaseId)
                .findAny();

            if (!phaseResult.isPresent()) {
                models.put("section", section.getLabel());
                models.put("phaseId", phaseId);
                return "org/librecms/ui/contentsection/phase-not-found.xhtml";
            }

            final Phase phase = phaseResult.get();
            final DateTimeFormatter dateTimeFormatter
                = DateTimeFormatter.ISO_DATE_TIME
                    .withZone(ZoneId.systemDefault());
            final LocalDateTime startLocalDateTime = LocalDateTime
                .parse(startDateParam, dateTimeFormatter);
            phase.setStartDateTime(
                Date.from(
                    startLocalDateTime.toInstant(
                        ZoneOffset.from(startLocalDateTime)
                    )
                )
            );
            final LocalDateTime endLocalDateTime = LocalDateTime
                .parse(endDateParam, dateTimeFormatter);
            phase.setEndDateTime(
                Date.from(
                    endLocalDateTime.toInstant(
                        ZoneOffset.from(endLocalDateTime)
                    )
                )
            );

            phaseRepository.save(phase);
        }

        return String.format(
            "redirect:/%s/documents/%s/@publish",
            sectionIdentifier,
            documentPath
        );
    }
}

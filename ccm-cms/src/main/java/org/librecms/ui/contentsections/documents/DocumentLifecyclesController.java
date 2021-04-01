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
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller for managing the lifecycles of a document.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents/{documentPath:(.+)?}/@lifecycle/")
@Controller
public class DocumentLifecyclesController {

    /**
     * {@link ContentItemRepository} instance for retrieving content items.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * Several common functions for {@link ContentSection}s.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Several common functions for documents/{@link ContentItem}s.
     */
    @Inject
    private DocumentUi documentUi;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Used to check permissions.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Used to retrive {
     *
     * @Phase}s of a {@link Lifecycle}.
     */
    @Inject
    private PhaseRepository phaseRepository;

    /**
     * Update the dates of a {@link Phase}.
     *
     * @param sectionIdentifier  The identifier of the current content section.
     * @param documentPath       The path of the document.
     * @param phaseId            The ID of the phase.
     * @param startDateTimeParam The start date of the phase of ISO formatted
     *                           date/time.
     * @param endDateTimeParam   The end date of the phase of ISO formatted
     *                           date/time.
     *
     * @return
     */
    @POST
    @Path("/phases/{phaseId}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updatePhaseDates(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("phaseId") final long phaseId,
        @FormParam("startDate") final String startDateTimeParam,
        @FormParam("endDate") final String endDateTimeParam
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
                return "org/librecms/ui/contentsection/documents/phase-not-found.xhtml";
            }

            final Phase phase = phaseResult.get();
            final DateTimeFormatter dateTimeFormatter
                = DateTimeFormatter.ISO_DATE_TIME
                    .withZone(ZoneId.systemDefault());
            final LocalDateTime startLocalDateTime = LocalDateTime
                .parse(startDateTimeParam, dateTimeFormatter);
            phase.setStartDateTime(
                Date.from(
                    startLocalDateTime.toInstant(
                        ZoneOffset.from(startLocalDateTime)
                    )
                )
            );
            final LocalDateTime endLocalDateTime = LocalDateTime
                .parse(endDateTimeParam, dateTimeFormatter);
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

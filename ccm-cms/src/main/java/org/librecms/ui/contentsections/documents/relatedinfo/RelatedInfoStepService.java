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
package org.librecms.ui.contentsections.documents.relatedinfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.contentsection.AttachmentListRepository;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ItemAttachmentManager;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "relatedinfo-service")
public class RelatedInfoStepService {
    
    private static final Logger LOGGER = LogManager.getLogger(
        RelatedInfoStepService.class
    );
    
    @Inject
    private AttachmentListRepository attachmentListRepo;
    
    @Inject
    private ItemAttachmentManager attachmentManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentSectionsUi sectionsUi;

    @POST
    @Path("/save-order")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public Response saveOrder(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        final RelatedInfoStepAttachmentOrder order
    ) {
        LOGGER.info("order = {}", order);
        
        return Response.ok().build();
        
//        final ContentSection contentSection = sectionsUi
//            .findContentSection(sectionIdentifier)
//            .orElseThrow(
//                () -> new NotFoundException(
//                    String.format(
//                        "No content identifed by %s found.",
//                        sectionIdentifier
//                    )
//                )
//            );
//
//        final ContentItem document = itemRepo
//            .findByPath(contentSection, documentPath)
//            .orElseThrow(
//                () -> new NotFoundException(
//                    String.format(
//                        "No document for path %s in section %s.",
//                        documentPath,
//                        contentSection.getLabel()
//                    )
//                )
//            );
//        
//        //final Map<String, Integer> attachmentListIndexes = 
    }

}

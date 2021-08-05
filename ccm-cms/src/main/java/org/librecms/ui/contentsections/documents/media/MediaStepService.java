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
package org.librecms.ui.contentsections.documents.media;

import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
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
@Path(MvcAuthoringSteps.PATH_PREFIX + "media-service")
public class MediaStepService {

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
        final MediaStepMediaOrder order
    ) {
        final ContentSection contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new NotFoundException(
                    String.format(
                        "No content identifed by %s found.",
                        sectionIdentifier
                    )
                )
            );

        final ContentItem document = itemRepo
            .findByPath(contentSection, documentPath)
            .orElseThrow(
                () -> new NotFoundException(
                    String.format(
                        "No document for path %s in section %s.",
                        documentPath,
                        contentSection.getLabel()
                    )
                )
            );

        final List<AttachmentList> attachmentLists = document.getAttachments();
        final List<String> attachmentListsOrder = order
            .getMediaListsOrder();

        if (attachmentListsOrder.size() != attachmentLists.size()) {
            throw new BadRequestException(
                String.format(
                    "Size of lists of attachment lists does not match list of "
                        + "attachment order list. attachmentLists.size = %d, "
                        + "attachmentListsOrder.size = %d.",
                    attachmentLists.size(),
                    attachmentListsOrder.size()
                )
            );
        }

        for (int i = 0; i < attachmentListsOrder.size(); i++) {
            final String listUuid = attachmentListsOrder.get(i);
            final AttachmentList attachmentList = attachmentLists
                .stream()
                .filter(list -> listUuid.equals(list.getUuid()))
                .findAny()
                .orElseThrow(
                    () -> new BadRequestException(
                        String.format(
                            "attachmentListsOrder has an entry for attachment "
                                + "list %s, but there no attachment list with "
                                + "that UUID.",
                            listUuid
                        )
                    )
                );

            attachmentList.setListOrder(i);
            attachmentListRepo.save(attachmentList);
        }

        for (final Map.Entry<String, List<String>> attachmentsOrder : order
            .getMediaOrder().entrySet()) {
            final AttachmentList attachmentList = document
                .getAttachments()
                .stream()
                .filter(list -> attachmentsOrder.getKey().equals(list.getUuid()))
                .findAny()
                .orElseThrow(
                    () -> new BadRequestException(
                        String.format(
                            "attachmentsOrder contains an entry for "
                                + "attachment list %s, but there no attachment "
                                + "list with that UUID.",
                            attachmentsOrder.getKey()
                        )
                    )
                );

            final List<ItemAttachment<?>> attachments = attachmentList
                .getAttachments();
            if (attachments.size() != attachmentsOrder.getValue().size()) {
                throw new BadRequestException(
                    String.format(
                        "Size of attachmentsOrder list does not match the size"
                            + "of the attachments list. "
                            + "attachmentsOrder.size = %d, "
                            + "attachmentsList.size = %d",
                        attachmentsOrder.getValue().size(),
                        attachments.size()
                    )
                );
            }

            for (int i = 0; i < attachmentsOrder.getValue().size(); i++) {
                final String attachmentUuid = attachmentsOrder.getValue().get(i);
                final ItemAttachment<?> attachment = attachments
                    .stream()
                    .filter(current -> attachmentUuid.equals(current.getUuid()))
                    .findAny()
                    .orElseThrow(
                        () -> new BadRequestException(
                            String.format(
                                "attachmentOrder order for attachment list %s "
                                    + "has an entry for attachment %s but "
                                    + "there is attachment with that UUID in "
                                    + "the list.",
                                attachmentList.getUuid(),
                                attachmentUuid
                            )
                        )
                    );
                attachment.setSortKey(i);
                attachmentManager.save(attachment);
            }
        }

        return Response.ok().build();
    }

}

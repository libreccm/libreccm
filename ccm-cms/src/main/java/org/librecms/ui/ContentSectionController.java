/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeManager;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}")
public class ContentSectionController {

    @Inject
    private CmsAdminMessages cmsAdminMessages;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemL10NManager itemL10NManager;

    @Inject
    private ContentSectionModel contentSectionModel;

    @Inject
    private ContentTypeManager contentTypeManager;

    @Inject
    private ContentTypeRepository contentTypeRepo;

    @Inject
    private FolderBrowserModel folderBrowserModel;

    @Inject
    private FolderManager folderManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private IdentifierParser identifierParser;

    @GET
    @Path("/folderbrowser")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listItems(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            sectionIdentifier
        );
        final ContentSection section;
        switch (identifier.getType()) {
            case ID:
                section = sectionRepo
                    .findById(Long.parseLong(identifier.getIdentifier()))
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection with ID %s found.",
                                identifier.getIdentifier()
                            )
                        )
                    );
                break;
            case UUID:
                section = sectionRepo
                    .findByUuid(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection with UUID %s found.",
                                identifier.getIdentifier()
                            )
                        )
                    );
                break;
            default:
                section = sectionRepo
                    .findByLabel(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection named %s found.",
                                identifier.getIdentifier()
                            )
                        )
                    );
                break;
        }

        contentSectionModel.setSection(section);

        final List<CcmObject> objects = folderRepo
            .findObjectsInFolder(
                section.getRootDocumentsFolder(), firstResult, maxResults
            );
        folderBrowserModel.setCount(
            folderRepo.countObjectsInFolder(section.getRootDocumentsFolder())
        );
        folderBrowserModel.setFirstResult(firstResult);
        folderBrowserModel.setMaxResults(maxResults);

        folderBrowserModel.setRows(
            objects
                .stream()
                .map(object -> buildRowModel(section, object))
                .collect(Collectors.toList())
        );

        return "org/librecms/ui/content-section/folderbrowser.xhtml";

    }

    private FolderBrowserRowModel buildRowModel(
        final ContentSection section, final CcmObject object
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(object);
        if (object instanceof ContentItem) {
            return buildRowModel(section, (ContentItem) object);
        } else if (object instanceof Folder) {
            return buildRowModel(section, (Folder) object);
        } else {
            final FolderBrowserRowModel row = new FolderBrowserRowModel();

            row.setCreated("");
            row.setDeletable(false);
            row.setIsFolder(false);
            row.setLanguages(Collections.emptySortedSet());
            row.setLastEditPublished(false);
            row.setLastEdited("");
            row.setName(object.getDisplayName());
            row.setTitle("");
            row.setType(object.getClass().getSimpleName());

            return row;
        }
    }

    private FolderBrowserRowModel buildRowModel(
        final ContentSection section, final ContentItem contentItem
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(contentItem);

        final FolderBrowserRowModel row = new FolderBrowserRowModel();
        row.setCreated(
            DateTimeFormatter.ISO_DATE.format(
                LocalDate.ofInstant(
                    contentItem.getCreationDate().toInstant(),
                    ZoneId.systemDefault()
                )
            )
        );
        row.setDeletable(!itemManager.isLive(contentItem));
        row.setIsFolder(false);
        row.setLanguages(
            new TreeSet<>(
                itemL10NManager
                    .availableLanguages(contentItem)
                    .stream()
                    .map(Locale::toString)
                    .collect(Collectors.toSet())
            )
        );
        if (itemManager.isLive(contentItem)) {
            final LocalDate draftLastModified = LocalDate.ofInstant(
                contentItem.getLastModified().toInstant(),
                ZoneId.systemDefault()
            );
            final LocalDate liveLastModified = LocalDate.ofInstant(
                itemManager
                    .getLiveVersion(contentItem, contentItem.getClass())
                    .map(ContentItem::getLastModified)
                    .map(Date::toInstant)
                    .get(),
                ZoneId.systemDefault()
            );
            row.setLastEditPublished(
                liveLastModified.isBefore(draftLastModified)
            );

        } else {
            row.setLastEditPublished(false);
        }

        row.setLastEdited(
            DateTimeFormatter.ISO_DATE.format(
                LocalDate.ofInstant(
                    contentItem.getLastModified().toInstant(),
                    ZoneId.systemDefault()
                )
            )
        );
        row.setName(contentItem.getDisplayName());
        row.setNoneCmsObject(false);
        row.setTitle(
            globalizationHelper.getValueFromLocalizedString(
                contentItem.getTitle()
            )
        );
        row.setType(
            contentTypeRepo
                .findByContentSectionAndClass(section, contentItem.getClass())
                .map(ContentType::getLabel)
                .map(
                    label -> globalizationHelper.getValueFromLocalizedString(
                        label
                    )
                ).orElse("?")
        );

        return row;
    }

    private FolderBrowserRowModel buildRowModel(
        final ContentSection section, final Folder folder
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(folder);

        final FolderBrowserRowModel row = new FolderBrowserRowModel();
        row.setCreated("");
        row.setDeletable(
            folderManager.folderIsDeletable(folder)
            == FolderManager.FolderIsDeletable.YES
        );
        row.setIsFolder(true);
        row.setLanguages(Collections.emptySortedSet());
        row.setLastEditPublished(false);
        row.setLastEdited("");
        row.setName(folder.getDisplayName());
        row.setNoneCmsObject(false);
        row.setTitle(
            globalizationHelper.getValueFromLocalizedString(folder.getTitle())
        );
        row.setType(
            globalizationHelper.getLocalizedTextsUtil(
                "org.libreccms.CmsAdminMessages"
            ).getText("contentsection.folderbrowser.types.folder")
        );
        
        return row;
    }

}

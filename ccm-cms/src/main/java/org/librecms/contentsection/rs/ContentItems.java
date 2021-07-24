/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.contentsection.rs;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Provides a Web Service (build using JAX-RS). Used for example by the
 * {@link ItemSearchWidget}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{content-section}/items/")
public class ContentItems {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private FolderManager folderManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentTypesManager itemTypesManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private Class<? extends ContentItem> toContentItemTypeClass(
        final String type) {

        final Class<?> clazz;
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                "Type '%s' is not a valid class.",
                type));
        }

        if (ContentItem.class.isAssignableFrom(clazz)) {
            @SuppressWarnings("unchecked")
            final Class<? extends ContentItem> typeClass
                = (Class<? extends ContentItem>) clazz;
            return typeClass;
        } else {
            throw new IllegalArgumentException(String.format(
                "Type '%s is not a subclass of '%s'.",
                type,
                ContentItem.class.getName()));
        }
    }

    private Map<String, String> createItemMapEntry(final Folder folder) {

        final Map<String, String> result = new HashMap<>();

        result.put("title",
                   folder
                       .getTitle().getValue(globalizationHelper
                           .getNegotiatedLocale()));

        result.put("type",
                   Folder.class.getName());
        result.put("place", "");

        return result;
    }

    private Map<String, String> createItemMapEntry(final ContentItem item) {

        final Map<String, String> result = new HashMap<>();

        result.put("itemId",
                   Long.toString(item.getObjectId()));

        result.put("name", item.getDisplayName());

        result.put("title",
                   globalizationHelper
                       .getValueFromLocalizedString(item.getTitle()));

        result.put("type",
                   item.getClass().getName());

        final ContentTypeInfo typeInfo = itemTypesManager
            .getContentTypeInfo(item.getClass());
        final ResourceBundle bundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle(),
                       globalizationHelper.getNegotiatedLocale());
        result.put("typeLabel", bundle.getString(typeInfo.getLabelKey()));

        final Optional<Folder> itemFolder = itemManager.getItemFolder(item);
        if (itemFolder.isPresent()) {
            result.put("place",
                       folderManager.getFolderPath(itemFolder.get()));
        } else {
            result.put("place", "");
        }

        return result;
    }

    @GET
    @Path("/")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findItems(
        @PathParam("content-section") final String section,
        @QueryParam("query") final String query,
        @QueryParam("type") final String type,
        @QueryParam("version") final String version) {

        final ContentSection contentSection = sectionRepo
            .findByLabel(section)
            .orElseThrow(() -> new NotFoundException(
            String.format("No content section '%s' found.", section)));

        final ContentItemVersion itemVersion;
        if (version != null) {
            itemVersion = ContentItemVersion.valueOf(version.toUpperCase());
        } else {
            itemVersion = ContentItemVersion.LIVE;
        }

        final List<ContentItem> items;
        if ((query == null || query.trim().isEmpty())
                && (type == null || type.trim().isEmpty())) {
            items = itemRepo
                .findByContentSection(contentSection, itemVersion);

        } else if ((query != null && !query.trim().isEmpty())
                       && (type == null || type.trim().isEmpty())) {

            items = itemRepo.findByNameAndContentSection(
                query,
                contentSection,
                itemVersion
            );
        } else if ((query == null || query.trim().isEmpty())
                       && (type != null && !type.trim().isEmpty())) {

            final Class<? extends ContentItem> itemType
                = toContentItemTypeClass(type);
            items = itemRepo.findByTypeAndContentSection(
                itemType,
                contentSection,
                itemVersion
            );
        } else {
            final Class<? extends ContentItem> itemType
                = toContentItemTypeClass(type);
            items = itemRepo.findByNameAndTypeAndContentSection(
                query,
                itemType,
                contentSection,
                itemVersion
            );
        }

        return items
            .stream()
            .map(item -> createItemMapEntry(item))
            .collect(Collectors.toList());
    }

    @GET
    @Path("/folders")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findItemsInRootFolder(
        @PathParam("content-section") final String section,
        @QueryParam("query") final String query,
        @QueryParam("type") final String type) {

        final ContentSection contentSection = sectionRepo
            .findByLabel(section)
            .orElseThrow(() -> new NotFoundException(String
            .format("No content section with '%s' found.", section)));

        final Folder folder = contentSection.getRootDocumentsFolder();

        return findItemsInFolder(folder, query, type);
    }

    @GET
    @Path("/folders/{folder}/")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findItemsInFolder(
        @PathParam("content-section") final String section,
        @PathParam("folder") final String folderPath,
        @QueryParam("query") final String query,
        @QueryParam("type") final String type) {

        final ContentSection contentSection = sectionRepo
            .findByLabel(section)
            .orElseThrow(() -> new NotFoundException(
            String.format("No content section '%s' found.", section)));

        final Folder folder = folderRepo.findByPath(contentSection,
                                                    folderPath,
                                                    FolderType.DOCUMENTS_FOLDER)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No documents folder with path '%s' in content section '%s'",
            folderPath,
            section)));

        return findItemsInFolder(folder, query, type);
    }

    private List<Map<String, String>> findItemsInFolder(final Folder folder,
                                                        final String query,
                                                        final String type) {

        final List<Map<String, String>> subFolderEntries = folder
            .getSubFolders()
            .stream()
            .map(subFolder -> createItemMapEntry(subFolder))
            .collect(Collectors.toList());

        final List<ContentItem> items;
        if ((query == null || query.trim().isEmpty())
                && (type == null || type.trim().isEmpty())) {
            items = itemRepo.findByFolder(folder);
        } else if ((query != null && !query.trim().isEmpty())
                       && (type == null || type.trim().isEmpty())) {
            items = itemRepo.filterByFolderAndName(folder, query);
        } else if ((query == null || query.trim().isEmpty())
                       && (type != null && type.trim().isEmpty())) {
            final Class<? extends ContentItem> itemType
                = toContentItemTypeClass(type);
            items = itemRepo.filterByFolderAndType(folder, itemType);
        } else {
            final Class<? extends ContentItem> itemType
                = toContentItemTypeClass(type);
            items = itemRepo.filterByFolderAndTypeAndName(folder,
                                                          itemType,
                                                          query);
        }

        final List<Map<String, String>> itemEntries = items
            .stream()
            .map(item -> createItemMapEntry(item))
            .collect(Collectors.toList());

        final List<Map<String, String>> result = new ArrayList<>();
        result.addAll(subFolderEntries);
        result.addAll(itemEntries);

        return result;

    }

}

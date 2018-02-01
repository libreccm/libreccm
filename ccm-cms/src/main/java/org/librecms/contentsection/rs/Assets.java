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

import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;

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
 * {@link AssetSearchWidget}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{content-section}/assets/")
public class Assets {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private FolderManager folderManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetManager assetManager;

    @Inject
    private AssetTypesManager assetTypesManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private Class<? extends Asset> toAssetTypeClass(final String type) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                "Type '%s' is not a valid class.",
                type));
        }

        if (Asset.class.isAssignableFrom(clazz)) {
            @SuppressWarnings("unchecked")
            final Class<? extends Asset> typeClass
                                             = (Class<? extends Asset>) clazz;
            return typeClass;
        } else {
            throw new IllegalArgumentException(String.format(
                "Type '%s is not a subclass of '%s'.",
                type,
                Asset.class.getName()));
        }
    }

    private Map<String, String> createAssetMapEntry(final Folder folder) {
        final Map<String, String> result = new HashMap<>();

        result.put("title",
                   folder
                       .getTitle()
                       .getValue(KernelConfig.getConfig().getDefaultLocale()));
        result.put("type",
                   Folder.class.getName());
        result.put("place", "");

        return result;
    }

    private Map<String, String> createAssetMapEntry(final Asset asset) {
        final Map<String, String> result = new HashMap<>();

        result.put("assetId",
                   Long.toString(asset.getObjectId()));

        result.put("uuid", asset.getUuid());

        result.put("name", asset.getDisplayName());

        result.put("title",
                   globalizationHelper
                       .getValueFromLocalizedString(asset.getTitle()));

        result.put("type",
                   asset.getClass().getName());

        final AssetTypeInfo typeInfo = assetTypesManager
            .getAssetTypeInfo(asset.getClass());
        final ResourceBundle bundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle(),
                       globalizationHelper.getNegotiatedLocale());
        result.put("typeLabel", bundle.getString(typeInfo.getLabelKey()));

        final Optional<Folder> assetFolder = assetManager.getAssetFolder(asset);
        if (assetFolder.isPresent()) {
            result.put("place",
                       folderManager.getFolderPath(assetFolder.get()));
        } else {
            result.put("place", "");
        }

        return result;
    }

    @GET
    @Path("/")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findAssets(
        @PathParam("content-section") final String section,
        @QueryParam("query") final String query,
        @QueryParam("type") final String type) {

        final ContentSection contentSection = sectionRepo
            .findByLabel(section)
            .orElseThrow(() -> new NotFoundException(
            String.format("No content section '%s' found.", section)));

        final List<Asset> assets;
        if ((query == null || query.trim().isEmpty())
                && (type == null || type.trim().isEmpty())) {
            assets = assetRepo.findByContentSection(contentSection);
        } else if ((query != null && !query.trim().isEmpty())
                       && (type == null || type.trim().isEmpty())) {
            assets = assetRepo.findByTitleAndContentSection(query,
                                                            contentSection);
        } else if ((query == null || query.trim().isEmpty())
                       && (type != null && !type.trim().isEmpty())) {
            final Class<? extends Asset> assetType = toAssetTypeClass(type);
            assets = assetRepo.findByTypeAndContentSection(assetType,
                                                           contentSection);
        } else {
            final Class<? extends Asset> assetType = toAssetTypeClass(type);
            assets = assetRepo.findByTitleAndTypeAndContentSection(
                query,
                assetType,
                contentSection);
        }

        return assets
            .stream()
            .map(asset -> createAssetMapEntry(asset))
            .collect(Collectors.toList());
    }

    @GET
    @Path("/folders/")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findAssetsInRootFolder(
        @PathParam("content-section") final String section,
        @QueryParam("query") final String query,
        @QueryParam("type") final String type) {

        final ContentSection contentSection = sectionRepo
            .findByLabel(section)
            .orElseThrow(() -> new NotFoundException(
            String.format("No content section '%s' found.", section)));

        final Folder folder = contentSection.getRootAssetsFolder();

        return findAssetsInFolder(folder, query, type);
    }

    @GET
    @Path("/folders/{folder}/")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findAssetsInFolder(
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
                                                    FolderType.ASSETS_FOLDER)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No assets folder with path '%s' in content section '%s'",
            folderPath,
            section)));

        return findAssetsInFolder(folder, query, type);
    }

    private List<Map<String, String>> findAssetsInFolder(final Folder folder,
                                                         final String query,
                                                         final String type) {

        final List<Map<String, String>> subFolderEntries = folder
            .getSubFolders()
            .stream()
            .map(subFolder -> createAssetMapEntry(subFolder))
            .collect(Collectors.toList());

        final List<Asset> assets;
        if ((query == null || query.trim().isEmpty())
                && ((type == null) || type.trim().isEmpty())) {
            assets = assetRepo.findByFolder(folder);
        } else if ((query != null && !query.trim().isEmpty())
                       && (type == null || type.trim().isEmpty())) {

            assets = assetRepo.filterByFolderAndTitle(folder, query);
        } else if ((query == null || query.trim().isEmpty())
                       && (type != null && !type.trim().isEmpty())) {
            final Class<? extends Asset> assetType = toAssetTypeClass(type);
            assets = assetRepo.filterByFolderAndType(folder, assetType);
        } else {
            final Class<? extends Asset> assetType = toAssetTypeClass(type);
            assets = assetRepo.filterByFolderAndTypeAndTitle(folder,
                                                             assetType,
                                                             query);
        }

        final List<Map<String, String>> assetEntries = assets
            .stream()
            .map(asset -> createAssetMapEntry(asset))
            .collect(Collectors.toList());

        final List<Map<String, String>> result = new ArrayList<>();
        result.addAll(subFolderEntries);
        result.addAll(assetEntries);

        return result;
    }

}

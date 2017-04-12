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
package org.librecms.assets;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;

import java.util.ArrayList;
import java.util.Collections;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{content-section}/assets")
public class AssetSearchService {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetTypesManager assetTypesManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private Class<? extends Asset> toAssetClass(final String type) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                "Type '%s' is not a valid class.",
                type));
        }

        if (clazz.isAssignableFrom(Asset.class)) {
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

    public List<Asset> findAssetsByQuery(final String query) {
        return assetRepo.findByTitle(query);
    }

    public List<Asset> findAssetsByType(final String type) {
        return AssetSearchService.this.findAssets(toAssetClass(type));
    }

    public List<Asset> findAssets(final Class<? extends Asset> type) {
        return assetRepo.findByType(type);
    }

    public List<Asset> findAssets(final String query, final String type) {

        final Class<?> clazz;
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                "Type '%s' is not a valid class.",
                type));
        }

        if (clazz.isAssignableFrom(Asset.class)) {
            @SuppressWarnings("unchecked")
            final Class<? extends Asset> typeClass
                                             = (Class<? extends Asset>) clazz;
            return AssetSearchService.this.findAssets(query, typeClass);
        } else {
            throw new IllegalArgumentException(String.format(
                "Type '%s is not a subclass of '%s'.",
                type,
                Asset.class.getName()));
        }

    }

    public List<Asset> findAssets(final ContentSection section,
                                  final String path) {
        final Optional<Folder> folder = folderRepo
            .findByPath(section,
                        path,
                        FolderType.ASSETS_FOLDER);

        if (!folder.isPresent()) {
            return Collections.emptyList();
        }
        
        return assetRepo.findByFolder(folder.get());
    }
    
    public List<Asset> findAssets(final String query,
                                  final Class<? extends Asset> type) {
        return assetRepo.findByTitleAndType(query, type);

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findAssetsByType(final ContentSection section,
                                  final String path,
                                  final String type) {

        final Optional<Folder> folder = folderRepo
            .findByPath(section,
                        path,
                        FolderType.ASSETS_FOLDER);

        if (!folder.isPresent()) {
            return Collections.emptyList();
        }

        return assetRepo.filterByFolderAndType(folder.get(),
                                               toAssetClass(type));
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findAssetsByQuery(final ContentSection section,
                                  final String path,
                                  final String query) {

        final Optional<Folder> folder = folderRepo
            .findByPath(section,
                        path,
                        FolderType.ASSETS_FOLDER);

        if (!folder.isPresent()) {
            return Collections.emptyList();
        }

        return assetRepo.filterByFolderAndTitle(folder.get(),
                                                query);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Asset> findAssets(final ContentSection section,
                                  final String path,
                                  final String query,
                                  final String type) {

        final Optional<Folder> folder = folderRepo
            .findByPath(section,
                        path,
                        FolderType.ASSETS_FOLDER);

        if (!folder.isPresent()) {
            return Collections.emptyList();
        }

        return assetRepo.filterByFolderAndTypeAndTitle(folder.get(),
                                                       toAssetClass(type),
                                                       query);

    }

    @GET
    @Produces("text/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findAssets(
        @PathParam("content-section") final String section,
        @QueryParam("query") final String query,
        @QueryParam("type") final String type) {

        return findAssets(section, "/", query, type);
    }

    @GET
    @Path("{folder}")
    @Produces("text/json")
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Map<String, String>> findAssets(
        @PathParam("content-section") final String section,
        @PathParam("folder") final String path,
        @QueryParam("query") final String query,
        @QueryParam("type") final String type) {

        final ContentSection contentSection = sectionRepo.findByLabel(section);

        final String folderPath;
        if (path == null || path.trim().isEmpty() || "/".equals(path.trim())) {
            folderPath = "/";
        } else {
            folderPath = String.format("/%s", path);
        }

        final String assetType;
        if (type == null || type.trim().isEmpty()) {
            assetType = Asset.class.getName();
        } else {
            assetType = type;
        }

        final Optional<Folder> folder = folderRepo
            .findByPath(contentSection, folderPath, FolderType.ASSETS_FOLDER);
        if (!folder.isPresent()) {
            return Collections.emptyList();
        }
        final List<Map<String, String>> subFolders = folder
            .get()
            .getSubFolders()
            .stream()
            .map(current -> createAssetMapEntry(current))
            .collect(Collectors.toList());

        final List<Asset> assets;
        if (query == null || query.trim().isEmpty()) {
            if (Asset.class.getName().equals(assetType)) {
                assets = findAssets(contentSection, folderPath);
            } else {
                assets = findAssetsByType(contentSection, folderPath, assetType);
            }
        } else {
            if (Asset.class.getName().equals(assetType)) {
                assets = findAssetsByQuery(contentSection, folderPath, query);
            } else {
                assets
                = findAssets(contentSection, folderPath, query, assetType);
            }
        }

        final List<Map<String, String>> assetEntries = assets
            .stream()
            .map(asset -> createAssetMapEntry(asset))
            .collect(Collectors.toList());

        final List<Map<String, String>> result = new ArrayList<>();
        result.addAll(subFolders);
        result.addAll(assetEntries);

        return result;
    }

    private Map<String, String> createAssetMapEntry(final Folder folder) {
        final Map<String, String> result = new HashMap<>();

        result.put("title",
                   folder
                       .getTitle()
                       .getValue(KernelConfig.getConfig().getDefaultLocale()));
        result.put("type",
                   Folder.class.getName());

        return result;
    }

    private Map<String, String> createAssetMapEntry(final Asset asset) {
        final Map<String, String> result = new HashMap<>();

        result.put("title",
                   asset.getTitle().getValue(globalizationHelper
                       .getNegotiatedLocale()));

        result.put("type",
                   asset.getClass().getName());

        final AssetTypeInfo typeInfo = assetTypesManager
            .getAssetTypeInfo(asset.getClass());
        final ResourceBundle bundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle(),
                       globalizationHelper.getNegotiatedLocale());
        result.put("typeLabel", bundle.getString(typeInfo.getLabelKey()));

        return result;
    }

}

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

import org.libreccm.configuration.ConfigurationManager;
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

import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
    private AssetManager assetManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetTypesManager assetTypesManager;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private FolderManager folderManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    @Any
    private Instance<AssetPropertiesProvider> assetPropertiesProviders;

    private Locale defaultLocale;

    @PostConstruct
    private void init() {

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public String findAssets(
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

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        assets
            .stream()
            .map(this::assetToJson)
            .forEach(arrayBuilder::add);

        final StringWriter writer = new StringWriter();
        final JsonWriter jsonWriter = Json.createWriter(writer);
        jsonWriter.writeArray(arrayBuilder.build());

        return writer.toString();
    }

    @GET
    @Path("/folders/")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public String findAssetsInRootFolder(
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
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public String findAssetsInFolder(
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

    private String findAssetsInFolder(final Folder folder,
                                      final String query,
                                      final String type) {

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

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        folder
            .getSubFolders()
            .stream()
            .map(this::assetToJson)
            .forEach(arrayBuilder::add);

        assets
            .stream()
            .map(this::assetToJson)
            .forEach(arrayBuilder::add);

        final StringWriter writer = new StringWriter();
        final JsonWriter jsonWriter = Json.createWriter(writer);
        jsonWriter.writeArray(arrayBuilder.build());

        return writer.toString();
    }

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

    private JsonObject assetToJson(final Folder folder) {

        return Json
            .createObjectBuilder()
            .add("title",
                 folder.getTitle().getValue(defaultLocale))
            .add("type", Folder.class.getName())
            .add("place", "")
            .build();
    }

    private JsonObject assetToJson(final Asset asset) {

        final AssetTypeInfo typeInfo = assetTypesManager
            .getAssetTypeInfo(asset.getClass());
        final ResourceBundle bundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle(),
                       globalizationHelper.getNegotiatedLocale());
        final String place;
        final Optional<Folder> assetFolder = assetManager.getAssetFolder(asset);
        if (assetFolder.isPresent()) {
            place = folderManager.getFolderPath(assetFolder.get());
        } else {
            place = "";
        }

        return Json
            .createObjectBuilder()
            .add("assetId", asset.getObjectId())
            .add("uuid", asset.getUuid())
            .add("name", asset.getDisplayName())
            .add("title", globalizationHelper.getValueFromLocalizedString(
                 asset.getTitle()))
            .add("type", asset.getClass().getName())
            .add("typeLabel", bundle.getString(typeInfo.getLabelKey()))
            .add("place", place)
            .add("properties", getAssetProperties(asset))
            .build();
    }

    private JsonObject getAssetProperties(final Asset asset) {

        final ProvidesPropertiesForAssetTypeLiteral literal
                                                 = new ProvidesPropertiesForAssetTypeLiteral(
                asset.getClass());

        final Instance<AssetPropertiesProvider> instance
                                                    = assetPropertiesProviders
                .select(literal);

        final JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        if (!instance.isUnsatisfied()) {

            instance
                .forEach(provider -> provider.addProperties(asset, objBuilder));
        }

        return objBuilder.build();
    }

    private static class ProvidesPropertiesForAssetTypeLiteral
        extends AnnotationLiteral<ProvidesPropertiesForAssetType>
        implements ProvidesPropertiesForAssetType {

        private static final long serialVersionUID = 1L;

        private final Class<? extends Asset> forAssetType;

        public ProvidesPropertiesForAssetTypeLiteral(
            final Class<? extends Asset> forAssetType) {

            this.forAssetType = forAssetType;
        }

        @Override
        public Class<? extends Asset> value() {

            return forAssetType;
        }

    }

}

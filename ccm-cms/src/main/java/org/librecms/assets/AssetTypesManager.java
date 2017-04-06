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

import org.libreccm.modules.CcmModule;
import org.librecms.contentsection.Asset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

/**
 * Provides informations about the available asset types.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetTypesManager {

    private static final String DEFAULT_DESCRIPTION_KEY = "description";
    private static final String DEFAULT_LABEL_KEY = "label";

    /**
     * A list of all asset types available.
     */
    private List<AssetTypeInfo> availableAssetTypes;

    /**
     * Initialises the class (is called by CDI). This method is called by the
     * CDI container after an instance of this class has been created by the CDI
     * container. This method creates the list {@link #availableAssetTypes}.
     */
    @PostConstruct
    protected void initialize() {

        final ServiceLoader<CcmModule> modules = ServiceLoader
                .load(CcmModule.class);

        final SortedSet<Class<? extends Asset>> assetTypes = new TreeSet<>(
                (type1, type2) -> type1.getName().compareTo(type2.getName()));

        for (final CcmModule module : modules) {
            final AssetTypes annotation = module
                    .getClass()
                    .getAnnotation(AssetTypes.class);

            if (annotation == null) {
                continue;
            }

            assetTypes.addAll(Arrays.asList(annotation.value()));
        }

        availableAssetTypes = assetTypes
                .stream()
                .filter(type -> type.getAnnotation(AssetType.class) != null)
                .map(assetTypeClass -> createAssetTypeInfo(assetTypeClass))
                .collect(Collectors.toList());
    }

    /**
     * Helper method for creating the info object for a asset type.
     *
     * @param assetTypeClass The class which provides the implementation of the
     * asset type.
     *
     * @return A {@link AssetTypeInfo} object describing the asset type.
     */
    private AssetTypeInfo createAssetTypeInfo(
            final Class<? extends Asset> assetTypeClass) {

        Objects.requireNonNull(assetTypeClass);

        final AssetTypeInfo assetTypeInfo = new AssetTypeInfo();
        assetTypeInfo.setAssetClass(assetTypeClass);

        final String defaultBundleName = String.format("%sBundle",
                                                       assetTypeClass.getName());
        final AssetType assetType = assetTypeClass
                .getAnnotation(AssetType.class);

        if (assetType == null) {
            assetTypeInfo.setLabelBundle(defaultBundleName);
            assetTypeInfo.setDescriptionBundle(defaultBundleName);
            assetTypeInfo.setLabelKey(DEFAULT_LABEL_KEY);
            assetTypeInfo.setDescriptionKey(DEFAULT_DESCRIPTION_KEY);
        } else {
            if (assetType.labelBundle().isEmpty()) {
                assetTypeInfo.setLabelBundle(defaultBundleName);
            } else {
                assetTypeInfo.setLabelBundle(assetType.labelBundle());
            }

            if (assetType.labelKey().isEmpty()) {
                assetTypeInfo.setLabelKey(DEFAULT_LABEL_KEY);
            } else {
                assetTypeInfo.setLabelKey(assetType.labelKey());
            }

            if (assetType.descriptionBundle().isEmpty()) {
                assetTypeInfo.setDescriptionBundle(defaultBundleName);
            } else {
                assetTypeInfo.setDescriptionKey(assetType.descriptionBundle());
            }

            if (assetType.descriptionKey().isEmpty()) {
                assetTypeInfo.setDescriptionKey(DEFAULT_DESCRIPTION_KEY);
            } else {
                assetTypeInfo.setDescriptionKey(assetType.descriptionKey());
            }

            assetTypeInfo.setAssetForm(assetType.assetForm());
        }

        return assetTypeInfo;
    }

    /**
     * Retrieves a list of all available asset types.
     *
     * @return A list all available asset types.
     */
    public List<AssetTypeInfo> getAvailableAssetTypes() {
        return Collections.unmodifiableList(availableAssetTypes);
    }

    /**
     * Get the {@link AssetTypeInfo} for a specific type.
     *
     * @param assetTypeClass The class representing the asset type.
     *
     * @return A {@link AssetTypeInfo} object describing the asset type.
     */
    public AssetTypeInfo getAssetTypeInfo(
            final Class<? extends Asset> assetTypeClass) {

        Objects.requireNonNull(assetTypeClass);

        return createAssetTypeInfo(assetTypeClass);
    }

    /**
     * Convenient method for getting the {@link AssetTypeInfo} about a specific
     * asset type.
     *
     * @param assetTypeClass The fully qualified name of the class representing
     * the asset type.
     *
     * @return A {@link AssetTypeInfo} object describing the asset type.
     *
     * @throws IllegalArgumentException If no class with the provided name
     * exists or the class is not a subclass of {@link Asset}.
     */
    @SuppressWarnings("unchecked")
    public AssetTypeInfo getAssetTypeInfo(final String assetTypeClass) {

        Objects.requireNonNull(assetTypeClass);

        final Class<?> clazz;
        try {
            clazz = Class.forName(assetTypeClass);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                    "There is no class \"%s\".", assetTypeClass),
                                               ex);
        }

        if (!Asset.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(String.format(
                    "Class \"%s\" is not a subclass of of \"%s\".",
                    assetTypeClass,
                    Asset.class.getName()));
        }

        return getAssetTypeInfo((Class<? extends Asset>) clazz);
    }

}

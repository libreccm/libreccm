/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.assets;

import org.librecms.assets.AssetL10NManager;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.Folder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * An base class for implementations of {@link AssetFormController}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractAssetFormController<T extends Asset> implements
    AssetFormController<T> {

    protected static final String DISPLAY_NAME = "displayName";
    protected static final String TITLE = "title";

    @Inject
    private AssetManager assetManager;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private AssetL10NManager l10nManager;

    /**
     * Retrieves the basic data of the provided asset. Subclasses should not
     * overrride this method. Instead they should provided an implementation of
     * {@link #getAssetData(org.librecms.contentsection.Asset, java.util.Locale)}.
     *
     * @param assetType      The {@link Asset} from which the data is read.
     * @param selectedLocale The locale for which the data is read.
     *
     * @return A map with the data of the basic properties of the provided
     *         asset.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Map<String, Object> getAssetData(final Long assetId,
                                            final Class<T> assetType,
                                            final Locale selectedLocale) {

        Objects.requireNonNull(assetId, "Can't get data from asset null.");
        Objects.requireNonNull(selectedLocale,
                               "Can't get data from asset for locale null.");

        final T asset = loadAsset(assetId, assetType);

        final Map<String, Object> data = new HashMap<>();

        data.put(DISPLAY_NAME, asset.getDisplayName());
        data.put(TITLE, asset.getTitle().getValue(selectedLocale));

        data.putAll(getAssetData(asset, selectedLocale));

        return data;
    }

    protected abstract Map<String, Object> getAssetData(
        final T asset, final Locale selectedLocale);

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public long createAsset(final Folder infolder,
                         final Locale selectedLocale,
                         final Class<T> assetType,
                         final Map<String, Object> data) {

        if (!data.containsKey(DISPLAY_NAME)) {
            throw new IllegalArgumentException(
                "data does not contain a value for displayName.");
        }

        if (!data.containsKey(TITLE)) {
            throw new IllegalArgumentException(
                "data does not contain a value for title.");
        }

        final String name = (String) data.get(DISPLAY_NAME);
        final String title = (String) data.get(TITLE);

        final T asset = assetManager
            .createAsset(name, 
                         title,
                         selectedLocale,
                         infolder,
                         assetType);
        
        return asset.getObjectId();
    }

    /**
     * Updates the provided asset with the provided data.
     *
     * This method is not intended to be overridden, but can't be {@code final}
     * because of limitations of CDI. To update type specific properties
     * implement
     * {@link #updateAssetProperties(org.librecms.contentsection.Asset, java.util.Locale, java.util.Map)}.
     *
     * This method calls
     * {@link AssetRepository#save(org.librecms.contentsection.Asset)} after the
     * properties are set to save the changes to the database.
     *
     * @param assetId        The ID of the asset to update.
     * @param selectedLocale The locale for which the asset is updated.
     * @param data           The data used to update the asset.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void updateAsset(final Long assetId,
                            final Locale selectedLocale,
                            final Class<T> assetType,
                            final Map<String, Object> data) {

        Objects.requireNonNull(selectedLocale,
                               "Can't get update asset for locale null.");
        Objects.requireNonNull(data, "Can't update asset without data.");

        final T asset = loadAsset(assetId, assetType);
        if (data.containsKey(DISPLAY_NAME)) {
            asset.setDisplayName((String) data.get(DISPLAY_NAME));
        }

        if (data.containsKey(TITLE)) {

            final String title = (String) data.get(TITLE);
            asset.getTitle().addValue(selectedLocale, title);
        }

        updateAssetProperties(asset, selectedLocale, data);

        assetRepository.save(asset);
    }

    /**
     * Override this method to process data for type specific properties.
     *
     * This method is called by
     * {@link #updateAsset(org.librecms.contentsection.Asset, java.util.Locale, java.util.Map)}.
     * Implementations should <strong>not</strong> call
     * {@link AssetRepository#save}. Saving the update asset is done by
     * {@link #updateAsset(org.librecms.contentsection.Asset, java.util.Locale, java.util.Map)}.
     *
     * An implementation should not assume that a value for each property is
     * present in the provided map. Instead the overriding method should check
     * if a value for a property is available by using
     * {@link Map#containsKey(java.lang.Object)} first.
     *
     * @param asset          The asset to update.
     * @param selectedLocale The locale for which the asset is updated.
     * @param data           The data used to update the asset.
     */
    public abstract void updateAssetProperties(final T asset,
                                               final Locale selectedLocale,
                                               final Map<String, Object> data);

    /**
     * Determines for which locales the provided asset has data.
     *
     * @param assetId The asset.
     *
     * @return A list of all locales for which the asset has data.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public List<Locale> availableLocales(final Long assetId,
                                         final Class<T> assetType) {

        Objects.requireNonNull(
            assetId,
            "Can't get available locales for asset with ID null.");

        final T selectedAsset = loadAsset(assetId, assetType);

        return new ArrayList<>(l10nManager.availableLocales(selectedAsset));
    }

    /**
     * Determines for locales the asset has no data yet.
     *
     * @param assetId The asset.
     *
     * @return A list of all locales for which the provided asset has no data
     *         yet.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public List<Locale> creatableLocales(final Long assetId,
                                         final Class<T> assetType) {

        Objects.requireNonNull(
            assetId,
            "Can't get creatable locales for asset with ID null.");

        final T selectedAsset = loadAsset(assetId, assetType);

        return new ArrayList<>(l10nManager.creatableLocales(selectedAsset));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void addLocale(final Long assetId,
                          final Locale locale,
                          final Class<T> assetType) {

        Objects.requireNonNull(assetId, "Can't add a locale to asset null.");
        Objects.requireNonNull(locale, "Can't add locale null to an asset.");

        final T selectedAsset = loadAsset(assetId, assetType);

        l10nManager.addLanguage(selectedAsset, locale);
    }

    /**
     *
     * @param assetId
     * @param assetType
     *
     * @return
     */
    protected T loadAsset(final Long assetId, final Class<T> assetType) {

        Objects.requireNonNull(assetId, "null is not a valid assetId");

        return assetRepository
            .findById(assetId, assetType)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No asset with ID %d found.", assetId)));
    }

}

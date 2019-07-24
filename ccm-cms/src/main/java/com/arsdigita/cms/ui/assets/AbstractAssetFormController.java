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
import org.librecms.contentsection.AssetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AbstractAssetFormController {

    @Inject
    private AssetRepository assetRepository;
    
    @Inject
    private AssetL10NManager l10nManager;

    @Transactional(Transactional.TxType.REQUIRED)
    public String getTitle(final Asset asset, final Locale locale) {

        Objects.requireNonNull(asset, "Can't get title from asset null.");
        Objects.requireNonNull(locale,
                               "Can't title from asset for locale null");

        final Asset result = assetRepository
            .findById(asset.getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("No asset with ID %d found.",
                                  asset.getObjectId())
                )
            );

        return result.getTitle().getValue(locale);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Locale> availableLocales(final Asset asset) {
        
        Objects.requireNonNull(asset, 
                               "Can't get available locales from asset null.");
        
        final Asset result = assetRepository
            .findById(asset.getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("No asset with ID %d found.",
                                  asset.getObjectId())
                )
            );
        
        return new ArrayList<>(l10nManager.availableLocales(result));
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Locale> creatableLocales(final Asset asset) {
        
        Objects.requireNonNull(asset, 
                               "Can't get creatable locales from asset null.");
        
        final Asset result = assetRepository
            .findById(asset.getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("No asset with ID %d found.",
                                  asset.getObjectId())
                )
            );
        
        return new ArrayList<>(l10nManager.creatableLocales(result));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void updateAsset(final Asset asset, 
                            final String displayName,
                            final String title,
                            final Locale selectedLocale) {
        
        Objects.requireNonNull(asset, "Can't update null");
        
        final Asset selected = assetRepository
        .findById(asset.getObjectId())
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Asset with ID %d found.", asset.getObjectId())));
        
    }
    
}

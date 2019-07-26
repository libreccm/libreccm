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

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetSearchWidgetController {

    protected static final String OBJECT_ID = "objectId";
    protected static final String TYPE = "type";
    protected static final String TITLE = "title";

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private AssetTypesManager typesManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, String> getData(final long assetId) {

        final Asset asset = assetRepository
            .findById(assetId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Asset with ID %d in the database.", assetId)));

        final Map<String, String> data = new HashMap<>();

        data.put(OBJECT_ID, Long.toString(asset.getObjectId()));
        
        data.put(TITLE,
                 globalizationHelper
                     .getValueFromLocalizedString(asset.getTitle()));
        final AssetTypeInfo typeInfo = typesManager
            .getAssetTypeInfo(asset.getClass().getName());
        final ResourceBundle bundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle(),
                       globalizationHelper.getNegotiatedLocale());
        final String typeLabel = bundle.getString(typeInfo.getLabelKey());

        data.put(TYPE, typeLabel);

        return data;
    }

}

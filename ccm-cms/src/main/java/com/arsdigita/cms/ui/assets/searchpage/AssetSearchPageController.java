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
package com.arsdigita.cms.ui.assets.searchpage;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class AssetSearchPageController {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetTypesManager assetTypesManager;

    @Inject
    private AssetManager assetManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<ResultsTableRow> findAssets(final String title) {

        final List<Asset> assets = assetRepo.findByTitle(title);

        return assets
            .stream()
            .map(asset -> createRow(asset))
            .collect(Collectors.toList());

    }

    private ResultsTableRow createRow(final Asset asset) {
        final ResultsTableRow row = new ResultsTableRow();

        row.setAssetUuid(asset.getUuid());
        row.setTitle(globalizationHelper
            .getValueFromLocalizedString(asset.getTitle()));

        final AssetTypeInfo typeInfo = assetTypesManager
            .getAssetTypeInfo(asset.getClass());
        final ResourceBundle bundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle(),
                       globalizationHelper.getNegotiatedLocale());
        row.setType(bundle.getString(typeInfo.getLabelKey()));

        row.setPlace(assetManager.getAssetPath(asset));

        return row;
    }

}

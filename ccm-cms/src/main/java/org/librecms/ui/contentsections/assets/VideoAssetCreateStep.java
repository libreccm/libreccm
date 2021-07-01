/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.assets;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.FileAsset;
import org.librecms.assets.VideoAsset;
import org.librecms.contentsection.AssetRepository;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsVideoAssetCreateStep")
public class VideoAssetCreateStep extends AbstractMvcAssetCreateStep<VideoAsset>{
 
    @Inject
    private AssetRepository assetRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private String fileDescription;

    @Override
    public String showCreateStep() {
        return "org/librecms/ui/contentsection/assets/videoasset/create-videoasset.xhtml";
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("videoasset.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("videoasset.description");
    }

    @Override
    public String getBundle() {
        return MvcAssetStepsConstants.BUNDLE;
    }

    @Override
    protected Class<VideoAsset> getAssetClass() {
        return VideoAsset.class;
    }

    @Override
    protected String setAssetProperties(
        final VideoAsset asset, final Map<String, String[]> formParams
    ) {
        fileDescription = Optional
            .ofNullable(formParams.get("description"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse("");
        asset.getDescription().addValue(
            new Locale(getInitialLocale()), fileDescription
        );

        assetRepo.save(asset);

        return String.format(
            "redirect:/%s/assets/%s/%s/@videoasset-edit",
            getContentSectionLabel(),
            getFolderPath(),
            getName()
        );
    }

}

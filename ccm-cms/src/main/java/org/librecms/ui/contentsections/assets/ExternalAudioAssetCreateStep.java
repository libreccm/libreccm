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
import org.librecms.assets.ExternalAudioAsset;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsExternalAudioAssetCreateStep")
public class ExternalAudioAssetCreateStep
    extends AbstractBookmarkCreateStep<ExternalAudioAsset> {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Override
    public String showCreateStep() {
        return "org/librecms/ui/contentsection/assets/external-audio-asset/create-external-audio-asset.xhtml";
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("externalaudioasset.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("externalaudioasset.description");
    }

    @Override
    protected Class<ExternalAudioAsset> getAssetClass() {
        return ExternalAudioAsset.class;
    }

    @Override
    protected String setAssetProperties(
        final ExternalAudioAsset asset, 
        final Map<String, String[]> formParams
    ) {
        super.setAssetProperties(asset, formParams);
        
         return String.format(
            "redirect:/%s/assets/%s/%s/@external-audio-asset-edit",
            getContentSectionLabel(),
            getFolderPath(),
            getName()
        );
    }
}

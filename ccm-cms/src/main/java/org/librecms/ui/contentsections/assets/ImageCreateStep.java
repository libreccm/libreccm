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
import org.librecms.assets.Image;
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
@Named("CmsImageCreatStep")
public class ImageCreateStep extends AbstractMvcAssetCreateStep<Image> {

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private String description;

    @Override
    public String showCreateStep() {
        return "org/librecms/ui/contentsection/assets/image/create-image.xhtml";
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("image.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("image.description");
    }

    @Override
    public String getBundle() {
        return MvcAssetStepsConstants.BUNDLE;
    }

    @Override
    protected Class<Image> getAssetClass() {
        return Image.class;
    }

    @Override
    protected String setAssetProperties(
        final Image image, final Map<String, String[]> formParams
    ) {
        description = Optional
            .ofNullable(formParams.get("description"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse("");
        image.getDescription().addValue(
            new Locale(getInitialLocale()), description
        );

        assetRepository.save(image);

        return String.format(
            "redirect:/%s/assets/%s/%s/@image-edit",
            getContentSectionLabel(),
            getFolderPath(),
            getName()
        );
    }

}

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

import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.ContentSection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Models;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetUi {

    @Inject
    private AssetManager assetManager;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    public String showAccessDenied(
        final ContentSection section,
        final Asset asset,
        final String step
    ) {
        return showAccessDenied(
            section, assetManager.getAssetPath(asset), step
        );
    }

    public String showAccessDenied(
        final ContentSection section, final String assetPath, final String step
    ) {
        models.put("section", section.getLabel());
        models.put("assetPath", assetPath);
        models.put(step, step);
        return "org/librecms/ui/contentsection/assets/access-denied.xhtml";
    }

    public String showAssetNotFound(
        final ContentSection section, final String assetPath
    ) {
        models.put("section", section.getLabel());
        models.put("assetPath", assetPath);
        return "/org/librecms/ui/contentsection/assets/asset-not-found.xhtml";
    }

}

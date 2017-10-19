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
package org.librecms.pagemodel.assets;

import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = LegalMetadata.class)
public class LegalMetadataRenderer extends AbstractAssetRenderer {

    @Override
    protected void renderAsset(final Asset asset, 
                               final Locale language,
                               final Map<String, Object> result) {
        
        final LegalMetadata legalMetadata;
        if (asset instanceof LegalMetadata) {
            legalMetadata = (LegalMetadata) asset;
        } else {
            return;
        }
        
        result.put("rightsHolder", legalMetadata.getRightsHolder());
        result.put("rights", legalMetadata.getRights().getValue(language));
        result.put("publisher", legalMetadata.getPublisher());
        result.put("creator", legalMetadata.getCreator());
        result.put("contributors", 
                   new ArrayList<>(legalMetadata.getContributors()));
    }
    
}

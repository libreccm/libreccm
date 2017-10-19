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

import org.librecms.assets.BinaryAsset;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class BinaryAssetRenderer extends AbstractAssetRenderer {

    @Override
    protected void renderAsset(final Asset asset, 
                               final Locale language,
                               final Map<String, Object> result) {
        
        final BinaryAsset binaryAsset;
        if (asset instanceof BinaryAsset) {
            binaryAsset = (BinaryAsset) asset;
        } else {
            return;
        }
        
        result.put("description", 
                   binaryAsset.getDescription().getValue(language));
        result.put("fileName", binaryAsset.getFileName());
        result.put("mimeType", Objects.toString(binaryAsset.getMimeType()));
        result.put("size", binaryAsset.getSize());
    }
    
}

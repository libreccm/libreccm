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

import org.librecms.contentsection.Asset;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Abstract base class for rendering {@link Asset}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractAssetRenderer {

    /**
     * Basic implementation rendering the common properties of an asset. For
     * rendering the specific properties of the asset this method calls
     * {@link #renderAsset(org.librecms.contentsection.Asset, java.util.Locale, java.util.Map)}.
     *
     * The common properties put into {@code result} are:
     * 
     * <pre>
     *  {
     *      "objectId": {@link Asset#getObjectId()}
     *      "uuid": {@link Asset#getUuid()}
     *      "displayName": {@link Asset#getDisplayName()}
     *      "title": {@link Asset#getTitle()}
     *  }
     * </pre>
     *
     * @param asset    The {@link Asset} to render.
     * @param language The current language.
     *
     * @return The rendered asset.
     */
    public Map<String, Object> render(final Asset asset,
                                      final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("objectId", asset.getObjectId());
        result.put("uuid", asset.getUuid());
        result.put("displayName", asset.getDisplayName());
        result.put("title", asset.getTitle().getValue(language));

        renderAsset(asset, language, result);

        return result;
    }

    /**
     * Renders the special properties of an specific asset type. If the provided
     * asset is not of the correct type for the renderer this an implementation
     * of this method should return without doing anything.
     *
     * @param asset    The {@link Asset} to render.
     * @param language The current language.
     * @param result   The map in which the rendered properties are stored.
     */
    protected abstract void renderAsset(final Asset asset,
                                        final Locale language,
                                        final Map<String, Object> result);

}

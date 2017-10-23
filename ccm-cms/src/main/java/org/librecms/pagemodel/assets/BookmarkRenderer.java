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

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

import org.librecms.assets.Bookmark;

/**
 * Renderer for {@link Bookmark} assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = Bookmark.class)
public class BookmarkRenderer extends AbstractAssetRenderer {

    /**
     * Renders the provided {@link BookmarkAsset}. The following properties
     * are put into {@code result}:
     * 
     * <pre>
     *  {
     *      "description": {@link Bookmark#getDescription()}
     *      "url": {@link Bookmark#getUrl()}.
     *  }
     * </pre>
     *
     * @param asset    The asset to render.
     * @param language The current language.
     * @param result   The map into which the result is placed.
     */
    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        final Bookmark bookmark;
        if (asset instanceof Bookmark) {
            bookmark = (Bookmark) asset;
        } else {
            return;
        }

        result.put("description", bookmark.getDescription().getValue(language));
        result.put("url", bookmark.getUrl());
    }

}

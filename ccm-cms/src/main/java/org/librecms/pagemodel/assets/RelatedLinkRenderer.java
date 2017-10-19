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

import org.librecms.assets.Bookmark;
import org.librecms.assets.RelatedLink;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.ContentItem;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = RelatedLink.class)
public class RelatedLinkRenderer extends AbstractAssetRenderer {

    @Inject
    @AssetRenderer(renders = Bookmark.class)
    private AbstractAssetRenderer bookmarkRenderer;

    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        final RelatedLink relatedLink;
        if (asset instanceof RelatedLink) {
            relatedLink = (RelatedLink) asset;
        } else {
            return;
        }

        if (relatedLink.getTargetItem() != null) {
            result.put("targetItem",
                       renderTargetItem(relatedLink.getTargetItem(),
                                        language));
        }

        if (relatedLink.getBookmark() != null) {
            result.put("bookmark",
                       bookmarkRenderer.render(relatedLink.getBookmark(),
                                               language));
        }
    }

    protected Map<String, Object> renderTargetItem(final ContentItem targetItem,
                                                   final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("objectId", targetItem.getObjectId());
        result.put("itemUuid", targetItem.getItemUuid());
        result.put("displayName", targetItem.getDisplayName());
        result.put("name", targetItem.getName().getValue(language));
        result.put("title", targetItem.getTitle().getValue(language));
        result.put("description",
                   targetItem.getDescription().getValue(language));

        return result;
    }

}

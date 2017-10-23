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
 * Renderer for {@link RelatedLink} assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = RelatedLink.class)
public class RelatedLinkRenderer extends AbstractAssetRenderer {

    @Inject
    @AssetRenderer(renders = Bookmark.class)
    private AbstractAssetRenderer bookmarkRenderer;

    /**
     * Render the provided {@link RelatedLink}.
     *
     * Depending on the type of the {@link RelatedLink} (internal or external)
     * different properties are placed into {@code result}.
     *
     * For internal links: An entry with the key {@code targetItem} and a map
     * containing the following properties of the target item:
     *
     * <pre>
     *      {
     *          "objectId": {@link ContentItem#getObjectId()}
     *          "itemUuid": {@link ContentItem#getItemUuid()}
     *          "displayName": {@link ContentItem#getDisplayName()}
     *          "name": {@link ContentItem#getName()}
     *          "title": {@link ContentItem#getTitle()}
     *          "description": {@link ContentItem#getDescription()}
     *      }
     * </pre>
     *
     * For external links an {@link RelatedLink} uses a association with a
     * {@link Bookmark}. The {@code Bookmark} is rendered using the
     * {@link BookmarkRenderer}. The result is put into {@code result} with the
     * key {@code bookmark}.
     *
     *
     * @param asset    The asset to render.
     * @param language The current language.
     * @param result   The map is which to result is stored.
     */
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

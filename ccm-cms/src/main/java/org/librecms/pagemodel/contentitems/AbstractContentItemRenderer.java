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
package org.librecms.pagemodel.contentitems;

import org.librecms.pagemodel.assets.AbstractAssetRenderer;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.pagemodel.assets.AssetRenderers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractContentItemRenderer {

    @Inject
    private AssetRenderers assetRenderers;

    public Map<String, Object> render(final ContentItem item,
                                      final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("objectId", item.getObjectId());
        result.put("uuid", item.getUuid());
        result.put("displayName", item.getDisplayName());
        result.put("itemUuid", item.getItemUuid());
        result.put("name", item.getName().getValue(language));
        result.put("title", item.getTitle().getValue(language));
        result.put("contentType", renderContentType(item.getContentType(),
                                                    language));
        result.put("description", item.getDescription().getValue(language));
        result.put("creationDate", item.getCreationDate());
        result.put("lastModified", item.getLastModified());
        result.put("creationUserName", item.getCreationUserName());
        result.put("lastModifyingUserName", item.getLastModifyingUserName());
        result.put("attachments",
                   item
                       .getAttachments()
                       .stream()
                       .map(list -> renderAttachmentList(list, language))
                       .collect(Collectors.toList()));

        renderItem(item, language, result);

        return result;
    }

    protected abstract void renderItem(final ContentItem item,
                                       final Locale language,
                                       final Map<String, Object> result);

    protected Map<String, Object> renderContentType(
        final ContentType contentType, final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("objectId", contentType.getObjectId());
        result.put("uuid", contentType.getUuid());
        result.put("displayName", contentType.getDisplayName());
        result.put("label", contentType.getLabel().getValue(language));

        return result;
    }

    protected Map<String, Object> renderAttachmentList(
        final AttachmentList attachmentList,
        final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("listId", attachmentList.getListId());
        result.put("uuid", attachmentList.getUuid());
        result.put("name", attachmentList.getName());
        result.put("order", attachmentList.getOrder());
        result.put("title", attachmentList.getTitle().getValue(language));
        result.put("description",
                   attachmentList.getDescription().getValue(language));

        result.put("attachments",
                   attachmentList
                       .getAttachments()
                       .stream()
                       .map(attachment -> renderAttachment(attachment, language))
                       .collect(Collectors.toList()));

        return result;
    }

    protected Map<String, Object> renderAttachment(
        final ItemAttachment<?> attachment,
        final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("attachmentId", attachment.getAttachmentId());
        result.put("uuid", attachment.getUuid());
        result.put("sortKey", attachment.getSortKey());

        final AbstractAssetRenderer renderer = assetRenderers
            .findRenderer(attachment.getAsset().getClass());
        result.put("asset", renderer.render(attachment.getAsset(), language));

        return result;
    }

}

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

import org.libreccm.messaging.Attachment;
import org.librecms.contentsection.Asset;
import org.librecms.pagemodel.assets.AbstractAssetRenderer;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.rs.ContentItems;
import org.librecms.pagemodel.assets.AssetRenderers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base class for the renderers for {@link ContentItems}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractContentItemRenderer implements Serializable {

    private static final long serialVersionUID = 1290408390406469580L;

//    private final AssetRenderers assetRenderers;
//
//    public AbstractContentItemRenderer(final AssetRenderers assetRenderers) {
//        this.assetRenderers = assetRenderers;
//    }

    /**
     * This method should be called to render a {@link ContentItem}. The method
     * puts the common properties for {@link ContentItem}s into {@code result}
     * and than calls
     * {@link #renderItem(org.librecms.contentsection.ContentItem, java.util.Locale, java.util.Map)}
     * to put the special properties of provided item into {@code result}.
     *
     * The common properties put into {@code result} are:
     *
     * <pre>
     *  {
     *      "objectId": {@link ContentItem#getObjectId()}
     *      "uuid": {@link ContentItem#getUuid()}
     *      "displayName": {@link ContentItem#getDisplayName()}
     *      "itemUuid": {@link ContentItem#getItemUuid()}
     *      "name": {@link ContentItem#getName()}
     *      "title": {@link ContentItem#getTitle()}
     *      "contentType": {@link ContentItem#getContentType()}
     *      "description": {@link ContentItem#getDescription()}
     *      "creationDate": {@link ContentItem#getCreationDate()}
     *      "lastModified": {@link ContentItem#getLastModified()}
     *      "creationUserName": {@link ContentItem#getCreationUserName()}
     *      "lastModifyingUserName": {@link ContentItem#getLastModifyingUserName()}
     *      "attachments": {@link ContentItem#getAttachments()}.
     *  }
     * </pre>
     *
     * The value of {@link ContentItem#getContentType} is rendered by
     * {@link #renderContentType(org.librecms.contentsection.ContentType, java.util.Locale)}.
     *
     * The value of {@link ContentItem#getAttachments()} is rendered using
     * {@link #renderAttachmentList(org.librecms.contentsection.AttachmentList, java.util.Locale)}.
     *
     * @param item     The item to render.
     * @param language The current language.
     *
     * @return A map with the data of the provided {@link ContentItem}.
     */
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
        result.put("version", item.getVersion().toString());
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
    
    protected abstract AssetRenderers getAssetRenderers();

    /**
     * Renders the {@link ContentType} of an {@link ContentItem}. The generated
     * map contains the following values:
     *
     * <pre>
     *  {
     *      "objectId": {@link ContentType#getObjectId()}
     *      "uuid": {@link ContentType#getUuid()}
     *      "displayName": {@link ContentType#getDisplayName()}
     *      "label": {@link ContentType#getLabel()}
     *  }
     * </pre>
     *
     * @param contentType The {@link ContentType} to render.
     * @param language    The current language.
     *
     * @return A map with the properties of the {@link ContentType}.
     */
    protected Map<String, Object> renderContentType(
        final ContentType contentType, final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("objectId", contentType.getObjectId());
        result.put("uuid", contentType.getUuid());
        result.put("displayName", contentType.getDisplayName());
        result.put("label", contentType.getLabel().getValue(language));

        return result;
    }

    /**
     * Renders a {@link AttachmentList} and all {@link ItemAttachment}s. in the
     * list. The map contains the following values:
     *
     * <pre>
     *  {
     *      "listId": {@link AttachmentList#getListId()}
     *      "uuid": {@link AttachmentList#getUuid()}
     *      "name": {@link AttachmentList#getName()}
     *      "order": {@link AttachmentList#getListOrder()}
     *      "title": {@link AttachmentList#getTitle()}
     *      "description": {@link AttachmentList#getDescription()}
     *      "attachments": {@link AttachmentList#getAttachments()}
     *  }
     * </pre>
     *
     * The attachments of the list are rendered using
     * {@link #renderAttachment(org.librecms.contentsection.ItemAttachment, java.util.Locale)}.
     *
     * @param attachmentList The {@link AttachmentList} to render.
     * @param language       The current language.
     *
     * @return A map containing the data of the {@link AttachmentList} and its
     *         {@link Attachment}s.
     */
    protected Map<String, Object> renderAttachmentList(
        final AttachmentList attachmentList,
        final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("listId", attachmentList.getListId());
        result.put("uuid", attachmentList.getUuid());
        result.put("name", attachmentList.getName());
        result.put("order", attachmentList.getListOrder());
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

    /**
     * Renders an {@link ItemAttachment}. The generated map contains the
     * following values:
     *
     * <pre>
     *  {
     *      "attachmentId": {@link ItemAttachment#getAttachmentId()}
     *      "uuid": {@link ItemAttachment#getUuid()}
     *      "sortKey": {@link ItemAttachment#getSortKey()}
     *      "asset": {@link ItemAttachment#getAsset()}
     *  }
     * </pre>
     *
     * The associated {@link Asset} is rendered using the appropriate
     * {@link AbstractAssetRenderer} implementation. The
     * {@link AbstractAssetRenderer} to use is retrieved using
     * {@link AssetRenderers#findRenderer(java.lang.Class)}.
     *
     * @param attachment The {@link ItemAttachment} to render.
     * @param language   The current language.
     *
     * @return A map with the data of the {@link ItemAttachment}.
     */
    protected Map<String, Object> renderAttachment(
        final ItemAttachment<?> attachment,
        final Locale language) {

        final Map<String, Object> result = new HashMap<>();

        result.put("attachmentId", attachment.getAttachmentId());
        result.put("uuid", attachment.getUuid());
        result.put("sortKey", attachment.getSortKey());

        final AbstractAssetRenderer renderer = getAssetRenderers()
            .findRenderer(attachment.getAsset().getClass());
        result.put("asset", renderer.render(attachment.getAsset(), language));

        return result;
    }

}

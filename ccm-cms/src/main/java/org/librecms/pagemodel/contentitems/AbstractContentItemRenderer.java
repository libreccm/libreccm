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

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractContentItemRenderer {

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

}

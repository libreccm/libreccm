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
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ItemAttachment;

/**
 * A DTO for providing data about an {@link ItemAttachment} in a form suitable
 * for a MVC view.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemAttachmentDto {

    /**
     * The ID of the attachment.
     */
    private long attachmentId;

    /**
     * The UUID of the attachment.
     */
    private String uuid;

    /**
     * The sort key of the attachment.
     */
    private long sortKey;

    /**
     * The type of the asset of the attachment.
     */
    private String assetType;

    /**
     * The title of the asset assigned to an content item. This value is
     * determined from the title of the asset using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String title;

    /**
     * An indicator if the attachment is containing an internal
     * {@link RelatedLink}.
     */
    private boolean internalLink;

    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(final long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(final String assetType) {
        this.assetType = assetType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isInternalLink() {
        return internalLink;
    }

    public void setInternalLink(final boolean internalLink) {
        this.internalLink = internalLink;
    }

}

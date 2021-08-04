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
package org.librecms.ui.contentsections.documents.media;

/**
 * A DTO for providing data about an {@link ItemAttachment} containing a media
 * asset in a form suitable for a MVC view.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MediaAttachmentDto {

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
     * The name of the asset type.
     */
    private String assetType;

    /**
     * Label for the type of the asset of the attachment.
     */
    private String assetTypeLabel;

    /**
     * The UUID of the attachment asset.
     */
    private String assetUuid;

    /**
     * The title of the media asset assigned to an content item. This value is
     * determined from the title of the asset using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String title;

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

    public String getAssetTypeLabel() {
        return assetTypeLabel;
    }

    public void setAssetTypeLabel(final String assetTypeLabel) {
        this.assetTypeLabel = assetTypeLabel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getAssetUuid() {
        return assetUuid;
    }

    public void setAssetUuid(final String assetUuid) {
        this.assetUuid = assetUuid;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(final String assetType) {
        this.assetType = assetType;
    }

}

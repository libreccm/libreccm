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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MovedAttachment {

    private String attachmentUuid;

    private String fromListUuid;

    private String toListUuid;

    public String getAttachmentUuid() {
        return attachmentUuid;
    }

    public void setAttachmentUuid(final String attachmentUuid) {
        this.attachmentUuid = attachmentUuid;
    }

    public String getFromListUuid() {
        return fromListUuid;
    }

    public void setFromListUuid(final String fromListUuid) {
        this.fromListUuid = fromListUuid;
    }

    public String getToListUuid() {
        return toListUuid;
    }

    public void setToListUuid(final String toListUuid) {
        this.toListUuid = toListUuid;
    }

}

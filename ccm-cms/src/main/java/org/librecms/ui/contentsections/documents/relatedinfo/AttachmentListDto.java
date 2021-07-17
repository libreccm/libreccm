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
package org.librecms.ui.contentsections.documents.relatedinfo;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.ContentItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.librecms.ui.contentsections.documents.ItemAttachmentDto;

/**
 * A data transfer object used by the template for the listing of the
 * {@link AttachmentList}s of a {@link ContentItem}.
 *
 * @see RelatedInfoStep
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AttachmentListDto {

    /**
     * The ID of the list.
     */
    private long listId;

    /**
     * The UUID of the list.
     */
    private String uuid;

    /**
     * The name of the list.
     */
    private String name;

    /**
     * The order value of the list.
     */
    private long order;

    /**
     * The title of the list. This value is determined using
     * {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)}.
     */
    private String title;

    /**
     * The description of the list. This value is determined using
     * {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)}.
     */
    private String description;

    /**
     * The @link{ItemAttachment}s associated with the {@link AttachmentList}.
     */
    private List<ItemAttachmentDto> attachments;

    public long getListId() {
        return listId;
    }

    public void setListId(final long listId) {
        this.listId = listId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<ItemAttachmentDto> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public void setAttachments(final List<ItemAttachmentDto> attachments) {
        this.attachments = new ArrayList<>(attachments);
    }

}

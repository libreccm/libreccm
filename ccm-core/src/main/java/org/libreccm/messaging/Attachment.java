/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.messaging;

import org.libreccm.jpautils.MimeTypeConverter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import javax.activation.MimeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "attachments")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 2063934721452863106L;

    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long attachmentId;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "mime_type")
    @Convert(converter = MimeTypeConverter.class)
    private MimeType mimeType;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "attachment_data")
    @Lob
    private byte[] data;

    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(final long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Message getMessage() {
        return message;
    }

    protected void setMessage(final Message message) {
        this.message = message;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(final MimeType mimeType) {
        this.mimeType = mimeType;
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

    public byte[] getData() {
        if (data == null) {
            return null;
        } else {
            return Arrays.copyOf(data, data.length);
        }
    }

    public void setData(byte[] data) {
        if (data == null) {
            this.data = null;
        } else {
            this.data = Arrays.copyOf(data, data.length);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash
            = 67 * hash + (int) (attachmentId ^ (attachmentId >>> 32));
        hash = 67 * hash + Objects.hashCode(message);
        hash = 67 * hash + Objects.hashCode(mimeType);
        hash = 67 * hash + Objects.hashCode(title);
        hash = 67 * hash + Objects.hashCode(description);
        hash = 67 * hash + Arrays.hashCode(data);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Attachment)) {
            return false;
        }
        final Attachment other = (Attachment) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (attachmentId != other.getAttachmentId()) {
            return false;
        }

        if (!Objects.equals(message, other.getMessage())) {
            return false;
        }

        if (!Objects.equals(mimeType, other.getMimeType())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        return Arrays.equals(data, other.getData());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Attachment;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "attachmentId = %d, "
                                 + "message = %s, "
                                 + "mimeType = \"%s\", "
                                 + "title = \"%s\""
                                 + " }",
                             super.toString(),
                             attachmentId,
                             Objects.toString(message),
                             Objects.toString(mimeType),
                             title);
    }

}

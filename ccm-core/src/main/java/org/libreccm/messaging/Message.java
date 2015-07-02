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

import org.libreccm.core.CcmObject;
import org.libreccm.core.Subject;
import org.libreccm.jpa.utils.MimeTypeConverter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.activation.MimeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "messages")
//Can't reduce complexity yet
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity"})
public class Message extends CcmObject implements Serializable {

    private static final long serialVersionUID = -9143137794418932025L;

    @OneToOne
    @JoinColumn(name = "sender_id")
    private Subject sender;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "body_mime_type")
    @Convert(converter = MimeTypeConverter.class)
    private MimeType bodyMimeType;

    @Column(name = "sent")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sent;

    @ManyToOne
    @JoinColumn(name = "in_reply_to_id")
    private Message inReplyTo;

    @OneToMany(mappedBy = "inReplyTo")
    private List<Message> replies;

    @OneToMany(mappedBy = "message")
    private List<Attachment> attachments;

    public Subject getSender() {
        return sender;
    }

    protected void setSender(final Subject sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public MimeType getBodyMimeType() {
        return bodyMimeType;
    }

    public void setBodyMimeType(final MimeType bodyMimeType) {
        this.bodyMimeType = bodyMimeType;
    }

    public Date getSent() {
        if (sent == null) {
            return null;
        } else {
            return new Date(sent.getTime());
        }
    }

    public void setSent(final Date sent) {
        if (sent == null) {
            this.sent = null;
        } else {
            this.sent = new Date(sent.getTime());
        }
    }

    public Message getInReplyTo() {
        return inReplyTo;
    }

    protected void setInReplyTo(final Message inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public List<Message> getReplies() {
        if (replies == null) {
            return null;
        } else {
            return Collections.unmodifiableList(replies);
        }
    }

    protected void setReplies(final List<Message> replies) {
        this.replies = replies;
    }

    protected void addReply(final Message reply) {
        replies.add(reply);
    }

    protected void removeReply(final Message reply) {
        replies.remove(reply);
    }

    public List<Attachment> getAttachments() {
        if (attachments == null) {
            return null;
        } else {
            return Collections.unmodifiableList(attachments);
        }
    }

    protected void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

    protected void addAttachment(final Attachment attachment) {
        attachments.add(attachment);
    }

    protected void removeAttachment(final Attachment attachment) {
        attachments.remove(attachment);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Objects.hashCode(sender);
        hash = 89 * hash + Objects.hashCode(subject);
        hash = 89 * hash + Objects.hashCode(body);
        hash = 89 * hash + Objects.hashCode(bodyMimeType);
        hash = 89 * hash + Objects.hashCode(sent);
        hash = 89 * hash + Objects.hashCode(inReplyTo);
        return hash;
    }

    @Override
    //Can't reduce complexity yet
    @SuppressWarnings({"PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity",
                       "PMD.NPathComplexity"})
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Message)) {
            return false;
        }
        final Message other = (Message) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(sender, other.getSender())) {
            return false;
        }
        if (!Objects.equals(subject, other.getSubject())) {
            return false;
        }
        if (!Objects.equals(body, other.getBody())) {
            return false;
        }
        if (!Objects.equals(bodyMimeType, other.getBodyMimeType())) {
            return false;
        }
        if (!Objects.equals(sent, other.getSent())) {
            return false;
        }
        return Objects.equals(inReplyTo, other.getInReplyTo());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Message;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", sender = %s, "
                                                    + "subject = \"%s\", "
                                                    + "bodyMimeType = \"%s\", "
                                                    + "sent = %tF %<tT, "
                                                    + "inReplyTo = %s%s",
                                            Objects.toString(sender),
                                            subject,
                                            Objects.toString(bodyMimeType),
                                            sent,
                                            Objects.toString(inReplyTo),
                                            data));
    }

}

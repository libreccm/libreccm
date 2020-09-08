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
package org.libreccm.notification;

import org.libreccm.core.CcmObject;
import org.libreccm.messaging.Message;
import org.libreccm.security.Party;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * The {@code Notification} class is used to create and send messages via email
 * to ACS users and groups. It acts as a wrapper for a {@link Message} which
 * contains the subject, sender, body and any attachments for the email. The
 * recipient can be a {@link
 * com.arsdigita.kernel.User} or a {@link org.libreccm.core.Group}. In the case
 * of Group, the message can be sent to the group's email address or expanded
 * into a separate message for each member of the group.
 *
 * <h4>Email Alerts</h4>
 *
 * When using notifications for email alerts, applications often need to wrap a
 * special header and signature around the contained Message object. This can be
 * useful for including introductory remarks and action links in the email body.
 * The {@code setHeader} and {@code setSignature} methods allow you to do this
 * without the need to create a separate Message for the modified email.
 *
 * <h4>Digests</h4>
 *
 * Finally, notifications can be sent in "instant processing mode" or as part of
 * a {@link Digest}. When sent as part of a digest all notifications to the same
 * recipient are collected into a single email and sent at regular internal. For
 * example, an hourly digest might send a user all of their workflow task
 * updates that have changed in the past hour, rather a much larger number of
 * individual messages every time an tasks changed.
 *
 * (Documentation taken from the {@code com.arsdigita.notification.Notification}
 * class)
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "NOTIFICATIONS", schema = DB_SCHEMA)
//Can't reduce complexity yet. Not sure what to do about the God class warning.
//Maybe we have to put some of the properties into an extra class.
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity",
                   "PMD.GodClass"})
public class Notification extends CcmObject implements Serializable {

    private static final long serialVersionUID = -6052859580690813506L;

    @OneToOne
    @JoinColumn(name = "RECEIVER_ID")
    private Party receiver;

    @OneToOne
    @JoinColumn(name = "DIGEST_ID")
    private Digest digest;

    @OneToOne
    @JoinColumn(name = "MESSAGE_ID")
    private Message message;

    @Column(name = "HEADER", length = 4096)
    private String header;

    @Column(name = "SIGNATURE", length = 4096)
    private String signature;

    @Column(name = "EXPAND_GROUP")
    private boolean expandGroup;

    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Column(name = "FULFILL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fulfillDate;

    @Column(name = "STATUS", length = 32)
    private String status;

    @Column(name = "MAX_RETRIES")
    private long maxRetries;

    @Column(name = "EXPUNGE")
    private boolean expunge;

    @Column(name = "EXPUNGE_MESSAGE")
    private boolean expungeMessage;

    public Party getReceiver() {
        return receiver;
    }

    public void setReceiver(final Party receiver) {
        this.receiver = receiver;
    }

    public Digest getDigest() {
        return digest;
    }

    public void setDigest(final Digest digest) {
        this.digest = digest;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(final Message message) {
        this.message = message;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(final String signature) {
        this.signature = signature;
    }

    public boolean isExpandGroup() {
        return expandGroup;
    }

    public void setExpandGroup(final boolean expandGroup) {
        this.expandGroup = expandGroup;
    }

    public Date getRequestDate() {
        if (requestDate == null) {
            return null;
        } else {
            return new Date(requestDate.getTime());
        }
    }

    public void setRequestDate(final Date requestDate) {
        if (requestDate == null) {
            this.requestDate = null;
        } else {
            this.requestDate = new Date(requestDate.getTime());
        }
    }

    public Date getFulfillDate() {
        if (fulfillDate == null) {
            return null;
        } else {
            return new Date(fulfillDate.getTime());
        }
    }

    public void setFulfillDate(final Date fulfillDate) {
        if (fulfillDate == null) {
            this.fulfillDate = null;
        } else {
            this.fulfillDate = new Date(fulfillDate.getTime());
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public long getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(final long maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean isExpunge() {
        return expunge;
    }

    public void setExpunge(final boolean expunge) {
        this.expunge = expunge;
    }

    public boolean isExpungeMessage() {
        return expungeMessage;
    }

    public void setExpungeMessage(final boolean expungeMessage) {
        this.expungeMessage = expungeMessage;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 53 * hash + Objects.hashCode(receiver);
        hash = 53 * hash + Objects.hashCode(digest);
        hash = 53 * hash + Objects.hashCode(message);
        hash = 53 * hash + Objects.hashCode(header);
        hash = 53 * hash + Objects.hashCode(signature);
        hash = 53 * hash + (expandGroup ? 1 : 0);
        hash = 53 * hash + Objects.hashCode(requestDate);
        hash = 53 * hash + Objects.hashCode(fulfillDate);
        hash = 53 * hash + Objects.hashCode(status);
        hash = 53 * hash + (int) (maxRetries ^ (maxRetries >>> 32));
        hash = 53 * hash + (expunge ? 1 : 0);
        hash = 53 * hash + (expungeMessage ? 1 : 0);
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
        if (!(obj instanceof Notification)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final Notification other = (Notification) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(receiver, other.getReceiver())) {
            return false;
        }
        if (!Objects.equals(digest, other.getDigest())) {
            return false;
        }
        if (!Objects.equals(message, other.getMessage())) {
            return false;
        }
        if (!Objects.equals(header, other.getHeader())) {
            return false;
        }
        if (!Objects.equals(signature, other.getSignature())) {
            return false;
        }
        if (expandGroup != other.isExpandGroup()) {
            return false;
        }
        if (!Objects.equals(requestDate, other.getRequestDate())) {
            return false;
        }
        if (!Objects.equals(fulfillDate, other.getFulfillDate())) {
            return false;
        }
        if (!Objects.equals(status, other.getStatus())) {
            return false;
        }
        if (maxRetries != other.getMaxRetries()) {
            return false;
        }
        if (expunge != other.isExpunge()) {
            return false;
        }
        return expungeMessage == other.isExpungeMessage();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Notification;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", receiver = %s, "
                                                    + "digest = %s, "
                                                    + "message = %s, "
                                                    + "expandGroup = %b, "
                                                    + "requestDate  = %tF %<tT, "
                                            + "fulfillDate = %tF %<tT, "
                                                    + "status = \"%s\", "
                                                    + "expunge = %b, "
                                                    + "expungeMessage = %b%s",
                                            Objects.toString(receiver),
                                            Objects.toString(digest),
                                            Objects.toString(message),
                                            expandGroup,
                                            requestDate,
                                            fulfillDate,
                                            status,
                                            expunge,
                                            expungeMessage,
                                            data));
    }

}

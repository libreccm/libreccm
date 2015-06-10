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

import org.libreccm.core.Party;
import org.libreccm.messaging.Message;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents a notification that has been transferred to the outbound message
 * queue. During processing, this class is used to retrieve information
 * necessary to convert the notification into an outbound email message.
 *
 * (Documentation taken from the [@code com.arsdigita.notifiction.QueueItem}
 * class.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "queue_items")
//Can't reduce complexity yet
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity"})
public class QueueItem implements Serializable {

    private static final long serialVersionUID = 396330385592074013L;

    @Id
    @Column(name = "queue_item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long queueItemId;

    @OneToOne
    @JoinColumn(name = "receiver_id")
    private Party receiver;

    @Column(name = "retry_count")
    private long retryCount;

    @Column(name = "successful_sended")
    private boolean successful;

    @Column(name = "receiver_address", length = 512)
    private String receiverAddress;

    @Column(name = "header", length = 4096)
    private String header;

    @Column(name = "signature", length = 4096)
    private String signature;

    @OneToOne
    @JoinColumn(name = "message_id")
    private Message message;

    public long getQueueItemId() {
        return queueItemId;
    }

    public void setQueueItemId(final long queueItemId) {
        this.queueItemId = queueItemId;
    }

    public Party getReceiver() {
        return receiver;
    }

    public void setReceiver(final Party receiver) {
        this.receiver = receiver;
    }

    public long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(final long retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(final boolean successful) {
        this.successful = successful;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(final String receiverAddress) {
        this.receiverAddress = receiverAddress;
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(final Message message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.queueItemId ^ (this.queueItemId >>> 32));
        hash = 59 * hash + Objects.hashCode(this.receiver);
        hash = 59 * hash + (int) (this.retryCount ^ (this.retryCount >>> 32));
        hash = 59 * hash + (this.successful ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.receiverAddress);
        hash = 59 * hash + Objects.hashCode(this.header);
        hash = 59 * hash + Objects.hashCode(this.signature);
        hash = 59 * hash + Objects.hashCode(this.message);
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
        if (!(obj instanceof QueueItem)) {
            return false;
        }
        final QueueItem other = (QueueItem) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (this.queueItemId != other.getQueueItemId()) {
            return false;
        }
        if (!Objects.equals(this.receiver, other.getReceiver())) {
            return false;
        }
        if (this.retryCount != other.getRetryCount()) {
            return false;
        }
        if (this.successful != other.isSuccessful()) {
            return false;
        }
        if (!Objects.equals(this.receiverAddress, other.getReceiverAddress())) {
            return false;
        }
        if (!Objects.equals(this.header, other.getHeader())) {
            return false;
        }
        if (!Objects.equals(this.signature, other.getSignature())) {
            return false;
        }
        return Objects.equals(this.message, other.getMessage());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof QueueItem;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                     + "queueItemId = %d, "
                                     + "receiver = %s, "
                                     + "retryCount = %d, "
                                     + "successful = %b, "
                                     + "receiverAddress = \"%s\", "
                                     + "message = %s"
                                     + " }",
                             super.toString(),
                             queueItemId,
                             Objects.toString(receiver),
                             retryCount,
                             successful,
                             receiverAddress,
                             Objects.toString(message));
    }

}

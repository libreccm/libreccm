/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import org.hibernate.envers.Audited;
import org.libreccm.core.Identifiable;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * An intermediate entity to model the relation between an {@link Asset} (either
 * shared or not shared) and an {@link AttachmentList}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
@Entity
@Table(schema = DB_SCHEMA, name = "ATTACHMENTS")
@Audited
@NamedQueries({
    @NamedQuery(
        name = "ItemAttachment.findById",
        query = "SELECT i FROM ItemAttachment i "
                    + "WHERE i.attachmentId = :attachmentId")
    ,
    @NamedQuery(
        name = "ItemAttachment.countByAssetIdAndList",
        query = "SELECT COUNT(i) FROM ItemAttachment i "
                    + "WHERE i.asset = :asset "
                    + "AND i.attachmentList = :attachmentList")
    ,
    @NamedQuery(
        name = "ItemAttachment.findByAssetByAndList",
        query = "SELECT i FROM ItemAttachment i "
                    + "WHERE i.asset = :asset "
                    + "AND i.attachmentList = :attachmentList"
    )
})
public class ItemAttachment<T extends Asset>
    implements Comparable<ItemAttachment<?>>,
               Identifiable,
               Serializable {

    private static final long serialVersionUID = -9005379413315191984L;

    /**
     * The ID of the attachment entity in the database.
     */
    @Column(name = "ATTACHMENT_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long attachmentId;

    /**
     * UUID of the attachment.
     */
    @Column(name = "uuid")
    private String uuid;

    /**
     * The {@link AttachmentList} to which this attachment belongs.
     */
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "ATTACHMENT_LIST_ID")
    private AttachmentList attachmentList;

    /**
     * The {@link Asset} which is linked by this attachment to the
     * {@link #attachmentList}.
     */
    @ManyToOne(targetEntity = Asset.class,
               cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "ASSET_ID")
    private T asset;

    /**
     * The sort key of this attachment in {@link #attachmentList}.
     */
    @Column(name = "SORT_KEY")
    private long sortKey;

    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(final long attachmentId) {
        this.attachmentId = attachmentId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public AttachmentList getAttachmentList() {
        return attachmentList;
    }

    protected void setAttachmentList(final AttachmentList attachmentList) {
        this.attachmentList = attachmentList;
    }

    public T getAsset() {
        return asset;
    }

    public void setAsset(final T asset) {
        this.asset = asset;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(final long sortKey) {
        this.sortKey = sortKey;
    }

    @Override
    public int compareTo(final ItemAttachment<?> other) {
        if (other == null) {
            throw new NullPointerException();
        }

        return Long.compare(sortKey, other.getSortKey());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash
            = 71 * hash + (int) (attachmentId ^ (attachmentId >>> 32));
        hash = 71 * hash + Objects.hashCode(uuid);
        hash = 71 * hash + Objects.hashCode(asset);
        hash = 71 * hash + (int) (sortKey ^ (sortKey >>> 32));
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ItemAttachment)) {
            return false;
        }
        final ItemAttachment<?> other = (ItemAttachment<?>) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (attachmentId != other.getAttachmentId()) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        if (sortKey != other.getSortKey()) {
            return false;
        }
        return Objects.equals(asset, other.getAsset());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ItemAttachment;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "attachmentId = %d, "
                                 + "uuid = %s, "
                                 + "asset = { %s }, "
                                 + "sortKey = %d%s"
                                 + " }",
                             super.toString(),
                             attachmentId,
                             uuid,
                             Objects.toString(asset),
                             sortKey,
                             data);
    }

}

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
package org.librecms.attachments;

import org.hibernate.envers.Audited;
import org.libreccm.core.Identifiable;
import org.libreccm.l10n.LocalizedString;
import org.librecms.assets.Asset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
@Entity
@Table(schema = DB_SCHEMA, name = "attachment_lists")
@Audited
public class AttachmentList<T extends Asset> implements Identifiable,
                                                        List<ItemAttachment<T>>,
                                                        Serializable {

    private static final long serialVersionUID = -7750330135750750047L;

    @Column(name = "LIST_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long listId;

    @Column(name = "UUID")
    private String uuid;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "ATTACHMENT_LIST_CAPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "LIST_ID")
                               }
        )
    )
    private LocalizedString caption;

    @Column(name = "ASSET_TYPE", length = 1024)
    private String assetType;

    @OneToMany(targetEntity = ItemAttachment.class)
    @JoinColumn(name = "LIST_ID")
    private List<ItemAttachment<T>> attachments;

    public long getListId() {
        return listId;
    }

    protected void setListId(final long listId) {
        this.listId = listId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public LocalizedString getCaption() {
        return caption;
    }

    public void setCaption(final LocalizedString caption) {
        this.caption = caption;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(final String assetType) {
        this.assetType = assetType;
    }

    public List<ItemAttachment<T>> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public void setAttachments(List<ItemAttachment<T>> attachments) {
        this.attachments = new ArrayList<>(attachments);
    }

    @Override
    public int size() {
        return attachments.size();
    }

    @Override
    public boolean isEmpty() {
        return attachments.isEmpty();
    }

    @Override
    public boolean contains(final Object obj) {
        return attachments.contains(obj);
    }

    @Override
    public Iterator<ItemAttachment<T>> iterator() {
        return attachments.iterator();
    }

    @Override
    public Object[] toArray() {
        return attachments.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] array) {
        return attachments.toArray(array);
    }

    @Override
    public boolean add(final ItemAttachment<T> attachment) {
        return attachments.add(attachment);
    }

    @Override
    public boolean remove(final Object obj) {
        return attachments.remove(obj);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return attachments.containsAll(collection);
    }

    @Override
    public boolean addAll(
        final Collection<? extends ItemAttachment<T>> collection) {

        return attachments.addAll(collection);
    }

    @Override
    public boolean addAll(
        final int index,
        final Collection<? extends ItemAttachment<T>> collection) {

        return attachments.addAll(index, collection);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return attachments.removeAll(collection);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return attachments.retainAll(collection);
    }

    @Override
    public void clear() {
        attachments.clear();
    }

    @Override
    public ItemAttachment<T> get(final int index) {
        return attachments.get(index);
    }

    @Override
    public ItemAttachment<T> set(final int index,
                                 final ItemAttachment<T> element) {
        return attachments.set(index, element);
    }

    @Override
    public void add(final int index, final ItemAttachment<T> element) {
        attachments.add(index, element);
    }

    @Override
    public ItemAttachment<T> remove(final int index) {
        return attachments.remove(index);
    }

    @Override
    public int indexOf(final Object obj) {
        return attachments.indexOf(obj);
    }

    @Override
    public int lastIndexOf(final Object obj) {
        return attachments.lastIndexOf(obj);
    }

    @Override
    public ListIterator<ItemAttachment<T>> listIterator() {
        return attachments.listIterator();
    }

    @Override
    public ListIterator<ItemAttachment<T>> listIterator(final int index) {
        return attachments.listIterator(index);
    }

    @Override
    public List<ItemAttachment<T>> subList(final int fromIndex,
                                           final int toIndex) {
        return attachments.subList(fromIndex, toIndex);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (listId ^ (listId >>> 32));
        hash = 97 * hash + Objects.hashCode(uuid);
        hash = 97 * hash + Objects.hashCode(caption);
        hash = 97 * hash + Objects.hashCode(assetType);
        hash = 97 * hash + Objects.hashCode(attachments);
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
        if (!(obj instanceof AttachmentList)) {
            return false;
        }
        final AttachmentList<?> other = (AttachmentList<?>) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (listId != other.getListId()) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        if (!Objects.equals(caption, other.getCaption())) {
            return false;
        }
        if (!Objects.equals(assetType, other.getAssetType())) {
            return false;
        }
        return Objects.equals(attachments, other.getAttachments());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof AttachmentList;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "listId = %d, "
                                 + "uuid = %s, "
                                 + "caption = { %s }, "
                                 + "assetType = %s, "
                                 + "attachments = { %s }%s"
                                 + " }",
                             super.toString(),
                             listId,
                             uuid,
                             Objects.toString(caption),
                             assetType,
                             Objects.toString(attachments),
                             data);
    }

}

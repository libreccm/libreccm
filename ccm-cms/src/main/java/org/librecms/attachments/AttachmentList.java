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
import org.librecms.contentsection.ContentItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * A list of assets attached a {@link ContentItem}. Each {@link ContentItem} may
 * have multiple lists of attachments. Each list can be identified by name which
 * can be used for example by the theme to determine the position where the list
 * is printed.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ATTACHMENT_LISTS", schema = DB_SCHEMA)
@Audited
@NamedQueries({
    @NamedQuery(
        name = "AttachmentList.findForItemAndName",
        query = "SELECT l FROM AttachmentList l "
                    + "WHERE l.name = :name "
                    + "AND l.item = :item "
                    + "ORDER BY l.order")
})
public class AttachmentList implements Comparable<AttachmentList>,
                                       Identifiable,
                                       Serializable {

    private static final long serialVersionUID = -7931234562247075541L;

    /**
     * Database ID of the list entity.
     */
    @Column(name = "LIST_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long listId;

    /**
     * UUID of the list.
     */
    @Column(name = "UUID")
    private String uuid;

    /**
     * The {@link ContentItem} which owns this list.
     */
    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private ContentItem item;

    /**
     * The name of the list.
     */
    @Column(name = "NAME")
    private String name;

    /**
     * A order index for ordering multiple attachment lists with the same
     * {@link #name}.
     */
    @Column(name = "LIST_ORDER")
    private long order;

    /**
     * The localised title of the list.
     */
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
    private LocalizedString title;

    /**
     * The description of the list.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "ATTACHMENT_LIST_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "LIST_ID")
                               }))
    private LocalizedString description;

    @OneToMany(mappedBy = "attachmentList")
    private List<ItemAttachment<?>> attachments;

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public ContentItem getItem() {
        return item;
    }

    public void setItem(final ContentItem item) {
        this.item = item;
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

    public void setOrder(final long order) {
        this.order = order;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public List<ItemAttachment<?>> getAttachments() {
        if (attachments == null) {
            return new ArrayList<>();
        } else {
            return Collections.unmodifiableList(attachments);
        }
    }

    protected void setAttachments(final List<ItemAttachment<?>> attachments) {
        this.attachments = Collections.unmodifiableList(attachments);
    }

    protected void addAttachment(final ItemAttachment<?> attachment) {
        attachments.add(attachment);
    }

    protected void removeAttachment(final ItemAttachment<?> attachment) {
        attachments.remove(attachment);
    }

    @Override
    public int compareTo(final AttachmentList other) {
        if (other == null) {
            throw new NullPointerException();
        }

        final int nameCompare = name.compareTo(other.getName());
        if (nameCompare == 0) {
            return Long.compare(order, other.getOrder());
        } else {
            return nameCompare;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (listId ^ (listId >>> 32));
        hash = 29 * hash + Objects.hashCode(uuid);
        hash = 29 * hash + Objects.hashCode(name);
        hash = 29 * hash + (int) (order ^ (order >>> 32));
        hash = 29 * hash + Objects.hashCode(title);
        hash = 29 * hash + Objects.hashCode(description);
        hash = 29 * hash + Objects.hashCode(attachments);
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
        final AttachmentList other = (AttachmentList) obj;
        if (!other.canEqual(this)) {
            System.out.println("Same object");
            return false;
        }

        if (listId != other.getListId()) {
            System.out.println("list ids are not equal");
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            System.out.println("uuid is not equal");
            return false;
        }
        if (order != other.getOrder()) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            System.out.println("name is not equal");
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            System.out.println("caption is not equal");
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            System.out.println("description is not equal");
            return false;
        }
        System.out.printf("attachments{%s}.equals({%s}) = %b\n",
                          Objects.toString(attachments),
                          Objects.toString(other.getAttachments()),
                          Objects.equals(attachments, other.getAttachments()));
        return Objects.equals(getAttachments(), other.getAttachments());
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
                                 + "name = \"%s\", "
                                 + "order = %d, "
                                 + "caption = { %s }, "
                                 + "description = { %s }, "
                                 + "attachments = { %s }%s"
                                 + " }",
                             super.toString(),
                             listId,
                             uuid,
                             name,
                             order,
                             Objects.toString(title),
                             Objects.toString(description),
                             Objects.toString(attachments),
                             data);
    }

}

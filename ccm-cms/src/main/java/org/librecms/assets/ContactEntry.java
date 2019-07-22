/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package org.librecms.assets;

import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * A contact entry for adding data to a {@link ContactableEntity}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "CONTACT_ENTRIES", schema = DB_SCHEMA)
public class ContactEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CONTACT_ENTRY_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long contactEntryId;

    /**
     * Determines the order of the contact entries.
     */
    @Column(name = "ENTRY_ORDER")
    private long order;

    /**
     * Key used to identify the entry.
     */
    @Column(name = "ENTRY_KEY", length = 255, nullable = false)
    private String key;

    /**
     * The value of the entry.
     */
    @Column(name = "ENTRY_VALUE", length = 4096)
    private String value;

    public ContactEntry() {
        super();
        
        order = 0;
    }
    
    public long getContactEntryId() {
        return contactEntryId;
    }

    protected void setContactEntryId(final long contactEntryId) {
        this.contactEntryId = contactEntryId;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(final long order) {
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (contactEntryId ^ (contactEntryId >>> 32));
        hash = 89 * hash + (int) (order ^ (order >>> 32));
        hash = 89 * hash + Objects.hashCode(key);
        hash = 89 * hash + Objects.hashCode(value);
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
        if (!(obj instanceof ContactEntry)) {
            return false;
        }
        final ContactEntry other = (ContactEntry) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (contactEntryId != other.getContactEntryId()) {
            return false;
        }
        if (order != other.getOrder()) {
            return false;
        }
        if (!Objects.equals(key, other.getKey())) {
            return false;
        }
        return Objects.equals(value, other.getValue());
    }

    public boolean canEqual(final Object obj) {

        return obj instanceof ContactEntry;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "contactEntryId = %d, "
                                 + "order = %d"
                                 + "key = \"%s\", "
                                 + "value = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             contactEntryId,
                             order,
                             key,
                             value,
                             data);
    }

}

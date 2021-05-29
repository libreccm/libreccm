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
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "CONTACT_ENTRY_KEYS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "ContactEntryKey.findByEntryKey",
        query = "SELECT k FROM ContactEntryKey k WHERE k.entryKey = :entryKey"
    )
})
public class ContactEntryKey
    implements Comparable<ContactEntryKey>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "KEY_ID")
    private long keyId;

    @Column(name = "ENTRY_KEY", length = 255)
    private String entryKey;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "CONTACT_ENTRY_KEY_LABELS",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "KEY_ID")
            }
        )
    )
    private LocalizedString label;

    public ContactEntryKey() {

        super();
        label = new LocalizedString();
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(final long keyId) {
        this.keyId = keyId;
    }

    public String getEntryKey() {
        return entryKey;
    }

    public void setEntryKey(final String entryKey) {
        this.entryKey = Objects.requireNonNull(entryKey);
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {

        this.label = Objects.requireNonNull(label);
    }

    @Override
    public int compareTo(final ContactEntryKey other) {

        return entryKey.compareTo(other.getEntryKey());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (keyId ^ (keyId >>> 32));
        hash = 73 * hash + Objects.hashCode(entryKey);
        hash = 73 * hash + Objects.hashCode(label);
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
        if (!(obj instanceof ContactEntryKey)) {
            return false;
        }
        final ContactEntryKey other = (ContactEntryKey) obj;
        if (keyId != other.getKeyId()) {
            return false;
        }
        if (!Objects.equals(entryKey, other.getEntryKey())) {
            return false;
        }
        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }
        return true;
    }

    public boolean canEqual(final Object obj) {

        return obj instanceof ContactEntryKey;
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "keyId = %d, "
                                 + "entryKey = \"%s\", "
                                 + "label = %s%s }",
                             super.toString(),
                             keyId,
                             entryKey,
                             Objects.toString(label),
                             data);
    }

    @Override
    public final String toString() {

        return toString("");
    }

}

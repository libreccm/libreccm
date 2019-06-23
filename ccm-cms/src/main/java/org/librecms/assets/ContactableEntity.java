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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.librecms.contentsection.Asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * Base class for contactable entities.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONTACTABLE_ENTITIES", schema = DB_SCHEMA)
public class ContactableEntity extends Asset {

    private static final long serialVersionUID = 1L;

    /**
     * Contact data for this entity.
     */
    @OneToMany
    @JoinColumn(name = "CONTACTABLE_ID")
    @OrderBy("order")
    @Cascade(CascadeType.ALL)
    private List<ContactEntry> contactEntries;

    /**
     * The postal address.
     */
    @OneToOne
    @JoinColumn(name = "POSTAL_ADDRESS_ID")
    private PostalAddress postalAddress;

    public ContactableEntity() {

        super();

        contactEntries = new ArrayList<>();
    }

    public List<ContactEntry> getContactEntries() {
        return Collections.unmodifiableList(contactEntries);
    }

    protected void addContactEntry(final ContactEntry contactEntry) {

        contactEntries.add(contactEntry);
    }

    protected void removeContactEntry(final ContactEntry contactEntry) {

        contactEntries.remove(contactEntry);
    }

    public void setContactEntries(final List<ContactEntry> contactEntries) {
        this.contactEntries = new ArrayList<>(contactEntries);
    }

    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    protected void setPostalAddress(final PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + Objects.hashCode(this.contactEntries);
        hash = 59 * hash + Objects.hashCode(this.postalAddress);
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
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ContactableEntity)) {
            return false;
        }
        final ContactableEntity other = (ContactableEntity) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(this.contactEntries, other.getContactEntries())) {
            return false;
        }
        return Objects.equals(this.postalAddress, other.getPostalAddress());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContactableEntity;
    }

    @Override
    public String toString(final String data) {

        return super.toString(String.format(
            "contactEntries = { %s }, "
                + "postalAddress = %s%s",
            Objects.toString(contactEntries),
            Objects.toString(postalAddress),
            data));
    }

}

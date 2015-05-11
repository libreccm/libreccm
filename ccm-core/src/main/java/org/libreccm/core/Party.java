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
package org.libreccm.core;

import java.io.Serializable;
import java.security.acl.Group;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Internal basic class for {@link User} and {@link Group}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "parties")
public class Party extends CcmObject implements Serializable {

    private static final long serialVersionUID = 6303836654273293979L;

    @ElementCollection
    @CollectionTable(name = "party_email_addresses",
                     joinColumns = {
                         @JoinColumn(name = "party_id")})
    @Size(min = 1)
    private List<EmailAddress> eMailAddresses;

    public List<EmailAddress> getEmailAddresses() {
        return Collections.unmodifiableList(eMailAddresses);
    }

    protected void setEmailAddresses(final List<EmailAddress> eMailAddresses) {
        this.eMailAddresses = eMailAddresses;
    }

    protected void addEmailAddress(final EmailAddress emailAddress) {
        eMailAddresses.add(emailAddress);
    }

    protected void removeEmailAddress(final EmailAddress emailAddress) {
        eMailAddresses.remove(emailAddress);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + Objects.hashCode(this.eMailAddresses);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Party other = (Party) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(this.eMailAddresses, other.getEmailAddresses());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Party;
    }

}

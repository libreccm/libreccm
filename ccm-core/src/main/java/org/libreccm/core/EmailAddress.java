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

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * An embeddable entity for storing email addresses.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Embeddable
public class EmailAddress implements Serializable {

    private static final long serialVersionUID = -4076089589412432766L;

    @Column(name = "email_address", length = 512, nullable = false)
    @NotBlank
    @Email
    private String eMailAddress;

    @Column(name = "bouncing")
    private boolean bouncing;

    @Column(name = "verified")
    private boolean verified;

    public String getEmailAddress() {
        return eMailAddress;
    }

    public void setEmailAddress(final String eMailAddress) {
        this.eMailAddress = eMailAddress;
    }

    public boolean isBouncing() {
        return bouncing;
    }

    public void setBouncing(final boolean bouncing) {
        this.bouncing = bouncing;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(eMailAddress);
        hash = 79 * hash + (bouncing ? 1 : 0);
        hash = 79 * hash + (verified ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmailAddress other = (EmailAddress) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        if (!Objects.equals(eMailAddress, other.getEmailAddress())) {
            return false;
        }
        if (bouncing != other.isBouncing()) {
            return false;
        }
        return verified == other.isVerified();
    }
    
    public boolean canEqual(final Object obj) {
        return obj instanceof EmailAddress;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "eMailAddress = \"%s\", "
                                 + "bouncing = %b, "
                                 + "verified = %b }",
                             super.toString(),
                             eMailAddress,
                             bouncing,
                             verified);
    }

}

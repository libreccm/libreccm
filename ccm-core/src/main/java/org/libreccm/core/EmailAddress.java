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

import static org.libreccm.core.CoreConstants.*;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An embeddable entity for storing email addresses.
 * 
 * In contrast to its predecessor {@code com.arsdigita.kernel.EmailAddress} 
 * this class does not provide verification methods. Verification is done using
 * the <em>Bean Validiation API</em> (Hibernate Validator is used as 
 * implementation). 
 * 
 * Because this class is an embeddable JPA entity it can be used in other 
 * entities to store eMail addresses.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Embeddable
@XmlRootElement(name = "email-address", namespace = CORE_XML_NS)
public class EmailAddress implements Serializable {

    private static final long serialVersionUID = -4076089589412432766L;

    @Column(name = "email_address", length = 512, nullable = false)
    @XmlElement(name = "address", namespace = CORE_XML_NS, required = true)
    @NotBlank
    @Email
    private String address;

    @Column(name = "bouncing")
    @XmlElement(name = "bouncing", namespace = CORE_XML_NS)
    private boolean bouncing;

    @Column(name = "verified")
    @XmlElement(name = "verified", namespace = CORE_XML_NS)
    private boolean verified;

    public String getAddress() {
        return address;
    }

    public void setAddress(final String eMailAddress) {
        this.address = eMailAddress;
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
        hash = 79 * hash + Objects.hashCode(address);
        hash = 79 * hash + (bouncing ? 1 : 0);
        hash = 79 * hash + (verified ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EmailAddress)) {
            return false;
        }
        final EmailAddress other = (EmailAddress) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        if (!Objects.equals(address, other.getAddress())) {
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
                             address,
                             bouncing,
                             verified);
    }

}

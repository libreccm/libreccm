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

import java.io.Serializable;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Internal basic class for {@link User} and {@link Group}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "subjects")
@Inheritance(strategy = InheritanceType.JOINED)
@XmlRootElement(name = "subject", namespace = CORE_XML_NS)
public class Subject implements Serializable {

    private static final long serialVersionUID = 6303836654273293979L;

    @Id
    @Column(name = "subject_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long subjectId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "subject_email_addresses",
                     joinColumns = {
                         @JoinColumn(name = "subject_id")})
    @Size(min = 1)
    @XmlElementWrapper(name = "email-addresses", namespace = CORE_XML_NS)
    @XmlElement(name = "email-address", namespace = CORE_XML_NS)
    private List<EmailAddress> emailAddresses;

    @OneToMany(mappedBy = "grantee")
    //Can't shorten this variable name without reducing descriptiveness
    @SuppressWarnings("PMD.LongVariable")
    @XmlElementWrapper(name = "granted-permissions", namespace = CORE_XML_NS)
    @XmlElement(name = "granted-permission", namespace = CORE_XML_NS)
    private List<Permission> grantedPermissions;

    public Subject() {
        super();

        emailAddresses = new ArrayList<>();
        grantedPermissions = new ArrayList<>();
    }

    public long getSubjectId() {
        return subjectId;
    }

    protected void setSubjectId(final long subjectId) {
        this.subjectId = subjectId;
    }

    public List<EmailAddress> getEmailAddresses() {
        if (emailAddresses == null) {
            return null;
        } else {
            return Collections.unmodifiableList(emailAddresses);
        }
    }

    protected void setEmailAddresses(final List<EmailAddress> eMailAddresses) {
        this.emailAddresses = eMailAddresses;
    }

    protected void addEmailAddress(final EmailAddress emailAddress) {
        emailAddresses.add(emailAddress);
    }

    protected void removeEmailAddress(final EmailAddress emailAddress) {
        emailAddresses.remove(emailAddress);
    }

    public List<Permission> getGrantedPermissions() {
        return Collections.unmodifiableList(grantedPermissions);
    }

    //Can't shorten this variable name without reducing descriptiveness
    @SuppressWarnings("PMD.LongVariable")
    protected void setGrantedPermissions(
        final List<Permission> grantedPermissions) {
        this.grantedPermissions = grantedPermissions;
    }

    protected void addGrantedPermission(final Permission permission) {
        grantedPermissions.add(permission);
    }

    protected void removeGrantedPermission(final Permission permission) {
        grantedPermissions.remove(permission);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (subjectId ^ (subjectId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Subject)) {
            return false;
        }
        final Subject other = (Subject) obj;
        if (subjectId != other.getSubjectId()) {
            return false;
        }

        if (!other.canEqual(this)) {
            return false;
        }

        return subjectId == other.getSubjectId();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Subject;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "subjectId = %d, "
                                 + "emailAddresses = %s"
                                 + "%s"
                                 + " }",
                             super.toString(),
                             subjectId,
                             Objects.toString(emailAddresses),
                             data);
    }

}

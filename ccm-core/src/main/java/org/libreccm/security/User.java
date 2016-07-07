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
package org.libreccm.security;

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.core.DefaultEntityGraph;
import org.libreccm.core.EmailAddress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A user is a person (or a system) accessing CCM. A user authenticates itself
 * using a password or other credentials.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "USERS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "User.findByName",
                query = "SELECT u FROM User u WHERE u.name = :name "
                            + "ORDER BY u.name, "
                            + "         u.familyName, "
                            + "         u.givenName, "
                            + "         u.primaryEmailAddress.address"),
    @NamedQuery(name = "User.findByEmailAddress",
                query = "SELECT u FROM User u WHERE "
                            + "u.primaryEmailAddress.address = :emailAddress "
                            + "ORDER BY u.name, "
                            + " u.familyName, "
                            + " u.givenName, "
                            + " u.primaryEmailAddress.address"),
    @NamedQuery(
        name = "User.filterByNameAndEmail",
        query = "SELECT u FROM User u WHERE "
                    + "LOWER(u.name) LIKE CONCAT(LOWER(:term), '%') "
                    + "OR LOWER(u.givenName) LIKE CONCAT(LOWER(:term), '%') "
                    + "OR LOWER(u.familyName) LIKE CONCAT(LOWER(:term), '%') "
                    + "OR LOWER(u.primaryEmailAddress.address) LIKE CONCAT('%', LOWER(:term), '%') "
                + "ORDER BY u.name,"
                    + "u.familyName, "
                    + "u.givenName, "
                    + "u.primaryEmailAddress.address"),
    @NamedQuery(
        name = "User.findAllOrderedByUsername",
        query = "SELECT u FROM User u ORDER BY u.name, "
                    + "                    u.familyName, "
                    + "                    u.givenName, "
                    + "                    u.primaryEmailAddress.address")
})
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "User.withGroupAndRoleMemberships",
        attributeNodes = {
            @NamedAttributeNode(
                value = "groupMemberships"),
            @NamedAttributeNode(
                value = "roleMemberships",
                subgraph = "role")},
        subgraphs = {
            @NamedSubgraph(
                name = "role",
                attributeNodes = {
                    @NamedAttributeNode(value = "role",
                                        subgraph = "permissions")
                }),
            @NamedSubgraph(
                name = "permissions",
                attributeNodes = {
                    @NamedAttributeNode(value = "permissions")}
            )
        })
})
@DefaultEntityGraph("User.withGroupAndRoleMemberships")
@XmlRootElement(name = "user", namespace = CORE_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
//Supressing a few warnings from PMD because they misleading here.
//User is perfectly fine class name, and the complexity is not to high...
@SuppressWarnings({"PMD.ShortClassName", "PMD.LongVariable"})
public class User extends Party implements Serializable {

    private static final long serialVersionUID = 4035223413596611393L;

    /**
     * The given name of the user.
     */
    @Column(name = "GIVEN_NAME", length = 512)
    @XmlElement(name = "given-name", namespace = CORE_XML_NS)
    private String givenName;

    /**
     * The family name of the user.
     */
    @Column(name = "FAMILY_NAME", length = 512)
    @XmlElement(name = "family-name", namespace = CORE_XML_NS)
    private String familyName;

    /**
     * The primary email address of the user.
     */
    @Embedded
    @AssociationOverride(
        name = "USER_PRIMARY_EMAIL_ADDRESSES",
        joinTable = @JoinTable(name = "USER_PRIMARY_EMAIL_ADDRESSES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "USER_ID")
                               }))
    @NotNull
    @XmlElement(name = "primary-email-address", namespace = CORE_XML_NS)
    private EmailAddress primaryEmailAddress;

    /**
     * Additional email addresses of the user.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_EMAIL_ADDRESSES",
                     schema = DB_SCHEMA,
                     joinColumns = {
                         @JoinColumn(name = "USER_ID")})
    @XmlElementWrapper(name = "email-addresses", namespace = CORE_XML_NS)
    @XmlElement(name = "email-address", namespace = CORE_XML_NS)
    private List<EmailAddress> emailAddresses;

    /**
     * A user can be banned which means that he or she can't login into the
     * system anymore. We use this approach rather than simply deleting users to
     * preserve the edit history of several objects.
     */
    @Column(name = "BANNED")
    @XmlElement(name = "banned", namespace = CORE_XML_NS)
    private boolean banned;

    /**
     * The hashed password of the user. The algorithm used is determined by the
     * Shiro configuration. The hash is stored in Shiros hash format which also
     * contains the algorithm used, the number of iterations and the salt used.
     */
    @Column(name = "PASSWORD", length = 2048)
    @XmlTransient
    private String password;

    /**
     * Indicates that the user should be forced to change his or her password on
     * the next login.
     */
    @Column(name = "PASSWORD_RESET_REQUIRED")
    //Can't shorten the name without making the name cryptic.
    private boolean passwordResetRequired;

    /**
     * The groups of which the user is a member.
     */
    @OneToMany(mappedBy = "member")
    @XmlElementWrapper(name = "group-memberships", namespace = CORE_XML_NS)
    @XmlElement(name = "group-membership", namespace = CORE_XML_NS)
    private Set<GroupMembership> groupMemberships = new HashSet<>();

    protected User() {
        super();
        emailAddresses = new ArrayList<>();
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }

    public EmailAddress getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public void setPrimaryEmailAddress(final EmailAddress primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    public List<EmailAddress> getEmailAddresses() {
        if (emailAddresses == null) {
            return null;
        } else {
            return Collections.unmodifiableList(emailAddresses);
        }
    }

    protected void setEmailAddresses(final List<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public void addEmailAddress(final EmailAddress emailAddress) {
        emailAddresses.add(emailAddress);
    }

    public void removeEmailAddress(final EmailAddress emailAddress) {
        emailAddresses.remove(emailAddress);
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(final boolean banned) {
        this.banned = banned;
    }

    public String getPassword() {
        return password;
    }

    protected void setPassword(final String password) {
        this.password = password;
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }

    public void setPasswordResetRequired(final boolean passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }

    public Set<GroupMembership> getGroupMemberships() {
        return Collections.unmodifiableSet(groupMemberships);
    }

    protected void setGroupMemberships(
        final Set<GroupMembership> groupMemberships) {
        this.groupMemberships = groupMemberships;
    }

    protected void addGroupMembership(final GroupMembership groupMembership) {
        groupMemberships.add(groupMembership);
    }

    protected void removeGroupMembership(
        final GroupMembership groupMembership) {
        groupMemberships.remove(groupMembership);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * hash + Objects.hashCode(givenName);
        hash = 31 * hash + Objects.hashCode(familyName);
        hash = 31 * hash + Objects.hashCode(primaryEmailAddress);
        hash = 31 * hash + (banned ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(givenName, other.getGivenName())) {
            return false;
        }
        if (!Objects.equals(familyName, other.getFamilyName())) {
            return false;
        }
        if (!Objects.equals(primaryEmailAddress, other.getPrimaryEmailAddress())) {
            return false;
        }
        return banned == other.isBanned();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof User;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(
            ", givenName = \"%s\", "
                + "familyName = \"%s\", "
                + "primaryEmailAddress = { %s }, "
                + "emailAddresses = { %s }, "
                + "banned = %b, "
                + "passwordResetRequired = %b%s",
            givenName,
            familyName,
            Objects.toString(primaryEmailAddress),
            Objects.toString(emailAddresses),
            banned,
            passwordResetRequired,
            data
        ));
    }

}

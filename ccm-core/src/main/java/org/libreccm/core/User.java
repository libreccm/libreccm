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
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The {@code User} entity stores the name and the password of a user along with
 * some other informations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ccm_users")
@NamedQueries({
    @NamedQuery(name = "findUserByScreenName",
                query = "SELECT u FROM User u WHERE u.screenName = :screenname"),
    @NamedQuery(name = "findUserByEmail",
                query = "SELECT u FROM User u JOIN u.emailAddresses e"
                            + "WHERE e.address = :emailAddress")})
@XmlRootElement(name = "user", namespace = CORE_XML_NS)
//Supressing a few warnings from PMD because they misleading here.
//User is perfectly fine class name, and the complexity is not to high...
@SuppressWarnings({"PMD.ShortClassName",
                   "PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity"})
public class User extends Subject implements Serializable {

    private static final long serialVersionUID = 892038270064849732L;

    /**
     * The real name of the user. We are using an {@code Embeddable} here.
     */
    @Embedded
    @AssociationOverride(
        name = "user_names",
        joinTable = @JoinTable(name = "user_names",
                               joinColumns = {
                                   @JoinColumn(name = " user_id")}))
    @XmlElement(name = "person-name", namespace = CORE_XML_NS)
    private PersonName name;

    /**
     * A user name of the user. Usually an abbreviation of the users real name.
     * For example a the <em>John Doe</em> might have the scree name
     * <code>jdoe</code>. The screen name is used as user name for logins (if
     * the system if configured so, otherwise the email address of the user is
     * used).
     */
    @Column(name = "screen_name", length = 255, nullable = false, unique = true)
    @NotBlank
    @XmlElement(name = "screen-name", namespace = CORE_XML_NS)
    private String screenName;

    /**
     * A user can be banned which means that he or she can't login into the
     * system anymore.
     */
    @Column(name = "banned")
    @XmlElement(name = "banned", namespace = CORE_XML_NS)
    private boolean banned;

    /**
     * An alias for the user used in an another system for SSO, for example
     * LDAP.
     */
    @Column(name = "sso_login", length = 512)
    @XmlElement(name = "sso-login", namespace = CORE_XML_NS)
    private String ssoLogin;

    /**
     * The hashed password of the user.
     */
    @Column(name = "password", length = 2048)
    @XmlTransient
    private String password;

    /**
     * The salt used to hash the password.
     */
    @Column(name = "salt", length = 2048)
    @XmlTransient
    private String salt;

    /**
     * The hash algorithm used to hash the password. This allows us the change
     * to another, stronger hash algorithm without invalidating existing
     * accounts. The algorithm to use for new passwords can be configured by the
     * administrator.
     *
     */
    @Column(name = "hash_algorithm", length = 64)
    @XmlTransient
    private String hashAlgorithm;

    /**
     * Indicates that the user should be forced to change his or her password on
     * the next login.
     */
    @Column(name = "password_reset_required")
    private boolean passwordResetRequired;

    /**
     * Question the recover a forgotten password.
     */
    @Column(name = "password_question", length = 2048)
    @XmlElement(name = "password-question", namespace = CORE_XML_NS)
    private String passwordQuestion;

    /**
     * Answer the the {@link #passwordQuestion}.
     */
    @Column(name = "password_answer", length = 2048)
    @XmlElement(name = "password-answer", namespace = CORE_XML_NS)
    private String passwordAnswer;

    /**
     * The groups of which the user is a member.
     */
    @OneToMany(mappedBy = "user")
    @XmlElementWrapper(name = "group-memberships")
    @XmlElement(name = "group-membership", namespace = CORE_XML_NS)
    private List<GroupMembership> groupMemberships;

    public User() {
        super();

        name = new PersonName();
        this.groupMemberships = new ArrayList<>();
    }

    public PersonName getName() {
        return name;
    }

    public void setName(final PersonName name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(final String screenName) {
        this.screenName = screenName;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(final boolean banned) {
        this.banned = banned;
    }

    public String getSsoLogin() {
        return ssoLogin;
    }

    public void setSsoLogin(final String ssoLogin) {
        this.ssoLogin = ssoLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(final String salt) {
        this.salt = salt;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(final String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }

    public void setPasswordResetRequired(final boolean passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }

    public String getPasswordQuestion() {
        return passwordQuestion;
    }

    public void setPasswordQuestion(final String passwordQuestion) {
        this.passwordQuestion = passwordQuestion;
    }

    public String getPasswordAnswer() {
        return passwordAnswer;
    }

    public void setPasswordAnswer(final String passwordAnswer) {
        this.passwordAnswer = passwordAnswer;
    }

    public List<GroupMembership> getGroupMemberships() {
        return Collections.unmodifiableList(groupMemberships);
    }

    protected void setGroupMemberships(
        final List<GroupMembership> groupMemberships) {
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
        hash = 59 * hash + Objects.hashCode(name);
        hash = 59 * hash + Objects.hashCode(screenName);
        hash = 59 * hash + (banned ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(ssoLogin);
        hash = 59 * hash + Objects.hashCode(password);
        hash = 59 * hash + Objects.hashCode(salt);
        hash = 59 * hash + Objects.hashCode(hashAlgorithm);
        hash = 59 * hash + (passwordResetRequired ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(passwordQuestion);
        hash = 59 * hash + Objects.hashCode(passwordAnswer);
        return hash;
    }

    @Override
    //Can't reduce complexity yet
    @SuppressWarnings({"PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity",
                       "PMD.NPathComplexity"})
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(screenName, other.getScreenName())) {
            return false;
        }
        if (banned != other.isBanned()) {
            return false;
        }
        if (!Objects.equals(ssoLogin, other.getSsoLogin())) {
            return false;
        }
        if (!Objects.equals(password, other.getPassword())) {
            return false;
        }
        if (!Objects.equals(salt, other.getSalt())) {
            return false;
        }

        if (!Objects.equals(hashAlgorithm, other.getHashAlgorithm())) {
            return false;
        }

        if (passwordResetRequired != other.isPasswordResetRequired()) {
            return false;
        }

        if (!Objects.equals(passwordQuestion, other.getPasswordQuestion())) {
            return false;
        }
        return Objects.equals(passwordAnswer, other.getPasswordAnswer());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof User;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", name = %s, "
                                                + "screenName = \"%s\", "
                                                + "banned = %b, "
                                                + "ssoLogin = \"%s\""
                                                + "hashAlgorithm = \"%s\""
                                                + "passwordResetRequired = %b%s",
                                            Objects.toString(name),
                                            screenName,
                                            banned,
                                            ssoLogin,
                                            hashAlgorithm,
                                            passwordResetRequired,
                                            data));
    }

}

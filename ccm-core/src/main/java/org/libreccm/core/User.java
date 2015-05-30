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

import javax.persistence.OneToMany;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "users")
public class User extends Party implements Serializable {

    private static final long serialVersionUID = 892038270064849732L;

    @Embedded
    @AssociationOverride(
        name = "user_names",
        joinTable = @JoinTable(name = "user_names",
                               joinColumns = {
                                   @JoinColumn(name = " user_id")}))
    private PersonName name;

    @Column(name = "screen_name", length = 255, nullable = false)
    @NotBlank
    private String screenName;

    @Column(name = "banned")
    private boolean banned;

    @Column(name = "sso_login", length = 512)
    private String ssoLogin;

    @Column(name = "password", length = 2048)
    private String password;

    @Column(name = "salt", length = 2048)
    private String salt;

    @Column(name = "password_question", length = 2048)
    private String passwordQuestion;

    @Column(name = "password_answer", length = 2048)
    private String passwordAnswer;

    @OneToMany(mappedBy = "user")
    private List<GroupMembership> groupMemberships;

    public User() {
        super();
        
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
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.screenName);
        hash = 59 * hash + (this.banned ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.ssoLogin);
        hash = 59 * hash + Objects.hashCode(this.password);
        hash = 59 * hash + Objects.hashCode(this.salt);
        hash = 59 * hash + Objects.hashCode(this.passwordQuestion);
        hash = 59 * hash + Objects.hashCode(this.passwordAnswer);
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
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        if (!Objects.equals(this.screenName, other.getScreenName())) {
            return false;
        }
        if (this.banned != other.isBanned()) {
            return false;
        }
        if (!Objects.equals(this.ssoLogin, other.getSsoLogin())) {
            return false;
        }
        if (!Objects.equals(this.password, other.getPassword())) {
            return false;
        }
        if (!Objects.equals(this.salt, other.getSalt())) {
            return false;
        }
        if (!Objects.equals(this.passwordQuestion, other.getPasswordQuestion())) {
            return false;
        }
        return Objects.equals(this.passwordAnswer, other.getPasswordAnswer());
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
                                                + "ssoLogin = \"%s\"%s",
                                            Objects.toString(name),
                                            screenName,
                                            banned,
                                            ssoLogin,
                                            data));
    }

}

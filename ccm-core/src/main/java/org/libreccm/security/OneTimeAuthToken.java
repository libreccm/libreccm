/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * The {@code OneTimeAuthToken} is used as a one time authentication mechanism
 * for several purposes. Usually a {@link OneTimeAuthToken} is used together
 * with a challenge created by the {@link ChallengeManager}. The supported
 * purposes at the moment are:
 * <dl>
 * <dt>account activation</dt>
 * <dd>If user registration is enabled a new user will receive an email from the
 * system with a {@link OneTimeAuthToken}. This token is used to verify the
 * email address of the user and to complete the registration process.
 * </dd>
 * <dt>Email verification</dt>
 * <dd>The systems sends a mail to an email address containing a
 * {@link OneTimeAuthToken}. The user must confirm his/her email address by
 * submitting the {@link OneTimeAuthToken} to the system.</dd>
 * <dt>Password recover</dt>
 * <dd>Sometimes users forget their password. To recover their password they
 * must first request a {@link OneTimeAuthToken}. After they submitted this
 * token to the system they can change their password.</dd>
 * </dl>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ONE_TIME_AUTH_TOKENS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "OneTimeAuthToken.findByUserAndPurpose",
        query = "SELECT t FROM OneTimeAuthToken t "
                    + "WHERE t.user = :user AND t.purpose = :purpose "
                    + "ORDER BY t.validUntil")})
public class OneTimeAuthToken implements Serializable {

    private static final long serialVersionUID = -9088185274208292873L;

    @Id
    @Column(name = "TOKEN_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long tokenId;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "TOKEN", length = 255)
    private String token;

    @Column(name = "VALID_UNTIL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validUntil;

    @Column(name = "PURPOSE")
    @Enumerated(EnumType.STRING)
    private OneTimeAuthTokenPurpose purpose;

    public long getTokenId() {
        return tokenId;
    }

    protected void setTokenId(final long tokenId) {
        this.tokenId = tokenId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public Date getValidUntil() {
        if (validUntil == null) {
            return null;
        } else {
            return new Date(validUntil.getTime());
        }
    }

    public void setValidUntil(final Date validUntil) {
        this.validUntil = new Date(validUntil.getTime());
    }

    public OneTimeAuthTokenPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(final OneTimeAuthTokenPurpose purpose) {
        this.purpose = purpose;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(user);
        hash = 97 * hash + Objects.hashCode(token);
        hash = 97 * hash + Objects.hashCode(validUntil);
        hash = 97 * hash + Objects.hashCode(purpose);
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
        if (!(obj instanceof OneTimeAuthToken)) {
            return false;
        }
        final OneTimeAuthToken other = (OneTimeAuthToken) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(token, other.getToken())) {
            return false;
        }
        if (!Objects.equals(user, other.getUser())) {
            return false;
        }
        if (!Objects.equals(validUntil, other.getValidUntil())) {
            return false;
        }
        return purpose == other.getPurpose();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof OneTimeAuthToken;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "tokenId = %d, "
                                 + "user = { %s }, "
                                 + "validUnit = %tF %<tT%s"
                                 + " }",
                             super.toString(),
                             tokenId,
                             Objects.toString(user),
                             validUntil,
                             data);
    }

}

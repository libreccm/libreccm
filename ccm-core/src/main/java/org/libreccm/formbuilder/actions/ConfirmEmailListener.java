/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.libreccm.formbuilder.actions;

import org.libreccm.formbuilder.ProcessListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FORMBUILDER_CONFIRM_EMAIL_LISTENER", schema = DB_SCHEMA)
public class ConfirmEmailListener
    extends ProcessListener
    implements Serializable {

    private static final long serialVersionUID = -7009695795355273248L;

    @Column(name = "FROM_EMAIL")
    private String fromEmail;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "BODY")
    @Lob
    private String body;

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(final String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 19 * hash + Objects.hashCode(this.fromEmail);
        hash = 19 * hash + Objects.hashCode(this.subject);
        hash = 19 * hash + Objects.hashCode(this.body);
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

        if (!(obj instanceof ConfirmEmailListener)) {
            return false;
        }
        final ConfirmEmailListener other = (ConfirmEmailListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(this.fromEmail, other.getFromEmail())) {
            return false;
        }
        if (!Objects.equals(this.subject, other.getSubject())) {
            return false;
        }
        return Objects.equals(this.body, other.getBody());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ConfirmEmailListener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", fromEmail = \"%s\","
                                                + "subject = \"%s\"%s",
                                            fromEmail,
                                            subject,
                                            data));
    }

}
